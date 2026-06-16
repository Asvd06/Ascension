package net.thejadeproject.ascension.refactor_packages.techniques.helpers;

import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.skills.custom.ModSkills;

public final class TechniqueSkillHelper {

    private static final int QI_RELEASE_UNLOCK_REALM = 1;
    private static final int REGENERATION_UNLOCK_REALM = 2;
    private static final int QI_FLIGHT_UNLOCK_REALM = 2;
    private static final int QI_SUSTAINED_UNLOCK_REALM = 3;
    private static final int TRUE_FLIGHT_UNLOCK_REALM = 4;

    private TechniqueSkillHelper() {
    }

    public static void refreshUniversal(IEntityData entityData) {
        int highestMajorRealm = getHighestActiveTechniqueMajorRealm(entityData);

        refreshSkill(
                entityData,
                ModSkills.QI_RELEASE.getId(),
                highestMajorRealm >= QI_RELEASE_UNLOCK_REALM
        );

        refreshSkill(
                entityData,
                ModSkills.QI_PULL.getId(),
                highestMajorRealm >= QI_RELEASE_UNLOCK_REALM
        );

        refreshSkill(
                entityData,
                ModSkills.REGENERATION_BOOST.getId(),
                highestMajorRealm >= REGENERATION_UNLOCK_REALM
        );

        refreshSkill(
                entityData,
                ModSkills.QI_SUSTAINED_BODY.getId(),
                highestMajorRealm >= QI_SUSTAINED_UNLOCK_REALM
        );

        refreshSkill(
                entityData,
                ModSkills.TRUE_FLIGHT.getId(),
                highestMajorRealm >= TRUE_FLIGHT_UNLOCK_REALM
        );

        refreshSkill(
                entityData,
                ModSkills.AIR_STEP.getId(),
                highestMajorRealm >= QI_FLIGHT_UNLOCK_REALM
        );
    }

    private static int getHighestActiveTechniqueMajorRealm(IEntityData entityData) {
        int highestMajorRealm = -1;

        for (IPathData pathData : entityData.getAllPathData()) {
            if (pathData == null) continue;
            if (pathData.getCurrentTechniqueId() == null) continue;

            highestMajorRealm = Math.max(highestMajorRealm, pathData.getMajorRealm());
        }

        return highestMajorRealm;
    }


    public static void refreshSkill(IEntityData entityData, ResourceLocation skillId, boolean shouldHave) {
        if (shouldHave) {
            if (!entityData.hasSkill(skillId)) {
                entityData.giveSkill(skillId, ModForms.MORTAL_VESSEL.getId());
            }

            return;
        }

        if (entityData.hasSkill(skillId)) {
            entityData.removeSkill(skillId, ModForms.MORTAL_VESSEL.getId());
        }
    }
}