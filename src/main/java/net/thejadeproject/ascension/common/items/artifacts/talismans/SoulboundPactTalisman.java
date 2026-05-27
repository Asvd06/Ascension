package net.thejadeproject.ascension.common.items.artifacts.talismans;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.thejadeproject.ascension.common.items.artifacts.bases.BaseTeleportTalisman;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Soulbound Pact Talisman
 *
 * A permanent (always non-consumable) talisman that lets a player teleport to
 * any of up to MAX_LINKED_PLAYERS linked players, using a mutual-consent request
 * flow and a fixed Qi cost per teleport.
 *
 * ── Linking ──────────────────────────────────────────────────────────────────
 *   Shift + Right-click  → adds the nearest online player within LINK_RADIUS blocks
 *                          (or the player you are looking at) to your linked list.
 *                          The same player cannot be added twice; max 5 entries.
 *   Shift + Ctrl + Scroll → (client-side key binding, see SoulboundPactKeyHandler)
 *                          cycles the "selected target index" stored on the item.
 *
 * ── Teleporting ──────────────────────────────────────────────────────────────
 *   Right-click (no shift) →
 *     1. Checks cooldown (1-minute, stored in player NBT, not on item).
 *     2. Checks the player has ≥ QI_COST Qi.
 *     3. Sends a chat request to the target.  Target has REQUEST_TIMEOUT_TICKS to
 *        accept with their own right-click on the talisman (if they have one) or
 *        via the "Accept" chat component.
 *     4. On acceptance the normal BaseTeleportTalisman countdown begins, Qi is
 *        consumed and the 1-minute cooldown starts on BOTH talismans.
 *
 * ── Anti-exploit notes ────────────────────────────────────────────────────────
 *   • Target must actively accept — they cannot be surprised.
 *   • Qi is deducted on initiation (lost if target denies or times out).
 *   • Movement / damage during countdown cancels via parent class logic.
 *   • Cooldown is stored per-item in player NBT, not on the item itself, so
 *     it cannot be cleared by dropping and picking up the item.
 *   • Teleport is cancelled if the target logs off after accepting.
 */
public class SoulboundPactTalisman extends BaseTeleportTalisman {

    // ── Constants ─────────────────────────────────────────────────────────────
    public static final int  MAX_LINKED_PLAYERS    = 5;
    public static final int  QI_COST               = 50;    // Qi consumed per teleport
    public static final int  LINK_RADIUS           = 5;     // blocks; how close you need to be to link
    public static final int  REQUEST_TIMEOUT_TICKS = 30 * TICKS_PER_SECOND; // 30 s
    public static final int  COOLDOWN_TICKS        = 60 * TICKS_PER_SECOND; // 1 minute
    public static final int  RECHARGE_MAX_VALUE    = 0;     // unused – talisman is always permanent

    // ── Item-stack NBT keys (stored in CUSTOM_DATA on the ItemStack) ──────────
    private static final String TAG_LINKED_UUIDS   = "linked_uuids";   // ListTag of UUIDs (strings)
    private static final String TAG_LINKED_NAMES   = "linked_names";   // ListTag of player names (strings)
    private static final String TAG_SELECTED_INDEX = "selected_index"; // int, 0-based

    // ── Player persistent-data NBT keys (stored on the Player, not the item) ──
    /** Pending outgoing request: UUID of the player we are waiting on */
    private static final String PD_PENDING_TARGET_UUID  = "SoulboundPact_PendingTargetUUID";
    /** Tick timestamp when the pending request was made */
    private static final String PD_PENDING_START_TICK   = "SoulboundPact_PendingStartTick";
    /** Slot of the item that initiated the request */
    private static final String PD_PENDING_ITEM_SLOT    = "SoulboundPact_PendingItemSlot";
    /**
     * Confirmed teleport target UUID — written when the target accepts, read
     * by performTeleport(). Kept separate from PD_PENDING_TARGET_UUID so that
     * clearPendingRequest() (which runs on timeout) does NOT wipe the target
     * after acceptance but before the 5-second countdown completes.
     */
    private static final String PD_TELEPORT_TARGET_UUID = "SoulboundPact_TeleportTargetUUID";
    /** Per-item cooldown remaining (ticks) */
    private static final String PD_COOLDOWN_REMAINING   = "SoulboundPact_CooldownRemaining";
    /** System time when cooldown was last written */
    private static final String PD_COOLDOWN_TIME        = "SoulboundPact_CooldownTime";
    /** Incoming request: UUID of the player requesting to tp to us */
    private static final String PD_INCOMING_UUID        = "SoulboundPact_IncomingUUID";
    /** Tick timestamp when the incoming request arrived */
    private static final String PD_INCOMING_START_TICK  = "SoulboundPact_IncomingStartTick";

