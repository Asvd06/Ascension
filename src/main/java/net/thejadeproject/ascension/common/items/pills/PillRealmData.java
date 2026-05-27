package net.thejadeproject.ascension.common.items.pills;

import net.minecraft.ChatFormatting;

/**
 * Central data class for pill realm names, purity grades, and bonus display text.
 *
 * ── Purity Grade system ───────────────────────────────────────────────────
 * Pills store a grade integer (0-3), NOT a raw 1-100 number.
 * This means two pills with the same realm + grade are byte-identical and stack.
 *
 *   0  Basic     (dark red)
 *   1  Average   (gold)
 *   2  Advanced  (green)
 *   3  Peak      (aqua)
 *
 * Use purityToGrade(int rawPurity) at craft-completion time to convert the
 * internal 1-100 roll into the stored grade before writing to the ItemStack.
 *
 * ── Major Realm names (1-9) ───────────────────────────────────────────────
 */
public class PillRealmData {

    // ── Major realm names ─────────────────────────────────────────
    private static final String[] MAJOR_REALM_NAMES = {
            "Mortal",       // 1
            "Spirit",       // 2
            "Earth",        // 3
            "Sky",          // 4
            "Heaven",       // 5
            "Profound",     // 6
            "Divine",       // 7
            "Immortal",     // 8
            "Transcendent"  // 9
    };

    // ── Purity grade tier constants ───────────────────────────────
    public static final int GRADE_BASIC    = 0;
    public static final int GRADE_AVERAGE  = 1;
    public static final int GRADE_ADVANCED = 2;
    public static final int GRADE_PEAK     = 3;

    // ── Kept for recipe/internal logic only ───────────────────────
    // These string constants are no longer stored on items; the int
    // grade (0-3) is stored instead.
    public static final String GRADE_BASIC_NAME    = "Basic";
    public static final String GRADE_AVERAGE_NAME  = "Average";
    public static final String GRADE_ADVANCED_NAME = "Advanced";
    public static final String GRADE_PEAK_NAME     = "Peak";

    /**
     * Converts a raw internal purity roll (1-100) into the stored grade (0-3).
     * Call this once when a craft finishes; write the grade to PILL_PURITY,
     * never the raw number.
     *
     *   1  – 30  → GRADE_BASIC    (0)
     *   31 – 70  → GRADE_AVERAGE  (1)
     *   71 – 89  → GRADE_ADVANCED (2)
     *   90 – 100 → GRADE_PEAK     (3)
     */
    public static int purityToGrade(int rawPurity) {
        if (rawPurity >= 90) return GRADE_PEAK;
        if (rawPurity >= 71) return GRADE_ADVANCED;
        if (rawPurity >= 31) return GRADE_AVERAGE;
        return GRADE_BASIC;
    }

    /**
     * Returns the display name for a stored purity grade (0-3).
     */
    public static String getPurityGradeName(int grade) {
        return switch (grade) {
            case GRADE_PEAK     -> GRADE_PEAK_NAME;
            case GRADE_ADVANCED -> GRADE_ADVANCED_NAME;
            case GRADE_AVERAGE  -> GRADE_AVERAGE_NAME;
            default             -> GRADE_BASIC_NAME;
        };
    }

    /**
     * Returns the ChatFormatting colour for a stored purity grade (0-3).
     */
    public static ChatFormatting getPurityGradeColor(int grade) {
        return switch (grade) {
            case GRADE_PEAK     -> ChatFormatting.AQUA;
            case GRADE_ADVANCED -> ChatFormatting.GREEN;
            case GRADE_AVERAGE  -> ChatFormatting.GOLD;
            default             -> ChatFormatting.DARK_RED;
        };
    }

    public static String getMajorRealmName(int majorRealm) {
        if (majorRealm < 1 || majorRealm > 9) return "Unknown";
        return MAJOR_REALM_NAMES[majorRealm - 1];
    }

    public static String getFullRealmDisplay(int majorRealm, int grade) {
        return getMajorRealmName(majorRealm) + " — " + getPurityGradeName(grade);
    }

    //TODO setup a registry for pill effects...

    // ── Bonus effect display strings ──────────────────────────────
    public static String getBonusEffectDisplay(String id) {
        if (id == null || id.isEmpty()) return "";
        return switch (id) {
            case "ascension:fire_imperviousness"        -> "[Fire] Impervious to fire and lava";
            case "ascension:ice_imperviousness"         -> "[Ice] Impervious to freezing; walk on powder snow";
            case "ascension:temperature_imperviousness" -> "[Fire & Ice] Impervious to temperature; walk on water";
            case "ascension:qi_nourishing"              -> "Qi Nourishing: Increased Qi regeneration";
            case "ascension:body_strengthening"         -> "Body Strengthening: Enhanced physical resilience";
            case "ascension:spirit_clarity"             -> "Spirit Clarity: Improved skill cooldown recovery";
            default -> "Unknown Bonus: " + id;
        };
    }
}