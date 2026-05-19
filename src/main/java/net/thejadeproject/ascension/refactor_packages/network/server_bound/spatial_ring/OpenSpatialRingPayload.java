package net.thejadeproject.ascension.refactor_packages.network.server_bound.spatial_ring;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.common.items.artifacts.SpatialRing;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.common.items.data_components.spatial_ring.SpatialRingComponent;
import net.thejadeproject.ascension.common.items.data_components.spatial_ring.SpatialRingItemStackHandler;
import net.thejadeproject.ascension.common.items.data_components.spatial_ring.SpatialRingMenuProvider;

public record OpenSpatialRingPayload() implements CustomPacketPayload {
    public static final Type<OpenSpatialRingPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "open_spatial_ring"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenSpatialRingPayload> STREAM_CODEC =
            StreamCodec.unit(new OpenSpatialRingPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handlePayload(OpenSpatialRingPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            ItemStack ring = findSpatialRing(player);

            if (ring.isEmpty()) {
                player.displayClientMessage(Component.literal("No spatial ring found."), true);
                return;
            }

            if (!ring.has(ModDataComponents.SPIRIT_RING_DATA)) {
                ring.set(ModDataComponents.SPIRIT_RING_DATA, new SpatialRingComponent(27, 18, 18));
            }

            player.openMenu(
                    new SpatialRingMenuProvider(
                            SpatialRingItemStackHandler.Type.INVENTORY,
                            ring
                    ),
                    buf -> ItemStack.STREAM_CODEC.encode(buf, ring)
            );
        });
    }

    private static ItemStack findSpatialRing(Player player) {
        if (ModList.get().isLoaded("curios")) {
            ItemStack curiosRing = findCuriosSpatialRing(player);
            if (!curiosRing.isEmpty()) {
                return curiosRing;
            }
        }

        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof SpatialRing) {
            return mainHand;
        }

        ItemStack offhand = player.getOffhandItem();
        if (offhand.getItem() instanceof SpatialRing) {
            return offhand;
        }

        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof SpatialRing) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    private static ItemStack findCuriosSpatialRing(Player player) {
        return top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player)
                .map(handler -> handler.findFirstCurio(stack ->
                        stack.getItem() instanceof SpatialRing
                ))
                .flatMap(result -> result.map(slotResult -> slotResult.stack()))
                .orElse(ItemStack.EMPTY);
    }

}