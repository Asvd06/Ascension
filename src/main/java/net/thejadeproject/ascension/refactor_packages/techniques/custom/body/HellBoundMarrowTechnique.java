package net.thejadeproject.ascension.refactor_packages.techniques.custom.body;

import net.minecraft.network.chat.Component;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.skills.custom.ModSkills;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.GenericTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.stat_change_handlers.BasicStatChangeHandler;
import net.thejadeproject.ascension.refactor_packages.techniques.helpers.TechniqueSkillHelper;

import java.util.Set;

public class HellBoundMarrowTechnique extends GenericTechnique {

    //TODO: Make this demonic path as well eventually lol - sortofSmart

    public HellBoundMarrowTechnique(BasicStatChangeHandler statChangeHandler) {
        super(
                ModPaths.BODY.getId(),
                Component.translatable("ascension.technique.hellbound_marrow_scripture"),
                8.0D,
                Set.of(ModSkills.MARROW_FURNACE.getId())
        );
        setStatChangeHandler(statChangeHandler);
    }

    @Override
    public void onTechniqueAdded(IEntityData heldEntity) {
        super.onTechniqueAdded(heldEntity);

        IPathData pathData = heldEntity.getPathData(getPath());

        heldEntity.giveSkill(ModSkills.MARROW_FURNACE.getId(), null, ModForms.MORTAL_VESSEL.getId());

        refreshRealmUnlockSkills(
                heldEntity,
                pathData == null ? 0 : pathData.getMajorRealm()
        );
    }

    @Override
    public void onTechniqueRemoved(IEntityData heldEntity, ITechniqueData techniqueData) {
        heldEntity.removeSkill(ModSkills.MARROW_FURNACE.getId(), ModForms.MORTAL_VESSEL.getId());
        super.onTechniqueRemoved(heldEntity, techniqueData);
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
                ModSkills.BODY_FLASH_STEP.getId(),
                majorRealm >= 2
        );
    }

    @Override
    public Component getShortDescription() {
        return Component.translatable("ascension.technique.hellbound_marrow_scripture.description.short");
    }

    @Override
    public Component getDescription() {
        return Component.translatable("ascension.technique.hellbound_marrow_scripture.description");
    }


}