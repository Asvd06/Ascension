package net.thejadeproject.ascension.refactor_packages.paths.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;

import java.util.function.Supplier;

public class ComprehensionPath extends GenericPath {

    private Supplier<ResourceLocation> defaultTechnique;

    public ComprehensionPath(Component title) {
        super(title);
    }

    public ComprehensionPath setDefaultTechnique(Supplier<ResourceLocation> defaultTechnique) {
        this.defaultTechnique = defaultTechnique;
        return this;
    }

    @Override
    public int getMaxMajorRealm() {
        return 4;
    }

    @Override
    public IPathData freshPathData(IEntityData heldEntity) {
        IPathData pathData = super.freshPathData(heldEntity);

        if (defaultTechnique != null) {
            pathData.setCurrentTechnique(defaultTechnique.get());
        }

        return pathData;
    }
}