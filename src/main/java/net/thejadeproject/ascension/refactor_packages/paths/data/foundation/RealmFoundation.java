package net.thejadeproject.ascension.refactor_packages.paths.data.foundation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.custom.FoundationPath;
import net.thejadeproject.ascension.refactor_packages.paths.data.foundation.stability.IStabilityHandler;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;

import java.util.ArrayList;
import java.util.HashMap;

public class RealmFoundation {

    private final ResourceLocation path;
    private final int majorRealm;

    private double foundationProgress;
    private boolean primordial;
    private static final ArrayList<Component> realmNames = new ArrayList<>(){{
        add(Component.translatable("ascension.foundation.realm_0"));
        add(Component.translatable("ascension.foundation.realm_1"));
        add(Component.translatable("ascension.foundation.realm_2"));
        add(Component.translatable("ascension.foundation.realm_3"));
        add(Component.translatable("ascension.foundation.realm_3"));
        add(Component.translatable("ascension.foundation.realm_4"));
    }};
    public RealmFoundation(ResourceLocation path, int majorRealm) {
        this.path = path;
        this.majorRealm = majorRealm;
    }
    public RealmFoundation(ResourceLocation path,int majorRealm,CompoundTag data){
        this.path = path;
        this.majorRealm = majorRealm;

        foundationProgress = data.getDouble("progress");
        primordial = data.getBoolean("primordial");
    }
    public FoundationPath getFoundationPath(){
        return (FoundationPath) AscensionRegistries.getRegistryObject(path,AscensionRegistries.Paths.PATHS_REGISTRY);
    }
    public IStabilityHandler getHandler(){
        return getFoundationPath().getStabilityHandler(majorRealm);
    }
    public int getFoundationPercentage(){
        IStabilityHandler handler = getFoundationPath().getStabilityHandler(majorRealm);
        if(foundationProgress < 0){
            return (int) (handler.getStability(foundationProgress*-1)*-1*100);
        }
        return (int) (handler.getStability(foundationProgress)*100);
    }
    public double getProgressInStage(){
        int stage = getFoundationRealm();
        if(stage == 4 && getFoundationPercentage() == 100) return 1;
        double percentage = getHandler().getStability(foundationProgress);

        return (percentage-(stage*0.25))/0.25;
    }

    public int getFoundationRealm(){
        if(getFoundationPercentage() < 0) return (getFoundationPercentage()*-1 /25)*-1;
        if(getFoundationPercentage() == 100 && primordial) return 5;
        return getFoundationPercentage() / 25;
    }

    public void  setFoundationProgress(double newProgress, IEntityData entityData){
        int currentStage = getFoundationRealm();

        this.foundationProgress = Math.clamp(newProgress,-getHandler().getMaxCultivationTicks(),getHandler().getMaxCultivationTicks());
        int newStage = getFoundationRealm();

        if(currentStage < newStage){

            for(int i = currentStage+1;i<=newStage;i++)getFoundationPath().onFoundationBreakthrough(entityData,majorRealm,i);
        }else if(currentStage > newStage){
            for(int i = currentStage-1;i>=newStage;i--) getFoundationPath().onFoundationDown(entityData,majorRealm,i);
        }
    }
    public double getFoundationProgress(){
        return this.foundationProgress;
    }
    public int getMajorRealm(){return majorRealm;}

    public static Component getRealmName(int realm){
        if(realm < 0) return Component.literal("Broken");

        return realmNames.get(realm);
    }
    public Component getCurrentRealmName(){
        return getRealmName(getFoundationRealm());
    }

    public boolean isPrimordial(){
        return getFoundationPercentage() == 100 && primordial;
    }
    public void setPrimordial(boolean state){
        this.primordial = state;
    }
    public CompoundTag write(){
        CompoundTag tag = new CompoundTag();

        tag.putDouble("progress",foundationProgress);
        tag.putBoolean("primordial",primordial);
        return tag;
    }
    public void encode(RegistryFriendlyByteBuf buf){
        buf.writeDouble(foundationProgress);
        buf.writeBoolean(primordial);

    }
    public void decode(RegistryFriendlyByteBuf buf){
        foundationProgress = buf.readDouble();
        primordial = buf.readBoolean();
    }

    public void reset(IEntityData entityData) {
        setPrimordial(false);
        setFoundationProgress(0, entityData);
    }

}
