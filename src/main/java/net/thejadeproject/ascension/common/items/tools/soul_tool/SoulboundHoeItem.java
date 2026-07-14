package net.thejadeproject.ascension.common.items.tools.soul_tool;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import net.thejadeproject.ascension.common.items.ModToolTiers;

import java.util.List;

public class SoulboundHoeItem extends HoeItem
        implements ISoulboundTool {

    public SoulboundHoeItem(Properties properties) {
        super(ModToolTiers.BLACK_IRON, properties);
    }

    @Override
    public SoulToolType getSoulToolType() {
        return SoulToolType.HOE;
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
    public InteractionResult useOn(UseOnContext context) {
        if (!isFunctioningSoulTool(context.getItemInHand())) {
            return InteractionResult.PASS;
        }

        return super.useOn(context);
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