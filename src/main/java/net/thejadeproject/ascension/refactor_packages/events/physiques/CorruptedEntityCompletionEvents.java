package net.thejadeproject.ascension.refactor_packages.events.physiques;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.common.items.ModItems;
import net.thejadeproject.ascension.common.items.physiques.evolution.DisembodiedNetherSoulItem;
import net.thejadeproject.ascension.common.items.physiques.evolution.FalseDeifiedOrbItem;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.physiques.ModPhysiques;
import net.thejadeproject.ascension.refactor_packages.util.PhysiqueEvolutionEventUtil;

@EventBusSubscriber(modid = AscensionCraft.MOD_ID)
public final class CorruptedEntityCompletionEvents {
    private CorruptedEntityCompletionEvents() {}

    private static final String TAG_ROOT = "ascension_corrupted_entity_completion";
    private static final String TAG_FALSE_ORB_NEAR_DEATH_PRIMED = "false_orb_near_death_primed";
    private static final String TAG_LAST_VITAL_ASSERTION_TICK = "last_vital_assertion_tick";

    private static final long TICKS_PER_SECOND = 20L;
    private static final long VITAL_ASSERTION_COOLDOWN = 333L * TICKS_PER_SECOND;

    private static final float NEAR_DEATH_HEALTH = 6.0F;

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer killer)) return;
        if (killer.level().isClientSide()) return;
        if (!killer.hasData(ModAttachments.ENTITY_DATA)) return;

        IEntityData entityData = killer.getData(ModAttachments.ENTITY_DATA);

        if (!PhysiqueEvolutionEventUtil.hasPhysique(entityData, ModPhysiques.CORRUPTED_ENTITY.getId())) {
            return;
        }

        ItemStack soul = findIncompleteDisembodiedNetherSoul(killer);
        if (soul.isEmpty()) return;

        LivingEntity victim = event.getEntity();

        int amount = getSoulGuiltValue(victim);
        if (amount <= 0) return;

        int before = DisembodiedNetherSoulItem.getSoulGuilt(soul);
        DisembodiedNetherSoulItem.addSoulGuilt(soul, amount);
        int after = DisembodiedNetherSoulItem.getSoulGuilt(soul);

        if (after != before) {
            killer.displayClientMessage(Component.translatable(
                    "ascension.message.physique_evolution.corrupted_entity.disembodied_nether_soul_progress",
                    after, DisembodiedNetherSoulItem.REQUIRED_SOUL_GUILT
            ).withStyle(after >= DisembodiedNetherSoulItem.REQUIRED_SOUL_GUILT
                    ? ChatFormatting.GREEN
                    : ChatFormatting.DARK_PURPLE), true);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;
        if (!player.hasData(ModAttachments.ENTITY_DATA)) return;

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        if (!PhysiqueEvolutionEventUtil.hasPhysique(entityData, ModPhysiques.CORRUPTED_ENTITY.getId())) {
            clearTag(player);
            return;
        }

        ItemStack orb = findIncompleteFalseDeifiedOrb(player);
        if (orb.isEmpty()) return;

        CompoundTag tag = getTag(player);

        if (player.getHealth() <= NEAR_DEATH_HEALTH) {
            if (!tag.getBoolean(TAG_FALSE_ORB_NEAR_DEATH_PRIMED)) {
                tag.putBoolean(TAG_FALSE_ORB_NEAR_DEATH_PRIMED, true);

                player.displayClientMessage(Component.translatable(
                        "ascension.message.physique_evolution.corrupted_entity.false_deified_orb_near_death"
                ).withStyle(ChatFormatting.DARK_PURPLE), true);
            }

            return;
        }

        if (!tag.getBoolean(TAG_FALSE_ORB_NEAR_DEATH_PRIMED)) {
            return;
        }

        if (!isEffectivelyFullHealth(player)) {
            return;
        }

        long gameTime = player.level().getGameTime();
        long lastAssertion = tag.getLong(TAG_LAST_VITAL_ASSERTION_TICK);

        if (lastAssertion > 0L && gameTime - lastAssertion < VITAL_ASSERTION_COOLDOWN) {
            long ticksLeft = VITAL_ASSERTION_COOLDOWN - (gameTime - lastAssertion);

            if (player.tickCount % 100 == 0) {
                player.displayClientMessage(Component.translatable(
                        "ascension.message.physique_evolution.corrupted_entity.false_deified_orb_stabilizing", ticksLeft / 20L
                ).withStyle(ChatFormatting.GRAY), true);
            }

            return;
        }

        FalseDeifiedOrbItem.addVitalAssertion(orb);

        int assertions = FalseDeifiedOrbItem.getVitalAssertions(orb);

        tag.putBoolean(TAG_FALSE_ORB_NEAR_DEATH_PRIMED, false);
        tag.putLong(TAG_LAST_VITAL_ASSERTION_TICK, gameTime);

        player.displayClientMessage(Component.translatable(
                "ascension.message.physique_evolution.corrupted_entity.false_deified_orb_progress",
                assertions, FalseDeifiedOrbItem.REQUIRED_VITAL_ASSERTIONS
        ).withStyle(assertions >= FalseDeifiedOrbItem.REQUIRED_VITAL_ASSERTIONS
                ? ChatFormatting.GREEN
                : ChatFormatting.GOLD), true);
    }

    private static boolean isEffectivelyFullHealth(ServerPlayer player) {
        return player.getHealth() >= player.getMaxHealth() - 0.01F;
    }

    private static int getSoulGuiltValue(LivingEntity victim) {
        if (victim instanceof AbstractVillager) {
            return 33;
        }

        if (victim instanceof Raider) {
            return 9;
        }

        return 0;
    }

    private static ItemStack findIncompleteDisembodiedNetherSoul(ServerPlayer player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModItems.DISEMBODIED_NETHER_SOUL.get())
                    && !DisembodiedNetherSoulItem.isComplete(stack)) {
                return stack;
            }
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (stack.is(ModItems.DISEMBODIED_NETHER_SOUL.get())
                    && !DisembodiedNetherSoulItem.isComplete(stack)) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    private static ItemStack findIncompleteFalseDeifiedOrb(ServerPlayer player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModItems.FALSE_DEIFIED_ORB.get())
                    && !FalseDeifiedOrbItem.isComplete(stack)) {
                return stack;
            }
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (stack.is(ModItems.FALSE_DEIFIED_ORB.get())
                    && !FalseDeifiedOrbItem.isComplete(stack)) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    private static CompoundTag getTag(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();

        if (!data.contains(TAG_ROOT, Tag.TAG_COMPOUND)) {
            data.put(TAG_ROOT, new CompoundTag());
        }

        return data.getCompound(TAG_ROOT);
    }

    private static void clearTag(ServerPlayer player) {
        player.getPersistentData().remove(TAG_ROOT);
    }
}