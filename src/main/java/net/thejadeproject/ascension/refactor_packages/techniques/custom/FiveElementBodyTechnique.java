package net.thejadeproject.ascension.refactor_packages.techniques.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skills.custom.ModSkills;
import net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.skill_data.GenericCultivationSkillData;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.stat_change_handlers.BasicStatChangeHandler;

import java.util.Set;

public class FiveElementBodyTechnique extends GenericTechnique {

    public FiveElementBodyTechnique(BasicStatChangeHandler handler) {
        super(ModPaths.BODY.getId(), Component.translatable("ascension.technique.five_element_body_technique"), 15.0, Set.of());
        setStatChangeHandler(handler);
    }

    @Override
    public void onTechniqueAdded(IEntityData heldEntity) {
        heldEntity.giveSkill(
            ModSkills.BODY_CULTIVATION_SKILL.getId(),
            new GenericCultivationSkillData(15.0, Set.of()),
            ModForms.MORTAL_VESSEL.getId()
        );
    }

    @Override
    public void onTechniqueRemoved(IEntityData heldEntity, ITechniqueData techniqueData) {
        heldEntity.getPathData(getPath()).handleRealmChange(
            heldEntity.getPathData(getPath()).getMajorRealm(), 0, heldEntity
        );
        heldEntity.removeSkill(ModSkills.BODY_CULTIVATION_SKILL.getId(), ModForms.MORTAL_VESSEL.getId());
    }

    @Override
    public boolean isCompatibleWith(ResourceLocation technique) {
        var tech = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(technique);
        return !(tech instanceof BodyElementTechnique) && !(tech instanceof CombinedBodyElementTechnique);
    }

    @Override
    public ITechniqueData freshTechniqueData(IEntityData heldEntity) { return null; }

    @Override
    public ITechniqueData fromCompound(CompoundTag tag) { return null; }

    @Override
    public ITechniqueData fromNetwork(RegistryFriendlyByteBuf buf) { return null; }
}
