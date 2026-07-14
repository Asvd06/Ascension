package net.thejadeproject.ascension.refactor_packages.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.common.items.pills.PillRealmData;
import net.thejadeproject.ascension.refactor_packages.alchemy.IPillEffect;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;

import java.util.ArrayList;
import java.util.List;

public class PillEffectUtil {

    private static final double REALM_MULTIPLIER = 3.5D;

    public static int getMajorRealm(ItemStack stack) {
        Integer storedRealm = stack.get(ModDataComponents.PILL_MAJOR_REALM.get());
        return Mth.clamp(storedRealm != null ? storedRealm : 1, 1, 9);
    }

    public static int getPurityGrade(ItemStack stack) {
        Integer storedPurity = stack.get(ModDataComponents.PILL_PURITY.get());
        if (storedPurity == null) {
            return PillRealmData.GRADE_BASIC;
        }
        if (storedPurity > PillRealmData.GRADE_PEAK) {
            return PillRealmData.purityToGrade(Mth.clamp(storedPurity, 1, 100));
        }
        return Mth.clamp(storedPurity, PillRealmData.GRADE_BASIC, PillRealmData.GRADE_PEAK);
    }

    public static double getRealmMultiplier(int majorRealm) {
        int clampedRealm = Mth.clamp(majorRealm, 1, 9);
        return Math.pow(REALM_MULTIPLIER, clampedRealm - 1);
    }

    public static double getRealmMultiplier(ItemStack stack) {
        return getRealmMultiplier(getMajorRealm(stack));
    }

    public static double getPurityScale(ItemStack stack) {
        return gradeToScale(getPurityGrade(stack));
    }

    public static double gradeToScale(int grade) {
        return switch (grade) {
            case PillRealmData.GRADE_PEAK     -> 1.80D;
            case PillRealmData.GRADE_ADVANCED -> 1.35D;
            case PillRealmData.GRADE_AVERAGE  -> 1.00D;
            default                           -> 0.75D;
        };
    }


    public static double getDurationRealmMultiplier(int majorRealm) {
        return Math.min(5.0D, 1.0D + Math.max(0, majorRealm - 1) * 0.75D);
    }

    public static double getDurationScale(ItemStack stack) {
        return getPurityScale(stack) * getDurationRealmMultiplier(getMajorRealm(stack));
    }

    public static List<IPillEffect> getPillEffects(ItemStack stack) {
        if (!stack.has(ModDataComponents.PILL_EFFECTS.get())) {
            return List.of();
        }

        List<IPillEffect> pillEffects = new ArrayList<>();
        List<String> rawEffects = stack.get(ModDataComponents.PILL_EFFECTS.get());

        if (rawEffects == null) {
            return List.of();
        }

        for (String rawString : rawEffects) {
            try {
                ResourceLocation effectId = ResourceLocation.parse(rawString);
                IPillEffect effect = AscensionRegistries.PillEffects.PILL_EFFECT_REGISTRY.get(effectId);

                if (effect != null) {
                    pillEffects.add(effect);
                }
            } catch (IllegalArgumentException ignored) {

            }
        }

        return pillEffects;
    }

    public static ItemStack applyPillData(ItemStack stack, int majorRealm, int grade, String bonusEffect) {
        stack.set(ModDataComponents.PILL_MAJOR_REALM.get(), Mth.clamp(majorRealm, 1, 9));
        stack.set(ModDataComponents.PILL_PURITY.get(), Mth.clamp(grade, PillRealmData.GRADE_BASIC, PillRealmData.GRADE_PEAK));

        if (bonusEffect != null && !bonusEffect.isEmpty()) {
            stack.set(ModDataComponents.PILL_BONUS_EFFECT.get(), bonusEffect);
        } else {
            stack.remove(ModDataComponents.PILL_BONUS_EFFECT.get());
        }

        return stack;
    }
}