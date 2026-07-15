package net.thejadeproject.ascension.menus.custom.herb_pouch;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.common.items.data_components.herb_pouch.HerbPouchComponent;
import net.thejadeproject.ascension.menus.ModMenuTypes;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.herb_pouch.SyncHerbPouchPayload;

import java.util.List;

public class HerbPouchMenu extends AbstractContainerMenu {

    private static final int PLAYER_INV_START = 0;
    private static final int PLAYER_INV_END = 27;

    private static final int HOTBAR_START = 27;
    private static final int HOTBAR_END = 36;

    private final ItemStack pouchStack;
    private final int lockedHotbarIndex;

    private HerbPouchComponent clientPouchData;

    public HerbPouchMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        super(ModMenuTypes.HERB_POUCH_MENU.get(), containerId);

        this.lockedHotbarIndex = inventory.selected;

        this.pouchStack = inventory.getItem(lockedHotbarIndex);

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        this.clientPouchData = getPouchData();
    }

    private HerbPouchComponent getPouchData() {
        HerbPouchComponent data = pouchStack.get(ModDataComponents.HERB_POUCH_DATA.get());

        if (data == null) {
            data = new HerbPouchComponent(HerbPouchComponent.DEFAULT_CAPACITY);
            pouchStack.set(ModDataComponents.HERB_POUCH_DATA.get(), data);

            return data;
        }

        if (data.capacity() < HerbPouchComponent.DEFAULT_CAPACITY) {
            int upgradedCapacity = Math.max(HerbPouchComponent.DEFAULT_CAPACITY, data.getTotalCount());
            data = new HerbPouchComponent(upgradedCapacity, data.herbs());
            pouchStack.set(ModDataComponents.HERB_POUCH_DATA.get(), data);
        }

        return data;
    }

    public void setPouchData(HerbPouchComponent component) {
        pouchStack.set(ModDataComponents.HERB_POUCH_DATA.get(), component);
        clientPouchData = component;
    }

    public void syncPouchData(ServerPlayer player) {
        HerbPouchComponent component = getPouchData();
        clientPouchData = component;
        PacketDistributor.sendToPlayer(player, new SyncHerbPouchPayload(component));
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(
                        new Slot(playerInventory, column + row * 9 + 9, 0, 0)
                );
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; i++) {
            final int hotbarIndex = i;

            addSlot(
                    new Slot(playerInventory, hotbarIndex, 0, 0) {
                        @Override
                        public boolean mayPickup(Player player) {
                            return hotbarIndex != lockedHotbarIndex;
                        }

                        @Override
                        public boolean mayPlace(ItemStack stack) {
                            return hotbarIndex != lockedHotbarIndex;
                        }
                    }
            );
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) {
            return ItemStack.EMPTY;
        }

        Slot slot = slots.get(index);

        if (slot == null || !slot.hasItem() || !slot.mayPickup(player)) {
            return ItemStack.EMPTY;
        }

        boolean fromInventory = index >= PLAYER_INV_START && index < PLAYER_INV_END;

        boolean fromHotbar = index >= HOTBAR_START && index < HOTBAR_END;

        if (!fromInventory && !fromHotbar) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        HerbPouchComponent.InsertResult result =
                getPouchData().insert(stack);

        if (result.remainder().getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }

        setPouchData(result.component());

        slot.set(result.remainder());
        slot.setChanged();

        broadcastChanges();

        if (player instanceof ServerPlayer serverPlayer) {
            syncPouchData(serverPlayer);
        }

        return original;
    }

    public boolean insertCarriedStack() {
        ItemStack carried = getCarried();

        if (carried.isEmpty()) {
            return false;
        }

        HerbPouchComponent.InsertResult result = getPouchData().insert(carried);

        if (result.remainder().getCount() == carried.getCount()) {
            return false;
        }

        setPouchData(result.component());
        setCarried(result.remainder());

        broadcastChanges();

        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getInventory().selected == lockedHotbarIndex && player.getInventory().getItem(lockedHotbarIndex) == pouchStack;
    }

    public ItemStack getPouchStack() {
        return pouchStack;
    }

    public void setClientPouchData(HerbPouchComponent component) {
        clientPouchData = component;

        pouchStack.set(ModDataComponents.HERB_POUCH_DATA.get(), component);
    }

    public List<ItemStack> getSummaryStacks() {
        if (clientPouchData == null) {
            return List.of();
        }

        return clientPouchData.getSummaryStacks();
    }

    public int getStoredCount() {
        return clientPouchData != null ? clientPouchData.getTotalCount() : 0;
    }

    public int getCapacity() {
        return clientPouchData != null ? clientPouchData.capacity() : HerbPouchComponent.DEFAULT_CAPACITY;
    }
}