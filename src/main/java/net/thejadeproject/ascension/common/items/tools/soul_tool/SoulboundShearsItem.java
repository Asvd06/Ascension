package net.thejadeproject.ascension.common.items.tools.soul_tool;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;

public class SoulboundShearsItem extends ShearsItem
        implements ISoulboundTool {

    public SoulboundShearsItem(Properties properties) {
        super(properties);
    }

    @Override
    public SoulToolType getSoulToolType() {
        return SoulToolType.SHEARS;
    }

    @Override
    public float getDestroySpeed(
            ItemStack stack,
            BlockState state
    ) {
        return isFunctioningSoulTool(stack)
                ? super.getDestroySpeed(stack, state)
                : 1.0F;
    }

    @Override
    public boolean isCorrectToolForDrops(
            ItemStack stack,
            BlockState state
    ) {
        return isFunctioningSoulTool(stack)
                && super.isCorrectToolForDrops(stack, state);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        return isFunctioningSoulTool(stack)
                && super.canPerformAction(stack, ability);
    }

    @Override
    public InteractionResult interactLivingEntity(
            ItemStack stack,
            Player player,
            LivingEntity target,
            InteractionHand hand
    ) {
        if (!isFunctioningSoulTool(stack)) {
            return InteractionResult.PASS;
        }

        return super.interactLivingEntity(
                stack,
                player,
                target,
                hand
        );
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean isValidRepairItem(
            ItemStack toRepair,
            ItemStack repair
    ) {
        return false;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        appendSoulToolTooltip(stack, tooltip);
        super.appendHoverText(stack, context, tooltip, flag);
    }
}