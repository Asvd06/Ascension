package net.thejadeproject.ascension.common.items.tools.soul_weapon;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;

import java.util.List;

public class SoulboundWeaponItem extends SwordItem {

    public SoulboundWeaponItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, net.minecraft.world.InteractionHand hand) {
        if (!SoulWeaponHelper.isOwner(stack, player)) {
            return InteractionResult.FAIL;
        }

        return super.interactLivingEntity(stack, player, target, hand);
    }

    @Override
    public Component getName(ItemStack stack) {
        var soulWeapon = stack.get(ModDataComponents.SOUL_WEAPON.get());

        String type = soulWeapon == null ? "" : soulWeapon.type();

        if (type == null || type.isBlank()) {
            return Component.translatable("item.ascension.soulbound_weapon");
        }

        return Component.translatable("item.ascension.soulbound_weapon.typed", capitalise(type));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        var soulWeapon = stack.get(ModDataComponents.SOUL_WEAPON.get());

        String type = soulWeapon == null ? "" : soulWeapon.type();

        tooltip.add(Component.translatable("item.ascension.soulbound_weapon.tooltip")
                .withStyle(ChatFormatting.DARK_PURPLE));

        if (type != null && !type.isBlank()) {
            tooltip.add(Component.translatable("item.ascension.soulbound_weapon.type", capitalise(type))
                    .withStyle(ChatFormatting.GRAY));
        }

        int grade = soulWeapon == null ? 0 : soulWeapon.grade();

            tooltip.add(Component.translatable("item.ascension.soulbound_weapon.currentGrade", grade)
                    .withStyle(ChatFormatting.AQUA));

        int tempering = soulWeapon == null ? 0 : soulWeapon.tempering();
        int lifetimeMarks = soulWeapon == null ? 0 : soulWeapon.forgedMarks();

        tooltip.add(Component.translatable(
                    "item.ascension.soulbound_weapon.tempering",
                    tempering,
                    SoulWeaponHelper.getRequiredTempering(grade, lifetimeMarks)
            ).withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, context, tooltip, flag);
    }

    private static String capitalise(String value) {
        if (value == null || value.isBlank()) return "";
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }





}