package net.thejadeproject.ascension.refactor_packages.techniques.custom.essence;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skills.custom.ModSkills;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.GenericTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.stat_change_handlers.BasicStatChangeHandler;
import net.thejadeproject.ascension.refactor_packages.techniques.helpers.TechniqueSkillHelper;

import java.util.Set;

public class OpenSkyBreathingTechnique extends GenericTechnique {

    public static final double BASE_RATE = 3.0D;

    public OpenSkyBreathingTechnique(BasicStatChangeHandler statChangeHandler) {
        super(
                ModPaths.ESSENCE.getId(),
                Component.translatable("ascension.technique.open_sky_breathing_scripture"),
                BASE_RATE,
                Set.of()
        );

        setStatChangeHandler(statChangeHandler);
    }

    @Override
    public Component getShortDescription() {
        return Component.translatable("ascension.technique.open_sky_breathing_scripture.description.short");
    }

    @Override
    public Component getDescription() {
        return Component.translatable("ascension.technique.open_sky_breathing_scripture.description");
    }

    @Override
    public void onTechniqueAdded(IEntityData heldEntity) {
        heldEntity.giveSkill(
                ModSkills.OPEN_SKY_BREATHING_SKILL.getId(),
                ModForms.MORTAL_VESSEL.getId()
        );

        IPathData pathData = heldEntity.getPathData(getPath());
        refreshUniversalTechniqueSkills(heldEntity);
        refreshRealmUnlockSkills(
                heldEntity,
                pathData == null ? 0 : pathData.getMajorRealm()
        );
    }

    @Override
    public void onTechniqueRemoved(IEntityData heldEntity, ITechniqueData techniqueData) {
        IPathData pathData = heldEntity.getPathData(getPath());

        if (pathData != null) {
            pathData.handleRealmChange(pathData.getMajorRealm(), 0, heldEntity);
        }

        heldEntity.removeSkill(
                ModSkills.OPEN_SKY_BREATHING_SKILL.getId(),
                ModForms.MORTAL_VESSEL.getId()
        );

        refreshUniversalTechniqueSkills(heldEntity);
        refreshRealmUnlockSkills(heldEntity, -1);
    }

    @Override
    public void onRealmChange(
            IEntityData entityData,
            int oldMajorRealm,
            int oldMinorRealm,
            int newMajorRealm,
            int newMinorRealm
    ) {
        super.onRealmChange(entityData, oldMajorRealm, oldMinorRealm, newMajorRealm, newMinorRealm);
        refreshRealmUnlockSkills(entityData, newMajorRealm);
    }

    private void refreshRealmUnlockSkills(IEntityData entityData, int majorRealm) {
        TechniqueSkillHelper.refreshSkill(
                entityData,
                ModSkills.ESSENCE_SHORT_RANGE_TELEPORTATION.getId(),
                majorRealm >= 4
        );
    }


    @Override
    public boolean isCompatibleWith(ResourceLocation technique) {
        ITechnique other = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(technique);
        return other instanceof OpenSkyBreathingTechnique;
    }
}