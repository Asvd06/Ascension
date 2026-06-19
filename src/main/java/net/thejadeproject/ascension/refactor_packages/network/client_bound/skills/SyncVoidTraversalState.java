package net.thejadeproject.ascension.refactor_packages.network.client_bound.skills;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.clients.renderer.skills.VoidTraversalPayloadHandler;

public record SyncVoidTraversalState(
        boolean active,
        int durationTicks
) implements CustomPacketPayload {

    public static final Type<SyncVoidTraversalState> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    AscensionCraft.MOD_ID,
                    "sync_void_traversal_state"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncVoidTraversalState> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    SyncVoidTraversalState::active,

                    ByteBufCodecs.VAR_INT,
                    SyncVoidTraversalState::durationTicks,

                    SyncVoidTraversalState::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handlePayload(SyncVoidTraversalState payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                VoidTraversalPayloadHandler.handle(payload);
            }
        });
    }
}