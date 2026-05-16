package net.thejadeproject.ascension.refactor_packages.paths.custom;

import net.minecraft.network.chat.Component;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.data.foundation.stability.IStabilityHandler;

public class FoundationPath extends GenericPath{
    private IStabilityHandler stabilityHandler;

    public FoundationPath(Component title) {
        super(title);
    }


    public IStabilityHandler getStabilityHandler(int realm){
        return stabilityHandler;
    }

    public void onFoundationBreakthrough(IEntityData entityData, int majorRealm, int foundationStage){

    }
    public void onFoundationDown(IEntityData entityData,int majorRealm,int newStage){

    }
}
