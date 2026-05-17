package net.thejadeproject.ascension.refactor_packages.skills.custom.passive.weapon.mastery;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.skills.vfx.weaponvfx.VfxColorRegistry;
import net.thejadeproject.ascension.refactor_packages.skills.vfx.weaponvfx.WeaponSwingVfxEntity;
import net.thejadeproject.ascension.refactor_packages.techniques.ModTechniques;
import net.thejadeproject.ascension.util.ModTags;
import org.joml.Vector3f;

public class FistMasterySkill extends GenericWeaponMasterySkill{
    static {
        final String T = WeaponSwingVfxEntity.TYPE_FIST;

    }
    @Override
    protected ResourceLocation getPathId() {
        return ModPaths.FIST.getId();
    }
    @Override
    protected TagKey<Item> getWeaponTag() {
        return ModTags.Items.FIST;
    }
    @Override
    public boolean matchesWeapon(ItemStack stack) { return stack.isEmpty() || (getWeaponTag() != null && stack.is(getWeaponTag())); }
    @Override
    protected String getVfxType() { return WeaponSwingVfxEntity.TYPE_FIST;}
    @Override protected String getFallbackColor() { return "simple_blue"; }
    @Override protected Vector3f getEffectRadius() { return new Vector3f(0.8f, 0.8f, 4.5f); }
    @Override
    protected String getTitleKey() { return "ascension.skill.fist_mastery_skill"; }
    @Override
    protected String getDescriptionKey() { return "ascension.skill.fist_mastery.description_skill"; }
    @Override
    protected String getIconPath() { return "textures/spells/fist_aura.png"; }
}
