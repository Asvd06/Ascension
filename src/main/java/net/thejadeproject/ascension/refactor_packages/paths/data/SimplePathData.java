package net.thejadeproject.ascension.refactor_packages.paths.data;

import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;
import net.thejadeproject.ascension.refactor_packages.util.ByteBufUtil;

import java.util.*;

public class SimplePathData implements IPathData{



    //────────────────────────current realm──────────────────────────
    private int majorRealm;
    private int minorRealm;
    private double progress;
    private ResourceLocation currentTechnique;

    private boolean cultivating;
    private boolean breakingThrough;

    //────────────────────────multi realm data──────────────────────────
    //index = major realm, val = technique used for that realm
    private final ArrayList<ResourceLocation> techniqueHistory = new ArrayList<>();

    private final HashMap<ResourceLocation,ITechniqueData> techniqueData = new HashMap<>();


    private final ResourceLocation path;
    public SimplePathData(ResourceLocation path){
        this.path = path;
    }


    //────────────────────────GETTERS──────────────────────────
    @Override
    public ResourceLocation getPath() {
        return path;
    }

    @Override
    public ResourceLocation getCurrentTechniqueId() {
        return currentTechnique;
    }

    @Override
    public int getMajorRealm() {
        return majorRealm;
    }

    @Override
    public int getMinorRealm() {
        return minorRealm;
    }

    @Override
    public double getCurrentRealmProgress() {
        return progress;
    }

    @Override
    public boolean isCultivating() {
        return cultivating;
    }

    @Override
    public boolean isBreakingThrough() {
        return breakingThrough;
    }

    /**
     *
     * @param technique the technique we are checking for
     * @return true -> we are either cultivating it or have cultivated it in the past. false -> we have not cultivated it
     */
    @Override
    public boolean hasTechnique(ResourceLocation technique) {
        return technique.equals(currentTechnique) || techniqueHistory.contains(technique);
    }

    @Override
    public ITechniqueData getTechniqueData(ResourceLocation technique) {
        return techniqueData.get(technique);
    }

    //returns all realms a technique has been used to fully cultivate (does not include current realm)
    @Override
    public Set<Integer> getCultivatedRealms(ResourceLocation technique) {
        HashSet<Integer> realms = new HashSet<>();
        for(int i = 0; i<techniqueHistory.size();i++){
            if(technique.equals(techniqueHistory.get(i))){
                realms.add(i);
            }
        }
        return realms;
    }

    @Override
    public List<ResourceLocation> getTechniqueHistory() {
        return techniqueHistory;
    }

    @Override
    public ITechnique getTechniqueForRealm(int realm) {
        return AscensionRegistries.getRegistryObject(getTechniqueIdForRealm(realm),AscensionRegistries.Techniques.TECHNIQUES_REGISTRY);
    }

    @Override
    public ResourceLocation getTechniqueIdForRealm(int realm) {
        return realm >= techniqueHistory.size() ? null : techniqueHistory.get(realm);
    }

    //────────────────────────ACCESSORS──────────────────────────
    @Override
    public void setBreakingThrough(boolean state) {
        breakingThrough = state;
    }

    @Override
    public void setCultivating(boolean state) {
        cultivating = state;
    }

    @Override
    public void setMinorRealm(int minorRealm) {
        this.minorRealm = minorRealm;
    }

    @Override
    public void setMajorRealm(int majorRealm) {
        this.majorRealm = majorRealm;
    }

    @Override
    public void setCurrentRealmProgress(double progress) {
        this.progress = progress;
    }

    @Override
    public void setCurrentTechnique(ResourceLocation technique) {
        if(currentTechnique == null){
            currentTechnique = technique;
        }else{
            if(currentTechnique.equals(technique)) return;
            ResourceLocation oldTechnique = currentTechnique;
            currentTechnique = technique;
            if(hasTechnique(oldTechnique)) return;

            removeTechniqueData(oldTechnique);
        }




    }

    @Override
    public void setTechniqueData(ResourceLocation technique, ITechniqueData techniqueData) {
        this.techniqueData.put(technique,techniqueData);
    }

    @Override
    public ITechniqueData removeTechniqueData(ResourceLocation technique) {
        return techniqueData.remove(technique);
    }

