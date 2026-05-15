package net.thejadeproject.ascension.refactor_packages.skills.custom.passive.weapon.projections.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.thejadeproject.ascension.refactor_packages.skills.castable.IPreCastData;

public class ProjectionTogglePreCastData implements IPreCastData {

    private boolean active;

    public void toggle(){
        active = !active;
    }
    public boolean isActive(){
        return active;
    }
    @Override
    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("active",active);
        return tag;
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(active);
    }
    public ProjectionTogglePreCastData decode(RegistryFriendlyByteBuf buf){
        active = buf.readBoolean();
        return this;
    }
    public ProjectionTogglePreCastData read(CompoundTag tag){
        active = tag.getBoolean("active");
        return this;
    }
}
