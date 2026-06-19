package net.thejadeproject.ascension.refactor_packages.techniques.custom.soul;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.data_attachments.attachments.SoulWeaponData;
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

public class SoulForgedWeaponTechnique extends GenericTechnique {

    public static final double BASE_RATE = 1.25D;

    public SoulForgedWeaponTechnique(BasicStatChangeHandler statChangeHandler) {
        super(
                ModPaths.SOUL.getId(),
                Component.translatable("ascension.technique.soul_forged_weapon_manual"),
                BASE_RATE,
                Set.of()
        );

        setStatChangeHandler(statChangeHandler);
    }

    @Override
    public Component getShortDescription() {
        return Component.translatable("ascension.technique.soul_forged_weapon_manual.description.short");
    }

    @Override
    public Component getDescription() {
        return Component.translatable("ascension.technique.soul_forged_weapon_manual.description");
    }

    @Override
    public void onTechniqueAdded(IEntityData heldEntity) {
        IPathData pathData = heldEntity.getPathData(getPath());

        heldEntity.giveSkill(
                ModSkills.SIMPLE_SOUL_CULTIVATION_SKILL.getId(),
                ModForms.MORTAL_VESSEL.getId()
        );

        heldEntity.giveSkill(
                ModSkills.SOUL_FORGE.getId(),
                ModForms.MORTAL_VESSEL.getId()
        );

        refreshUniversalTechniqueSkills(heldEntity);
        refreshRealmUnlockSkills(
                heldEntity,
                pathData == null ? 0 : pathData.getMajorRealm()
        );
    }

    @Override
    public void onTechniqueRemoved(IEntityData heldEntity, ITechniqueData techniqueData) {
        if (heldEntity.getPathData(getPath()) != null) {
            heldEntity.getPathData(getPath()).handleRealmChange(
                    heldEntity.getPathData(getPath()).getMajorRealm(),
                    0,
                    heldEntity
            );
        }

        heldEntity.removeSkill(ModSkills.SIMPLE_SOUL_CULTIVATION_SKILL.getId(), ModForms.MORTAL_VESSEL.getId());
        heldEntity.removeSkill(ModSkills.SOUL_FORGE.getId(), ModForms.MORTAL_VESSEL.getId());

        if (heldEntity.getAttachedEntity() != null) {
            SoulWeaponData data = heldEntity.getAttachedEntity().getData(ModAttachments.SOUL_WEAPON);
            data.clear();
        }

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
                ModSkills.SOUL_SHIFT.getId(),
                majorRealm >= 1
        );
    }

    @Override
    public boolean isCompatibleWith(ResourceLocation technique) {
        return true;
    }
}