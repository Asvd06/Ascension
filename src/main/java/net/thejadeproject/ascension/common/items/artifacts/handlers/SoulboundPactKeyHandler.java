package net.thejadeproject.ascension.common.items.artifacts.handlers;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.common.items.artifacts.talismans.SoulboundPactTalisman;
import net.thejadeproject.ascension.network.serverBound.SoulboundPactScrollPacket;

import java.util.List;

/**
 * Listens for mouse-scroll events on the client.
 * When the player holds Shift+Ctrl and is holding a SoulboundPactTalisman,
 * cycles the selected target index and sends a packet to the server.
 *
 * Also drives the action-bar overlay that shows the current selection.
 */
@EventBusSubscriber(modid = AscensionCraft.MOD_ID, value = Dist.CLIENT)
public class SoulboundPactKeyHandler {

    /** Ticks the selection overlay stays visible after the last scroll input. */
    private static final int OVERLAY_DURATION_TICKS = 60; // 3 seconds

    private static int overlayTicksRemaining = 0;

    // ── Scroll intercept ──────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.screen != null) return;

        // Require Shift + Ctrl held simultaneously
        if (!player.isShiftKeyDown()) return;
        if (!isCtrlHeld()) return;

        // Find talisman in either hand
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand  = player.getOffhandItem();

        ItemStack pactStack = null;
        if (mainHand.getItem() instanceof SoulboundPactTalisman) {
            pactStack = mainHand;
        } else if (offHand.getItem() instanceof SoulboundPactTalisman) {
            pactStack = offHand;
        }

        if (pactStack == null) return;

        // Consume the scroll so the hotbar doesn't switch slots
        event.setCanceled(true);

        List<String> names = SoulboundPactTalisman.getLinkedNames(pactStack);
        if (names.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable("ascension.soulboundpact.no_linked_players")
                            .withStyle(ChatFormatting.RED), true);
            return;
        }

        // scroll up (positive Y) = previous; scroll down = next
        int delta = event.getScrollDeltaY() > 0 ? -1 : 1;

        // Update locally for immediate visual feedback
        SoulboundPactTalisman.cycleSelectedIndex(pactStack, delta);

        // Sync to server
        PacketDistributor.sendToServer(new SoulboundPactScrollPacket(delta));

        // Show the overlay
        overlayTicksRemaining = OVERLAY_DURATION_TICKS;
        showSelectionOverlay(pactStack);
    }

    // ── Overlay countdown ─────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onClientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
        if (overlayTicksRemaining > 0) {
            overlayTicksRemaining--;
        }
    }

    // ── Overlay display ───────────────────────────────────────────────────────

    /**
     * Renders all linked player names above the hotbar, with the currently
     * selected one highlighted in gold and wrapped in brackets.
     *
     * Example:  §7Wei Lan  §e§l[Fang Yun]  §7Xu Ming
     */
    public static void showSelectionOverlay(ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        List<String> names = SoulboundPactTalisman.getLinkedNames(stack);
        int selected = SoulboundPactTalisman.getSelectedIndex(stack);
        if (names.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < names.size(); i++) {
            if (i > 0) sb.append("   ");
            if (i == selected) {
                sb.append(ChatFormatting.GOLD).append(ChatFormatting.BOLD)
                        .append("[").append(names.get(i)).append("]")
                        .append(ChatFormatting.RESET);
            } else {
                sb.append(ChatFormatting.GRAY)
                        .append(names.get(i))
                        .append(ChatFormatting.RESET);
            }
        }

        player.displayClientMessage(Component.literal(sb.toString()), true);
    }

    /** Returns true while the overlay should be rendered (call from HUD renderer). */
    public static boolean isOverlayVisible() {
        return overlayTicksRemaining > 0;
    }

    // ── Util ──────────────────────────────────────────────────────────────────

    private static boolean isCtrlHeld() {
        long win = Minecraft.getInstance().getWindow().getWindow();
        return org.lwjgl.glfw.GLFW.glfwGetKey(win, org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL)
                == org.lwjgl.glfw.GLFW.GLFW_PRESS
                || org.lwjgl.glfw.GLFW.glfwGetKey(win, org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL)
                == org.lwjgl.glfw.GLFW.GLFW_PRESS;
    }
}