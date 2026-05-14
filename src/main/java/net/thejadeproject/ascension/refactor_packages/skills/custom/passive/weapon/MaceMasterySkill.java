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
 * Mace mastery passive — area smash + Slowness II on hit.
 *
 * Texture folders expected under textures/entity/vfx/mace_smash/<color>/:
 *   blue, green, purple
 *   (matches screenshot: mace_particle sub-folders)
 *
 * Technique → color mappings:
 *   Default / lightning / metal / soul  → blue
 *   Wood / earth / water / poison       → green
 *   Soul (Pale Moon, Bloodfeast, etc.)  → purple
 */
public class MaceMasterySkill extends GenericWeaponMasterySkill {

    static {
        final String T = WeaponSwingVfxEntity.TYPE_MACE;

        // Body techniques
        VfxColorRegistry.register(T, ModTechniques.WHITE_LIGHTNING_TEN_STAGE_TECHNIQUE.getId(), "blue");
        VfxColorRegistry.register(T, ModTechniques.HELLBOUND_MARROW_SCRIPTURE.getId(), "purple");
        VfxColorRegistry.register(T, ModTechniques.HEART_FIRE_TECHNIQUE.getId(), "blue");
        VfxColorRegistry.register(T, ModTechniques.KIDNEY_WATER_TECHNIQUE.getId(), "green");
        VfxColorRegistry.register(T, ModTechniques.LIVER_WOOD_TECHNIQUE.getId(), "green");
        VfxColorRegistry.register(T, ModTechniques.SPLEEN_EARTH_TECHNIQUE.getId(), "green");
        VfxColorRegistry.register(T, ModTechniques.LUNG_METAL_TECHNIQUE.getId(), "blue");

        // Essence/elemental techniques
        VfxColorRegistry.register(T, ModTechniques.FIRE_ESSENCE_TECHNIQUE.getId(), "blue");
        VfxColorRegistry.register(T, ModTechniques.WATER_ESSENCE_TECHNIQUE.getId(), "green");
        VfxColorRegistry.register(T, ModTechniques.WOOD_ESSENCE_TECHNIQUE.getId(), "green");
        VfxColorRegistry.register(T, ModTechniques.EARTH_ESSENCE_TECHNIQUE.getId(), "green");
        VfxColorRegistry.register(T, ModTechniques.METAL_ESSENCE_TECHNIQUE.getId(), "blue");
        VfxColorRegistry.register(T, ModTechniques.LIGHTNING_ESSENCE_TECHNIQUE.getId(), "blue");
        VfxColorRegistry.register(T, ModTechniques.WIND_ESSENCE_TECHNIQUE.getId(), "blue");

        // Soul techniques
        VfxColorRegistry.register(T, ModTechniques.BLOODFEAST_SOUL_REFINING_SCRIPTURE.getId(), "purple");
        VfxColorRegistry.register(T, ModTechniques.PALE_MOON_SCRIPTURE.getId(), "purple");
        VfxColorRegistry.register(T, ModTechniques.GIBBOUS_MOON_SCRIPTURE.getId(), "purple");
        VfxColorRegistry.register(T, ModTechniques.DAWNING_SUN_SCRIPTURE.getId(), "blue");
        VfxColorRegistry.register(T, ModTechniques.ZENITH_SUN_SCRIPTURE.getId(), "blue");
        VfxColorRegistry.register(T, ModTechniques.SCHOLARLY_SOUL_TECHNIQUE.getId(), "blue");

        // Poison
        VfxColorRegistry.register(T, ModTechniques.MYRIAD_VENOM_REFINEMENT_SCRIPTURE.getId(), "green");

        // ── Add future mace technique mappings below ──
    }

    @Override protected ResourceLocation getPathId() { return ModPaths.MACE.getId(); }
    @Override protected TagKey<Item> getWeaponTag() { return ItemTags.MACE_ENCHANTABLE; }
    @Override protected String getVfxType() { return WeaponSwingVfxEntity.TYPE_MACE; }
    @Override protected String getFallbackColor() { return "blue"; }

    @Override protected Vector3f getEffectRadius() { return new Vector3f(3.0f, 2.5f, 3.0f); }
    @Override protected double getBaseDamage() { return 9.0; }
    @Override protected int getSwingIntervalTicks() { return 30; }
    @Override protected int getEffectDuration() { return 12;    }
    @Override protected float getRotationZ() { return 90.0f; }
    @Override protected double getQiCostPerSwing(double m) { return 3.0 * m; }

    @Override protected String getTitleKey() { return "ascension.skill.mace_mastery_skill"; }
    @Override protected String getDescriptionKey() { return "ascension.skill.mace_mastery.description_skill"; }
}