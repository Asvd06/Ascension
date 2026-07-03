package net.thejadeproject.ascension.refactor_packages.techniques.custom.essence;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skills.custom.ModSkills;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.GenericTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.stat_change_handlers.BasicStatChangeHandler;

import java.util.Set;

public class AstralEssenceTechnique extends GenericTechnique {

    public static final double BASE_RATE = 4.0D;

    public AstralEssenceTechnique(BasicStatChangeHandler statChangeHandler) {
        super(
                ModPaths.ESSENCE.getId(),
                Component.translatable("ascension.technique.astral_essence_technique"),
                BASE_RATE,
                Set.of()
        );
        setStatChangeHandler(statChangeHandler);
    }

    @Override
    public Component getShortDescription() {
        return Component.translatable("ascension.technique.astral_essence_technique.description.short");
    }

    @Override
    public Component getDescription() {
        return Component.translatable("ascension.technique.astral_essence_technique.description");
    }

    @Override
    public void onTechniqueAdded(IEntityData heldEntity) {
        heldEntity.giveSkill(
                ModSkills.ASTRAL_ESSENCE_CULTIVATION_SKILL.getId(),
                ModForms.MORTAL_VESSEL.getId()
        );
        refreshUniversalTechniqueSkills(heldEntity);
    }

    @Override
    public void onTechniqueRemoved(IEntityData heldEntity, ITechniqueData techniqueData) {
        heldEntity.getPathData(getPath()).handleRealmChange(heldEntity.getPathData(getPath()).getMajorRealm(), 0, heldEntity);
        heldEntity.removeSkill(
                ModSkills.ASTRAL_ESSENCE_CULTIVATION_SKILL.getId(),
                ModForms.MORTAL_VESSEL.getId()
        );
        refreshUniversalTechniqueSkills(heldEntity);
    }

    @Override
    public boolean isCompatibleWith(ResourceLocation technique) {
        ITechnique other = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(technique);
        return other instanceof AstralEssenceTechnique;
    }
}