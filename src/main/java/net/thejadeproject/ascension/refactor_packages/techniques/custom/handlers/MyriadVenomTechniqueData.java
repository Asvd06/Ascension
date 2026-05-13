package net.thejadeproject.ascension.refactor_packages.techniques.custom.handlers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.PathData;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;

public class MyriadVenomTechniqueData implements ITechniqueData {

    private static final int[] REALM_CONSUMPTION_GATES = {0, 10, 30, 60};

    private int totalConsumptionCount = 0;
    private IEntityData entityData;

    public MyriadVenomTechniqueData(IEntityData entityData) {
        this.entityData = entityData;
    }

    public MyriadVenomTechniqueData(IEntityData entityData, CompoundTag tag) {
        this.entityData = entityData;
        this.totalConsumptionCount = tag.getInt("totalConsumptionCount");
    }

    public MyriadVenomTechniqueData(IEntityData entityData, RegistryFriendlyByteBuf buf) {
        this.entityData = entityData;
        this.totalConsumptionCount = buf.readInt();
    }

    public void setEntityData(IEntityData entityData) {
        this.entityData = entityData;
    }

    public IEntityData getEntityData() {
        return entityData;
    }

    public int getCurrentMajorRealm() {
        if (entityData == null) return 0;
        PathData pathData = entityData.getPathData(ModPaths.POISON.getId());
        if (pathData == null) return 0;
        return pathData.getMajorRealm();
    }

    public void incrementConsumption() {
        totalConsumptionCount++;
    }

    public int getTotalConsumptionCount() {
        return totalConsumptionCount;
    }

    public boolean hasMetGateForNextRealm() {
        int nextRealm = getCurrentMajorRealm() + 1;
        int index = nextRealm - 1;
        if (index < 0 || index >= REALM_CONSUMPTION_GATES.length) return true;
        return totalConsumptionCount >= REALM_CONSUMPTION_GATES[index];
    }

    public int getGateForNextRealm() {
        int nextRealm = getCurrentMajorRealm() + 1;
        int index = nextRealm - 1;
        if (index < 0 || index >= REALM_CONSUMPTION_GATES.length) return 0;
        return REALM_CONSUMPTION_GATES[index];
    }

    @Override
    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("totalConsumptionCount", totalConsumptionCount);
        return tag;
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeInt(totalConsumptionCount);
    }
}