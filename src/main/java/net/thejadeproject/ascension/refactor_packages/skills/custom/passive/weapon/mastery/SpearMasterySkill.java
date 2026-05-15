package net.thejadeproject.ascension.refactor_packages.skills.custom.passive.weapon.mastery;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.skills.vfx.weaponvfx.VfxColorRegistry;
import net.thejadeproject.ascension.refactor_packages.skills.vfx.weaponvfx.WeaponSwingVfxEntity;
import net.thejadeproject.ascension.refactor_packages.techniques.ModTechniques;
import net.thejadeproject.ascension.util.ModTags;
import org.joml.Vector3f;

/**
 * Spear mastery passive — ray-cast hit detection (narrow + long).
 *
 * Texture folders expected under textures/entity/vfx/spear_thrust/<color>/:
 *   simple_blue, simple_red, simple_green, simple_purple, simple_softred
 *   (matches the screenshot: spear_particle sub-folders)
 *
 * Technique → color mappings:
 *   SWORD_COMPREHENSION          → simple_blue  (default)
 *   FIRE elemental body/essence  → simple_red
 *   WATER / WOOD / EARTH         → simple_green
 *   SOUL (Pale Moon, etc.)       → simple_purple
 *   BLOODFEAST / HELLBOUND       → simple_softred
 *   LIGHTNING / WIND / METAL     → simple_blue
 *   POISON (Myriad Venom)        → simple_green
 */
public class SpearMasterySkill extends GenericWeaponMasterySkill {

    static {
        final String T = WeaponSwingVfxEntity.TYPE_SPEAR;

        // Weapon techniques
        VfxColorRegistry.register(T, ModTechniques.SWORD_COMPREHENSION_TECHNIQUE.getId(), "simple_blue");

        // Body techniques
        VfxColorRegistry.register(T, ModTechniques.WHITE_LIGHTNING_TEN_STAGE_TECHNIQUE.getId(), "simple_blue");
        VfxColorRegistry.register(T, ModTechniques.HELLBOUND_MARROW_SCRIPTURE.getId(), "simple_softred");
        VfxColorRegistry.register(T, ModTechniques.HEART_FIRE_TECHNIQUE.getId(), "simple_red");
        VfxColorRegistry.register(T, ModTechniques.KIDNEY_WATER_TECHNIQUE.getId(), "simple_blue");
        VfxColorRegistry.register(T, ModTechniques.LIVER_WOOD_TECHNIQUE.getId(), "simple_green");
        VfxColorRegistry.register(T, ModTechniques.SPLEEN_EARTH_TECHNIQUE.getId(), "simple_green");
        VfxColorRegistry.register(T, ModTechniques.LUNG_METAL_TECHNIQUE.getId(), "simple_blue");

        // Essence/elemental techniques
        VfxColorRegistry.register(T, ModTechniques.FIRE_ESSENCE_TECHNIQUE.getId(), "simple_red");
        VfxColorRegistry.register(T, ModTechniques.WATER_ESSENCE_TECHNIQUE.getId(), "simple_blue");
        VfxColorRegistry.register(T, ModTechniques.WOOD_ESSENCE_TECHNIQUE.getId(), "simple_green");
        VfxColorRegistry.register(T, ModTechniques.EARTH_ESSENCE_TECHNIQUE.getId(), "simple_green");
        VfxColorRegistry.register(T, ModTechniques.METAL_ESSENCE_TECHNIQUE.getId(), "simple_blue");
        VfxColorRegistry.register(T, ModTechniques.LIGHTNING_ESSENCE_TECHNIQUE.getId(), "simple_blue");
        VfxColorRegistry.register(T, ModTechniques.WIND_ESSENCE_TECHNIQUE.getId(), "simple_blue");

        // Soul techniques
        VfxColorRegistry.register(T, ModTechniques.BLOODFEAST_SOUL_REFINING_SCRIPTURE.getId(), "simple_softred");
        VfxColorRegistry.register(T, ModTechniques.PALE_MOON_SCRIPTURE.getId(), "simple_purple");
        VfxColorRegistry.register(T, ModTechniques.GIBBOUS_MOON_SCRIPTURE.getId(), "simple_purple");
        VfxColorRegistry.register(T, ModTechniques.DAWNING_SUN_SCRIPTURE.getId(), "simple_red");
        VfxColorRegistry.register(T, ModTechniques.ZENITH_SUN_SCRIPTURE.getId(), "simple_red");
        VfxColorRegistry.register(T, ModTechniques.SCHOLARLY_SOUL_TECHNIQUE.getId(), "simple_blue");

        // Poison
        VfxColorRegistry.register(T, ModTechniques.MYRIAD_VENOM_REFINEMENT_SCRIPTURE.getId(), "simple_green");

        // ── Add future spear technique mappings below ──
    }

    @Override protected ResourceLocation getPathId() { return ModPaths.SPEAR.getId(); }
    @Override protected TagKey<Item> getWeaponTag() { return ModTags.Items.SPEAR; }
    @Override protected String getVfxType() { return WeaponSwingVfxEntity.TYPE_SPEAR; }
    @Override protected String getFallbackColor() { return "simple_blue"; }

    @Override protected Vector3f getEffectRadius() { return new Vector3f(0.8f, 0.8f, 4.5f); }
    @Override protected double getBaseDamage() { return 6.0; }
    @Override protected int getSwingIntervalTicks() { return 18; }
    @Override protected int getEffectDuration() { return 6; }
    @Override protected double getQiCostPerSwing(double m) { return 1.5 * m; }

    @Override protected String getTitleKey() { return "ascension.skill.spear_mastery_skill";             }
    @Override protected String getDescriptionKey() { return "ascension.skill.spear_mastery.description_skill"; }
}