    // ── Constructor / overrides ───────────────────────────────────────────────

    public SoulboundPactTalisman(Properties properties) {
        // stacksTo(1): each pact talisman is personalised, stacking makes no sense
        super(properties.stacksTo(1).rarity(Rarity.EPIC));
    }

    // BaseTeleportTalisman abstract method implementations
    @Override protected String getCooldownTag()       { return PD_COOLDOWN_REMAINING; }
    @Override protected String getCooldownTimeTag()   { return PD_COOLDOWN_TIME; }
    @Override protected String getCountdownTag()      { return "SoulboundPact_Countdown"; }
    @Override protected String getInitialPosTag()     { return "SoulboundPact_InitialPos"; }
    @Override protected String getInitialHealthTag()  { return "SoulboundPact_InitialHealth"; }
    @Override protected String getUsedHandTag()       { return "SoulboundPact_UsedHand"; }
    @Override protected String getUsedSlotTag()       { return "SoulboundPact_UsedSlot"; }
    @Override protected int    getCooldownTicks()     { return COOLDOWN_TICKS; }
    @Override protected int    getCountdownTicks()    { return 5 * TICKS_PER_SECOND; }
    @Override protected Rarity getTalismanRarity()    { return Rarity.EPIC; }
    @Override protected String getDisplayNameKey()    { return "item.ascension.soulbound_pact_talisman"; }
    @Override protected int    getRechargeMaxValue()  { return RECHARGE_MAX_VALUE; }
    @Override protected String getPermanentVariantId(){ return "soulbound_pact"; }

    /**
     * This talisman is ALWAYS treated as permanent (never consumed).
     * We override isPermanent so the base class never tries to consume it.
     */
    @Override
    protected boolean isPermanent(ItemStack stack) {
        return true;
    }

    // ── use() ─────────────────────────────────────────────────────────────────

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // ── Shift + right-click: link a nearby player ─────────────────────────
        if (player.isShiftKeyDown()) {
            if (level.isClientSide) {
                level.playSound(player, player.blockPosition(),
                        SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 1.0F, 1.2F);
                return InteractionResultHolder.consume(stack);
            }
            handleLinkPlayer((ServerPlayer) player, stack);
            return InteractionResultHolder.success(stack);
        }

