package net.thejadeproject.ascension.common.items.tools.soul_tool;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.thejadeproject.ascension.common.items.ModItems;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.common.items.tools.data.soul_tool.SoulToolComponent;
import net.thejadeproject.ascension.data_attachments.attachments.SoulToolData;

import java.util.List;

public final class SoulToolHelper {

    private static final Tool NEUTRAL_TOOL =
            new Tool(List.of(), 1.0F, 0);

    private SoulToolHelper() {
    }

    public static boolean isSoulTool(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() instanceof ISoulboundTool;
    }

    public static SoulToolComponent getComponent(ItemStack stack) {
        if (!isSoulTool(stack)) return null;
        return stack.get(ModDataComponents.SOUL_TOOL.get());
    }

    public static SoulToolType getType(ItemStack stack) {
        if (!(stack.getItem() instanceof ISoulboundTool soulboundTool)) {
            return null;
        }

        return soulboundTool.getSoulToolType();
    }

    public static boolean isOwner(
            ItemStack stack,
            ServerPlayer player
    ) {
        SoulToolComponent component = getComponent(stack);

        return component != null
                && component.owner().equals(player.getUUID());
    }


    public static boolean isCurrentManifestation(
            ItemStack stack,
            ServerPlayer player,
            SoulToolData data
    ) {
        if (!data.bound || !data.summoned || !data.hasImplementId()) {
            return false;
        }

        SoulToolComponent component = getComponent(stack);
        SoulToolType actualType = getType(stack);

        if (component == null || actualType == null) {
            return false;
        }

        return component.owner().equals(player.getUUID())
                && component.implementId().equals(data.implementId)
                && component.type() == actualType;
    }

    public static ItemStack createManifestation(
            ServerPlayer player,
            SoulToolData data
    ) {
        return createManifestation(
                player,
                data,
                data.activeType
        );
    }

    public static ItemStack createManifestation(
            ServerPlayer player,
            SoulToolData data,
            SoulToolType type
    ) {
        ItemStack stack = new ItemStack(getItem(type));

        writeSoulToolComponent(stack, player, data, type);

        return stack;
    }

    public static Item getItem(SoulToolType type) {
        return switch (type) {
            case PICKAXE -> ModItems.SOULBOUND_PICKAXE.get();
            case SHOVEL -> ModItems.SOULBOUND_SHOVEL.get();
            case HOE -> ModItems.SOULBOUND_HOE.get();
            case SHEARS -> ModItems.SOULBOUND_SHEARS.get();
        };
    }

    public static void refreshStack(
            ItemStack stack,
            ServerPlayer player,
            SoulToolData data
    ) {
        SoulToolType type = getType(stack);

        if (type == null) return;

        writeSoulToolComponent(stack, player, data, type);
    }

    public static void writeSoulToolComponent(
            ItemStack stack,
            ServerPlayer player,
            SoulToolData data,
            SoulToolType type
    ) {
        stack.set(
                ModDataComponents.SOUL_TOOL.get(),
                new SoulToolComponent(
                        player.getUUID(),
                        data.implementId,
                        type,
                        data.core,
                        data.harvest,
                        data.flow,
                        data.spirit,
                        data.lastSoulMajor,
                        data.lastSoulMinor
                )
        );

        applyToolComponent(
                stack,
                type,
                data.core
        );

        SoulToolEffectHelper.applyStackEffects(
                stack,
                player,
                data
        );
    }

    private static void applyToolComponent(ItemStack stack, SoulToolType type, SoulToolCore core) {
        if (!core.isFormed()) {
            stack.set(DataComponents.TOOL, NEUTRAL_TOOL);
            return;
        }

        if (type == SoulToolType.SHEARS) {
            Tool vanillaShearsTool = Items.SHEARS.getDefaultInstance().get(DataComponents.TOOL);
            stack.set(DataComponents.TOOL, vanillaShearsTool == null ? NEUTRAL_TOOL : vanillaShearsTool);

            return;
        }

        Tier tier = core.tier();

        TagKey<Block> mineableTag = switch (type) {
            case PICKAXE -> BlockTags.MINEABLE_WITH_PICKAXE;
            case SHOVEL -> BlockTags.MINEABLE_WITH_SHOVEL;
            case HOE -> BlockTags.MINEABLE_WITH_HOE;
            case SHEARS -> throw new IllegalStateException(
                    "Shears do not use a normal tool tier"
            );
        };

        stack.set(DataComponents.TOOL, tier.createToolProperties(mineableTag));
    }

