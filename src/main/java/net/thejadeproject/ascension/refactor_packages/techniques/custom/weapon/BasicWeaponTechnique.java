package net.thejadeproject.ascension.refactor_packages.techniques.custom.weapon;

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

public class BasicWeaponTechnique extends GenericTechnique {

    private final List<ResourceLocation> startingSkills;
    private final List<RealmSkillUnlock> realmSkillUnlocks;

    public BasicWeaponTechnique(
            ResourceLocation path,
            Component title,
            double baseRate,
            Set<ResourceLocation> secondaryPaths,
            List<ResourceLocation> startingSkills
    ) {
        this(path, title, baseRate, secondaryPaths, startingSkills, List.of());
    }

    public BasicWeaponTechnique(
            ResourceLocation path,
            Component title,
            double baseRate,
            Set<ResourceLocation> secondaryPaths,
            List<ResourceLocation> startingSkills,
            List<RealmSkillUnlock> realmSkillUnlocks
    ) {
        super(path, title, baseRate, secondaryPaths);
        this.startingSkills = List.copyOf(startingSkills);
        this.realmSkillUnlocks = List.copyOf(realmSkillUnlocks);
    }

    @Override
    public void onTechniqueAdded(IEntityData heldEntity) {
        for (ResourceLocation skill : startingSkills) {
            heldEntity.giveSkill(
                    skill,
                    ModForms.MORTAL_VESSEL.getId()
            );
        }

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

        for (ResourceLocation skill : startingSkills) {
            heldEntity.removeSkill(
                    skill,
                    ModForms.MORTAL_VESSEL.getId()
            );
        }

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
        for (RealmSkillUnlock unlock : realmSkillUnlocks) {
            TechniqueSkillHelper.refreshSkill(
                    entityData,
                    unlock.skill(),
                    majorRealm >= unlock.requiredMajorRealm()
            );
        }
    }

    @Override
    public BasicWeaponTechnique setStatChangeHandler(BasicStatChangeHandler statChangeHandler) {
        super.setStatChangeHandler(statChangeHandler);
        return this;
    }

    @Override
    public boolean isCompatibleWith(ResourceLocation technique) {
        return AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(technique)
                instanceof GenericTechnique other
                && other.getPath().equals(this.getPath());
    }

    public record RealmSkillUnlock(
            ResourceLocation skill,
            int requiredMajorRealm
    ) {
    }

    @Override
    public BasicWeaponTechnique setRealmChangeHandler(RealmChangeHandler handler){
        return this;
    }


}