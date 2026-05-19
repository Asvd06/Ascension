package net.thejadeproject.ascension.common.items.data_components.spatial_ring;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.menus.custom.spirit_ring.SpatialRingInventoryMenu;
import net.thejadeproject.ascension.menus.custom.spirit_ring.SpatialRingModifierMenu;
import org.jetbrains.annotations.Nullable;

public class SpatialRingMenuProvider implements MenuProvider {
    private final SpatialRingItemStackHandler.Type type;
    private final ItemStack stack;

    public SpatialRingMenuProvider(SpatialRingItemStackHandler.Type type, ItemStack stack) {
        this.type = type;
        this.stack = stack;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("item.ascension.spatial_ring");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        if (!stack.has(ModDataComponents.SPIRIT_RING_DATA)) {
            stack.set(ModDataComponents.SPIRIT_RING_DATA, new SpatialRingComponent(27, 18, 18));
        }

        if (type == SpatialRingItemStackHandler.Type.INVENTORY) {
            return new SpatialRingInventoryMenu(id, inventory, stack);
        }

        return new SpatialRingModifierMenu(id, inventory, stack);
    }
}