    @Override
    public void removeTechniqueHistoryEntry(int realm) {
        techniqueHistory.remove(realm);
    }
    //────────────────────────HELPERS──────────────────────────

    //TODO go through and call realm change on techniques
    //TODO update techniques to now have realm up/down for each type

    @Override
    public void minorRealmUp(IEntityData entityData) {
        progress = 0;
        if(getCurrentTechnique() != null) getCurrentTechnique().onRealmChange(entityData,getMajorRealm(),getMinorRealm()-1,getMajorRealm(),getMinorRealm());
    }

    @Override
    public void minorRealmDown(IEntityData entityData) {
        if(getCurrentTechnique() != null) getCurrentTechnique().onRealmChange(entityData,getMajorRealm(),getMinorRealm()+1,getMajorRealm(),getMinorRealm());
    }

    @Override
    public void majorRealmUp(IEntityData entityData) {
        techniqueHistory.add(currentTechnique);
        progress = 0;
        if(getCurrentTechnique() != null) getCurrentTechnique().onRealmChange(entityData,getMajorRealm()-1,getMaxMinorRealm(getMajorRealm()-1),getMajorRealm(),getMinorRealm());
    }

    @Override
    public void majorRealmDown(IEntityData entityData) {
        progress = 0;
        if(getCurrentTechnique() != null) getCurrentTechnique().onRealmChange(entityData,getMajorRealm()+1,0,getMajorRealm(),getMinorRealm());

    }
    //────────────────────────Data Handling──────────────────────────

    @Override
    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("major_realm",getMajorRealm());
        tag.putInt("minor_realm",getMinorRealm());
        tag.putDouble("progress",getCurrentRealmProgress());
        tag.putString("technique",currentTechnique==null?"none":currentTechnique.toString());


        ListTag techniqueDataTags = new ListTag();
        for(ResourceLocation technique : this.techniqueData.keySet()){
            CompoundTag techniqueDataTag = new CompoundTag();
            techniqueDataTag.putString("technique",technique == null? "none": technique.toString());
            techniqueDataTag.put("data",techniqueData.get(technique) == null? new CompoundTag(): techniqueData.get(technique).write());
            techniqueDataTags.add(techniqueDataTag);
        }
        tag.put("technique_data",techniqueDataTags);
        ListTag techniqueHistory = new ListTag();
        for (ResourceLocation technique : this.techniqueHistory) {
            techniqueHistory.add(StringTag.valueOf(technique == null ? "none" : technique.toString()));
        }
        tag.put("technique_history",techniqueHistory);

        return tag;
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeInt(getMajorRealm());
        buf.writeInt(getMinorRealm());
        buf.writeDouble(getCurrentRealmProgress());
        buf.writeBoolean(isCultivating());
        buf.writeBoolean(currentTechnique != null);
        if(currentTechnique != null) ByteBufUtil.encodeString(buf,currentTechnique.toString());



