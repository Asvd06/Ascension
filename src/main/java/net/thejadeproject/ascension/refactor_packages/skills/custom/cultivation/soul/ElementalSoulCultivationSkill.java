package net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.soul;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.textures.TextureDataSubsection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.gui.elements.skills.cultivation.CultivationProgressBar;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastResult;
import net.thejadeproject.ascension.refactor_packages.skills.castable.ICastData;
import net.thejadeproject.ascension.refactor_packages.skills.castable.IPreCastData;
import net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.elemental.ElementalCultivationSkill;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.GenericTechnique;
import net.thejadeproject.ascension.refactor_packages.util.CultivationUtil;

import java.util.List;

public class ElementalSoulCultivationSkill extends ElementalCultivationSkill {

    private static final ResourceLocation SOUL_PATH = ModPaths.SOUL.getId();

    private static final double FALLBACK_SOUL_RATE = 2.0D;
    private static final double ELEMENTAL_SPLASH_RATE = 0.25D;

    private final ResourceLocation elementPath;

    public ElementalSoulCultivationSkill(ResourceLocation elementPath) {
        this.elementPath = elementPath;
    }

    @Override
    protected ResourceLocation getElementPath() {
        return elementPath;
    }

    @Override
    public CastResult canCast(Entity caster, IPreCastData preCastData) {
        return new CastResult(CastResult.Type.SUCCESS);
    }

    @Override
    public boolean continueCasting(
            int ticksElapsed,
            Entity caster,
            ICastData castData
    ) {
        if (!caster.hasData(ModAttachments.INPUT_STATES)) {
            return false;
        }

        if (!caster.level().isClientSide()) {
            IEntityData entityData = caster.getData(ModAttachments.ENTITY_DATA);

            double soulRate = getSoulCultivationRate(entityData);
            double soulGain = soulRate * getEnvironmentMultiplier(caster);

            boolean cultivatedSoul = CultivationUtil.tryCultivate(caster, SOUL_PATH, List.of(elementPath), soulGain);

            if (cultivatedSoul) {
                CultivationUtil.tryCultivate(caster, elementPath, List.of(SOUL_PATH), soulGain * ELEMENTAL_SPLASH_RATE);
            }
        }

        return caster.getData(ModAttachments.INPUT_STATES).isHeld("skill_cast");
    }

    private double getSoulCultivationRate(IEntityData entityData) {
        IPathData soulPathData =
                entityData.getPathData(SOUL_PATH);

        if (soulPathData == null) {
            return FALLBACK_SOUL_RATE;
        }

        ITechnique technique =
                soulPathData.getCurrentTechnique();

        if (technique instanceof GenericTechnique genericTechnique) {
            return genericTechnique.getBaseRate();
        }

        return FALLBACK_SOUL_RATE;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public RenderableElement getCastElement(UIFrame frame) {
        return new CultivationProgressBar(
                frame,
                new TextureDataSubsection(
                        ResourceLocation.fromNamespaceAndPath(
                                AscensionCraft.MOD_ID,
                                "textures/gui/overlay/overlays_all.png"
                        ),
                        256,
                        256,
                        0,
                        0,
                        65,
                        7
                ),
                SOUL_PATH
        );
    }

    @Override
    public Component getTitle(IEntityData entityData) {
        return Component.translatable(
                "ascension.skill.elemental_soul_cultivation",
                getElementTitle()
        );
    }

    @Override
    public Component getDescription(IEntityData entityData) {
        return Component.translatable(
                "ascension.skill.elemental_soul_cultivation.description",
                getElementTitle()
        );
    }
}