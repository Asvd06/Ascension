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
 * Blade mastery passive (custom blade tag — katanas, scimitars, etc.)
 *
 * Blade shares the sword_swing VFX type so it maps into the same texture folder.
 * If you add a dedicated blade_swing folder later, change getVfxType() and the keys below.
 *
 * Technique → color mappings (textures/entity/vfx/sword_swing/<color>/):
 *   SWORD_COMPREHENSION_TECHNIQUE  → blue  (fallback for blade as well)
 *
 * Add more entries below as blade-specific techniques are created.
 */
public class BladeMasterySkill extends GenericWeaponMasterySkill {

    static {
        final String T = WeaponSwingVfxEntity.TYPE_SWORD; // shares sword folder
        VfxColorRegistry.register(T, ModTechniques.SWORD_COMPREHENSION_TECHNIQUE.getId(), "blue");

        // ── Add future blade technique mappings below ──
        // VfxColorRegistry.register(T, ModTechniques.MORTAL_NINE_SABER.getId(), "red");
    }

    @Override protected ResourceLocation getPathId() { return ModPaths.BLADE.getId(); }
    @Override protected TagKey<Item> getWeaponTag() { return ModTags.Items.BLADE; }
    @Override protected String getVfxType() { return WeaponSwingVfxEntity.TYPE_SWORD; }
    @Override protected String getFallbackColor() { return "blue"; }

    @Override protected Vector3f getEffectRadius() { return new Vector3f(2.0f, 1.5f, 3.0f); }
    @Override protected double getBaseDamage() { return 5.5; }
    @Override protected float getRotationZ() { return 30.0f; }
    @Override protected int getEffectDuration() { return 8; }

    @Override protected String getTitleKey() { return "ascension.skill.blade_mastery_skill"; }
    @Override protected String getDescriptionKey() { return "ascension.skill.blade_mastery.description_skill"; }
}