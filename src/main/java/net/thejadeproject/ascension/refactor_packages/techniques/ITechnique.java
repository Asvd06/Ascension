package net.thejadeproject.ascension.refactor_packages.techniques;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.breakthroughs.IBreakthroughInstance;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.IEntityFormData;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.paths.data.foundation.stability.IStabilityHandler;

public interface ITechnique {



    Component getDisplayTitle();
    Component getShortDescription();
    Component getDescription();

    //the path this technique is for
    ResourceLocation getPath();

    void onTechniqueAdded(IEntityData heldEntity);
    void onTechniqueRemoved(IEntityData heldEntity,ITechniqueData techniqueData);

    //if realm change covers a breakthrough, get stability from pathData
    void onRealmChange(IEntityData entityData,int oldMajorRealm,int oldMinorRealm,int newMajorRealm,int newMinorRealm);


    void onFormRemoved(IEntityData heldEntity, IEntityFormData removedForm, IPathData pathData);
    void onFormAdded(IEntityData heldEntity,IEntityFormData addedForm,IPathData pathData);


    //figure something out with compatibility
    boolean isCompatibleWith(ResourceLocation technique);

    //TODO think about making these pass technique data as well

    default Component getMajorRealmName(int majorRealm){
        return AscensionRegistries.Paths.PATHS_REGISTRY.get(getPath()).getMajorRealmName(majorRealm);
    }
    default Component getMinorRealmName(int majorRealm,int minorRealm){
        return AscensionRegistries.Paths.PATHS_REGISTRY.get(getPath()).getMinorRealmName(majorRealm,minorRealm);
    }



    default int getMaxMajorRealm(){
        return AscensionRegistries.Paths.PATHS_REGISTRY.get(getPath()).getMaxMajorRealm();
    }
    default int getMaxMinorRealm(int majorRealm){
        return AscensionRegistries.Paths.PATHS_REGISTRY.get(getPath()).getMaxMinorRealm(majorRealm);
    }
    default double getMaxQiForRealm(int majorRealm,int minorRealm){
        return AscensionRegistries.Paths.PATHS_REGISTRY.get(getPath()).getMaxQiForRealm(majorRealm,minorRealm);
    }

    default boolean canBreakthroughMinorRealm(IEntityData entityData,int majorRealm,int minorRealm,double progress){
        return progress >= getMaxQiForRealm(majorRealm,minorRealm);
    }
    default boolean canBreakthrough(IEntityData entityData,int majorRealm,int minorRealm,double progress){
        return minorRealm == getMaxMinorRealm(majorRealm) && majorRealm < getMaxMajorRealm() && !entityData.isBreakingThrough(getPath()) && progress >= getMaxQiForRealm(majorRealm,minorRealm);
    }

    default ResourceLocation getDefaultForm(){
        return AscensionRegistries.Paths.PATHS_REGISTRY.get(getPath()).defaultForm();
    }
    RenderableElement getInformationContainer(UIFrame frame,IPathData pathData);

    IStabilityHandler getStabilityHandler();

    ITechniqueData freshTechniqueData(IEntityData heldEntity);
    ITechniqueData fromCompound(CompoundTag tag);
    ITechniqueData fromNetwork(RegistryFriendlyByteBuf buf);

    IBreakthroughInstance freshBreakthroughData(IEntityData heldEntity);
    IBreakthroughInstance breakthroughInstanceFromCompound(CompoundTag tag,int majorRealm,int minorRealm,ITechniqueData techniqueData);
    IBreakthroughInstance breakthroughInstanceFromNetwork(RegistryFriendlyByteBuf buf,int majorRealm,int minorRealm,ITechniqueData techniqueData);

    static ITechniqueData getFromCompound(IEntityData entityData, ITechnique technique, CompoundTag tag){
        try {
            return technique.fromCompound(tag);
        }catch (Exception e){
            AscensionCraft.LOGGER.error("error trying to load technique data data data for technique: "+
                    AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.getKey(technique),e);
            return technique.freshTechniqueData(entityData);
        }
    }
}
