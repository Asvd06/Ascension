package net.thejadeproject.ascension.common.items.tools.soul_weapon;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.TooltipFlag;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;

import java.util.List;

public class SoulboundMaceItem extends MaceItem implements ISoulboundItem {
    public SoulboundMaceItem(Properties properties) {
        super(properties);
    }

    @Override
    public SoulWeaponType getSoulWeaponType() {
        return SoulWeaponType.MACE;
    }


    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 15;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        var soulWeapon = stack.get(ModDataComponents.SOUL_WEAPON.get());

        tooltip.add(Component.translatable("item.ascension.soulbound_weapon.tooltip")
                .withStyle(ChatFormatting.DARK_PURPLE));


        int grade = soulWeapon == null ? 0 : soulWeapon.grade();
        int tempering = soulWeapon == null ? 0 : soulWeapon.tempering();
        int marks = soulWeapon == null ? 0 : soulWeapon.forgedMarks();

        tooltip.add(Component.translatable(
                "item.ascension.soulbound_weapon.currentGrade",
                grade
        ).withStyle(ChatFormatting.AQUA));

        tooltip.add(Component.translatable(
                "item.ascension.soulbound_weapon.tempering",
                tempering,
                SoulWeaponHelper.getRequiredTempering(grade, marks)
        ).withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, context, tooltip, flag);
    }


}
