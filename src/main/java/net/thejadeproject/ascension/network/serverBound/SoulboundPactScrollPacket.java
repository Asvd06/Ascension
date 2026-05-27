package net.thejadeproject.ascension.network.serverBound;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.common.items.artifacts.talismans.SoulboundPactTalisman;

/**
 * Sent client → server when the player Shift+Ctrl+Scrolls while holding a
 * SoulboundPactTalisman.  The server applies the index delta to the
 * authoritative item stack.
 *
 * delta is always -1 (scroll up / previous) or +1 (scroll down / next).
 */
public record SoulboundPactScrollPacket(int delta) implements CustomPacketPayload {

    public static final Type<SoulboundPactScrollPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "soulbound_pact_scroll")
    );

    public static final StreamCodec<ByteBuf, SoulboundPactScrollPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    SoulboundPactScrollPacket::delta,
                    SoulboundPactScrollPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handlePayload(SoulboundPactScrollPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand  = player.getOffhandItem();

            if (mainHand.getItem() instanceof SoulboundPactTalisman) {
                SoulboundPactTalisman.cycleSelectedIndex(mainHand, payload.delta());
            } else if (offHand.getItem() instanceof SoulboundPactTalisman) {
                SoulboundPactTalisman.cycleSelectedIndex(offHand, payload.delta());
            }
        });
    }
}