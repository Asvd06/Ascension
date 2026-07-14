package net.thejadeproject.ascension.refactor_packages.handlers.player;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.common.items.tools.data.soul_tool.SoulToolComponent;
import net.thejadeproject.ascension.common.items.tools.soul_tool.ISoulboundTool;
import net.thejadeproject.ascension.common.items.tools.soul_tool.SoulToolEffectHelper;
import net.thejadeproject.ascension.common.items.tools.soul_tool.SoulToolHelper;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.data_attachments.attachments.SoulToolData;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;

@EventBusSubscriber(modid = AscensionCraft.MOD_ID)
public final class SoulToolHandler {

    private SoulToolHandler() {
    }

    @SubscribeEvent
    public static void onAttackEntity(
            AttackEntityEvent event
    ) {
        if (!(event.getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        if (!SoulToolHelper.isSoulTool(
                player.getMainHandItem()
        )) {
            return;
        }

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onSoulToolTossed(
            ItemTossEvent event
    ) {
        if (!(event.getPlayer()
                instanceof ServerPlayer player)) {
            return;
        }

        ItemStack stack =
                event.getEntity().getItem();

        if (!SoulToolHelper.isSoulTool(stack)) {
            return;
        }

        SoulToolData data =
                player.getData(ModAttachments.SOUL_TOOL);

        boolean currentManifestation =
                SoulToolHelper.isCurrentManifestation(
                        stack,
                        player,
                        data
                );

        event.setCanceled(true);
        event.getEntity().discard();

        if (currentManifestation) {
            data.summoned = false;
            SoulToolHelper.removeAllSoulTools(player);

            player.displayClientMessage(
                    Component.translatable(
                            "ascension.skill.soul_implement.unsummoned"
                    ),
                    true
            );
        }
    }

    @SubscribeEvent
    public static void onSoulToolOwnerDeath(
            LivingDeathEvent event
    ) {
        if (!(event.getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        SoulToolData data =
                player.getData(ModAttachments.SOUL_TOOL);

        SoulToolHelper.removeAllSoulTools(player);
        data.summoned = false;
    }

    @SubscribeEvent
    public static void onPlayerTick(
            PlayerTickEvent.Post event
    ) {
        if (!(event.getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        if (player.tickCount % 20 != 0) {
            return;
        }

        SoulToolData data =
                player.getData(ModAttachments.SOUL_TOOL);

        syncSoulRealm(player, data);

        boolean removedFromExternalContainer =
                removeExternalSoulTools(
                        player,
                        data
                );

        if (removedFromExternalContainer) {
            data.summoned = false;
            SoulToolHelper.removeAllSoulTools(player);

            player.displayClientMessage(
                    Component.translatable(
                            "ascension.skill.soul_implement.returned_from_container"
                    ),
                    true
            );
        }

        SoulToolHelper.reconcile(player, data);
    }

    @SubscribeEvent
    public static void onPlayerLogin(
            PlayerEvent.PlayerLoggedInEvent event
    ) {
        if (!(event.getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        SoulToolData data =
                player.getData(ModAttachments.SOUL_TOOL);

        syncSoulRealm(player, data);
        SoulToolHelper.reconcile(player, data);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(
            PlayerEvent.PlayerRespawnEvent event
    ) {
        if (!(event.getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        SoulToolData data =
                player.getData(ModAttachments.SOUL_TOOL);

        syncSoulRealm(player, data);
        SoulToolHelper.reconcile(player, data);
    }

    @SubscribeEvent
    public static void onContainerClose(
            PlayerContainerEvent.Close event
    ) {
        if (!(event.getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        SoulToolData data =
                player.getData(ModAttachments.SOUL_TOOL);

        boolean removed =
                removeExternalSoulTools(player, data);

        if (!removed) return;

        data.summoned = false;
        SoulToolHelper.removeAllSoulTools(player);

        player.displayClientMessage(
                Component.translatable(
                        "ascension.skill.soul_implement.returned_from_container"
                ),
                true
        );
    }

    private static void syncSoulRealm(
            ServerPlayer player,
            SoulToolData data
    ) {
        if (!player.hasData(
                ModAttachments.ENTITY_DATA
        )) {
            return;
        }

        IEntityData entityData =
                player.getData(
                        ModAttachments.ENTITY_DATA
                );

        IPathData soulPath =
                entityData.getPathData(
                        ModPaths.SOUL.getId()
                );

        int soulMajor =
                soulPath == null
                        ? 0
                        : soulPath.getMajorRealm();

        int soulMinor =
                soulPath == null
                        ? 0
                        : soulPath.getMinorRealm();

        SoulToolHelper.syncSoulRealmProgress(
                data,
                soulMajor,
                soulMinor
        );
    }

    private static boolean removeExternalSoulTools(
            ServerPlayer player,
            SoulToolData data
    ) {
        boolean removedCurrentManifestation = false;
        boolean removedAnything = false;

        for (var slot : player.containerMenu.slots) {
            if (slot.container instanceof Inventory) {
                continue;
            }

            ItemStack stack = slot.getItem();

            if (!SoulToolHelper.isSoulTool(stack)) {
                continue;
            }

            if (SoulToolHelper.isCurrentManifestation(
                    stack,
                    player,
                    data
            )) {
                removedCurrentManifestation = true;
            }

            slot.set(ItemStack.EMPTY);
            removedAnything = true;
        }

        if (removedAnything) {
            player.containerMenu.broadcastChanges();
        }

        return removedCurrentManifestation;
    }

    @SubscribeEvent
    public static void onSoulToolBreakSpeed(
            PlayerEvent.BreakSpeed event
    ) {
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();

        if (!(stack.getItem()
                instanceof ISoulboundTool soulTool)) {
            return;
        }

        SoulToolComponent component =
                stack.get(ModDataComponents.SOUL_TOOL.get());

        if (component == null
                || !component.owner().equals(
                player.getUUID()
        )
                || component.type()
                != soulTool.getSoulToolType()
                || !component.core().isFormed()) {
            return;
        }

        event.setNewSpeed(
                SoulToolEffectHelper.modifyBreakSpeed(player, component, event.getNewSpeed()
                )
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSoulToolBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer()
                instanceof ServerPlayer player)) {
            return;
        }

        SoulToolData data = player.getData(ModAttachments.SOUL_TOOL);

        if (!SoulToolEffectHelper.shouldQueueVeinMining(player, data, event.getState()
        )) {
            return;
        }

        SoulToolEffectHelper.queueVeinMining(player, event.getPos(), event.getState()
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSoulToolBlockDrops(
            BlockDropsEvent event) {
        if (!(event.getBreaker()
                instanceof ServerPlayer player)) {
            return;
        }

        SoulToolData data =
                player.getData(ModAttachments.SOUL_TOOL);

        if (!SoulToolHelper.isCurrentManifestation(event.getTool(), player, data
        )) {
            return;
        }

        SoulToolEffectHelper.handleBlockDrops(event, player, data
        );
    }
}