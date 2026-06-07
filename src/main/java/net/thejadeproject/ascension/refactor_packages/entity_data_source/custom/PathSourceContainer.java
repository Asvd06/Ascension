package net.thejadeproject.ascension.refactor_packages.entity_data_source.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.entity_data_source.IEntityDataSource;
import net.thejadeproject.ascension.refactor_packages.entity_data_source.IEntityDataSourceContainer;
import net.thejadeproject.ascension.refactor_packages.entity_data_source.ModDataSources;
import net.thejadeproject.ascension.refactor_packages.util.ByteBufUtil;

public class PathSourceContainer implements IEntityDataSourceContainer {
    private final ResourceLocation path;
    private final ResourceLocation instanceIdentifier;
    private final boolean removePathOnSourceRemoved;

    public PathSourceContainer(ResourceLocation path, ResourceLocation instanceIdentifier) {
        this(path, instanceIdentifier, false);
    }

    public PathSourceContainer(ResourceLocation path, ResourceLocation instanceIdentifier, boolean removePathOnSourceRemoved) {
        this.path = path;
        this.instanceIdentifier = instanceIdentifier;
        this.removePathOnSourceRemoved = removePathOnSourceRemoved;
    }

    public ResourceLocation getPath() {
        return path;
    }

    public boolean shouldRemovePathOnSourceRemoved() {
        return removePathOnSourceRemoved;
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        ByteBufUtil.encodeString(buf, path.toString());
        ByteBufUtil.encodeString(buf, instanceIdentifier.toString());
        buf.writeBoolean(removePathOnSourceRemoved);
    }

    @Override
    public void write(CompoundTag tag) {
        tag.putString("path", path.toString());
        tag.putString("instance_identifier", instanceIdentifier.toString());
        tag.putBoolean("remove_path_on_source_removed", removePathOnSourceRemoved);
    }

    @Override
    public ResourceLocation getInstanceIdentifier() {
        return instanceIdentifier;
    }

    @Override
    public IEntityDataSource getDataSource() {
        return ModDataSources.PATH_SOURCE.get();
    }
}