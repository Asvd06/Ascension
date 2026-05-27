package net.thejadeproject.ascension.refactor_packages.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.common.items.pills.PillRealmData;
import net.thejadeproject.ascension.refactor_packages.alchemy.IPillEffect;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;

import java.util.ArrayList;
import java.util.List;

public class PillEffectUtil {

    // ── Realm multiplier ──────────────────────────────────────────
    /** Each major realm multiplies effects by this factor over the previous. */
    private static final double REALM_MULTIPLIER = 3.5;

    public static double getRealmMultiplier(int majorRealm) {
        // Realm 1 = 1.0×, Realm 2 = 3.5×, Realm 3 = 12.25×, etc.
        return Math.pow(REALM_MULTIPLIER, Math.max(0, majorRealm - 1));
    }

    public static List<IPillEffect> getPillEffects(ItemStack stack) {
        ArrayList<IPillEffect> pillEffects = new ArrayList<>();
        if (!stack.has(ModDataComponents.PILL_EFFECTS.get())) return List.of();
        List<String> raw = stack.get(ModDataComponents.PILL_EFFECTS);
        for (String rawString : raw) {
            pillEffects.add(AscensionRegistries.PillEffects.PILL_EFFECT_REGISTRY.get(ResourceLocation.parse(rawString)));
        }
        return pillEffects;
    }

    /**
     * Returns a 0.0–1.0 scale derived from the stored purity grade (0-3).
     *
     *   GRADE_BASIC    (0) → 0.25
     *   GRADE_AVERAGE  (1) → 0.50
     *   GRADE_ADVANCED (2) → 0.75
     *   GRADE_PEAK     (3) → 1.00
     *
     * Previously this divided a raw 1-100 number by 100. Now that PILL_PURITY
     * stores a grade (0-3), we map each grade to an equivalent fixed scale.
     */
    public static double getPurityScale(ItemStack stack) {
        Integer gradeComp = stack.get(ModDataComponents.PILL_PURITY.get());
        int grade = (gradeComp != null) ? gradeComp : PillRealmData.GRADE_BASIC;
        return gradeToScale(grade);
    }

    /**
     * Converts a purity grade (0-3) to a 0.0–1.0 scale for effect calculations.
     */
    public static double gradeToScale(int grade) {
        return switch (grade) {
            case PillRealmData.GRADE_PEAK     -> 1.00;
            case PillRealmData.GRADE_ADVANCED -> 0.75;
            case PillRealmData.GRADE_AVERAGE  -> 0.50;
            default                           -> 0.25; // GRADE_BASIC
        };
    }

    public static double getRealmMultiplier(ItemStack stack) {
        Integer majorComp = stack.get(ModDataComponents.PILL_MAJOR_REALM.get());
        int majorRealm = (majorComp != null) ? majorComp : 1;
        return getRealmMultiplier(majorRealm);
    }

    /**
     * Writes realm and purity grade to an ItemStack.
     *
     * @param stack       The pill ItemStack to modify.
     * @param majorRealm  Major realm (1-9).
     * @param grade       Purity grade (0-3). Use PillRealmData.purityToGrade() to
     *                    convert a raw 1-100 roll before calling this.
     * @param bonusEffect Optional bonus effect ID, or null/empty for none.
     */
    public static ItemStack applyPillData(ItemStack stack, int majorRealm,
                                          int grade, String bonusEffect) {
        stack.set(ModDataComponents.PILL_MAJOR_REALM.get(), majorRealm);
        stack.set(ModDataComponents.PILL_PURITY.get(), grade);
        if (bonusEffect != null && !bonusEffect.isEmpty()) {
            stack.set(ModDataComponents.PILL_BONUS_EFFECT.get(), bonusEffect);
        }
        return stack;
    }
}