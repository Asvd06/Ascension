package net.thejadeproject.ascension.refactor_packages.events.physiques;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.events.PhysiqueChangeEvent;
import net.thejadeproject.ascension.refactor_packages.physiques.ModPhysiques;
import net.thejadeproject.ascension.refactor_packages.physiques.custom.EvolvingPhysique;
import net.thejadeproject.ascension.refactor_packages.physiques.custom.helpers.PhysiqueEvolutionHelper;

@EventBusSubscriber(modid = AscensionCraft.MOD_ID)
public final class MarrowCleanseTreatmentEvents {

    private static final String TAG_ROOT = "ascension_marrow_cleanse_treatment";
    private static final String TAG_ACTIVE = "active";
    private static final String TAG_TARGET = "target";
    private static final String TAG_PROGRESS = "progress";
    private static final String TAG_FUEL = "fuel";
    private static final String TAG_LAST_MESSAGE_TICK = "last_message_tick";

    private static final long TICKS_PER_SECOND = 20L;
    private static final long TICKS_PER_MINUTE = 1200L;

    private static final long STRENGTHENED_VESSELS_REQUIRED_TIME = 20L * TICKS_PER_MINUTE;
    private static final long POISON_SLIGHTED_MERIDIANS_REQUIRED_TIME = 30L * TICKS_PER_MINUTE;

    private static final long BASE_FUEL_PER_PILL = 4L * TICKS_PER_MINUTE;
    private static final long MAX_FUEL = 8L * TICKS_PER_MINUTE;

    private static final int MISSED_DOSE_WEAKNESS_DURATION = 12 * 20;
    private static final int MISSED_DOSE_WEAKNESS_AMPLIFIER = 0;
    private static final long NO_FUEL_MESSAGE_COOLDOWN = 10L * TICKS_PER_SECOND;

    private MarrowCleanseTreatmentEvents() {}

    public static boolean addTreatmentFuel(
            ServerPlayer player,
            IEntityData entityData,
            ResourceLocation targetPhysique,
            double purityScale,
            double realmMultiplier
    ) {
        if (player == null) return false;
        if (entityData == null) return false;
        if (targetPhysique == null) return false;
        if (!isSupportedTreatmentTarget(targetPhysique)) return false;

        if (!(entityData.getPhysique() instanceof EvolvingPhysique evolvingPhysique)) {
            return false;
        }

        if (!evolvingPhysique.canEvolveInto(targetPhysique)) {
            return false;
        }

        CompoundTag tag = getTag(player);
        String targetString = targetPhysique.toString();

        if (!tag.getBoolean(TAG_ACTIVE) || !targetString.equals(tag.getString(TAG_TARGET))) {
            tag.putBoolean(TAG_ACTIVE, true);
            tag.putString(TAG_TARGET, targetString);
            tag.putLong(TAG_PROGRESS, 0L);
            tag.putLong(TAG_FUEL, 0L);
        }

        long fuelGain = getFuelGain(purityScale, realmMultiplier);
        long newFuel = Math.min(MAX_FUEL, tag.getLong(TAG_FUEL) + fuelGain);
        tag.putLong(TAG_FUEL, newFuel);

        long progress = tag.getLong(TAG_PROGRESS);
        long requiredTime = getRequiredTreatmentTime(targetPhysique);

        sendActionBar(player, Component.literal(
                "Marrow cleanse treatment: "
                        + formatMinutes(progress) + " / " + formatMinutes(requiredTime)
                        + " min, fuel " + formatMinutes(newFuel) + " min"
        ));

        return true;
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;
        if (player.tickCount % TICKS_PER_SECOND != 0) return;
        if (!player.hasData(ModAttachments.ENTITY_DATA)) return;

        CompoundTag tag = getExistingTag(player);
        if (tag == null || !tag.getBoolean(TAG_ACTIVE)) return;

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        ResourceLocation targetPhysique = getTargetPhysique(tag);

        if (targetPhysique == null || !isSupportedTreatmentTarget(targetPhysique)) {
            clearTag(player);
            return;
        }

        if (!(entityData.getPhysique() instanceof EvolvingPhysique evolvingPhysique)
                || !evolvingPhysique.canEvolveInto(targetPhysique)) {
            clearTag(player);
            return;
        }

        long fuel = tag.getLong(TAG_FUEL);
        if (fuel <= 0L) {
            punishMissedDose(player, tag);
            return;
        }

        tag.putLong(TAG_FUEL, Math.max(0L, fuel - TICKS_PER_SECOND));

        long requiredTime = getRequiredTreatmentTime(targetPhysique);
        long progress = tag.getLong(TAG_PROGRESS) + TICKS_PER_SECOND;
        tag.putLong(TAG_PROGRESS, progress);

        if (progress % TICKS_PER_MINUTE == 0L && shouldSendProgress(progress, requiredTime)) {
            sendActionBar(player, Component.literal(
                    "Marrow cleansing: "
                            + formatMinutes(progress) + " / " + formatMinutes(requiredTime)
                            + " min"
            ));
        }

        if (progress < requiredTime) return;

        boolean evolved = PhysiqueEvolutionHelper.tryEvolveInto(player, entityData, targetPhysique);
        if (evolved) {
            clearTag(player);
        }
    }

