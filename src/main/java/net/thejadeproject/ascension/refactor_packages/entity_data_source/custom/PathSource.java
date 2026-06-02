package net.thejadeproject.ascension.refactor_packages.entity_data_source.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.entity_data_source.IEntityDataSource;
import net.thejadeproject.ascension.refactor_packages.entity_data_source.IEntityDataSourceContainer;
import net.thejadeproject.ascension.refactor_packages.events.path_data.TryRemovePathDataEvent;
import net.thejadeproject.ascension.refactor_packages.paths.IPath;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.util.ByteBufUtil;

public class PathSource implements IEntityDataSource {

    @Override
    public void onAdded(IEntityData entityData, IEntityDataSourceContainer container) {
        if (!(container instanceof PathSourceContainer pathContainer)) return;

        ResourceLocation path = pathContainer.getPath();

        if (entityData.hasPath(path)) return;

        IPath pathInstance = AscensionRegistries.Paths.PATHS_REGISTRY.get(path);

        if (pathInstance == null) {return;}

        entityData.addPathData(
                path,
                pathInstance.freshPathData(entityData)
        );
    }

    @Override
    public void onRemoved(IEntityData entityData, IEntityDataSourceContainer container) {
        if (!(container instanceof PathSourceContainer pathContainer)) return;
        if (!pathContainer.shouldRemovePathOnSourceRemoved()) return;

        ResourceLocation path = pathContainer.getPath();

        TryRemovePathDataEvent event = new TryRemovePathDataEvent(entityData, path);
        NeoForge.EVENT_BUS.post(event);

        if (!event.isCanceled()) {
            entityData.removePath(path);
        }
    }

    @Override
    public void tick(IEntityData entityData, IEntityDataSourceContainer container) {
    }

    public static PathSourceContainer create(ResourceLocation path, ResourceLocation identifier) {
        return new PathSourceContainer(path, identifier, false);
    }

    public static PathSourceContainer create(ResourceLocation path, ResourceLocation identifier, boolean removePathOnSourceRemoved) {
        return new PathSourceContainer(path, identifier, removePathOnSourceRemoved);
    }

    @Override
    public IEntityDataSourceContainer fromCompound(CompoundTag tag) {
        ResourceLocation path = ResourceLocation.parse(tag.getString("path"));
        ResourceLocation identifier = ResourceLocation.parse(tag.getString("instance_identifier"));
        boolean removePathOnSourceRemoved = tag.getBoolean("remove_path_on_source_removed");

        return new PathSourceContainer(path, identifier, removePathOnSourceRemoved);
    }

    @Override
    public IEntityDataSourceContainer fromNetwork(RegistryFriendlyByteBuf buf) {
        ResourceLocation path = ByteBufUtil.readResourceLocation(buf);
        ResourceLocation identifier = ByteBufUtil.readResourceLocation(buf);
        boolean removePathOnSourceRemoved = buf.readBoolean();

        return new PathSourceContainer(path, identifier, removePathOnSourceRemoved);
    }
}