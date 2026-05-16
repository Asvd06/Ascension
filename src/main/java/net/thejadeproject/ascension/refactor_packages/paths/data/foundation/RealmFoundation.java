package net.thejadeproject.ascension.refactor_packages.paths.data.foundation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.custom.FoundationPath;
import net.thejadeproject.ascension.refactor_packages.paths.data.foundation.stability.IStabilityHandler;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;

public class RealmFoundation {

    private final ResourceLocation path;
    private final int majorRealm;

    private double foundationProgress;
    private boolean primordial;

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
        //TODO calc the percentage. then find the percentage of that of the stage

        return 0.0;
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

}
