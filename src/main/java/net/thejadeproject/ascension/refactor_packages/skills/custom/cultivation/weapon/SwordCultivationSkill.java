package net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.weapon;

import net.lucent.easygui.gui.textures.ITextureData;
import net.lucent.easygui.gui.textures.TextureData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.physiques.IPhysiqueData;
import net.thejadeproject.ascension.refactor_packages.skills.IPersistentSkillData;
import net.thejadeproject.ascension.refactor_packages.skills.ISkill;

public class SwordCultivationSkill implements ISkill {
    @Override
    public void onAdded(IEntityData attachedEntityData) {

    }

    @Override
    public void onRemoved(IEntityData attachedEntityData, IPersistentSkillData persistentData) {

    }

    @Override
    public void onFormAdded(IEntityData heldEntity, ResourceLocation form, IPhysiqueData physiqueData) {

    }

    @Override
    public void onFormRemoved(IEntityData heldEntity, ResourceLocation form, IPhysiqueData physiqueData) {

    }

    @Override
    public void finishedCooldown(IEntityData attachedEntityData, String identifier) {

    }

    @Override
    public IPersistentSkillData freshPersistentData(IEntityData heldEntity) {
        return null;
    }

    @Override
    public IPersistentSkillData fromCompound(CompoundTag tag, IEntityData heldEntity) {
        return null;
    }

    @Override
    public IPersistentSkillData fromNetwork(RegistryFriendlyByteBuf buf) {
        return null;
    }

    @Override
    public ITextureData getIcon() {
        return new TextureData(
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"textures/spells/icon/placeholder.png"),
                16,16
        );
    }

    @Override
    public Component getTitle() {
        return Component.translatable("ascension.skill.sword_cultivation_skill");
    }

    @Override
    public Component getDescription() {
        return Component.translatable("ascension.skill.sword_cultivation_skill.description");
    }
}
