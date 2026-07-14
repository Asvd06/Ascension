package net.thejadeproject.ascension.common.items.tools.soul_weapon;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.data_attachments.attachments.SoulWeaponData;

public final class SoulImmolationHelper {

    public static final int MINIMUM_SACRIFICE_SCORE = 10;

    private static final double BASE_BOOST = 0.10D;
    private static final double MAX_BOOST = 0.65D;
    private static final double GROWTH_DIVISOR = 126.0D;

    private SoulImmolationHelper() {}

    public static int calculateImmolationScore(SoulWeaponData data) {
        if (data == null) {
            return 0;
        }

        return Math.max(
                0,
                data.currentGrade + data.lifetimeMarks
        );
    }

    public static double calculateCultivationBoost(int immolationScore) {
        if (immolationScore < MINIMUM_SACRIFICE_SCORE) {
            return 0.0D;
        }

        double boost =
                BASE_BOOST
                        + (MAX_BOOST - BASE_BOOST)
                        * (
                        1.0D - Math.exp(
                                -(immolationScore - MINIMUM_SACRIFICE_SCORE)
                                        / GROWTH_DIVISOR
                        )
                );

        return Math.min(boost, MAX_BOOST);
    }

    public static double getCultivationMultiplier(Entity entity) {
        if (!(entity instanceof Player)) {
            return 1.0D;
        }

        int score = entity.getData(ModAttachments.SOUL_IMMOLATION)
                .getHighestSacrificedScore();

        return 1.0D + calculateCultivationBoost(score);
    }

    public static int getDisplayedBoostPercent(int immolationScore) {
        return (int) Math.round(
                calculateCultivationBoost(immolationScore) * 100.0D
        );
    }
}