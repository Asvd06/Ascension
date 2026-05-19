package net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.soul;

import net.lucent.easygui.gui.textures.ITextureData;
import net.lucent.easygui.gui.textures.TextureData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastResult;
import net.thejadeproject.ascension.refactor_packages.skills.castable.ICastData;
import net.thejadeproject.ascension.refactor_packages.skills.castable.IPreCastData;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.soul.ZenithSunTechnique;

public class ZenithSunCultivationSkill extends SimpleSoulCultivationSkill {

    private static final double SUN_MULTIPLIER = 2.0D;

    public ZenithSunCultivationSkill() {
        super(ZenithSunTechnique.BASE_RATE);
    }

    @Override
    public CastResult canCast(Entity caster, IPreCastData preCastData) {
        if (!caster.level().canSeeSky(caster.blockPosition())) {
            return new CastResult(
                    CastResult.Type.FAILURE,
                    Component.translatable("ascension.skill.zenith_sun_cultivation_skill.blocked_indoors")
            );
        }

        return super.canCast(caster, preCastData);
    }

    @Override
    protected double getEffectiveRate(Entity caster) {
        double rate = super.getEffectiveRate(caster);

        if (DawningSunCultivationSkill.isLookingAtSun(caster)) {
            rate *= SUN_MULTIPLIER;
        }

        return rate;
    }

    @Override
    public boolean continueCasting(int ticksElapsed, Entity caster, ICastData castData) {
        boolean continuing = super.continueCasting(ticksElapsed, caster, castData);

        if (continuing && !caster.level().isClientSide()) {
            DawningSunCultivationSkill.damageIfMoonExposed(caster);
        }

        return continuing;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ITextureData getIcon(IEntityData entityData) {
        return new TextureData(
                ResourceLocation.fromNamespaceAndPath(
                        AscensionCraft.MOD_ID,
                        "textures/spells/icon/placeholder.png"
                ),
                16, 16
        );
    }

    @Override
    public Component getTitle(IEntityData entityData) {
        return Component.translatable("ascension.skill.zenith_sun_cultivation_skill");
    }

    @Override
    public Component getDescription(IEntityData entityData) {
        return Component.translatable("ascension.skill.zenith_sun_cultivation_skill.description");
    }
}