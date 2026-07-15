package net.thejadeproject.ascension.common.items.herbs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class HerbQuality {

    // ── Quality tier constants ────────────────────────────────────

    public static final int BASIC = 0;
    public static final int AVERAGE = 1;
    public static final int ADVANCED = 2;
    public static final int PEAK = 3;

    // ── Age thresholds ────────────────────────────────────────────

    public static final long AGE_YOUNG = 0L;
    public static final long AGE_MATURE = 100_000L;
    public static final long AGE_ELDER = 500_000L;
    public static final long AGE_ANCIENT = 1_000_000L;

    public enum AgeTier {
        YOUNG(AGE_YOUNG, "Young", "Y"),
        MATURE(AGE_MATURE, "Mature", "M"),
        ELDER(AGE_ELDER, "Elder", "E"),
        ANCIENT(AGE_ANCIENT, "Ancient", "A");

        private final long minimumTicks;
        private final String displayName;
        private final String shortName;

        AgeTier(long minimumTicks, String displayName, String shortName) {
            this.minimumTicks = minimumTicks;
            this.displayName = displayName;
            this.shortName = shortName;
        }

        public long minimumTicks() {
            return minimumTicks;
        }

        public String displayName() {
            return displayName;
        }

        public String shortName() {
            return shortName;
        }

        public static AgeTier fromTicks(long ageTicks) {
            if (ageTicks >= AGE_ANCIENT) return ANCIENT;
            if (ageTicks >= AGE_ELDER) return ELDER;
            if (ageTicks >= AGE_MATURE) return MATURE;
            return YOUNG;
        }
    }

    // ── Quality names and colors ──────────────────────────────────

    public static String getQualityName(int quality) {
        return switch (quality) {
            case AVERAGE -> "Average";
            case ADVANCED -> "Advanced";
            case PEAK -> "Peak";
            default -> "Basic";
        };
    }

    public static ChatFormatting getQualityColor(int quality) {
        return switch (quality) {
            case AVERAGE -> ChatFormatting.GOLD;
            case ADVANCED -> ChatFormatting.GREEN;
            case PEAK -> ChatFormatting.AQUA;
            default -> ChatFormatting.DARK_RED;
        };
    }

    // ── Age helpers ───────────────────────────────────────────────

    public static long ticksToYears(long ageTicks) {
        return ageTicks / 1_000L;
    }

    public static ChatFormatting getAgeColor(long ageTicks) {
        if (ageTicks >= AGE_ANCIENT) return ChatFormatting.LIGHT_PURPLE;
        if (ageTicks >= AGE_ELDER) return ChatFormatting.AQUA;
        if (ageTicks >= AGE_MATURE) return ChatFormatting.YELLOW;
        return ChatFormatting.GRAY;
    }

    public static AgeTier getAgeTier(long ageTicks) {
        return AgeTier.fromTicks(ageTicks);
    }

    public static AgeTier getAgeTier(ItemStack stack) {
        Long ageComponent = stack.get(ModDataComponents.HERB_AGE_TIER.get());
        return AgeTier.fromTicks(ageComponent != null ? ageComponent : 0L);
    }

    public static long getCanonicalAgeTicks(long ageTicks) {
        return AgeTier.fromTicks(ageTicks).minimumTicks();
    }

    // ── Random quality roll ───────────────────────────────────────

    public static int rollQuality() {
        double roll = ThreadLocalRandom.current().nextDouble();

        if (roll < 0.05) return PEAK;
        if (roll < 0.20) return ADVANCED;
        if (roll < 0.50) return AVERAGE;

        return BASIC;
    }

    // ── Purity contribution ───────────────────────────────────────

    public static int getPurityBonus(ItemStack stack) {
        if (stack.isEmpty()) return 0;

        Integer qualityComponent =
                stack.get(ModDataComponents.HERB_QUALITY.get());

        Long ageComponent =
                stack.get(ModDataComponents.HERB_AGE_TIER.get());

        int quality =
                qualityComponent != null ? qualityComponent : BASIC;

        long age =
                ageComponent != null ? ageComponent : 0L;

        int baseBonus = switch (quality) {
            case PEAK -> 15;
            case ADVANCED -> 8;
            case AVERAGE -> 3;
            default -> 0;
        };

        float ageMultiplier;

        if (age >= AGE_ANCIENT) {
            ageMultiplier = 2.0F;
        } else if (age >= AGE_ELDER) {
            ageMultiplier = 1.6F;
        } else if (age >= AGE_MATURE) {
            ageMultiplier = 1.3F;
        } else {
            ageMultiplier = 1.0F;
        }

        return (int) (baseBonus * ageMultiplier);
    }

    public static double getRealmUpgradeChance(ItemStack stack) {
        if (stack.isEmpty()) return 0.0;

        Integer qualityComponent = stack.get(ModDataComponents.HERB_QUALITY.get());
        Long ageComponent = stack.get(ModDataComponents.HERB_AGE_TIER.get());
        int quality = qualityComponent != null ? qualityComponent : BASIC;
        long age = ageComponent != null ? ageComponent : 0L;

        if (quality < PEAK) return 0.0;

        if (age >= AGE_ANCIENT) return 0.20;
        if (age >= AGE_ELDER) return 0.12;
        if (age >= AGE_MATURE) return 0.05;

        return 0.0;
    }

    public static int getPillRealmBonus(ItemStack stack) {
        if (stack.isEmpty()) return 0;

        Long ageComponent = stack.get(ModDataComponents.HERB_AGE_TIER.get());
        long age = ageComponent != null ? ageComponent : 0L;

        if (age >= AGE_ANCIENT) return 3;
        if (age >= AGE_ELDER) return 2;
        if (age >= AGE_MATURE) return 1;

        return 0;
    }

    // ── Tooltips ──────────────────────────────────────────────────

    public static void appendHerbTooltip(ItemStack stack, List<Component> tooltip) {
        Integer qualityComponent = stack.get(ModDataComponents.HERB_QUALITY.get());
        Long ageComponent = stack.get(ModDataComponents.HERB_AGE_TIER.get());

        if (qualityComponent == null && ageComponent == null) {
            return;
        }

        if (qualityComponent != null) {
            tooltip.add(
                    Component.literal("Quality: ")
                            .withStyle(ChatFormatting.YELLOW)
                            .append(Component.literal(getQualityName(qualityComponent))
                                    .withStyle(getQualityColor(qualityComponent)))
            );
        }

        if (ageComponent != null) {
            long years = ticksToYears(ageComponent);
            AgeTier ageTier = AgeTier.fromTicks(ageComponent);

            tooltip.add(
                    Component.literal("Age: ")
                            .withStyle(ChatFormatting.YELLOW)
                            .append(Component.literal(ageTier.displayName() + " (" + years + " years)")
                                    .withStyle(getAgeColor(ageComponent)))
            );
        }
    }
}