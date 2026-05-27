package net.thejadeproject.ascension.mob_cultivation;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public final class MobCultivationRoller {

    private MobCultivationRoller() {
    }

    // -------------------------------------------------------------------------
    // Normal mob pool — mortal 1, 2, 3 only.
    // All naturally spawned mobs that don't win the elite roll land here.
    // -------------------------------------------------------------------------

    private static final List<WeightedMobCultivation> MORTAL_WEIGHTS = List.of(
            new WeightedMobCultivation(MobCultivationList.get("mortal", 1), 50),
            new WeightedMobCultivation(MobCultivationList.get("mortal", 2), 30),
            new WeightedMobCultivation(MobCultivationList.get("mortal", 3), 20)
    );

    // -------------------------------------------------------------------------
    // Elite mob pool — above-mortal ranks only.
    //
    // Higher realms are always reachable but exponentially rarer.
    // Add more realms here as the mod progresses; the system supports it
    // without any other changes.
    //
    // Current enabled realms (in order, with approximate relative odds):
    //   qi_gathering_1        ~33%
    //   qi_gathering_2        ~20%
    //   qi_gathering_3        ~14%
    //   formation_est_1       ~10%
    //   formation_est_2        ~7%
    //   formation_est_3        ~5%
    //   golden_core_1          ~4%
    //   golden_core_2          ~3%
    //   golden_core_3          ~2%
    //   nascent_soul_1         ~1.5%
    //   nascent_soul_2         ~1%
    //   nascent_soul_3        ~0.5%
    // -------------------------------------------------------------------------

    private static final List<WeightedMobCultivation> ELITE_WEIGHTS = List.of(
            // Qi Gathering — most common elite tier
            new WeightedMobCultivation(MobCultivationList.get("qi_gathering", 1), 330),
            new WeightedMobCultivation(MobCultivationList.get("qi_gathering", 2), 200),
            new WeightedMobCultivation(MobCultivationList.get("qi_gathering", 3), 140),

            // Formation Establishment
            new WeightedMobCultivation(MobCultivationList.get("formation_establishment", 1), 100),
            new WeightedMobCultivation(MobCultivationList.get("formation_establishment", 2),  70),
            new WeightedMobCultivation(MobCultivationList.get("formation_establishment", 3),  50),

            // Golden Core
            new WeightedMobCultivation(MobCultivationList.get("golden_core", 1),  40),
            new WeightedMobCultivation(MobCultivationList.get("golden_core", 2),  30),
            new WeightedMobCultivation(MobCultivationList.get("golden_core", 3),  20),

            // Nascent Soul — rare
            new WeightedMobCultivation(MobCultivationList.get("nascent_soul", 1), 15),
            new WeightedMobCultivation(MobCultivationList.get("nascent_soul", 2), 10),
            new WeightedMobCultivation(MobCultivationList.get("nascent_soul", 3),  5)

            // Future realms: soul_formation, void_refinement, body_integration,
            // tribulation_transcendence, mahayana, earth_immortal
            // Add them here with progressively lower weights when ready.
    );

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public static boolean canHaveRank(LivingEntity entity) {
        if (entity instanceof Player) return false;
        return true;
    }

    public static boolean canInitializeRank(LivingEntity entity) {
        if (entity.level().isClientSide()) return false;
        return canHaveRank(entity);
    }

    /**
     * Rolls a rank from the normal mortal pool (mortal 1–3).
     * Used as the default for all naturally spawned mobs that don't win
     * the elite roll.
     */
    public static MobCultivationDefinition rollMortalRank(LivingEntity entity) {
        return rollFromWeights(MORTAL_WEIGHTS, entity);
    }

    /**
     * Rolls a rank from the elite pool (qi_gathering through nascent_soul).
     * Higher realms are always reachable but exponentially rarer.
     * Used when a mob wins the elite spawn chance roll in
     * {@link net.thejadeproject.ascension.mob_cultivation.events.MobCultivationEvents}.
     */
    public static MobCultivationDefinition rollEliteRank(LivingEntity entity) {
        return rollFromWeights(ELITE_WEIGHTS, entity);
    }

    // -------------------------------------------------------------------------
    // Internal
    // -------------------------------------------------------------------------

    private static MobCultivationDefinition rollFromWeights(
            List<WeightedMobCultivation> weights,
            LivingEntity entity) {

        int totalWeight = 0;
        for (WeightedMobCultivation w : weights) {
            totalWeight += w.weight();
        }

        if (totalWeight <= 0) {
            return MobCultivationList.getFirst();
        }

        int roll = entity.getRandom().nextInt(totalWeight);
        int running = 0;

        for (WeightedMobCultivation w : weights) {
            running += w.weight();
            if (roll < running) {
                return w.definition();
            }
        }

        return MobCultivationList.getFirst();
    }
}