package net.thejadeproject.ascension.refactor_packages.paths.data.foundation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.data.SimplePathData;

import java.util.ArrayList;

public class FoundationPathData extends SimplePathData {


    private RealmFoundation currentFoundation;

    private final ArrayList<RealmFoundation> foundations = new ArrayList<>();
    private final ArrayList<RealmFoundation> cachedFoundations = new ArrayList<>();
    public FoundationPathData(ResourceLocation path) {
        super(path);
        currentFoundation = new RealmFoundation(path,0);
    }

    public RealmFoundation getCurrentFoundation(){
        return currentFoundation;
    }
    public RealmFoundation getFoundationForRealm(int realm){
        return realm >= foundations.size() ? null : foundations.get(realm);
    }


    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        super.encode(buf);

        currentFoundation.encode(buf);

        buf.writeInt(foundations.size());
        for(RealmFoundation foundation : foundations){
            foundation.encode(buf);
        }
    }

    @Override
    public void load(RegistryFriendlyByteBuf buf) {
        super.load(buf);
        currentFoundation = new RealmFoundation(getPath(),getMajorRealm());
        currentFoundation.decode(buf);

        int size =  buf.readInt();
        for(int i = 0; i < size ; i++){
            foundations.add(new RealmFoundation(getPath(),i));
            foundations.getLast().decode(buf);
        }
    }

    @Override
    public CompoundTag write() {
        CompoundTag tag = super.write();
        tag.put("current_foundation",currentFoundation.write());

        ListTag previousFoundations = new ListTag();

        for(RealmFoundation foundation : foundations){
            previousFoundations.add(foundation.write());
        }
        tag.put("foundations",previousFoundations);

        return tag;
    }

    @Override
    public void load(CompoundTag tag, IEntityData entityData) {
        ListTag prev = tag.getList("foundations", Tag.TAG_COMPOUND);
        for(int i = 0;i<prev.size();i++){
            cachedFoundations.add(new RealmFoundation(getPath(),i,prev.getCompound(i)));
        }
        cachedFoundations.add(new RealmFoundation(getPath(),prev.size(),tag.getCompound("current_foundation")));
        currentFoundation = cachedFoundations.removeFirst();
        int stage = currentFoundation.getFoundationRealm();
        if(stage < 0){
            for(int i = -1; i >=stage;i--){
                currentFoundation.getFoundationPath().onFoundationDown(entityData,0,i);
            }
        }else{
            for(int i = 1; i<=stage; i ++){
                currentFoundation.getFoundationPath().onFoundationBreakthrough(entityData,0,i);
            }
        }
        super.load(tag, entityData);

        cachedFoundations.clear();
    }

    @Override
    public void majorRealmUp(IEntityData entityData) {
        foundations.add(currentFoundation);
        if(cachedFoundations.isEmpty()) currentFoundation = new RealmFoundation(getPath(),getMajorRealm());
        else {
            currentFoundation = cachedFoundations.removeFirst();
            int stage = currentFoundation.getFoundationRealm();
            if(stage < 0){
                for(int i = -1; i >=stage;i--){
                    currentFoundation.getFoundationPath().onFoundationDown(entityData,currentFoundation.getMajorRealm(),i);
                }
            }else{
                for(int i = 1; i<=stage; i ++){
                    currentFoundation.getFoundationPath().onFoundationBreakthrough(entityData,currentFoundation.getMajorRealm(),i);
                }
            }
        }

        super.majorRealmUp(entityData);

    }

    @Override
    public void majorRealmDown(IEntityData entityData) {
        //TODO make sure to call foundation removed here
        RealmFoundation oldFoundation = currentFoundation;
        currentFoundation = foundations.removeLast();
        if(oldFoundation.getFoundationProgress()<0) currentFoundation.setFoundationProgress(currentFoundation.getFoundationProgress()+oldFoundation.getFoundationProgress(),entityData);
        super.majorRealmDown(entityData);
    }

    public void resetCurrentFoundation(IEntityData entityData) {
        currentFoundation.reset(entityData);
    }

    public void resetAllFoundations(IEntityData entityData) {
        currentFoundation.reset(entityData);

        for (RealmFoundation foundation : foundations) {
            foundation.reset(entityData);
        }
    }

}
