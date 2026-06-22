package net.thejadeproject.ascension.refactor_packages.techniques.custom.elemental;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
import net.thejadeproject.ascension.refactor_packages.handlers.realm_change.RealmChangeHandler;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.GenericTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.stat_change_handlers.BasicStatChangeHandler;
import net.thejadeproject.ascension.refactor_packages.techniques.helpers.TechniqueSkillHelper;

import java.util.List;
import java.util.Set;

public class BasicElementalTechnique extends GenericTechnique {

    public record RealmSkillUnlock(ResourceLocation skillId, int majorRealm) {}

    private final ResourceLocation cultivationSkill;
    private final List<ResourceLocation> grantedSkills;
    private final List<RealmSkillUnlock> realmSkillUnlocks;

    public BasicElementalTechnique(
            ResourceLocation path,
            Component title,
            double baseRate,
            Set<ResourceLocation> secondaryPaths,
            ResourceLocation cultivationSkill,
            List<ResourceLocation> grantedSkills,
            List<RealmSkillUnlock> realmSkillUnlocks
    ) {
        super(path, title, baseRate, secondaryPaths);
        this.cultivationSkill = cultivationSkill;
        this.grantedSkills = grantedSkills;
        this.realmSkillUnlocks = realmSkillUnlocks;
    }

    @Override
    public void onTechniqueAdded(IEntityData heldEntity) {
        super.onTechniqueAdded(heldEntity);

        heldEntity.giveSkill(cultivationSkill, ModForms.MORTAL_VESSEL.getId());

        for (ResourceLocation skillId : grantedSkills) {
            heldEntity.giveSkill(skillId, ModForms.MORTAL_VESSEL.getId());
        }

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
            refreshRealmUnlockSkills(heldEntity, -1);
        }

        heldEntity.removeSkill(cultivationSkill, ModForms.MORTAL_VESSEL.getId());

        for (ResourceLocation skillId : grantedSkills) {
            heldEntity.removeSkill(skillId, ModForms.MORTAL_VESSEL.getId());
        }

        super.onTechniqueRemoved(heldEntity, techniqueData);
    }

    @Override
    public void onRealmChange(
            IEntityData entityData,
            int oldMajorRealm,
            int oldMinorRealm,
            int newMajorRealm,
            int newMinorRealm
    ) {
        super.onRealmChange(
                entityData,
                oldMajorRealm,
                oldMinorRealm,
                newMajorRealm,
                newMinorRealm
        );

        refreshRealmUnlockSkills(entityData, newMajorRealm);
    }

    protected void refreshRealmUnlockSkills(IEntityData entityData, int majorRealm) {
        for (RealmSkillUnlock unlock : realmSkillUnlocks) {
            TechniqueSkillHelper.refreshSkill(entityData, unlock.skillId(), majorRealm >= unlock.majorRealm());
        }
    }

    public boolean isCompatibleWith(ResourceLocation technique) {
        return AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(technique)
                instanceof GenericTechnique other
                && other.getPath().equals(this.getPath());
    }

    @Override
    public BasicElementalTechnique setRealmChangeHandler(RealmChangeHandler handler){
        super.setRealmChangeHandler(handler);
        return this;
    }

    @Override
    public BasicElementalTechnique setStatChangeHandler(BasicStatChangeHandler statChangeHandler) {
        super.setStatChangeHandler(statChangeHandler);
        return this;
    }

}