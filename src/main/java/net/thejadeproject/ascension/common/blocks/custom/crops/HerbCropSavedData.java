package net.thejadeproject.ascension.common.blocks.custom.crops;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

public class HerbCropSavedData extends SavedData {

    private static final String DATA_NAME = "ascension_herb_crops";
    private static final SavedData.Factory<HerbCropSavedData> FACTORY =
            new SavedData.Factory<>(HerbCropSavedData::new, HerbCropSavedData::load);

    private final Map<Long, CropData> crops = new HashMap<>();

    public static HerbCropSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public static HerbCropSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        HerbCropSavedData data = new HerbCropSavedData();
        ListTag entries = tag.getList("crops", Tag.TAG_COMPOUND);

        for (int i = 0; i < entries.size(); i++) {
            CompoundTag entry = entries.getCompound(i);
            data.crops.put(
                    entry.getLong("pos"),
                    new CropData(entry.getLong("grown_since"), entry.getInt("quality"))
            );
        }

        return data;
    }

    public void store(BlockPos pos, long grownSince, int quality) {
        crops.put(pos.asLong(), new CropData(grownSince, quality));
        setDirty();
    }

    public CropData remove(BlockPos pos) {
        CropData removed = crops.remove(pos.asLong());
        if (removed != null) {
            setDirty();
        }
        return removed;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag entries = new ListTag();

        for (Map.Entry<Long, CropData> crop : crops.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putLong("pos", crop.getKey());
            entry.putLong("grown_since", crop.getValue().grownSince());
            entry.putInt("quality", crop.getValue().quality());
            entries.add(entry);
        }

        tag.put("crops", entries);
        return tag;
    }

    public record CropData(long grownSince, int quality) {}
}
