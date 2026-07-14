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
import net.thejadeproject.ascension.common.items.tools.soul_tool.SoulToolHelper;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.data_attachments.attachments.SoulToolData;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.gui.elements.info_elements.DescriptionDisplayContainer;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.physiques.IPhysiqueData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastEndData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastResult;
import net.thejadeproject.ascension.refactor_packages.skills.IPersistentSkillData;
import net.thejadeproject.ascension.refactor_packages.skills.castable.CastType;
import net.thejadeproject.ascension.refactor_packages.skills.castable.ICastData;
import net.thejadeproject.ascension.refactor_packages.skills.castable.ICastableSkill;
import net.thejadeproject.ascension.refactor_packages.skills.castable.IPreCastData;

public class SoulImplementSkill implements ICastableSkill {

    private static final int REQUIRED_SOUL_REALM = 3;
    private static final int COOLDOWN_TICKS = 10;
    private static final int DISSOLVE_CONFIRMATION_TICKS = 100;

    @Override
    public CastResult canCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) {
            return new CastResult(
                    CastResult.Type.FAILURE
            );
        }

        if (!hasRequiredSoulRealm(player)) {
            return new CastResult(
                    CastResult.Type.FAILURE
            );
        }

        player.getData(ModAttachments.SOUL_TOOL);

        return new CastResult(
                CastResult.Type.SUCCESS
        );
    }

    @Override
    public void initialCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) {
            return;
        }

        if (player.level().isClientSide()) {
            return;
        }

        SoulToolData data =
                player.getData(ModAttachments.SOUL_TOOL);

        if (!hasRequiredSoulRealm(player)) {
            SoulToolHelper.unsummon(player, data);

            player.displayClientMessage(
                    Component.translatable(
                            "ascension.skill.soul_implement.realm_too_low",
                            REQUIRED_SOUL_REALM
                    ),
                    true
            );

            return;
        }

        syncCurrentSoulRealm(player, data);

        if (player.isShiftKeyDown()) {
            handleSneakCast(player, data);
            return;
        }

        if (!data.bound) {
            bindAndSummon(player, data);
            return;
        }

        toggleManifestation(player, data);
    }

    private void bindAndSummon(ServerPlayer player, SoulToolData data) {
        IPathData soulPath = getSoulPath(player);

        int major =
                soulPath == null
                        ? 0
                        : soulPath.getMajorRealm();

        int minor =
                soulPath == null
                        ? 0
                        : soulPath.getMinorRealm();

        data.bind(major, minor);

        player.displayClientMessage(
                Component.translatable(
                        "ascension.skill.soul_implement.bound"
                ),
                true
        );

        if (SoulToolHelper.summon(player, data)) {
            player.displayClientMessage(
                    Component.translatable(
                            "ascension.skill.soul_implement.summoned"
                    ),
                    true
            );
        } else {
            player.displayClientMessage(
                    Component.translatable(
                            "ascension.skill.soul_implement.inventory_full"
                    ),
                    true
            );
        }
    }

    private void toggleManifestation(ServerPlayer player, SoulToolData data) {
        if (data.summoned) {
            SoulToolHelper.unsummon(player, data);

            player.displayClientMessage(
                    Component.translatable(
                            "ascension.skill.soul_implement.unsummoned"
                    ),
                    true
            );

            return;
        }

        if (SoulToolHelper.summon(player, data)) {
            player.displayClientMessage(
                    Component.translatable(
                            "ascension.skill.soul_implement.summoned"
                    ),
                    true
            );
        } else {
            player.displayClientMessage(
                    Component.translatable(
                            "ascension.skill.soul_implement.inventory_full"
                    ),
                    true
            );
        }
    }

    private void handleSneakCast(ServerPlayer player, SoulToolData data) {
        ItemStack mainHand = player.getMainHandItem();

        ItemStack offHand = player.getOffhandItem();

        if (SoulToolHelper.isCurrentManifestation(mainHand, player, data)) {
            if (offHand.isEmpty()) {
                SoulToolHelper.cycleHeldForm(player, data);
            } else {
                SoulToolHelper.tryAssimilate(player, data, mainHand, offHand);
            }

            return;
        }

        if (mainHand.isEmpty()
                && SoulToolHelper.isCurrentManifestation(
                offHand,
                player,
                data
        )) {
            attemptDissolve(player, data);
            return;
        }

        showStatus(player, data);
    }

    private void attemptDissolve(ServerPlayer player, SoulToolData data) {
        long currentTick = player.level().getGameTime();

        if (data.dissolveConfirmUntilTick < currentTick) {
            data.dissolveConfirmUntilTick = currentTick + DISSOLVE_CONFIRMATION_TICKS;

            player.displayClientMessage(
                    Component.translatable(
                            "ascension.skill.soul_implement.dissolve_warning"
                    ),
                    true
            );

            return;
        }

        SoulToolHelper.removeAllSoulTools(player);
        data.clear();

        player.displayClientMessage(
                Component.translatable(
                        "ascension.skill.soul_implement.dissolved"
                ),
                true
        );
    }

    private void showStatus(ServerPlayer player, SoulToolData data) {
        if (!data.bound) {
            player.displayClientMessage(
                    Component.translatable(
                            "ascension.skill.soul_implement.status.unbound"
                    ),
                    false
            );

            return;
        }

        player.displayClientMessage(
                Component.translatable(
                        "ascension.skill.soul_implement.status.bound",
                        Component.translatable(
                                data.activeType.translationKey()
                        ),
                        Component.translatable(
                                data.core.translationKey()
                        ),
                        data.summoned
                ),
                false
        );
    }

    private void syncCurrentSoulRealm(ServerPlayer player, SoulToolData data) {
        IPathData soulPath = getSoulPath(player);

        int major = soulPath == null ? 0 : soulPath.getMajorRealm();
        int minor = soulPath == null ? 0 : soulPath.getMinorRealm();

        SoulToolHelper.syncSoulRealmProgress(data, major, minor);
    }

    private boolean hasRequiredSoulRealm(ServerPlayer player) {
        IPathData soulPath = getSoulPath(player);
        return soulPath != null && soulPath.getMajorRealm() >= REQUIRED_SOUL_REALM;
    }

    private IPathData getSoulPath(ServerPlayer player) {
        if (!player.hasData(ModAttachments.ENTITY_DATA)) {
            return null;
        }

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        return entityData.getPathData(ModPaths.SOUL.getId());
    }

    @Override
    public void onRemoved(IEntityData attachedEntityData, IPersistentSkillData persistentData) {
        if (!(attachedEntityData.getAttachedEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        SoulToolData data = player.getData(ModAttachments.SOUL_TOOL);
        boolean hadImplement = data.bound || data.summoned;
        SoulToolHelper.removeAllSoulTools(player);
        data.clear();

        if (hadImplement) {
            player.displayClientMessage(
                    Component.translatable(
                            "ascension.skill.soul_implement.removed"
                    ),
                    true
            );
        }
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @Override
    public int getCooldown(CastEndData castEndData) {
        return COOLDOWN_TICKS;
    }

    @Override
    public boolean continueCasting(int ticksElapsed, Entity caster, ICastData castData) {
        return false;
    }

    @Override
    public Component getTitle(IEntityData entityData) {
        return Component.translatable("ascension.skill.soul_implement");
    }

    @Override
    public Component getDescription(IEntityData entityData) {
        return Component.translatable("ascension.skill.soul_implement.description");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ITextureData getIcon(IEntityData entityData) {
        return new TextureData(
                ResourceLocation.fromNamespaceAndPath(
                        AscensionCraft.MOD_ID,
                        "textures/spells/icon/placeholder.png"
                ),
                16,
                16
        );
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public RenderableElement getInformationContainer(UIFrame frame, IEntityData entityData) {
        return new DescriptionDisplayContainer(
                frame,
                getTitle(entityData),
                getDescription(entityData)
        );
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public RenderableElement getCastElement(UIFrame frame) {
        return null;
    }

    @Override
    public void onEquip(IEntityData entityData) {}
    @Override
    public void onUnEquip(IEntityData entityData, IPreCastData preCastData) {}
    @Override
    public void finalCast(CastEndData reason, Entity caster, ICastData castData) {}
    @Override
    public void selected(IEntityData entityData) {}
    @Override
    public void unselected(IEntityData entityData) {}
    @Override
    public IPreCastData freshPreCastData() {return null;}
    @Override
    public IPreCastData preCastDataFromCompound(CompoundTag tag) {return null;}
    @Override
    public IPreCastData preCastDataFromNetwork(RegistryFriendlyByteBuf buf) {return null;}
    @Override
    public ICastData freshCastData() {return null;}
    @Override
    public ICastData castDataFromCompound(CompoundTag tag) {return null;}
    @Override
    public ICastData castDataFromNetwork(RegistryFriendlyByteBuf buf) {return null;}
    @Override
    public IPersistentSkillData freshPersistentInstance() {return null;}
    @Override
    public IPersistentSkillData persistentInstanceFromCompound(CompoundTag tag) {return null;}
    @Override
    public IPersistentSkillData persistentInstanceFromNetwork(RegistryFriendlyByteBuf buf) {return null;}
    @Override
    public void onAdded(IEntityData attachedEntityData) {}
    @Override
    public void onFormAdded(IEntityData heldEntity, ResourceLocation form, IPhysiqueData physiqueData) {}
    @Override
    public void onFormRemoved(IEntityData heldEntity, ResourceLocation form, IPhysiqueData physiqueData) {}
    @Override
    public void finishedCooldown(IEntityData attachedEntityData, String identifier) {}
    @Override
    public IPersistentSkillData freshPersistentData(IEntityData heldEntity) {return null;}
    @Override
    public IPersistentSkillData fromCompound(CompoundTag tag, IEntityData heldEntity) {return null;}
    @Override
    public IPersistentSkillData fromNetwork(RegistryFriendlyByteBuf buf) {return null;}
}