        buf.writeInt(techniqueHistory.size());
        for(ResourceLocation technique:techniqueHistory){
            buf.writeBoolean(technique != null);
            if(technique != null) ByteBufUtil.encodeString(buf,technique.toString());
        }
        buf.writeInt(techniqueData.size());
        for(ResourceLocation technique:techniqueData.keySet()){
            ITechniqueData data = techniqueData.get(technique);
            buf.writeBoolean(data != null);
            if(data != null){
                ByteBufUtil.encodeString(buf,technique.toString());
                //System.out.println("trying to write data for skill : "+technique.toString());
                techniqueData.get(technique).encode(buf);
            }
        }
        //breakthrough stuff
        buf.writeBoolean(breakingThrough);

    }

    @Override
    public void load(CompoundTag tag,IEntityData entityData) {
        try {
            System.out.println(tag);

            ListTag techniqueData = tag.getList("technique_data",Tag.TAG_COMPOUND);
            ListTag techniqueHistory = tag.getList("technique_history",Tag.TAG_STRING);


            //load techniques
            HashMap<ResourceLocation,ITechniqueData> cachedTechniqueData = new HashMap<>();
            for(int i=0;i<techniqueData.size();i++){
                //TODO
                CompoundTag techniqueDataTag = techniqueData.getCompound(i);
                ResourceLocation techniqueId = ResourceLocation.bySeparator(techniqueDataTag.getString("technique"),':');
                ITechnique technique = AscensionRegistries.getRegistryObject(techniqueId,AscensionRegistries.Techniques.TECHNIQUES_REGISTRY);
                if(technique == null) continue; //technique was most likely deleted
                ITechniqueData techniqueDataInstance = ITechnique.getFromCompound(entityData,technique,techniqueDataTag.getCompound("data"));
                if(techniqueDataInstance != null) cachedTechniqueData.put(techniqueId,techniqueDataInstance);
            }
            //simulate history
            for(int i =0;i<techniqueHistory.size();i++){

                ResourceLocation techniqueId = ResourceLocation.bySeparator(techniqueHistory.getString(i),':');
                ITechnique technique = AscensionRegistries.getRegistryObject(techniqueId,AscensionRegistries.Techniques.TECHNIQUES_REGISTRY);
                if(technique == null) {
                    //remove non used technique data
                    Collection<ResourceLocation> techniques = cachedTechniqueData.keySet();
                    for(ResourceLocation dataTechniqueId : techniques){
                        if(!this.techniqueHistory.contains(dataTechniqueId)){
                            this.techniqueData.remove(dataTechniqueId);
                        }
                    }

                    AscensionCraft.LOGGER.error("error loading technique: {} removing clearing data from realm {} onwards", techniqueHistory.getString(i),i);
                    return;
                };
                if(!entityData.setTechnique(techniqueId,cachedTechniqueData.get(techniqueId))){
                    AscensionCraft.LOGGER.error("failed to set technique {} for simulation of realm {}", techniqueId,majorRealm);
                }
                handleRealmChange(i+1,0,entityData);
            }

            //simulate current realm
            int majorRealm = tag.getInt("major_realm");
            int minorRealm = tag.getInt("minor_realm");
            double progress = tag.getDouble("progress");
            int stability = tag.getInt("stability");
            String rawTechnique = tag.getString("technique");
            if(!rawTechnique.equals("none")){
                ResourceLocation techniqueId = ResourceLocation.bySeparator(tag.getString("technique"),':');
                ITechnique technique = AscensionRegistries.getRegistryObject(techniqueId,AscensionRegistries.Techniques.TECHNIQUES_REGISTRY);
                if(technique == null){
                    AscensionCraft.LOGGER.error("error loading technique: {} removing clearing data from realm {} onwards", techniqueId,majorRealm);
                    return;
                }
                entityData.setTechnique(techniqueId,cachedTechniqueData.get(techniqueId));

                handleRealmChange(majorRealm,minorRealm,entityData);

                this.progress = progress;

            }

        }catch (Exception e){
            AscensionCraft.LOGGER.error("error when trying to load path data for path: "+path,e);

            minorRealm = 0;
            majorRealm = 0;
            cultivating = false;
            progress = 0;
            currentTechnique = null;
            techniqueHistory.clear();
            techniqueData.clear();
            breakingThrough = false;

        }

    }

    @Override
    public void load(RegistryFriendlyByteBuf buf) {
        setMajorRealm(buf.readInt());
        setMinorRealm(buf.readInt());
        setCurrentRealmProgress(buf.readDouble());
        setCultivating(buf.readBoolean());
        if(buf.readBoolean())setCurrentTechnique(ByteBufUtil.readResourceLocation(buf));



        int size = buf.readInt();
        techniqueHistory.clear();
        for(int i=0;i<size;i++){
            if(buf.readBoolean()) techniqueHistory.add(ByteBufUtil.readResourceLocation(buf));
            techniqueHistory.add(null);
        }
        size = buf.readInt();
        techniqueData.clear();
        for(int i =0;i<size;i++){
            if(!buf.readBoolean()) continue;
            ResourceLocation technique = ByteBufUtil.readResourceLocation(buf);
            ITechniqueData techniqueDataInstance = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(technique).fromNetwork(buf);
            techniqueData.put(technique,techniqueDataInstance);
        }
        breakingThrough = buf.readBoolean();

    }
}
