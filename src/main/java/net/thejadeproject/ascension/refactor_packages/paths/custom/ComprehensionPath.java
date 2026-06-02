package net.thejadeproject.ascension.refactor_packages.paths.custom;

import net.minecraft.network.chat.Component;
import net.neoforged.fml.common.Mod;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.techniques.ModTechniques;

public class ComprehensionPath extends GenericPath{
    public ComprehensionPath(Component title) {
        super(title);
    }

    @Override
    public int getMaxMajorRealm() {
        return 4;
    }

    @Override
    public IPathData freshPathData(IEntityData heldEntity) {
        if ( this == ModPaths.SWORD.get() ){
            IPathData pathData = super.freshPathData(heldEntity);
            pathData.setCurrentTechnique(ModTechniques.SIMPLE_SWORD_MANUAL.getId());
            return pathData;
        }
        if ( this == ModPaths.MACE.get() ) {
            IPathData pathData = super.freshPathData(heldEntity);
            pathData.setCurrentTechnique(ModTechniques.SIMPLE_MACE_MANUAL.getId());
            return pathData;
        }
        if ( this == ModPaths.SPEAR.get() ) {
            IPathData pathData = super.freshPathData(heldEntity);
            pathData.setCurrentTechnique(ModTechniques.SIMPLE_SPEAR_MANUAL.getId());
            return pathData;
        }
        if ( this == ModPaths.BLADE.get() ) {
            IPathData pathData = super.freshPathData(heldEntity);
            pathData.setCurrentTechnique(ModTechniques.SIMPLE_BLADE_MANUAL.getId());
            return pathData;
        }
        if ( this == ModPaths.AXE.get() ) {
            IPathData pathData = super.freshPathData(heldEntity);
            pathData.setCurrentTechnique(ModTechniques.SIMPLE_AXE_MANUAL.getId());
            return pathData;
        }
        return super.freshPathData(heldEntity);
    }
}
