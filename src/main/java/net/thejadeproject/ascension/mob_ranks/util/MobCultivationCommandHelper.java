package net.thejadeproject.ascension.mob_ranks.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.mob_ranks.MobRankApplier;
import net.thejadeproject.ascension.mob_ranks.MobRankData;
import net.thejadeproject.ascension.mob_ranks.MobRankList;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.mob_ranks.SyncMobRank;

public final class MobCultivationCommandHelper {
    private MobCultivationCommandHelper() {}

    public static boolean applyCultivation(LivingEntity entity, String realmId, int stage) {
        if (!MobRankList.isValidRealm(realmId)) {
            return false;
        }

        MobRankData data = entity.getData(ModAttachments.MOB_RANK);
        if (data == null) return false;

        data.setRealmId(realmId);
        data.setStage(Math.max(1, Math.min(3, stage)));
        data.setInitialized(true);

        MobRankApplier.applyFromData(entity, data);
        sync(entity);
        return true;
    }

    public static Component getStatsMessage(LivingEntity entity) {
        MobRankData data = entity.getData(ModAttachments.MOB_RANK);

        if (data == null || !data.isInitialized()) {
            return Component.literal(entity.getName().getString() + " has no initialized mob cultivation.");
        }

        return Component.literal(
                entity.getName().getString()
                        + " | Realm: " + data.getRealmId()
                        + " | Stage: " + data.getStage()
                        + " | HP: " + String.format("%.1f", entity.getMaxHealth())
        );
    }

    private static void sync(LivingEntity entity) {
        if (entity.level().isClientSide()) return;

        MobRankData data = entity.getData(ModAttachments.MOB_RANK);
        if (data == null) return;

        PacketDistributor.sendToPlayersTrackingEntity(
                entity,
                new SyncMobRank(
                        entity.getId(),
                        data.getRealmId(),
                        data.getStage(),
                        data.isInitialized()
                )
        );
    }
}