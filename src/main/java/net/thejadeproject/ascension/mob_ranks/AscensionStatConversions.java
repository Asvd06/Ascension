package net.thejadeproject.ascension.mob_ranks;

public final class AscensionStatConversions {

    private AscensionStatConversions() {
    }

    // Subject to change [crazy how that works]
    public static double maxHealthBonus(double vitality) {
        return vitality * 3.5;
    }

    public static double attackDamageBonus(double strength) {
        return strength;
    }

    public static double movementSpeedBonus(double strength, double agility) {
        return (strength * 0.0001) + (agility * 0.001);
    }

    public static double safeFallBonus(MobRankStatProfile stats) {
        return (stats.vitality() * 0.1 + stats.agility() * 0.5);
    }

    public static double hostileArmorBonus(MobRankStatProfile stats) {
        return 3.0 + stats.vitality() * 0.1 + stats.strength() * 0.05;
    }

    public static double hostileArmorToughnessBonus(MobRankStatProfile stats) {
        return 1.0 + stats.vitality() * 0.03 + stats.strength() * 0.01;
    }

    public static double hostileWaterMovementBonus(MobRankStatProfile stats) {
        return stats.agility() * 0.02;
    }

}