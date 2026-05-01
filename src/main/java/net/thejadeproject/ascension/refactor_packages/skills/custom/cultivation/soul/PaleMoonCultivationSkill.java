package net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.soul;

import net.lucent.easygui.gui.textures.ITextureData;
import net.lucent.easygui.gui.textures.TextureData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.GenericCultivationSkill;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.soul.PaleMoonTechnique;

public class PaleMoonCultivationSkill extends GenericCultivationSkill {

    public PaleMoonCultivationSkill() {
        super(PaleMoonTechnique.BASE_RATE, ModPaths.SOUL.getId());
    }

    @Override
    protected double getEffectiveRate(Entity caster) {
        return super.getEffectiveRate(caster) * getMoonMultiplier(caster);
    }

    private static double getMoonMultiplier(Entity caster) {
        Level level = caster.level();

        if (!level.isNight()) {
            return 1.0D;
        }

        if (!level.canSeeSky(caster.blockPosition())) {
            return 1.0D;
        }

        return 1.5D;
    }

    @Override
    public ITextureData getIcon() {
        return new TextureData(
                ResourceLocation.fromNamespaceAndPath(
                        AscensionCraft.MOD_ID,
                        "textures/spells/icon/placeholder.png"
                ),
                16,
                16
        );
    }

    @Override
    public Component getTitle() {
        return Component.translatable("ascension.skill.pale_moon_cultivation_skill");
    }

    @Override
    public Component getDescription() {
        return Component.translatable("ascension.skill.pale_moon_cultivation_skill.description");
    }
}