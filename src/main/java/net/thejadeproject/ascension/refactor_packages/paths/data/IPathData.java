package net.thejadeproject.ascension.refactor_packages.paths.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.IEntityFormData;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.path_data.SyncPathData;
import net.thejadeproject.ascension.refactor_packages.paths.IPath;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;

import java.util.List;
import java.util.Set;

public interface IPathData {

    //────────────────────────GETTERS──────────────────────────
    ResourceLocation getPath();
    ResourceLocation getCurrentTechniqueId();
    default ITechnique getCurrentTechnique(){
        return AscensionRegistries.getRegistryObject(getCurrentTechniqueId(),AscensionRegistries.Techniques.TECHNIQUES_REGISTRY);
    }

    int getMajorRealm();
    int getMinorRealm();
    double getCurrentRealmProgress();


    boolean isCultivating();
    boolean isBreakingThrough();

    //checks if the data has a history of cultivating this technique
    boolean hasTechnique(ResourceLocation technique);

    default ITechniqueData getCurrentTechniqueData(){
        return getTechniqueData(getCurrentTechniqueId());
    }
    ITechniqueData getTechniqueData(ResourceLocation technique);
    //returns a list of major realms a technique has been used to cultivate (only includes completed realms)
    Set<Integer> getCultivatedRealms(ResourceLocation technique);
    List<ResourceLocation> getTechniqueHistory();
    ITechnique getTechniqueForRealm(int realm);
    ResourceLocation getTechniqueIdForRealm(int realm);

    default int getMaxMajorRealm(){
        ITechnique technique = getCurrentTechnique();
        if(technique != null) return technique.getMaxMajorRealm();

        IPath path = AscensionRegistries.getRegistryObject(getPath(),AscensionRegistries.Paths.PATHS_REGISTRY);
        return path == null ? 0 : path.getMaxMajorRealm();
    }
    default int getMaxMinorRealm(int majorRealm){
        ITechnique technique = getCurrentTechnique();
        if(technique != null) return technique.getMaxMinorRealm(majorRealm);

        IPath path = AscensionRegistries.getRegistryObject(getPath(),AscensionRegistries.Paths.PATHS_REGISTRY);
        return path == null ? 0 : path.getMaxMinorRealm(majorRealm);
    }

    //────────────────────────ACCESSORS──────────────────────────
    void setBreakingThrough(boolean state);
    void setCultivating(boolean state);

    void setMinorRealm(int minorRealm);
    void setMajorRealm(int majorRealm);

    void setCurrentRealmProgress(double progress);

    void setCurrentTechnique(ResourceLocation technique);
    void setTechniqueData(ResourceLocation technique,ITechniqueData techniqueData);

    ITechniqueData removeTechniqueData(ResourceLocation technique);

    void removeTechniqueHistoryEntry(int realm);
    //────────────────────────HELPERS──────────────────────────

    default void handleRealmChange(int newMajorRealm, int newMinorRealm, IEntityData entityData){
        int oldMajorRealm = getMajorRealm();
        int oldMinorRealm = getMinorRealm();
        if(oldMajorRealm < newMajorRealm || (oldMajorRealm == newMajorRealm && newMinorRealm>oldMinorRealm)){
            newMajorRealm = Math.min(newMajorRealm,getMaxMajorRealm());
            newMinorRealm = Math.min(newMinorRealm,getMaxMinorRealm(newMajorRealm));
            if(newMajorRealm != oldMajorRealm) {

                //loop through each major realm
                for(int i = oldMinorRealm+1;i <= getMaxMinorRealm(oldMajorRealm);i++){
                    setMinorRealm(i);
                    minorRealmUp(entityData);
                }
                for(int i = oldMajorRealm+1;i<newMajorRealm;i++){


                    setMajorRealm(i);
                    setMinorRealm(0);
                    majorRealmUp(entityData);
                    for(int j = 1;j <= getMaxMinorRealm(oldMajorRealm);j++){
                        setMinorRealm(j);
                        minorRealmUp(entityData);
                    }
                }
                setMajorRealm(newMajorRealm);
                setMinorRealm(0);
                majorRealmUp(entityData);

                for(int i =1;i<=newMinorRealm;i++){
                    setMinorRealm(i);
                    minorRealmUp(entityData);
                }
            }else{

                for(int i = oldMinorRealm+1;i<=newMinorRealm;i++){
                    setMinorRealm(i);
                    minorRealmUp(entityData);
                }
            }

        }else{

            newMajorRealm = Math.max(newMajorRealm,0);
            newMinorRealm = Math.max(newMinorRealm,0);

            if(newMajorRealm != oldMajorRealm){
                for(int i = oldMajorRealm-1;i>0;i--){
                    setMinorRealm(i);
                    minorRealmDown(entityData);
                }

                for(int i = oldMajorRealm-1;i>newMajorRealm;i--){

                    updateTechnique(entityData);
                    setMajorRealm(i);
                    setMinorRealm(getMaxMinorRealm(getMajorRealm()));
                    majorRealmDown(entityData);
                    removeTechniqueHistoryEntry(getMajorRealm());

                    for(int j = getMinorRealm()-1;j>0;j--){
                        setMinorRealm(j);
                        minorRealmDown(entityData);
                    }
                }
                updateTechnique(entityData);

                setMajorRealm(newMajorRealm);
                setMinorRealm(getMaxMinorRealm(getMajorRealm()));
                majorRealmDown(entityData);

                for(int i =getMinorRealm()-1;i>=newMinorRealm;i--){
                    setMinorRealm(i);
                    minorRealmDown(entityData);
                }
            }else{

                for(int i = oldMinorRealm-1;i>=newMinorRealm;i--){
                    setMinorRealm(i);
                    minorRealmDown(entityData);
                }

            }
        }

    }
    default void resetCultivation(IEntityData entityData){

    }
    default void updateTechnique(IEntityData entityData){
        ITechnique technique = getCurrentTechnique();
        ResourceLocation newTechniqueId = getTechniqueIdForRealm(getMajorRealm()-1);
        if(technique != null && !getCurrentTechniqueId().equals(newTechniqueId)){
            ITechniqueData techniqueData = removeTechniqueData(getCurrentTechniqueId());
            setCurrentTechnique(null);
            technique.onTechniqueRemoved(entityData,techniqueData);
            ITechnique newTechnique = getTechniqueForRealm(getMajorRealm()-1);
            if(newTechnique != null){
                setCurrentTechnique(newTechniqueId);
                newTechnique.onTechniqueAdded(entityData);
            }
        }

    }
    void minorRealmUp(IEntityData entityData);
    void minorRealmDown(IEntityData entityData);

    void majorRealmUp(IEntityData entityData);
    void majorRealmDown(IEntityData entityData);
    default void onFormRemoved(IEntityData heldEntity, IEntityFormData removedFormData){

    };
    default void onFormAdded(IEntityData heldEntity, IEntityFormData addedFormData){

    };
    //────────────────────────Data Handling──────────────────────────

    /**
     *
     * @param player the player this is attached to, not used for syncing other players
     */
    default void sync(Player player){
        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        for(ResourceLocation form : entityData.getPathDataForms(getPath()))  PacketDistributor.sendToPlayer((ServerPlayer) player,new SyncPathData(form,this));

    }

    CompoundTag write();
    //we expect path to be written to this
    void encode(RegistryFriendlyByteBuf buf);

    //these are called by the path

    void load(CompoundTag tag,IEntityData entityData);
    void load(RegistryFriendlyByteBuf buf);
}
