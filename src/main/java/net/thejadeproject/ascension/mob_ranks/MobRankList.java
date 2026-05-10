package net.thejadeproject.ascension.mob_ranks;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public final class MobRankList {
    private MobRankList() {
    }

    private static final String[] REALMS = {
            "mortal",
            "qi_gathering",
            "formation_establishment",
            "golden_core",
            "nascent_soul",
            "soul_formation",
            "void_refinement",
            "body_integration",
            "tribulation_transcendence",
            "mahayana",
            "earth_immortal"
    };


    private static final int STAGES_PER_REALM = 3;



    private static MobRankDefinition rank(String realmId, int stage, MobRankStatProfile stats) {
        return new MobRankDefinition(realmId, stage, stats);
    }

    // Helpers for command
    public static List<String> getRealmIds() {
        return List.of(REALMS);
    }

    public static boolean isValidRealm(String realmId) {
        return getRealmIndex(realmId) >= 0;
    }

    public static int getStagesPerRealm() {
        return STAGES_PER_REALM;
    }

    // Like the cultivators i hope? adds all previous minor and major realms stats with the current realms gains.
    private static MobRankStatProfile buildStats(int majorRealmIndex, int stage) {
        MobRankStatProfile stats = new MobRankStatProfile(0, 0, 0);

        for (int major = 0; major <= majorRealmIndex; major++) {
            stats = stats.add(getMajorGain(major));

            int maxStageForThisRealm = (major == majorRealmIndex) ? stage : STAGES_PER_REALM;
            for (int s = 1; s <= maxStageForThisRealm; s++) {
                stats = stats.add(getMinorGain(major, s));
            }
        }

        return stats;
    }

    // How much is added each major realm increase
    private static MobRankStatProfile getMajorGain(int majorRealmIndex) {
        return switch (majorRealmIndex) {
            case 0 -> new MobRankStatProfile(0, 0, 0);
            case 1 -> new MobRankStatProfile(6, 4, 2);
            case 2 -> new MobRankStatProfile(10, 7, 3);
            case 3 -> new MobRankStatProfile(18, 13, 5);
            case 4 -> new MobRankStatProfile(35, 22, 8);
            case 5 -> new MobRankStatProfile(50, 30, 10);
            case 6 -> new MobRankStatProfile(65, 40, 13);
            case 7 -> new MobRankStatProfile(85, 52, 17);
            case 8 -> new MobRankStatProfile(110, 68, 22);
            case 9 -> new MobRankStatProfile(140, 86, 28);
            case 10 -> new MobRankStatProfile(180, 110, 35);
            default -> new MobRankStatProfile(0, 0, 0);
        };
    }

    // How much is added each minor realm per major realm
    private static MobRankStatProfile getMinorGain(int majorRealmIndex, int stage) {
        MobRankStatProfile base = switch (majorRealmIndex) {
            case 0 -> new MobRankStatProfile(3, 2, 1);
            case 1 -> new MobRankStatProfile(5, 3, 1.5);
            case 2 -> new MobRankStatProfile(7, 5, 2);
            case 3 -> new MobRankStatProfile(12, 9, 3);
            case 4 -> new MobRankStatProfile(20, 12, 5);
            case 5 -> new MobRankStatProfile(28, 16, 7);
            case 6 -> new MobRankStatProfile(36, 20, 9);
            case 7 -> new MobRankStatProfile(48, 27, 11);
            case 8 -> new MobRankStatProfile(62, 35, 14);
            case 9 -> new MobRankStatProfile(80, 45, 18);
            case 10 -> new MobRankStatProfile(105, 60, 24);
            default -> new MobRankStatProfile(0, 0, 0);
        };

        return switch (stage) {
            case 1 -> base;
            case 2 -> base.multiply(1.15);
            case 3 -> base.multiply(1.35);
            default -> base;
        };
    }


    private static List<MobRankDefinition> buildAll() {
        List<MobRankDefinition> ranks = new ArrayList<>();

        for (int major = 0; major < REALMS.length; major++) {
            for (int stage = 1; stage <= STAGES_PER_REALM; stage++) {
                ranks.add(rank(REALMS[major], stage, buildStats(major, stage)));
            }
        }

        return List.copyOf(ranks);
    }

    public static final List<MobRankDefinition> ALL = buildAll();

    private static final Map<String, MobRankDefinition> LOOKUP = buildLookup();

    private static Map<String, MobRankDefinition> buildLookup() {
        Map<String, MobRankDefinition> map = new HashMap<>();
        for (MobRankDefinition def : ALL) {
            map.put(key(def.realmId(), def.stage()), def);
        }
        return Map.copyOf(map);
    }

    private static String key(String realmId, int stage) {
        return realmId + ":" + stage;
    }

    public static MobRankDefinition get(String realmId, int stage) {
        return LOOKUP.getOrDefault(key(realmId, stage), getFirst());
    }

    public static MobRankDefinition getFirst() {
        return new MobRankDefinition("mortal", 1, buildStats(0, 1));
    }

    // Drop Rule Helpers
    public static int getRealmIndex(String realmId) {
        for (int i = 0; i < REALMS.length; i++) {
            if (REALMS[i].equals(realmId)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isAtLeast(String realmId, int stage, String minRealmId, int minStage) {
        int realm = getRealmIndex(realmId);
        int minRealm = getRealmIndex(minRealmId);

        if (realm < 0 || minRealm < 0) return false;
        if (realm > minRealm) return true;
        if (realm < minRealm) return false;
        return stage >= minStage;
    }

    public static int compare(String realmIdA, int stageA, String realmIdB, int stageB) {
        int realmA = getRealmIndex(realmIdA);
        int realmB = getRealmIndex(realmIdB);

        if (realmA < 0 || realmB < 0) {
            return Integer.MIN_VALUE;
        }

        if (realmA != realmB) {
            return Integer.compare(realmA, realmB);
        }

        return Integer.compare(stageA, stageB);
    }

    public static boolean isExact(String realmId, int stage, String targetRealmId, int targetStage) {
        return compare(realmId, stage, targetRealmId, targetStage) == 0;
    }

    public static boolean isBetweenInclusive(String realmId, int stage,
                                             String minRealmId, int minStage,
                                             String maxRealmId, int maxStage) {
        int lower = compare(realmId, stage, minRealmId, minStage);
        int upper = compare(realmId, stage, maxRealmId, maxStage);

        if (lower == Integer.MIN_VALUE || upper == Integer.MIN_VALUE) {
            return false;
        }

        return lower >= 0 && upper <= 0;
    }

}