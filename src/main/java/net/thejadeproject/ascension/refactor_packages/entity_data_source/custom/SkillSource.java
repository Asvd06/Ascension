package net.thejadeproject.ascension.refactor_packages.entity_data_source.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.entity_data_source.IEntityDataSource;
import net.thejadeproject.ascension.refactor_packages.entity_data_source.IEntityDataSourceContainer;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
import net.thejadeproject.ascension.refactor_packages.util.ByteBufUtil;

public class SkillSource implements IEntityDataSource {
    @Override
    public void onAdded(IEntityData entityData, IEntityDataSourceContainer container) {
        if (container instanceof SkillSourceContainer skillSourceContainer) {
            entityData.giveSkill(skillSourceContainer.getSkill(), ModForms.MORTAL_VESSEL.getId());
        }
    }

    @Override
    public void onRemoved(IEntityData entityData, IEntityDataSourceContainer container) {
        if (container instanceof SkillSourceContainer skillSourceContainer) {
            entityData.removeSkill(skillSourceContainer.getSkill(), ModForms.MORTAL_VESSEL.getId());
        }
    }

    public static SkillSourceContainer create(ResourceLocation skill, ResourceLocation identifier) {
        return new SkillSourceContainer(skill, identifier);
    }

    @Override
    public void tick(IEntityData entityData, IEntityDataSourceContainer container) {
    }

    @Override
    public IEntityDataSourceContainer fromCompound(CompoundTag tag) {
        ResourceLocation skill = ResourceLocation.parse(tag.getString("skill"));
        ResourceLocation identifier = ResourceLocation.parse(tag.getString("instance_identifier"));

        return new SkillSourceContainer(skill, identifier);
    }

    @Override
    public IEntityDataSourceContainer fromNetwork(RegistryFriendlyByteBuf buf) {
        ResourceLocation skill = ByteBufUtil.readResourceLocation(buf);
        ResourceLocation identifier = ByteBufUtil.readResourceLocation(buf);

        return new SkillSourceContainer(skill, identifier);
    }
}