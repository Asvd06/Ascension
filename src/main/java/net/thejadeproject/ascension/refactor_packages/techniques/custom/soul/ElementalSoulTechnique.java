package net.thejadeproject.ascension.refactor_packages.techniques.custom.soul;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.GenericTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.stat_change_handlers.BasicStatChangeHandler;

import java.util.Set;

public class ElementalSoulTechnique extends GenericTechnique {

    private final String translationName;
    private final ResourceLocation elementPath;
    private final ResourceLocation cultivationSkill;

    public ElementalSoulTechnique(
            String translationName,
            ResourceLocation elementPath,
            double baseRate,
            ResourceLocation cultivationSkill,
            BasicStatChangeHandler statChangeHandler
    ) {
        super(
                ModPaths.SOUL.getId(),
                Component.translatable("ascension.technique." + translationName),
                baseRate,
                Set.of(elementPath)
        );

        this.translationName = translationName;
        this.elementPath = elementPath;
        this.cultivationSkill = cultivationSkill;

        setStatChangeHandler(statChangeHandler);
    }

    public ResourceLocation getElementPath() {
        return elementPath;
    }

    @Override
    public Component getShortDescription() {
        return Component.translatable(
                "ascension.technique." + translationName + ".description.short"
        );
    }

    @Override
    public Component getDescription() {
        return Component.translatable(
                "ascension.technique." + translationName + ".description"
        );
    }

    @Override
    public void onTechniqueAdded(IEntityData heldEntity) {
        heldEntity.giveSkill(
                cultivationSkill,
                ModForms.MORTAL_VESSEL.getId()
        );

        heldEntity.getPathBonusHandler().addPathBonus(
                elementPath,
                1.0D
        );

        super.onTechniqueAdded(heldEntity);
    }

    @Override
    public void onTechniqueRemoved(
            IEntityData heldEntity,
            ITechniqueData techniqueData
    ) {
        heldEntity.removeSkill(
                cultivationSkill,
                ModForms.MORTAL_VESSEL.getId()
        );

        heldEntity.getPathBonusHandler().removePathBonus(
                elementPath,
                1.0D
        );

        super.onTechniqueRemoved(heldEntity, techniqueData);
    }

    @Override
    public boolean isCompatibleWith(ResourceLocation technique) {
        ITechnique other = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(technique);
        return other instanceof ElementalSoulTechnique;
    }
}