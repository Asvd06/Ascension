package net.thejadeproject.ascension.refactor_packages.skills.custom.passive.weapon;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.skills.vfx.weaponvfx.VfxColorRegistry;
import net.thejadeproject.ascension.refactor_packages.skills.vfx.weaponvfx.WeaponSwingVfxEntity;
import net.thejadeproject.ascension.refactor_packages.techniques.ModTechniques;
import org.joml.Vector3f;

/**
 * Axe mastery passive.
 *
 * Technique → color mappings (textures/entity/vfx/axe_swing/<color>/):
 *
 *   Body techniques (physical focus):
 *     WHITE_LIGHTNING_TEN_STAGE    → blue
 *     HELLBOUND_MARROW_SCRIPTURE   → blackred
 *     HEART_FIRE_TECHNIQUE         → red
 *     SPLEEN_EARTH_TECHNIQUE       → green
 *     LUNG_METAL_TECHNIQUE         → king   (gold-ish)
 *
 *   Essence/elemental:
 *     FIRE_ESSENCE_TECHNIQUE       → red
 *     WOOD_ESSENCE_TECHNIQUE       → green
 *     WIND_ESSENCE_TECHNIQUE       → blue
 *     LIGHTNING_ESSENCE_TECHNIQUE  → blue
 *     EARTH_ESSENCE_TECHNIQUE      → green
 *     METAL_ESSENCE_TECHNIQUE      → king
 *
 * Unmapped techniques → "blue" (fallback).
 *
 * Color folder names must match what exists under textures/entity/vfx/axe_swing/.
 * From the screenshot: simple_blackred, simple_blue, simple_green, simple_king, simple_pink, simple_purple, simple_red
 * Note: the sub-folder IS the color name you register here, so register "simple_blue" not "blue"
 * if that is what you named them on disk.  Adjust to match your actual folder names.
 */
public class AxeMasterySkill extends GenericWeaponMasterySkill {

    static {
        final String T = WeaponSwingVfxEntity.TYPE_AXE;

        // Body techniques
        VfxColorRegistry.register(T, ModTechniques.WHITE_LIGHTNING_TEN_STAGE_TECHNIQUE.getId(), "simple_blue");
        VfxColorRegistry.register(T, ModTechniques.HELLBOUND_MARROW_SCRIPTURE.getId(), "simple_blackred");
        VfxColorRegistry.register(T, ModTechniques.HEART_FIRE_TECHNIQUE.getId(), "simple_red");
        VfxColorRegistry.register(T, ModTechniques.SPLEEN_EARTH_TECHNIQUE.getId(), "simple_green");
        VfxColorRegistry.register(T, ModTechniques.LUNG_METAL_TECHNIQUE.getId(), "simple_king");
        VfxColorRegistry.register(T, ModTechniques.KIDNEY_WATER_TECHNIQUE.getId(), "simple_blue");
        VfxColorRegistry.register(T, ModTechniques.LIVER_WOOD_TECHNIQUE.getId(), "simple_green");

        // Essence techniques
        VfxColorRegistry.register(T, ModTechniques.FIRE_ESSENCE_TECHNIQUE.getId(), "simple_red");
        VfxColorRegistry.register(T, ModTechniques.WOOD_ESSENCE_TECHNIQUE.getId(), "simple_green");
        VfxColorRegistry.register(T, ModTechniques.WIND_ESSENCE_TECHNIQUE.getId(), "simple_blue");
        VfxColorRegistry.register(T, ModTechniques.LIGHTNING_ESSENCE_TECHNIQUE.getId(), "simple_blue");
        VfxColorRegistry.register(T, ModTechniques.EARTH_ESSENCE_TECHNIQUE.getId(), "simple_green");
        VfxColorRegistry.register(T, ModTechniques.METAL_ESSENCE_TECHNIQUE.getId(), "simple_king");
        VfxColorRegistry.register(T, ModTechniques.WATER_ESSENCE_TECHNIQUE.getId(), "simple_blue");

        // Soul techniques
        VfxColorRegistry.register(T, ModTechniques.BLOODFEAST_SOUL_REFINING_SCRIPTURE.getId(), "simple_blackred");
        VfxColorRegistry.register(T, ModTechniques.PALE_MOON_SCRIPTURE.getId(), "simple_purple");
        VfxColorRegistry.register(T, ModTechniques.SCHOLARLY_SOUL_TECHNIQUE.getId(), "simple_blue");

        // Poison
        VfxColorRegistry.register(T, ModTechniques.MYRIAD_VENOM_REFINEMENT_SCRIPTURE.getId(), "simple_green");

        // ── Add future axe technique mappings below ──
        // VfxColorRegistry.register(T, ModTechniques.SOME_TECHNIQUE.getId(), "simple_pink");
    }

    @Override protected ResourceLocation getPathId() { return ModPaths.AXE.getId(); }
    @Override protected TagKey<Item> getWeaponTag() { return ItemTags.AXES; }
    @Override protected String getVfxType() { return WeaponSwingVfxEntity.TYPE_AXE; }
    @Override protected String getFallbackColor() { return "simple_blue"; }

    @Override protected Vector3f getEffectRadius() { return new Vector3f(3.0f, 2.0f, 3.0f); }
    @Override protected double getBaseDamage() { return 7.0; }
    @Override protected int getSwingIntervalTicks(){ return 25;  }
    @Override protected int getEffectDuration() { return 10;  }
    @Override protected float getRotationZ() { return -15.0f; }

    @Override protected String getTitleKey() { return "ascension.skill.axe_mastery_skill"; }
    @Override protected String getDescriptionKey() { return "ascension.skill.axe_mastery.description_skill"; }
}