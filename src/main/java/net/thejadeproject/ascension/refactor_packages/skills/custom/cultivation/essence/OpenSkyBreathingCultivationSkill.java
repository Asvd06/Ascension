package net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.essence;

import net.lucent.easygui.gui.textures.ITextureData;
import net.lucent.easygui.gui.textures.TextureData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastResult;
import net.thejadeproject.ascension.refactor_packages.skills.castable.ICastData;
import net.thejadeproject.ascension.refactor_packages.skills.castable.IPreCastData;
import net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.GenericCultivationSkill;

public class OpenSkyBreathingCultivationSkill extends GenericCultivationSkill {

    public static final double BASE_RATE = 3.0D;

    public OpenSkyBreathingCultivationSkill() {
        super(BASE_RATE, ModPaths.ESSENCE.getId());
    }

    @Override
    public Component getTitle(IEntityData entityData) {
        return Component.translatable("ascension.skill.open_sky_breathing_skill");
    }

    @Override
    public Component getDescription(IEntityData entityData) {
        return Component.translatable("ascension.skill.open_sky_breathing_skill.description");
    }

    @Override
    public CastResult canCast(Entity caster, IPreCastData preCastData) {
        if (!canSeeOpenSky(caster)) {
            return new CastResult(CastResult.Type.FAILURE);
        }

        return super.canCast(caster, preCastData);
    }

    @Override
    public boolean continueCasting(int ticksElapsed, Entity caster, ICastData castData) {
        if (!canSeeOpenSky(caster)) {
            return false;
        }

        return super.continueCasting(ticksElapsed, caster, castData);
    }

    private boolean canSeeOpenSky(Entity caster) {
        return caster.level().canSeeSky(caster.blockPosition());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ITextureData getIcon(IEntityData entityData) {
        return new TextureData(
                ResourceLocation.fromNamespaceAndPath(
                        AscensionCraft.MOD_ID,
                        "textures/spells/icon/placeholder.png"
                ),
                16,
                16
        );
    }
}