    @SubscribeEvent
    public static void onPhysiqueChange(PhysiqueChangeEvent.Post event) {
        if (!(event.entityData.getAttachedEntity() instanceof ServerPlayer player)) return;

        if (!ModPhysiques.DECAYING_MERIDIANS.getId().equals(event.newPhysique)
                && !ModPhysiques.TWISTED_VESSELS.getId().equals(event.newPhysique)) {
            clearTag(player);
        }
    }

    private static void punishMissedDose(ServerPlayer player, CompoundTag tag) {
        player.addEffect(new MobEffectInstance(
                MobEffects.WEAKNESS,
                MISSED_DOSE_WEAKNESS_DURATION,
                MISSED_DOSE_WEAKNESS_AMPLIFIER,
                true,
                false,
                true
        ));

        long lastMessageTick = tag.getLong(TAG_LAST_MESSAGE_TICK);
        if (player.tickCount - lastMessageTick < NO_FUEL_MESSAGE_COOLDOWN) return;

        tag.putLong(TAG_LAST_MESSAGE_TICK, player.tickCount);
        sendActionBar(player, Component.literal("Your marrow cleanse treatment needs another pill."));
    }

    private static boolean isSupportedTreatmentTarget(ResourceLocation targetPhysique) {
        return ModPhysiques.POISON_SLIGHTED_MERIDIANS.getId().equals(targetPhysique)
                || ModPhysiques.STRENGTHENED_VESSELS.getId().equals(targetPhysique);
    }

    private static long getRequiredTreatmentTime(ResourceLocation targetPhysique) {
        if (ModPhysiques.POISON_SLIGHTED_MERIDIANS.getId().equals(targetPhysique)) {
            return POISON_SLIGHTED_MERIDIANS_REQUIRED_TIME;
        }

        if (ModPhysiques.STRENGTHENED_VESSELS.getId().equals(targetPhysique)) {
            return STRENGTHENED_VESSELS_REQUIRED_TIME;
        }

        return 0L;
    }

    private static long getFuelGain(double purityScale, double realmMultiplier) {
        double scale = clamp(purityScale * realmMultiplier, 0.25D, 3.0D);
        return Math.max(TICKS_PER_SECOND, (long) (BASE_FUEL_PER_PILL * scale));
    }

    private static ResourceLocation getTargetPhysique(CompoundTag tag) {
        if (!tag.contains(TAG_TARGET, Tag.TAG_STRING)) return null;
        return ResourceLocation.parse(tag.getString(TAG_TARGET));
    }

    private static CompoundTag getTag(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();

        if (!data.contains(TAG_ROOT, Tag.TAG_COMPOUND)) {
            data.put(TAG_ROOT, new CompoundTag());
        }

        return data.getCompound(TAG_ROOT);
    }

    private static CompoundTag getExistingTag(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();

        if (!data.contains(TAG_ROOT, Tag.TAG_COMPOUND)) {
            return null;
        }

        return data.getCompound(TAG_ROOT);
    }

    private static void clearTag(ServerPlayer player) {
        player.getPersistentData().remove(TAG_ROOT);
    }

    private static void sendActionBar(ServerPlayer player, Component message) {
        player.displayClientMessage(message, true);
    }

    private static boolean shouldSendProgress(long progress, long requiredTime) {
        long minute = progress / TICKS_PER_MINUTE;
        long requiredMinutes = requiredTime / TICKS_PER_MINUTE;

        return minute == 1L
                || progress >= requiredTime
                || minute % 5L == 0L
                || requiredMinutes - minute <= 5L;
    }

    private static long formatMinutes(long ticks) {
        return Math.max(0L, ticks / TICKS_PER_MINUTE);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
