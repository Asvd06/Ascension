package net.thejadeproject.ascension.refactor_packages.skills.custom.passive.weapon.mastery;

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
 * Sword / saber mastery passive.
 *
 * Technique → color mappings (textures/entity/vfx/sword_swing/<color>/):
 *   SWORD_COMPREHENSION_TECHNIQUE  → blue   (default)
 *
 * Add more entries here as you register new sword techniques.
 * Any unmapped technique falls back to "blue".
 */
public class SwordMasterySkill extends GenericWeaponMasterySkill {

    static {
        final String T = WeaponSwingVfxEntity.TYPE_SWORD;
        // Default sword technique
        VfxColorRegistry.register(T, ModTechniques.SWORD_COMPREHENSION_TECHNIQUE.getId(), "blue");

        // ── Add future sword technique mappings below ──
        // VfxColorRegistry.register(T, ModTechniques.NINE_BLADES_SABER.getId(), "purple");
        // VfxColorRegistry.register(T, ModTechniques.FALLING_LEAF_BLADE.getId(), "green");
        // VfxColorRegistry.register(T, ModTechniques.EDGE_TEMPERING_METHOD.getId(), "gold");
    }

    @Override protected ResourceLocation getPathId() { return ModPaths.SWORD.getId();}
    @Override protected TagKey<Item> getWeaponTag() { return ItemTags.SWORDS;}
    @Override protected String getVfxType() { return WeaponSwingVfxEntity.TYPE_SWORD;}
    @Override protected String getFallbackColor() { return "blue"; }

    @Override protected Vector3f getEffectRadius() { return new Vector3f(2.5f, 1.5f, 2.5f); }
    @Override protected double getBaseDamage() { return 5.0; }
    @Override protected int getEffectDuration() { return 8; }

    @Override protected String getTitleKey() { return "ascension.skill.sword_mastery_skill";             }
    @Override protected String getDescriptionKey() { return "ascension.skill.sword_mastery_skill.description"; }

}