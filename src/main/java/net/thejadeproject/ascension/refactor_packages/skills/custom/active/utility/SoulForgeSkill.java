package net.thejadeproject.ascension.refactor_packages.skills.custom.active.utility;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.textures.ITextureData;
import net.lucent.easygui.gui.textures.TextureData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.common.items.ModItems;
import net.thejadeproject.ascension.common.items.tools.soul_weapon.SoulWeaponHelper;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.data_attachments.attachments.SoulWeaponData;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.gui.elements.info_elements.DescriptionDisplayContainer;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.physiques.IPhysiqueData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastEndData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastResult;
import net.thejadeproject.ascension.refactor_packages.skills.IPersistentSkillData;
import net.thejadeproject.ascension.refactor_packages.skills.castable.*;
import net.thejadeproject.ascension.common.items.tools.soul_weapon.SoulWeaponType;

public class SoulForgeSkill implements ICastableSkill {
    private static final int COOLDOWN_TICKS = 40;

    @Override
    public CastResult canCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) return new CastResult(CastResult.Type.FAILURE);
        if (!hasSoulPath(player)) return new CastResult(CastResult.Type.FAILURE);

        player.getData(ModAttachments.SOUL_WEAPON);

        return new CastResult(CastResult.Type.SUCCESS);
    }

    @Override
    public void initialCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;

        if (!hasSoulPath(player)) {
            SoulWeaponHelper.removeOwnedSoulWeapons(player);
            player.getData(ModAttachments.SOUL_WEAPON).summoned = false;

            player.displayClientMessage(
                    Component.translatable("ascension.skill.soul_forge.no_soul_path"),
                    true
            );
            return;
        }

        SoulWeaponData soulWeaponData = player.getData(ModAttachments.SOUL_WEAPON);

        if (player.isShiftKeyDown()) {
            showSoulWeaponStatus(player, soulWeaponData);
            return;
        }

        if (!soulWeaponData.bound) {
            bindWeapon(player, soulWeaponData);
            return;
        }

        toggleSoulWeapon(player, soulWeaponData);
    }

    private void bindWeapon(ServerPlayer player, SoulWeaponData data) {
        ItemStack held = player.getMainHandItem();
        SoulWeaponType type = SoulWeaponType.fromStack(held);

        if (held.isEmpty() || type == null) {
            player.displayClientMessage(
                    Component.translatable("ascension.skill.soul_forge.no_weapon"),
                    true
            );
            return;
        }

        ItemStack soulWeapon = type.createSoulboundStack();

        SoulWeaponHelper.copyForgedComponents(held, soulWeapon);

        data.bound = true;
        data.weaponType = type.id();
        data.currentGrade = 0;
        data.currentTempering = 0;
        data.summoned = false;
        data.lifetimeMarks = 0;

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        IPathData soulPath = entityData.getPathData(ModPaths.SOUL.getId());
        data.lastSoulMajor = soulPath == null ? 0 : soulPath.getMajorRealm();
        data.lastSoulMinor = soulPath == null ? 0 : soulPath.getMinorRealm();

        ensureWeaponPath(entityData, type.path());

        SoulWeaponHelper.updateSoulWeaponAttributes(soulWeapon, data);
        SoulWeaponHelper.writeSoulWeaponComponent(soulWeapon, player, data);

        data.storedWeapon = soulWeapon.copyWithCount(1);

        held.shrink(1);

        player.displayClientMessage(
                Component.translatable("ascension.skill.soul_forge.bound", type.id()),
                true
        );
    }

    private void ensureWeaponPath(IEntityData entityData, ResourceLocation path) {
        if (entityData.getPathData(path) != null) return;

        entityData.addPathData(
                path,
                AscensionRegistries.Paths.PATHS_REGISTRY
                        .get(path)
                        .freshPathData(entityData)
        );
    }

    private void toggleSoulWeapon(ServerPlayer player, SoulWeaponData data) {
        if (data.summoned) {
            unsummonSoulWeapon(player, data);
        } else {
            summonSoulWeapon(player, data);
        }
    }

    private void summonSoulWeapon(ServerPlayer player, SoulWeaponData data) {
        SoulWeaponHelper.removeOwnedSoulWeapons(player);

        SoulWeaponType type = SoulWeaponType.fromId(data.weaponType);

        if (type == null) {
            player.displayClientMessage(
                    Component.translatable("ascension.skill.soul_forge.no_weapon"),
                    true
            );
            data.summoned = false;
            return;
        }

        ItemStack soulWeapon;

        if (data.storedWeapon.isEmpty()) {
            soulWeapon = type.createSoulboundStack();
        } else if (data.storedWeapon.is(ModItems.SOULBOUND_WEAPON.get())) {
            soulWeapon = type.createSoulboundStack();
            SoulWeaponHelper.copyForgedComponents(data.storedWeapon, soulWeapon);
        } else {
            soulWeapon = data.storedWeapon.copyWithCount(1);
        }

        SoulWeaponHelper.updateSoulWeaponAttributes(soulWeapon, data);
        SoulWeaponHelper.writeSoulWeaponComponent(soulWeapon, player, data);
        data.storedWeapon = soulWeapon.copyWithCount(1);

        if (!player.getInventory().add(soulWeapon)) {
            player.displayClientMessage(Component.literal("Your inventory is full."), true);
            data.summoned = false;
            return;
        }

        data.storedWeapon = soulWeapon.copyWithCount(1);
        data.summoned = true;

        player.displayClientMessage(
                Component.translatable("ascension.skill.soul_forge.summoned"),
                true
        );
    }

    private void unsummonSoulWeapon(ServerPlayer player, SoulWeaponData data) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (!SoulWeaponHelper.isSoulWeapon(stack)) continue;
            if (!SoulWeaponHelper.isOwner(stack, player)) continue;

            SoulWeaponHelper.saveSummonedSoulWeapon(stack, player, data);
            break;
        }

        SoulWeaponHelper.removeOwnedSoulWeapons(player);

        data.summoned = false;

        player.displayClientMessage(
                Component.translatable("ascension.skill.soul_forge.unsummoned"),
                true
        );
    }

    private void unsummonSoulWeaponSilently(ServerPlayer player) {
        SoulWeaponHelper.removeOwnedSoulWeapons(player);
    }

    @Override
    public void onRemoved(IEntityData attachedEntityData, IPersistentSkillData persistentData) {
        if (!(attachedEntityData.getAttachedEntity() instanceof ServerPlayer player)) return;

        SoulWeaponData data = player.getData(ModAttachments.SOUL_WEAPON);
        data.clear();

        SoulWeaponHelper.removeOwnedSoulWeapons(player);

        player.displayClientMessage(
                Component.translatable("ascension.skill.soul_forge.weapon_unbound"),
                true
        );
    }

    private void showSoulWeaponStatus(ServerPlayer player, SoulWeaponData data) {
        if (!data.bound) {
            player.displayClientMessage(
                    Component.translatable("ascension.skill.soul_forge.status.unbound"),
                    false
            );
            return;
        }

        int required = SoulWeaponHelper.getRequiredTempering(data.currentGrade, data.lifetimeMarks);

        player.displayClientMessage(
                Component.translatable(
                        "ascension.skill.soul_forge.status.bound",
                        data.weaponType,
                        data.currentGrade,
                        data.currentTempering,
                        required,
                        data.summoned
                ),
                false
        );
    }

    private boolean hasSoulPath(ServerPlayer player) {
        if (!player.hasData(ModAttachments.ENTITY_DATA)) return false;

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        return entityData.getPathData(ModPaths.SOUL.getId()) != null;
    }


    @Override public CastType getCastType() { return CastType.INSTANT; }
    @Override public int getCooldown(CastEndData castEndData) { return COOLDOWN_TICKS; }
    @Override public boolean continueCasting(int ticksElapsed, Entity caster, ICastData castData) { return false; }

    @Override public Component getTitle(IEntityData entityData) {
        return Component.translatable("ascension.skill.soul_forge");
    }

    @Override public Component getDescription(IEntityData entityData) {
        return Component.translatable("ascension.skill.soul_forge.description");
    }

    @OnlyIn(Dist.CLIENT)
    @Override public ITextureData getIcon(IEntityData entityData) {
        return new TextureData(
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "textures/spells/icon/placeholder.png"),
                16,
                16
        );
    }

    @OnlyIn(Dist.CLIENT)
    @Override public RenderableElement getInformationContainer(UIFrame frame, IEntityData entityData) {
        return new DescriptionDisplayContainer(frame, getTitle(entityData), getDescription(entityData));
    }

    @OnlyIn(Dist.CLIENT)
    @Override public RenderableElement getCastElement(UIFrame frame) { return null; }

    @Override public void onEquip(IEntityData entityData) {}
    @Override public void onUnEquip(IEntityData entityData, IPreCastData preCastData) {}
    @Override public void finalCast(CastEndData reason, Entity caster, ICastData castData) {}
    @Override public void selected(IEntityData entityData) {}
    @Override public void unselected(IEntityData entityData) {}

    @Override public IPreCastData freshPreCastData() { return null; }
    @Override public IPreCastData preCastDataFromCompound(CompoundTag tag) { return null; }
    @Override public IPreCastData preCastDataFromNetwork(RegistryFriendlyByteBuf buf) { return null; }

    @Override public ICastData freshCastData() { return null; }
    @Override public ICastData castDataFromCompound(CompoundTag tag) { return null; }
    @Override public ICastData castDataFromNetwork(RegistryFriendlyByteBuf buf) { return null; }

    @Override public IPersistentSkillData freshPersistentInstance() { return null; }
    @Override public IPersistentSkillData persistentInstanceFromCompound(CompoundTag tag) { return null; }
    @Override public IPersistentSkillData persistentInstanceFromNetwork(RegistryFriendlyByteBuf buf) { return null; }

    @Override public void onAdded(IEntityData attachedEntityData) {}
    @Override public void onFormAdded(IEntityData heldEntity, ResourceLocation form, IPhysiqueData physiqueData) {}
    @Override public void onFormRemoved(IEntityData heldEntity, ResourceLocation form, IPhysiqueData physiqueData) {}
    @Override public void finishedCooldown(IEntityData attachedEntityData, String identifier) {}

    @Override public IPersistentSkillData freshPersistentData(IEntityData heldEntity) { return null; }
    @Override public IPersistentSkillData fromCompound(CompoundTag tag, IEntityData heldEntity) { return null; }
    @Override public IPersistentSkillData fromNetwork(RegistryFriendlyByteBuf buf) { return null; }
}