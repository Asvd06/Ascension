package net.thejadeproject.ascension.refactor_packages.techniques.custom.weapon;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skills.custom.ModSkills;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.GenericTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.stat_change_handlers.BasicStatChangeHandler;

import java.util.Set;

public class FistCultivationTechnique extends GenericTechnique {

    public FistCultivationTechnique(
            ResourceLocation path,
            Component title,
            double baseRate,
            Set<ResourceLocation> secondaryPaths
    ) {
        super(path, title, baseRate, secondaryPaths);
    }

    @Override
    public void onTechniqueAdded(IEntityData heldEntity) {
        heldEntity.giveSkill(
                ModSkills.FIST_CULTIVATION_SKILL.getId(),
                ModForms.MORTAL_VESSEL.getId()
        );

        heldEntity.giveSkill(
                ModSkills.FIST_MASTERY_SKILL.getId(),
                ModForms.MORTAL_VESSEL.getId()
        );

        refreshUniversalTechniqueSkills(heldEntity);

        IPathData pathData = heldEntity.getPathData(getPath());
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
                ModSkills.FIST_CULTIVATION_SKILL.getId(),
                ModForms.MORTAL_VESSEL.getId()
        );

        heldEntity.removeSkill(
                ModSkills.FIST_MASTERY_SKILL.getId(),
                ModForms.MORTAL_VESSEL.getId()
        );

        refreshRealmUnlockSkills(heldEntity, -1);
        clearUniversalTechniqueSkills(heldEntity);
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
    }

    @Override
    public FistCultivationTechnique setStatChangeHandler(BasicStatChangeHandler statChangeHandler) {
        super.setStatChangeHandler(statChangeHandler);
        return this;
    }

    @Override
    public boolean isCompatibleWith(ResourceLocation technique) {
        return AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(technique)
                instanceof FistCultivationTechnique;
    }
}