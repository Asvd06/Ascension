package net.thejadeproject.ascension.common.items.artifacts;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.common.items.data_components.spatial_ring.SpatialRingComponent;
import net.thejadeproject.ascension.common.items.data_components.spatial_ring.SpatialRingItemStackHandler;
import net.thejadeproject.ascension.common.items.data_components.spatial_ring.SpatialRingMenuProvider;

public class SpatialRing extends Item {
    public SpatialRing(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (usedHand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.fail(player.getItemInHand(usedHand));
        }

        ItemStack stack = player.getMainHandItem();

        if (!stack.has(ModDataComponents.SPIRIT_RING_DATA)) {
            stack.set(ModDataComponents.SPIRIT_RING_DATA, new SpatialRingComponent(27, 18, 18));
        }

        if (!level.isClientSide()) {
            player.openMenu(
                    new SpatialRingMenuProvider(
                            player.isShiftKeyDown()
                                    ? SpatialRingItemStackHandler.Type.MODIFIERS
                                    : SpatialRingItemStackHandler.Type.INVENTORY,
                            stack
                    ),
                    buf -> ItemStack.STREAM_CODEC.encode(buf, stack)
            );
        }

        return InteractionResultHolder.success(stack);
    }


}