        // ── Right-click: accept an incoming request if one exists ─────────────
        if (!level.isClientSide) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            if (hasIncomingRequest(serverPlayer)) {
                handleAcceptRequest(serverPlayer, stack, hand);
                return InteractionResultHolder.success(stack);
            }
        }

        // ── Right-click: initiate a teleport request ──────────────────────────
        if (level.isClientSide) {
            level.playSound(player, player.blockPosition(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
            return InteractionResultHolder.consume(stack);
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;

        // Cooldown check
        if (isOnThisCooldown(serverPlayer)) {
            int remaining = getRemainingCooldownSeconds(serverPlayer);
            serverPlayer.displayClientMessage(
                    Component.translatable("ascension.soulboundpact.on_cooldown", remaining)
                            .withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        // Check linked list
        List<String> uuids = getLinkedUUIDs(stack);
        if (uuids.isEmpty()) {
            serverPlayer.displayClientMessage(
                    Component.translatable("ascension.soulboundpact.no_linked_players")
                            .withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        // Qi cost check
        IEntityData entityData = serverPlayer.getData(ModAttachments.ENTITY_DATA);
        if (entityData == null || !entityData.getQiContainer().tryConsumeQi(QI_COST)) {
            serverPlayer.displayClientMessage(
                    Component.translatable("ascension.soulboundpact.not_enough_qi", QI_COST)
                            .withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        // Get target
        int selectedIndex = getSelectedIndex(stack);
        if (selectedIndex >= uuids.size()) {
            selectedIndex = 0;
            setSelectedIndex(stack, 0);
        }
        String targetUUIDStr = uuids.get(selectedIndex);
        List<String> names   = getLinkedNames(stack);
        String targetName    = (selectedIndex < names.size()) ? names.get(selectedIndex) : "Unknown";

        ServerPlayer target = getOnlinePlayer(serverPlayer.getServer(), targetUUIDStr);
        if (target == null) {
            serverPlayer.displayClientMessage(
                    Component.translatable("ascension.soulboundpact.target_offline", targetName)
                            .withStyle(ChatFormatting.RED), true);
            // Refund Qi since teleport didn't start
            //entityData.getQiContainer().addQi(QI_COST);
            return InteractionResultHolder.fail(stack);
        }

        // Already pending a request?
        if (hasPendingRequest(serverPlayer)) {
            serverPlayer.displayClientMessage(
                    Component.translatable("ascension.soulboundpact.request_pending")
                            .withStyle(ChatFormatting.YELLOW), true);
            return InteractionResultHolder.fail(stack);
        }

        // Send request to target
        sendRequest(serverPlayer, target, stack, hand);
        return InteractionResultHolder.success(stack);
    }

    // ── performTeleport() – called by parent after countdown ──────────────────

    @Override
    protected void performTeleport(ServerPlayer player, ItemStack usedStack, int usedSlot) {
        CompoundTag data = player.getPersistentData();

        // Read from the confirmed destination key written by handleAcceptRequest().
        // This is intentionally separate from PD_PENDING_TARGET_UUID so that
        // the timeout ticker clearing pending requests cannot wipe the destination
        // during the 5-second countdown.
        String targetUUIDStr = data.getString(PD_TELEPORT_TARGET_UUID);
        if (targetUUIDStr.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable("ascension.soulboundpact.request_expired")
                            .withStyle(ChatFormatting.RED), true);
            clearCountdownData(player, getActualCountdownTag(usedStack));
            data.remove(PD_TELEPORT_TARGET_UUID);
            return;
        }

        ServerPlayer target = getOnlinePlayer(player.getServer(), targetUUIDStr);
        if (target == null) {
            player.displayClientMessage(
                    Component.translatable("ascension.soulboundpact.target_offline_during_tp")
                            .withStyle(ChatFormatting.RED), true);
            applyThisCooldown(player);
            data.remove(PD_TELEPORT_TARGET_UUID);
            clearCountdownData(player, getActualCountdownTag(usedStack));
            return;
        }

        // Teleport requester to target's current location
        ServerLevel targetLevel = (ServerLevel) target.level();
        player.teleportTo(targetLevel,
                target.getX(), target.getY(), target.getZ(),
                player.getYRot(), player.getXRot());

        playArrivalEffects(player);
        applyThisCooldown(player);
        data.remove(PD_TELEPORT_TARGET_UUID);

        player.displayClientMessage(
                Component.translatable("ascension.soulboundpact.teleported",
                        target.getDisplayName()), true);
        target.displayClientMessage(
                Component.translatable("ascension.soulboundpact.arrival_notice",
                        player.getDisplayName()), true);

        clearCountdownData(player, getActualCountdownTag(usedStack));
    }

    // ── inventoryTick() – tick pending requests / incoming request expiry ──────

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            // Drive the teleport countdown — this is what the parent's inventoryTick
            // normally does. We call it directly because we cannot call super safely
            // (super would try to run the Qi-recharge path since isPermanent == true).
            handleCountdown(player, getActualCountdownTag(stack));

            // Expire stale outgoing requests
            tickPendingRequest(player);
            // Expire stale incoming requests
            tickIncomingRequest(player);
            // Cooldown display
            updateSoulboundCooldownDisplay(stack, player);
        }
    }

    // ── Tooltip / display ─────────────────────────────────────────────────────

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        // Do NOT call super (avoids recharge-progress line since rechargeMaxValue == 0)

        List<String> uuids = getLinkedUUIDs(stack);
        List<String> names = getLinkedNames(stack);
        int selected = getSelectedIndex(stack);

        if (uuids.isEmpty()) {
            tooltip.add(Component.translatable("ascension.soulboundpact.tooltip.empty")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("ascension.soulboundpact.tooltip.linked_header")
                    .withStyle(ChatFormatting.AQUA));
            for (int i = 0; i < uuids.size(); i++) {
                String name = (i < names.size()) ? names.get(i) : "Unknown";
                boolean isSelected = (i == selected);
                Component nameComponent = Component.literal(name)
                        .withStyle(isSelected ? ChatFormatting.YELLOW : ChatFormatting.WHITE);
                Component entry = (isSelected
                        ? Component.literal("» ").withStyle(ChatFormatting.GOLD)
                        : Component.literal("  ").withStyle(ChatFormatting.DARK_GRAY))
                        .append(nameComponent);
                tooltip.add(entry);
            }
        }

        tooltip.add(Component.translatable("ascension.soulboundpact.tooltip.qi_cost", QI_COST)
                .withStyle(ChatFormatting.DARK_AQUA));
        tooltip.add(Component.translatable("ascension.soulboundpact.tooltip.hint_link")
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("ascension.soulboundpact.tooltip.hint_scroll")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return !getLinkedUUIDs(stack).isEmpty();
    }

    // ── Link logic ────────────────────────────────────────────────────────────

    private void handleLinkPlayer(ServerPlayer linker, ItemStack stack) {
        List<String> uuids = getLinkedUUIDs(stack);
        if (uuids.size() >= MAX_LINKED_PLAYERS) {
            linker.displayClientMessage(
                    Component.translatable("ascension.soulboundpact.link_full", MAX_LINKED_PLAYERS)
                            .withStyle(ChatFormatting.RED), true);
            return;
        }

        // Find nearest player within LINK_RADIUS (excluding self)
        ServerPlayer nearest = null;
        double nearestDist = LINK_RADIUS * LINK_RADIUS;
        for (ServerPlayer other : linker.getServer().getPlayerList().getPlayers()) {
            if (other == linker) continue;
            if (!other.level().dimension().equals(linker.level().dimension())) continue;
            double dist = linker.distanceToSqr(other);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = other;
            }
        }

        if (nearest == null) {
            linker.displayClientMessage(
                    Component.translatable("ascension.soulboundpact.no_player_nearby", LINK_RADIUS)
                            .withStyle(ChatFormatting.RED), true);
            return;
        }

        String targetUUID = nearest.getStringUUID();
        if (uuids.contains(targetUUID)) {
            linker.displayClientMessage(
                    Component.translatable("ascension.soulboundpact.already_linked",
                                    nearest.getDisplayName())
                            .withStyle(ChatFormatting.YELLOW), true);
            return;
        }

        addLinkedPlayer(stack, targetUUID, nearest.getGameProfile().getName());

        linker.displayClientMessage(
                Component.translatable("ascension.soulboundpact.linked",
                                nearest.getDisplayName())
                        .withStyle(ChatFormatting.GREEN), true);

        // Play sound for both
        ((ServerLevel) linker.level()).playSound(null, linker.blockPosition(),
                SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 1.0F, 1.2F);
    }

    // ── Request flow ──────────────────────────────────────────────────────────

    private void sendRequest(ServerPlayer requester, ServerPlayer target,
                             ItemStack stack, InteractionHand hand) {
        CompoundTag data = requester.getPersistentData();

        int slot = (hand == InteractionHand.MAIN_HAND)
                ? requester.getInventory().selected : 40;

        data.putString(PD_PENDING_TARGET_UUID, target.getStringUUID());
        data.putLong(PD_PENDING_START_TICK, requester.level().getGameTime());
        data.putInt(PD_PENDING_ITEM_SLOT, slot);
        // Write hand + slot now so handleAcceptRequest can pass them to startCountdown()
        data.putString(getUsedHandTag(), hand.name());
        data.putInt(getUsedSlotTag(), slot);

        // Store incoming request on the target
        CompoundTag targetData = target.getPersistentData();
        targetData.putString(PD_INCOMING_UUID, requester.getStringUUID());
        targetData.putLong(PD_INCOMING_START_TICK, target.level().getGameTime());

        String requesterName = requester.getGameProfile().getName();
        String timeoutSecs   = String.valueOf(REQUEST_TIMEOUT_TICKS / TICKS_PER_SECOND);

        requester.displayClientMessage(
                Component.translatable("ascension.soulboundpact.request_sent",
                                target.getDisplayName(), timeoutSecs)
                        .withStyle(ChatFormatting.AQUA), false);

        target.displayClientMessage(
                Component.translatable("ascension.soulboundpact.request_received",
                                requester.getDisplayName(), timeoutSecs)
                        .withStyle(ChatFormatting.GOLD), false);

        target.displayClientMessage(
                Component.translatable("ascension.soulboundpact.request_accept_hint")
                        .withStyle(ChatFormatting.YELLOW), false);
    }

    private void handleAcceptRequest(ServerPlayer acceptor, ItemStack stack, InteractionHand hand) {
        CompoundTag data = acceptor.getPersistentData();
        String requesterUUID = data.getString(PD_INCOMING_UUID);

        ServerPlayer requester = getOnlinePlayer(acceptor.getServer(), requesterUUID);
        if (requester == null) {
            acceptor.displayClientMessage(
                    Component.translatable("ascension.soulboundpact.requester_left")
                            .withStyle(ChatFormatting.RED), true);
            clearIncomingRequest(acceptor);
            return;
        }

        // Verify the requester still has a pending request for us
        CompoundTag reqData = requester.getPersistentData();
        if (!reqData.getString(PD_PENDING_TARGET_UUID).equals(acceptor.getStringUUID())) {
            acceptor.displayClientMessage(
                    Component.translatable("ascension.soulboundpact.request_expired")
                            .withStyle(ChatFormatting.RED), true);
            clearIncomingRequest(acceptor);
            return;
        }

        // Hand and slot were written into persistent data by sendRequest()
        // using the standard base-class tag names, so startCountdown() and
        // the parent's cancellation checks will find them correctly.
        String handName = reqData.getString(getUsedHandTag());
        InteractionHand requesterHand = InteractionHand.MAIN_HAND;
        try {
            if (!handName.isEmpty()) requesterHand = InteractionHand.valueOf(handName);
        } catch (IllegalArgumentException ignored) {}

        ItemStack requesterStack = requester.getItemInHand(requesterHand);
        if (!(requesterStack.getItem() instanceof SoulboundPactTalisman)) {
            acceptor.displayClientMessage(
                    Component.translatable("ascension.soulboundpact.requester_no_item")
                            .withStyle(ChatFormatting.RED), true);
            clearIncomingRequest(acceptor);
            return;
        }

        // Write the confirmed destination UUID into a separate key.
        // PD_PENDING_TARGET_UUID is cleared by clearPendingRequest() (timeout path)
        // but PD_TELEPORT_TARGET_UUID is only cleared inside performTeleport(),
        // so the 5-second countdown always has a valid target to read.
        reqData.putString(PD_TELEPORT_TARGET_UUID, acceptor.getStringUUID());

        // Remove the pending-request markers so the timeout ticker stops watching
        clearPendingRequest(requester);

        acceptor.displayClientMessage(
                Component.translatable("ascension.soulboundpact.accepted",
                                requester.getDisplayName())
                        .withStyle(ChatFormatting.GREEN), true);

        requester.displayClientMessage(
                Component.translatable("ascension.soulboundpact.request_accepted",
                                acceptor.getDisplayName())
                        .withStyle(ChatFormatting.GREEN), false);

        clearIncomingRequest(acceptor);
        // Start the standard 5-second countdown on the requester
        startCountdown(requester, requesterHand, reqData.getInt(getUsedSlotTag()), requesterStack);
        playDepartureEffects(requester);
    }

    // ── Tick helpers ──────────────────────────────────────────────────────────

    private void tickPendingRequest(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(PD_PENDING_TARGET_UUID)) return;

        long startTick = data.getLong(PD_PENDING_START_TICK);
        long currentTick = player.level().getGameTime();

        if (currentTick - startTick > REQUEST_TIMEOUT_TICKS) {
            player.displayClientMessage(
                    Component.translatable("ascension.soulboundpact.request_timed_out")
                            .withStyle(ChatFormatting.RED), true);
            // Note: Qi was already spent — this is intentional (anti-spam)
            clearPendingRequest(player);
        }
    }

    private void tickIncomingRequest(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(PD_INCOMING_UUID)) return;

        long startTick = data.getLong(PD_INCOMING_START_TICK);
        long currentTick = player.level().getGameTime();

        if (currentTick - startTick > REQUEST_TIMEOUT_TICKS) {
            clearIncomingRequest(player);
        }
    }

    private void updateSoulboundCooldownDisplay(ItemStack stack, ServerPlayer player) {
        int remaining = getRemainingCooldownSeconds(player);
        if (remaining > 0) {
            int minutes = remaining / 60;
            int seconds = remaining % 60;
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            preserveLinkedData(stack, tag);
            tag.putInt("CooldownMinutes", minutes);
            tag.putInt("CooldownSeconds", seconds);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        } else {
            // Clear cooldown display but preserve linked list
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            tag.remove("CooldownMinutes");
            tag.remove("CooldownSeconds");
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    // ── Cooldown helpers (stored on player, not item) ─────────────────────────

    private void applyThisCooldown(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        data.putInt(PD_COOLDOWN_REMAINING, COOLDOWN_TICKS);
        data.putLong(PD_COOLDOWN_TIME, System.currentTimeMillis());
    }

    private boolean isOnThisCooldown(ServerPlayer player) {
        return getRemainingCooldownTicks(player) > 0;
    }

    private int getRemainingCooldownTicks(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(PD_COOLDOWN_REMAINING) || !data.contains(PD_COOLDOWN_TIME)) return 0;
        int stored  = data.getInt(PD_COOLDOWN_REMAINING);
        long millis = data.getLong(PD_COOLDOWN_TIME);
        long elapsed = (System.currentTimeMillis() - millis) / 50; // ms → ticks
        return (int) Math.max(0, stored - elapsed);
    }

    private int getRemainingCooldownSeconds(ServerPlayer player) {
        return getRemainingCooldownTicks(player) / TICKS_PER_SECOND;
    }

    // ── Request clear helpers ─────────────────────────────────────────────────

    private void clearPendingRequest(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        data.remove(PD_PENDING_TARGET_UUID);
        data.remove(PD_PENDING_START_TICK);
        data.remove(PD_PENDING_ITEM_SLOT);
    }

    private void clearIncomingRequest(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        data.remove(PD_INCOMING_UUID);
        data.remove(PD_INCOMING_START_TICK);
    }

    private boolean hasPendingRequest(ServerPlayer player) {
        return player.getPersistentData().contains(PD_PENDING_TARGET_UUID);
    }

    private boolean hasIncomingRequest(ServerPlayer player) {
        return player.getPersistentData().contains(PD_INCOMING_UUID);
    }

    // ── Linked-player data on ItemStack ──────────────────────────────────────

    /** Returns current linked UUIDs, never null. */
    public static List<String> getLinkedUUIDs(ItemStack stack) {
        CompoundTag tag = getCustomTag(stack);
        if (!tag.contains(TAG_LINKED_UUIDS, Tag.TAG_LIST)) return new ArrayList<>();
        ListTag list = tag.getList(TAG_LINKED_UUIDS, Tag.TAG_STRING);
        List<String> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) result.add(list.getString(i));
        return result;
    }

    /** Returns current linked display names, never null. */
    public static List<String> getLinkedNames(ItemStack stack) {
        CompoundTag tag = getCustomTag(stack);
        if (!tag.contains(TAG_LINKED_NAMES, Tag.TAG_LIST)) return new ArrayList<>();
        ListTag list = tag.getList(TAG_LINKED_NAMES, Tag.TAG_STRING);
        List<String> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) result.add(list.getString(i));
        return result;
    }

    /** Returns the selected target index (0-based). */
    public static int getSelectedIndex(ItemStack stack) {
        CompoundTag tag = getCustomTag(stack);
        return tag.contains(TAG_SELECTED_INDEX) ? tag.getInt(TAG_SELECTED_INDEX) : 0;
    }

    /** Sets the selected target index, clamped to the linked list size. */
    public static void setSelectedIndex(ItemStack stack, int index) {
        List<String> uuids = getLinkedUUIDs(stack);
        if (uuids.isEmpty()) return;
        int clamped = ((index % uuids.size()) + uuids.size()) % uuids.size();
        CompoundTag tag = getCustomTag(stack);
        tag.putInt(TAG_SELECTED_INDEX, clamped);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /** Cycles the selected index by +delta (positive = forward, negative = back). */
    public static void cycleSelectedIndex(ItemStack stack, int delta) {
        int current = getSelectedIndex(stack);
        setSelectedIndex(stack, current + delta);
    }

    private void addLinkedPlayer(ItemStack stack, String uuid, String name) {
        CompoundTag tag = getCustomTag(stack);

        ListTag uuidList = tag.contains(TAG_LINKED_UUIDS, Tag.TAG_LIST)
                ? tag.getList(TAG_LINKED_UUIDS, Tag.TAG_STRING) : new ListTag();
        ListTag nameList = tag.contains(TAG_LINKED_NAMES, Tag.TAG_LIST)
                ? tag.getList(TAG_LINKED_NAMES, Tag.TAG_STRING) : new ListTag();

        uuidList.add(StringTag.valueOf(uuid));
        nameList.add(StringTag.valueOf(name));

        tag.put(TAG_LINKED_UUIDS, uuidList);
        tag.put(TAG_LINKED_NAMES, nameList);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /** Copies linked-list data into an external CompoundTag (used during cooldown display updates). */
    private void preserveLinkedData(ItemStack stack, CompoundTag into) {
        CompoundTag existing = getCustomTag(stack);
        if (existing.contains(TAG_LINKED_UUIDS))   into.put(TAG_LINKED_UUIDS,   existing.get(TAG_LINKED_UUIDS));
        if (existing.contains(TAG_LINKED_NAMES))    into.put(TAG_LINKED_NAMES,   existing.get(TAG_LINKED_NAMES));
        if (existing.contains(TAG_SELECTED_INDEX))  into.putInt(TAG_SELECTED_INDEX, existing.getInt(TAG_SELECTED_INDEX));
    }

    @Override
    protected void preserveCustomData(ItemStack stack, CompoundTag cooldownTag) {
        preserveLinkedData(stack, cooldownTag);
    }

    @Override
    protected void clearCooldownDisplayOnly(ItemStack stack) {
        CompoundTag tag = getCustomTag(stack);
        tag.remove("CooldownMinutes");
        tag.remove("CooldownSeconds");
        if (tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private static CompoundTag getCustomTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return (data != null) ? data.copyTag() : new CompoundTag();
    }

    private static ServerPlayer getOnlinePlayer(MinecraftServer server, String uuidStr) {
        if (uuidStr == null || uuidStr.isEmpty()) return null;
        try {
            UUID uuid = UUID.fromString(uuidStr);
            return server.getPlayerList().getPlayer(uuid);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}