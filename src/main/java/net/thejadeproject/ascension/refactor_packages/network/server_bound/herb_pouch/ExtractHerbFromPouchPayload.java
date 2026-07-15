package net.thejadeproject.ascension.refactor_packages.network.server_bound.herb_pouch;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.common.items.data_components.herb_pouch.HerbPouchComponent;
import net.thejadeproject.ascension.common.items.data_components.herb_pouch.HerbPouchExtractionMode;
import net.thejadeproject.ascension.menus.custom.herb_pouch.HerbPouchMenu;
import net.thejadeproject.ascension.util.ModTags;

import java.util.List;

public record ExtractHerbFromPouchPayload(ItemStack summaryStack, HerbPouchExtractionMode mode) implements CustomPacketPayload {

    public static final Type<ExtractHerbFromPouchPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            AscensionCraft.MOD_ID,
                            "extract_herb_from_pouch"
                    )
            );

    private static final StreamCodec<RegistryFriendlyByteBuf, HerbPouchExtractionMode> MODE_STREAM_CODEC =
            StreamCodec.of(
                    (buffer, mode) -> buffer.writeVarInt(mode.networkId()),
                    buffer -> HerbPouchExtractionMode.fromNetworkId(buffer.readVarInt())
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ExtractHerbFromPouchPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.OPTIONAL_STREAM_CODEC,
                    ExtractHerbFromPouchPayload::summaryStack,
                    MODE_STREAM_CODEC,
                    ExtractHerbFromPouchPayload::mode,
                    ExtractHerbFromPouchPayload::new
            );

    public ExtractHerbFromPouchPayload {
        summaryStack = summaryStack.copy();

        if (!summaryStack.isEmpty()) {
            summaryStack.setCount(1);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handlePayload(ExtractHerbFromPouchPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            if (!(player.containerMenu instanceof HerbPouchMenu menu)) {
                return;
            }

            ItemStack summary = payload.summaryStack();

            if (
                    summary.isEmpty()
                            || !summary.is(ModTags.Items.HERBS)
            ) {
                return;
            }

            HerbPouchComponent component =
                    menu.getPouchStack().get(
                            ModDataComponents.HERB_POUCH_DATA.get()
                    );

            if (component == null) {
                return;
            }

            Extraction extraction = switch (payload.mode()) {
                case FIRST_ONE -> {
                    HerbPouchComponent.ExtractResult result =
                            component.extractOneByAgeGroup(
                                    summary,
                                    false
                            );

                    yield new Extraction(
                            result.component(),
                            result.extracted().isEmpty()
                                    ? List.of()
                                    : List.of(result.extracted())
                    );
                }

                case LAST_ONE -> {
                    HerbPouchComponent.ExtractResult result =
                            component.extractOneByAgeGroup(
                                    summary,
                                    true
                            );

                    yield new Extraction(
                            result.component(),
                            result.extracted().isEmpty()
                                    ? List.of()
                                    : List.of(result.extracted())
                    );
                }

                case ALL_FROM_AGE_GROUP -> {
                    HerbPouchComponent.ExtractManyResult result =
                            component.extractAllByAgeGroup(summary);

                    yield new Extraction(
                            result.component(),
                            result.extracted()
                    );
                }

                case ALL_FROM_HERB -> {
                    HerbPouchComponent.ExtractManyResult result =
                            component.extractAllByItem(summary);

                    yield new Extraction(
                            result.component(),
                            result.extracted()
                    );
                }
            };

            if (extraction.stacks().isEmpty()) {
                return;
            }

            menu.setPouchData(extraction.component());

            giveStacksToPlayer(
                    player,
                    extraction.stacks()
            );

            menu.broadcastChanges();
            menu.syncPouchData(player);
        });
    }

    private static void giveStacksToPlayer(ServerPlayer player, List<ItemStack> stacks) {
        for (ItemStack extracted : stacks) {
            ItemStack remainder =
                    extracted.copy();

            player.getInventory().add(remainder);

            if (!remainder.isEmpty()) {
                player.drop(remainder, false);
            }
        }
    }

    private record Extraction(HerbPouchComponent component, List<ItemStack> stacks) {
    }
}