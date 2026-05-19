package net.thejadeproject.ascension.common.items.physiques;

import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;

import java.util.List;

public class UnstableElementalCoreAmalgamItem extends Item {

    public static final long LIFESPAN_MS = 35_000L; // 35 seconds

    // Cycle speeds per phase
    public static final long CYCLE_SLOW   = 1500L; // >50% remaining
    public static final long CYCLE_FAST   = 500L;  // 10–50% remaining
    public static final long CYCLE_FRANTIC = 150L; // <10% remaining

    public UnstableElementalCoreAmalgamItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        super.onCraftedBy(stack, level, player);
        if (!level.isClientSide()) {
            stack.set(ModDataComponents.CRAFT_TIMESTAMP.get(), System.currentTimeMillis());
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide() || !(entity instanceof Player player)) return;

        Long timestamp = stack.get(ModDataComponents.CRAFT_TIMESTAMP.get());
        if (timestamp == null) {
            stack.set(ModDataComponents.CRAFT_TIMESTAMP.get(), System.currentTimeMillis());
            return;
        }

        if (System.currentTimeMillis() - timestamp >= LIFESPAN_MS) {
            triggerExplosion(stack, level, player);
        }
    }

    private void triggerExplosion(ItemStack stack, Level level, Player player) {
        stack.shrink(1);
        level.explode(null, player.getX(), player.getY(), player.getZ(), 3.0f, false, Level.ExplosionInteraction.NONE);
        player.hurt(level.damageSources().explosion(null, null), 15.0f);
        player.sendSystemMessage(Component.translatable("ascension.unstable_essence.exploded").withStyle(ChatFormatting.RED));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("ascension.item.unstable_5_element_essence.tooltip").withStyle(ChatFormatting.DARK_RED));

        Long timestamp = stack.get(ModDataComponents.CRAFT_TIMESTAMP.get());
        if (timestamp != null) {
            long remaining = Math.max(0, LIFESPAN_MS - (System.currentTimeMillis() - timestamp));
            long seconds = remaining / 1000;
            ChatFormatting color = seconds > 90 ? ChatFormatting.GOLD : seconds > 18 ? ChatFormatting.RED : ChatFormatting.DARK_RED;
            tooltip.add(Component.translatable("ascension.unstable_essence.time_remaining", seconds).withStyle(color));
        }
    }

    public static float getCyclePredicate(ItemStack stack, ClientLevel clientLevel) {
        Long timestamp = stack.get(ModDataComponents.CRAFT_TIMESTAMP.get());
        if (timestamp == null) {

            if (clientLevel == null) return 0.0f;
            return ((int)(clientLevel.getGameTime() / 25L) % 5) * 0.2f;
        }
        long elapsed = System.currentTimeMillis() - timestamp;
        long remaining = Math.max(0, LIFESPAN_MS - elapsed);
        long cycleSpeed = remaining > LIFESPAN_MS / 2 ? CYCLE_SLOW
                        : remaining > LIFESPAN_MS / 10 ? CYCLE_FAST
                        : CYCLE_FRANTIC;
        return ((int)(elapsed / cycleSpeed) % 5) * 0.2f;
    }
}
