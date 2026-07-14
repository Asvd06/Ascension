package net.thejadeproject.ascension.common.items.tools.soul_tool;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import net.thejadeproject.ascension.common.items.ModToolTiers;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SoulboundPickaxeItem extends PickaxeItem
        implements ISoulboundTool {

    public SoulboundPickaxeItem(Properties properties) {
        super(ModToolTiers.BLACK_IRON, properties);
    }

    @Override
    public SoulToolType getSoulToolType() {
        return SoulToolType.PICKAXE;
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