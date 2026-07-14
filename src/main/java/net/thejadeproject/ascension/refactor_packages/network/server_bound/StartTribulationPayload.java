package net.thejadeproject.ascension.refactor_packages.network.server_bound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.util.ByteBufUtil;

public record StartTribulationPayload(ResourceLocation path) implements CustomPacketPayload {

    public static final Type<StartTribulationPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "start_tribulation"));

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            StartTribulationPayload> STREAM_CODEC = StreamCodec.of(
            StartTribulationPayload::encode,
            StartTribulationPayload::decode
    );

    private static void encode(RegistryFriendlyByteBuf buf, StartTribulationPayload payload) {
        ByteBufUtil.encodeString(buf, payload.path().toString());
    }

    private static StartTribulationPayload decode(RegistryFriendlyByteBuf buf) {
        return new StartTribulationPayload(ByteBufUtil.readResourceLocation(buf));
    }

    public static void handlePayload(StartTribulationPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        IPathData pathData = entityData.getPathData(payload.path());

        if (pathData == null) {
            return;
        }

        if (pathData.beginBreakthrough(entityData)) {
            pathData.sync(player);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}