    public static boolean summon(ServerPlayer player, SoulToolData data) {
        if (!data.bound || !data.hasImplementId()) {
            data.summoned = false;
            return false;
        }

        removeAllSoulTools(player);

        ItemStack manifestation = createManifestation(player, data);

        boolean placed;

        if (player.getMainHandItem().isEmpty()) {
            player.setItemInHand(
                    InteractionHand.MAIN_HAND,
                    manifestation
            );

            placed = true;
        } else {
            placed = player.getInventory().add(manifestation);
        }

        if (!placed) {
            data.summoned = false;
            return false;
        }

        data.summoned = true;
        player.getInventory().setChanged();

        return true;
    }

    public static void unsummon(ServerPlayer player, SoulToolData data) {
        removeAllSoulTools(player);
        data.summoned = false;
    }

    public static boolean cycleHeldForm(ServerPlayer player, SoulToolData data) {
        ItemStack held = player.getMainHandItem();

        if (!isCurrentManifestation(held, player, data)) {
            return false;
        }

        SoulToolType nextType = data.activeType.next();
        data.activeType = nextType;

        ItemStack replacement = createManifestation(player, data, nextType);

        player.setItemInHand(
                InteractionHand.MAIN_HAND,
                replacement
        );

        player.displayClientMessage(
                Component.translatable(
                        "ascension.skill.soul_implement.form_changed",
                        Component.translatable(nextType.translationKey())
                ),
                true
        );

        return true;
    }


    public static boolean tryAssimilate(
            ServerPlayer player,
            SoulToolData data,
            ItemStack toolStack,
            ItemStack ingredient
    ) {
        if (!isCurrentManifestation(
                toolStack,
                player,
                data
        )) {
            player.displayClientMessage(
                    Component.translatable("ascension.skill.soul_implement.invalid_manifestation"),
                    true
            );

            return false;
        }

        if (ingredient.isEmpty()) {
            return false;
        }

        SoulToolCore coreCandidate = SoulToolCore.fromIngredient(ingredient);

        if (coreCandidate != SoulToolCore.NONE) {
            return assimilateCore(
                    player,
                    data,
                    toolStack,
                    ingredient,
                    coreCandidate
            );
        }

        if (!data.core.isFormed()) {
            player.displayClientMessage(
                    Component.translatable(
                            "ascension.skill.soul_implement.needs_core"
                    ),
                    true
            );

            return false;
        }

        SoulToolHarvest harvestCandidate = SoulToolHarvest.fromIngredient(ingredient);

        if (harvestCandidate != SoulToolHarvest.NONE) {
            if (data.harvest != SoulToolHarvest.NONE) {
                displayLockedCategory(
                        player,
                        AssimilationCategory.HARVEST,
                        data.harvest.translationKey()
                );

                return false;
            }

            data.harvest = harvestCandidate;

            completeAssimilation(
                    player,
                    data,
                    toolStack,
                    ingredient,
                    AssimilationCategory.HARVEST,
                    harvestCandidate.translationKey()
            );

            return true;
        }

        SoulToolFlow flowCandidate = SoulToolFlow.fromIngredient(ingredient);

        if (flowCandidate != SoulToolFlow.NONE) {
            if (data.flow != SoulToolFlow.NONE) {
                displayLockedCategory(
                        player,
                        AssimilationCategory.FLOW,
                        data.flow.translationKey()
                );

                return false;
            }

            data.flow = flowCandidate;

            completeAssimilation(
                    player,
                    data,
                    toolStack,
                    ingredient,
                    AssimilationCategory.FLOW,
                    flowCandidate.translationKey()
            );

            return true;
        }

        SoulToolSpirit spiritCandidate = SoulToolSpirit.fromIngredient(ingredient);

        if (spiritCandidate != SoulToolSpirit.NONE) {
            if (data.spirit != SoulToolSpirit.NONE) {
                displayLockedCategory(player, AssimilationCategory.SPIRIT, data.spirit.translationKey());
                return false;
            }

            data.spirit = spiritCandidate;

            completeAssimilation(
                    player,
                    data,
                    toolStack,
                    ingredient,
                    AssimilationCategory.SPIRIT,
                    spiritCandidate.translationKey()
            );

            return true;
        }

        player.displayClientMessage(
                Component.translatable("ascension.skill.soul_implement.unknown_material"),
                true
        );

        return false;
    }

