package net.thejadeproject.ascension.common.items.tools.soul_tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.common.items.tools.data.soul_tool.SoulToolComponent;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.data_attachments.attachments.SoulToolData;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.qi.EntityQiContainer;
import net.thejadeproject.ascension.util.ModTags;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SoulToolEffectHelper {

    private static final double BASE_VEIN_QI_COST = 12.0D;
    private static final double BASE_EMERALD_LUCK = 1.0D;
    private static final double EMERALD_LUCK_PER_POWER = 0.50D;

    private static final ResourceLocation EMERALD_LUCK_ID =
            ResourceLocation.fromNamespaceAndPath(
                    AscensionCraft.MOD_ID,
                    "soul_implement_emerald_luck"
            );

    private static final Set<UUID> ACTIVE_VEIN_MINERS =
            ConcurrentHashMap.newKeySet();

    private SoulToolEffectHelper() {
    }

    public static void applyStackEffects(
            ItemStack stack,
            ServerPlayer player,
            SoulToolData data
    ) {
        applyHarvestEnchantment(stack, player, data);
        applyEmeraldLuck(stack, data);
    }

    private static void applyHarvestEnchantment(
            ItemStack stack,
            ServerPlayer player,
            SoulToolData data
    ) {
        var enchantmentRegistry =
                player.registryAccess()
                        .registryOrThrow(Registries.ENCHANTMENT);

        ItemEnchantments.Mutable mutable =
                new ItemEnchantments.Mutable(
                        ItemEnchantments.EMPTY
                );

        switch (data.harvest) {
            case DIAMOND -> mutable.set(
                    enchantmentRegistry.getHolderOrThrow(
                            Enchantments.FORTUNE
                    ),
                    getFortuneLevel(data.lastSoulMajor)
            );

            case QUARTZ -> mutable.set(
                    enchantmentRegistry.getHolderOrThrow(
                            Enchantments.SILK_TOUCH
                    ),
                    1
            );

            case NONE, SPIRIT_STONE -> {
            }
        }

        ItemEnchantments desired =
                mutable.toImmutable().withTooltip(false);

        ItemEnchantments current =
                stack.get(DataComponents.ENCHANTMENTS);

        if (!desired.equals(current)) {
            EnchantmentHelper.setEnchantments(
                    stack,
                    desired
            );
        }
    }

    private static void applyEmeraldLuck(
            ItemStack stack,
            SoulToolData data
    ) {
        ItemAttributeModifiers desired;

        if (data.spirit == SoulToolSpirit.EMERALD) {
            desired = ItemAttributeModifiers.builder()
                    .add(
                            Attributes.LUCK,
                            new AttributeModifier(
                                    EMERALD_LUCK_ID,
                                    getEmeraldLuckBonus(data),
                                    AttributeModifier.Operation.ADD_VALUE
                            ),
                            EquipmentSlotGroup.MAINHAND
                    )
                    .build()
                    .withTooltip(false);
        } else {
            desired = ItemAttributeModifiers.EMPTY;
        }

        ItemAttributeModifiers current =
                stack.get(DataComponents.ATTRIBUTE_MODIFIERS);

        if (!desired.equals(current)) {
            stack.set(
                    DataComponents.ATTRIBUTE_MODIFIERS,
                    desired
            );
        }
    }


    public static float modifyBreakSpeed(
            Player player,
            SoulToolComponent component,
            float currentSpeed
    ) {
        if (currentSpeed <= 0.0F) {
            return currentSpeed;
        }

        if (component == null
                || !component.owner().equals(player.getUUID())
                || !component.core().isFormed()) {
            return currentSpeed;
        }

        double power = component.soulToolPower();

        double multiplier =
                getRealmSpeedMultiplier(power)
                        * component.core().speedMultiplier();

        if (component.flow() == SoulToolFlow.REDSTONE) {
            multiplier *= getRedstoneSpeedMultiplier(power);
        }

        if (component.flow() == SoulToolFlow.UNDEAD_CORE) {
            double resistance =
                    getPhantomExcavationResistance(power);

            double penaltyRecovery =
                    1.0D + 4.0D * resistance;

            if (!player.onGround()) {
                multiplier *= penaltyRecovery;
            }

            if (player.isEyeInFluid(FluidTags.WATER)
                    && !hasAquaAffinity(player)) {
                multiplier *= penaltyRecovery;
            }
        }

        return (float) Math.min(
                Float.MAX_VALUE,
                currentSpeed * multiplier
        );
    }

    private static boolean hasAquaAffinity(Player player) {
        var enchantmentRegistry =
                player.registryAccess()
                        .registryOrThrow(Registries.ENCHANTMENT);

        return EnchantmentHelper.getEnchantmentLevel(
                enchantmentRegistry.getHolderOrThrow(
                        Enchantments.AQUA_AFFINITY
                ),
                player
        ) > 0;
    }


    public static void handleBlockDrops(
            BlockDropsEvent event,
            ServerPlayer player,
            SoulToolData data
    ) {
        if (player.isCreative()) {
            return;
        }

        if (data.harvest == SoulToolHarvest.SPIRIT_STONE) {
            applyResonantExtraction(
                    event,
                    player,
                    data
            );
        }

        switch (data.spirit) {
            case EMERALD -> applyProsperityDrop(
                    event,
                    player,
                    data
            );

            case LAPIS -> applyExperienceMultiplier(
                    event,
                    data
            );

            case LIVING_CORE -> applyLivingCoreReplant(
                    event,
                    player,
                    data
            );

            case NONE -> {
            }
        }
    }

    private static void applyResonantExtraction(
            BlockDropsEvent event,
            ServerPlayer player,
            SoulToolData data
    ) {
        if (!event.getState().is(
                ModTags.Blocks.SOUL_TOOL_RESONANT_BLOCKS
        )) {
            return;
        }

        if (!player.hasData(ModAttachments.ENTITY_DATA)) {
            return;
        }

        IEntityData entityData =
                player.getData(ModAttachments.ENTITY_DATA);

        float hardness = Math.max(
                0.0F,
                event.getState().getDestroySpeed(
                        event.getLevel(),
                        event.getPos()
                )
        );

        double qiGain = Math.min(
                30.0D,
                4.0D
                        + getSoulToolPower(data) * 1.25D
                        + Math.min(8.0D, hardness * 0.40D)
                        + event.getDroppedExperience() * 0.75D
        );

        entityData.getQiContainer().addQi(qiGain);
    }


    private static void applyProsperityDrop(
            BlockDropsEvent event,
            ServerPlayer player,
            SoulToolData data
    ) {
        BlockState state = event.getState();

        if (!state.is(
                ModTags.Blocks.SOUL_TOOL_PROSPERITY_BLOCKS
        )) {
            return;
        }

        double chance =
                getProsperityChance(getSoulToolPower(data));

        if (event.getLevel().random.nextDouble() >= chance) {
            return;
        }

        Item brokenBlockItem =
                state.getBlock().asItem();

        List<ItemEntity> eligibleDrops =
                new ArrayList<>();

        for (ItemEntity dropEntity : event.getDrops()) {
            ItemStack drop = dropEntity.getItem();

            if (drop.isEmpty()) {
                continue;
            }

            if (drop.is(brokenBlockItem)) {
                continue;
            }

            eligibleDrops.add(dropEntity);
        }

        if (eligibleDrops.isEmpty()) {
            return;
        }

        ItemEntity selected =
                eligibleDrops.get(
                        event.getLevel().random.nextInt(
                                eligibleDrops.size()
                        )
                );

        ItemStack bonus =
                selected.getItem().copyWithCount(1);

        ItemEntity bonusEntity =
                new ItemEntity(
                        event.getLevel(),
                        event.getPos().getX() + 0.5D,
                        event.getPos().getY() + 0.5D,
                        event.getPos().getZ() + 0.5D,
                        bonus
                );

        bonusEntity.setDefaultPickUpDelay();
        event.getDrops().add(bonusEntity);
    }

    private static void applyExperienceMultiplier(
            BlockDropsEvent event,
            SoulToolData data
    ) {
        int originalExperience =
                event.getDroppedExperience();

        if (originalExperience <= 0) {
            return;
        }

        double multiplied =
                originalExperience
                        * getExperienceMultiplier(
                        getSoulToolPower(data)
                );

        event.setDroppedExperience(
                stochasticRound(
                        event.getLevel(),
                        multiplied
                )
        );
    }

    private static int stochasticRound(
            ServerLevel level,
            double value
    ) {
        int whole = Mth.floor(value);
        double remainder = value - whole;

        if (level.random.nextDouble() < remainder) {
            whole++;
        }

        return Math.max(0, whole);
    }

    private static void applyLivingCoreReplant(
            BlockDropsEvent event,
            ServerPlayer player,
            SoulToolData data
    ) {
        SoulToolComponent component =
                event.getTool().get(
                        ModDataComponents.SOUL_TOOL.get()
                );

        if (component == null
                || component.type() != SoulToolType.HOE) {
            return;
        }

        if (!(event.getState().getBlock()
                instanceof CropBlock crop)) {
            return;
        }

        if (!crop.isMaxAge(event.getState())) {
            return;
        }

        ItemStack plantingItem =
                crop.getCloneItemStack(
                        event.getLevel(),
                        event.getPos(),
                        event.getState()
                );

        if (plantingItem.isEmpty()) {
            return;
        }

        ItemEntity plantingDrop =
                findPlantingDrop(
                        event.getDrops(),
                        plantingItem
                );

        if (plantingDrop == null) {
            return;
        }

        double preserveChance =
                getSeedPreservationChance(
                        getSoulToolPower(data)
                );

        boolean preserveSeed =
                event.getLevel().random.nextDouble()
                        < preserveChance;

        if (!preserveSeed) {
            ItemStack droppedSeeds =
                    plantingDrop.getItem();

            droppedSeeds.shrink(1);

            if (droppedSeeds.isEmpty()) {
                event.getDrops().remove(plantingDrop);
            }
        }

        ServerLevel level = event.getLevel();
        BlockPos pos = event.getPos().immutable();
        BlockState ageZero = crop.getStateForAge(0);

        level.getServer().execute(() -> {
            if (!level.getBlockState(pos).isAir()) {
                return;
            }

            if (!ageZero.canSurvive(level, pos)) {
                return;
            }

            level.setBlock(
                    pos,
                    ageZero,
                    Block.UPDATE_ALL
            );
        });
    }

    private static ItemEntity findPlantingDrop(
            List<ItemEntity> drops,
            ItemStack plantingItem
    ) {
        for (ItemEntity dropEntity : drops) {
            ItemStack droppedStack =
                    dropEntity.getItem();

            if (!droppedStack.isEmpty()
                    && droppedStack.is(
                    plantingItem.getItem()
            )) {
                return dropEntity;
            }
        }

        return null;
    }

    public static boolean shouldQueueVeinMining(
            ServerPlayer player,
            SoulToolData data,
            BlockState originalState
    ) {
        if (ACTIVE_VEIN_MINERS.contains(
                player.getUUID()
        )) {
            return false;
        }

        if (!player.isShiftKeyDown()) {
            return false;
        }

        if (!data.bound
                || !data.summoned
                || !data.core.isFormed()
                || data.flow != SoulToolFlow.COPPER
                || data.activeType != SoulToolType.PICKAXE) {
            return false;
        }

        if (!originalState.is(
                ModTags.Blocks.SOUL_TOOL_VEIN_MINEABLE
        )) {
            return false;
        }

        return SoulToolHelper.isCurrentManifestation(
                player.getMainHandItem(),
                player,
                data
        );
    }

    public static void queueVeinMining(
            ServerPlayer player,
            BlockPos origin,
            BlockState originState
    ) {
        ServerLevel level = player.serverLevel();

        level.getServer().execute(() ->
                performVeinMining(
                        player,
                        level,
                        origin.immutable(),
                        originState
                )
        );
    }

    private static void performVeinMining(
            ServerPlayer player,
            ServerLevel level,
            BlockPos origin,
            BlockState originState
    ) {
        UUID playerId = player.getUUID();

        if (!ACTIVE_VEIN_MINERS.add(playerId)) {
            return;
        }

        try {
            if (!player.isAlive()
                    || player.serverLevel() != level) {
                return;
            }

            SoulToolData data =
                    player.getData(
                            ModAttachments.SOUL_TOOL
                    );

            if (!data.bound
                    || !data.summoned
                    || !data.core.isFormed()
                    || data.flow != SoulToolFlow.COPPER
                    || data.activeType
                    != SoulToolType.PICKAXE) {
                return;
            }

            ItemStack tool =
                    player.getMainHandItem();

            if (!SoulToolHelper.isCurrentManifestation(
                    tool,
                    player,
                    data
            )) {
                return;
            }

            if (!player.hasData(
                    ModAttachments.ENTITY_DATA
            )) {
                return;
            }

            IEntityData entityData =
                    player.getData(
                            ModAttachments.ENTITY_DATA
                    );

            EntityQiContainer qiContainer =
                    entityData.getQiContainer();

            double qiCostPerBlock =
                    getVeinQiCost(data.core);

            int affordableBlocks =
                    (int) Math.floor(
                            qiContainer.getCurrentQi()
                                    / qiCostPerBlock
                    );

            int maxExtraBlocks = Math.min(
                    getMaximumVeinBlocks(data),
                    affordableBlocks
            );

            if (maxExtraBlocks <= 0) {
                return;
            }

            List<BlockPos> connectedBlocks =
                    findConnectedVeinBlocks(
                            level,
                            origin,
                            originState,
                            maxExtraBlocks
                    );

            int brokenBlocks = 0;

            for (BlockPos targetPos : connectedBlocks) {
                if (brokenBlocks >= maxExtraBlocks) {
                    break;
                }

                ItemStack currentTool =
                        player.getMainHandItem();

                SoulToolData currentData =
                        player.getData(
                                ModAttachments.SOUL_TOOL
                        );

                if (!SoulToolHelper.isCurrentManifestation(
                        currentTool,
                        player,
                        currentData
                )) {
                    break;
                }

                BlockState targetState =
                        level.getBlockState(targetPos);

                if (!targetState.is(
                        ModTags.Blocks.SOUL_TOOL_VEIN_MINEABLE
                )) {
                    continue;
                }

                if (!sameVeinFamily(
                        originState,
                        targetState
                )) {
                    continue;
                }

                if (targetState.hasBlockEntity()) {
                    continue;
                }

                if (!player.hasCorrectToolForDrops(
                        targetState
                )) {
                    continue;
                }

                if (player.gameMode.destroyBlock(
                        targetPos
                )) {
                    brokenBlocks++;
                }
            }

            if (brokenBlocks > 0) {
                double totalCost =
                        brokenBlocks * qiCostPerBlock;

                qiContainer.setCurrentQi(
                        Math.max(
                                0.0D,
                                qiContainer.getCurrentQi()
                                        - totalCost
                        )
                );
            }
        } finally {
            ACTIVE_VEIN_MINERS.remove(playerId);
        }
    }

    private static List<BlockPos> findConnectedVeinBlocks(
            ServerLevel level,
            BlockPos origin,
            BlockState originState,
            int maximum
    ) {
        List<BlockPos> found = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> searchQueue =
                new ArrayDeque<>();

        visited.add(origin);
        addNeighbours(searchQueue, origin);

        while (!searchQueue.isEmpty()
                && found.size() < maximum) {
            BlockPos current =
                    searchQueue.removeFirst();

            if (!visited.add(current)) {
                continue;
            }

            if (!level.hasChunkAt(current)) {
                continue;
            }

            BlockState state =
                    level.getBlockState(current);

            if (!state.is(
                    ModTags.Blocks.SOUL_TOOL_VEIN_MINEABLE
            )) {
                continue;
            }

            if (!sameVeinFamily(
                    originState,
                    state
            )) {
                continue;
            }

            found.add(current.immutable());
            addNeighbours(searchQueue, current);
        }

        return found;
    }

    private static void addNeighbours(
            Deque<BlockPos> queue,
            BlockPos center
    ) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }

                    queue.addLast(
                            center.offset(x, y, z)
                    );
                }
            }
        }
    }

    /**
     * Treats iron_ore and deepslate_iron_ore as the same vein family.
     */
    private static boolean sameVeinFamily(
            BlockState first,
            BlockState second
    ) {
        return getVeinFamily(first.getBlock())
                .equals(
                        getVeinFamily(
                                second.getBlock()
                        )
                );
    }

    private static String getVeinFamily(Block block) {
        ResourceLocation id =
                BuiltInRegistries.BLOCK.getKey(block);

        String path = id.getPath();

        if (path.startsWith("deepslate_")) {
            path = path.substring(
                    "deepslate_".length()
            );
        }

        return id.getNamespace() + ":" + path;
    }

    // ========================================================================
    // Formulas
    // ========================================================================

    public static double getSoulToolPower(
            SoulToolData data
    ) {
        return data.lastSoulMajor
                + data.lastSoulMinor * 0.10D;
    }

    public static int getFortuneLevel(
            int soulMajor
    ) {
        return Mth.clamp(
                soulMajor - 2,
                1,
                4
        );
    }

    public static double getRealmSpeedMultiplier(
            double power
    ) {
        double awakenedProgress =
                Math.max(0.0D, power - 3.0D);

        return 1.0D
                + awakenedProgress * 0.04D;
    }

    public static double getRedstoneSpeedMultiplier(double power) {
        return 1.0D + power * 0.05D;
    }

    public static double getPhantomExcavationResistance(double power) {
        return Math.min(0.75D, power * 0.12D);
    }

    public static double getProsperityChance(double power) {
        return Math.min(0.15D, power * 0.02D);
    }

    public static double getExperienceMultiplier(double power) {
        return 1.0D + power * 0.10D;
    }

    public static double getSeedPreservationChance(double power) {
        return Math.min(0.40D, power * 0.05D);
    }

    public static int getMaximumVeinBlocks(SoulToolData data) {
        return 2 + Mth.floor(getSoulToolPower(data) * 2.0D);
    }

    public static double getVeinQiCost(SoulToolCore core) {
        return BASE_VEIN_QI_COST * core.qiCostMultiplier();
    }

    public static double getEmeraldLuckBonus(SoulToolData data) {
        double awakenedProgress = Math.max(0.0D, getSoulToolPower(data) - 3.0D);
        return BASE_EMERALD_LUCK + awakenedProgress * EMERALD_LUCK_PER_POWER;
    }
}