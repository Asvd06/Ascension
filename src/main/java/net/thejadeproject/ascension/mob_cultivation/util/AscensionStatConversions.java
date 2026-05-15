package net.thejadeproject.ascension.mob_cultivation.util;

import net.thejadeproject.ascension.mob_cultivation.MobCultivationCategory;
import net.thejadeproject.ascension.mob_cultivation.MobCultivationStatProfile;

public final class AscensionStatConversions {

    private AscensionStatConversions() {
    }

    // All formulas are subject to change [crazy how that works]
    public static double maxHealthBonus(double vitality) {
        return vitality * 3.5;
    }

    public static double attackDamageBonus(double strength, MobCultivationCategory category) {
        return switch (category) {
            case PASSIVE -> strength * 0.35;
            case HOSTILE -> strength * 0.75;
            case BOSS -> strength * 0.90;
        };
    }

    public static double movementSpeedBonus(double strength, double agility, MobCultivationCategory category) {
        double base = (strength * 0.0001) + (agility * 0.001);

        return switch (category) {
            case PASSIVE -> base * 0.75;
            case HOSTILE -> base;
            case BOSS -> base * 0.65;
        };
    }

    public static double safeFallBonus(MobCultivationStatProfile stats) {
        return (stats.vitality() * 0.1 + stats.agility() * 0.5);
    }

    public static double hostileArmorBonus(MobCultivationStatProfile stats) {
        return 3.0 + stats.vitality() * 0.1 + stats.strength() * 0.05;
    }

    public static double hostileArmorToughnessBonus(MobCultivationStatProfile stats) {
        return 1.0 + stats.vitality() * 0.03 + stats.strength() * 0.01;
    }

    public static double hostileWaterMovementBonus(MobCultivationStatProfile stats) {
        return stats.agility() * 0.02;
    }

    public static double projectileDamageBonus(double strength) {
        return strength * 0.75;
    }

    public static double stepHeightBonus(MobCultivationStatProfile stats) {
        return Math.min(1.5, stats.agility() * 0.01 + stats.strength() * 0.002);
    }

}