    private static boolean assimilateCore(
            ServerPlayer player,
            SoulToolData data,
            ItemStack toolStack,
            ItemStack ingredient,
            SoulToolCore candidate
    ) {
        if (!candidate.canReplace(data.core)) {
            player.displayClientMessage(
                    Component.translatable(
                            "ascension.skill.soul_implement.core_not_upgrade",
                            Component.translatable(data.core.translationKey()),
                            Component.translatable(candidate.translationKey())
                    ),
                    true
            );

            return false;
        }

        data.core = candidate;

        completeAssimilation(
                player,
                data,
                toolStack,
                ingredient,
                AssimilationCategory.CORE,
                candidate.translationKey()
        );

        return true;
    }

    private static void completeAssimilation(
            ServerPlayer player,
            SoulToolData data,
            ItemStack toolStack,
            ItemStack ingredient,
            AssimilationCategory category,
            String valueTranslationKey
    ) {
        if (!player.getAbilities().instabuild) {
            ingredient.shrink(1);
        }

        refreshStack(toolStack, player, data);
        player.getInventory().setChanged();

        player.displayClientMessage(
                Component.translatable(
                        "ascension.skill.soul_implement.assimilated",
                        Component.translatable(categoryTranslationKey(category)),
                        Component.translatable(valueTranslationKey)
                ),
                true
        );
    }

    private static void displayLockedCategory(
            ServerPlayer player,
            AssimilationCategory category,
            String currentValueTranslationKey
    ) {
        player.displayClientMessage(
                Component.translatable(
                        "ascension.skill.soul_implement.category_locked",
                        Component.translatable(categoryTranslationKey(category)),
                        Component.translatable(currentValueTranslationKey)
                ),
                true
        );
    }

    private static String categoryTranslationKey(AssimilationCategory category) {
        return "ascension.soul_tool.category." + category.id();
    }

    public static void syncSoulRealmProgress(
            SoulToolData data,
            int soulMajor,
            int soulMinor
    ) {
        data.lastSoulMajor = Math.max(0, soulMajor);
        data.lastSoulMinor = Math.max(0, soulMinor);
    }

    public static double getSoulToolPower(
            SoulToolData data
    ) {
        return data.lastSoulMajor
                + data.lastSoulMinor * 0.10D;
    }


    public static void reconcile(
            ServerPlayer player,
            SoulToolData data
    ) {
        if (!data.bound || !data.hasImplementId()) {
            if (data.bound && !data.hasImplementId()) {
                data.clear();
            }

            removeAllSoulTools(player);
            return;
        }

        int survivingSlot = -1;

        for (int i = 0;
             i < player.getInventory().getContainerSize();
             i++) {

            ItemStack stack =
                    player.getInventory().getItem(i);

            if (!isSoulTool(stack)) continue;

            SoulToolComponent component =
                    getComponent(stack);

            SoulToolType actualType =
                    getType(stack);

            boolean validIdentity =
                    data.summoned
                            && component != null
                            && actualType != null
                            && component.owner().equals(
                            player.getUUID()
                    )
                            && component.implementId().equals(
                            data.implementId
                    )
                            && component.type() == actualType;

            if (!validIdentity || survivingSlot >= 0) {
                player.getInventory().setItem(
                        i,
                        ItemStack.EMPTY
                );

                continue;
            }

            survivingSlot = i;
        }

        if (!data.summoned) {
            player.getInventory().setChanged();
            return;
        }

        if (survivingSlot >= 0) {
            ItemStack survivingStack =
                    player.getInventory()
                            .getItem(survivingSlot);

            SoulToolType actualType =
                    getType(survivingStack);

            if (actualType != data.activeType) {
                player.getInventory().setItem(
                        survivingSlot,
                        createManifestation(player, data)
                );
            } else {
                refreshStack(
                        survivingStack,
                        player,
                        data
                );
            }

            player.getInventory().setChanged();
            return;
        }

        if (!summon(player, data)) {
            data.summoned = false;
        }
    }

    public static void removeAllSoulTools(
            ServerPlayer player
    ) {
        boolean removed = false;

        for (int i = 0;
             i < player.getInventory().getContainerSize();
             i++) {

            ItemStack stack =
                    player.getInventory().getItem(i);

            if (!isSoulTool(stack)) continue;

            player.getInventory().setItem(
                    i,
                    ItemStack.EMPTY
            );

            removed = true;
        }

        if (removed) {
            player.getInventory().setChanged();
        }
    }
}