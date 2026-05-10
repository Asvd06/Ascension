package net.thejadeproject.ascension.mob_ranks;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.PathData;

public final class MobRankResolver {

    /*
    Fix Armor Stands having Cultivation.
    Mobs with above mortal cultivation hit you back if you hit them (override ai?).
    Add a mob cultivation command
    */

    private static final double DEFAULT_PLAYER_SEARCH_RANGE = 32.0;

    private MobRankResolver() {
    }

    public static MobRankDefinition resolveDefinition(MobRankData data) {
        return MobRankList.get(data.getRealmId(), data.getStage());
    }

    public static MobBodyStatBias resolveBodyBias(LivingEntity entity) {
        return MobBiasList.getFor(entity);
    }

    public static MobRankStatProfile resolveFinalStats(LivingEntity entity, MobRankDefinition definition) {
        return definition.baseStats().add(resolveBodyBias(entity).asProfile());
    }


    public static MobRankDefinition resolveFromNearbyPlayer(LivingEntity entity) {
        Player player = findNearestRelevantPlayer(entity, DEFAULT_PLAYER_SEARCH_RANGE);
        if (player == null) return null;

        return resolveFromPlayer(player);
    }

    public static MobRankDefinition resolveFromPlayer(Player player) {
        PathData strongest = getStrongestPath(player);
        if (strongest == null) {
            return MobRankList.getFirst();
        }

        String realmId = mapPathMajorRealmToMobRealm(strongest.getMajorRealm());
        int stage = mapPathMinorRealmToMobStage(strongest.getMinorRealm());

        return MobRankList.get(realmId, stage);
    }


    private static PathData getStrongestPath(Player player) {
        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        if (entityData == null) return null;

        PathData best = null;

        for (PathData candidate : entityData.getAllPathData()) {
            if (candidate == null) continue;

            if (best == null || isStronger(candidate, best)) {
                best = candidate;
            }
        }

        return best;
    }

    private static boolean isStronger(PathData candidate, PathData currentBest) {
        if (candidate.getMajorRealm() != currentBest.getMajorRealm()) {
            return candidate.getMajorRealm() > currentBest.getMajorRealm();
        }

        return candidate.getMinorRealm() > currentBest.getMinorRealm();
    }

    private static Player findNearestRelevantPlayer(LivingEntity entity, double range) {
        return entity.level().getNearestPlayer(
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                range,
                player -> player.isAlive() && !player.isSpectator()
        );
    }

    private static String mapPathMajorRealmToMobRealm(int majorRealm) {
        return switch (majorRealm) {
            case 0 -> "mortal";
            case 1 -> "qi_gathering";
            case 2 -> "formation_establishment";
            case 3 -> "golden_core";
            case 4 -> "nascent_soul";
            case 5 -> "soul_formation";
            case 6 -> "void_refinement";
            case 7 -> "body_integration";
            case 8 -> "tribulation_transcendence";
            case 9 -> "mahayana";
            case 10 -> "earth_immortal";
            default -> majorRealm < 0 ? "mortal" : "earth_immortal";
        };
    }

    private static int mapPathMinorRealmToMobStage(int minorRealm) {
        int clamped = Math.max(0, Math.min(9, minorRealm));

        if (clamped <= 2) return 1;
        if (clamped <= 5) return 2;
        return 3;
    }

    public static MobRankCategory resolveCategory(LivingEntity entity) {
        return MobCategoryResolver.resolve(entity);
    }

    // relative realm gap
    public static int resolveCombatPower(LivingEntity entity) {
        if (entity instanceof Player player) {
            return resolvePlayerCombatPower(player);
        }

        MobRankData data = entity.getData(ModAttachments.MOB_RANK);
        if (data == null || !data.isInitialized() || data.isUnranked()) {
            return 0;
        }

        return getRankPower(data.getRealmId(), data.getStage());
    }

    public static int resolvePlayerCombatPower(Player player) {
        PathData strongest = getStrongestPath(player);
        if (strongest == null) {
            return 0;
        }

        String realmId = mapPathMajorRealmToMobRealm(strongest.getMajorRealm());
        int stage = mapPathMinorRealmToMobStage(strongest.getMinorRealm());

        return getRankPower(realmId, stage);
    }

    public static int getRankPower(String realmId, int stage) {
        int realmIndex = MobRankList.getRealmIndex(realmId);
        if (realmIndex < 0) {
            return 0;
        }

        int clampedStage = Math.max(1, Math.min(3, stage));
        return realmIndex * 3 + (clampedStage - 1);
    }

}