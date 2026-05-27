package net.thejadeproject.ascension.common.items.bloodlines;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.thejadeproject.ascension.common.items.ModItems;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.bloodlines.IBloodline;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.toast.ShowAscensionToast;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;

import java.util.List;

import static net.thejadeproject.ascension.clients.toast.AscensionToastInterface.DEFAULT_BACKGROUND;

public class BloodlineTransferItem extends Item {

    public BloodlineTransferItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide()) return InteractionResultHolder.pass(stack);

        String targetBloodlineId = stack.get(ModDataComponents.BLOODLINE_ID.get());
        Integer purity = stack.get(ModDataComponents.PURITY.get());

        if (targetBloodlineId == null || targetBloodlineId.isEmpty() || purity == null) {
            return InteractionResultHolder.fail(stack);
        }

        if (purity < 100) {
            player.sendSystemMessage(
                    Component.literal("Cannot transfer bloodline: ")
                            .append(Component.literal(purity + "%").withStyle(ChatFormatting.GOLD))
                            .append(Component.literal(" purity. Need "))
                            .append(Component.literal("100%").withStyle(ChatFormatting.GREEN))
                            .append(Component.literal(" purity."))
            );
            return InteractionResultHolder.fail(stack);
        }

        if (!player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(stack);
        }

        Component bloodlineName = getBloodlineDisplayName(targetBloodlineId);
        ItemStack toastIcon = stack.copy();
        toastIcon.setCount(1);

        ResourceLocation targetId = ResourceLocation.bySeparator(targetBloodlineId, ':');

        player.getData(ModAttachments.ENTITY_DATA).setBloodline(targetId);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(
                    serverPlayer,
                    new ShowAscensionToast(
                            bloodlineName.getString(),
                            "Bloodline Transferred",
                            toastIcon,
                            DEFAULT_BACKGROUND
                    )
            );
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        String targetBloodlineId = stack.get(ModDataComponents.BLOODLINE_ID.get());
        Integer purity = stack.get(ModDataComponents.PURITY.get());

        if (targetBloodlineId != null && !targetBloodlineId.isEmpty()) {
            ResourceLocation bloodlineResource = ResourceLocation.parse(targetBloodlineId);
            IBloodline bloodline = AscensionRegistries.Bloodlines.BLOODLINE_REGISTRY.get(bloodlineResource);

            if (bloodline != null) {
                Component baseName = Component.empty()
                        .append(bloodline.getDisplayTitle())
                        .append(" Essence");

                if (purity != null && purity < 100) {
                    return baseName.copy()
                            .append(Component.literal(" [").withStyle(ChatFormatting.GRAY))
                            .append(Component.literal(purity + "%").withStyle(ChatFormatting.GOLD))
                            .append(Component.literal("]").withStyle(ChatFormatting.GRAY));
                }

                return baseName;
            }
        }

        return Component.literal("Bloodline Essence");
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        String targetBloodlineId = stack.get(ModDataComponents.BLOODLINE_ID.get());
        Integer purity = stack.get(ModDataComponents.PURITY.get());

        if (targetBloodlineId != null && !targetBloodlineId.isEmpty()) {
            tooltip.add(Component.literal("Target: ").withStyle(ChatFormatting.GRAY)
                    .append(getBloodlineDisplayName(targetBloodlineId)));

            if (purity != null) {
                ChatFormatting purityColor = ChatFormatting.GRAY;

                if (purity >= 100) purityColor = ChatFormatting.GREEN;
                else if (purity >= 75) purityColor = ChatFormatting.YELLOW;
                else if (purity >= 50) purityColor = ChatFormatting.GOLD;
                else if (purity >= 25) purityColor = ChatFormatting.RED;

                tooltip.add(Component.literal("Purity: ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(purity + "%").withStyle(purityColor)));
            }
        } else {
            tooltip.add(Component.literal("No bloodline set").withStyle(ChatFormatting.GRAY));
        }

        tooltip.add(Component.translatable("ascension.bloodline_essence.tooltip.replace")
                .withStyle(ChatFormatting.GRAY));

        if (purity != null && purity >= 100) {
            tooltip.add(Component.literal("Ready to use!").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
        } else if (purity != null) {
            tooltip.add(Component.literal("Requires 100% purity to use").withStyle(ChatFormatting.RED));
        }
    }

    private Component getBloodlineDisplayName(String targetBloodlineId) {
        ResourceLocation bloodlineResource = ResourceLocation.parse(targetBloodlineId);
        IBloodline bloodline = AscensionRegistries.Bloodlines.BLOODLINE_REGISTRY.get(bloodlineResource);

        if (bloodline != null) {
            return Component.empty().append(bloodline.getDisplayTitle()).withStyle(ChatFormatting.RED);
        }

        return Component.literal("Unknown Bloodline").withStyle(ChatFormatting.GRAY);
    }

    public static ItemStack createWithBloodline(String bloodlineId, int purity) {
        ItemStack stack = new ItemStack(ModItems.BLOODLINE_ESSENCE.get());
        stack.set(ModDataComponents.BLOODLINE_ID.get(), bloodlineId);
        stack.set(ModDataComponents.PURITY.get(), Math.min(purity, 100));
        return stack;
    }

    public static boolean canCombine(ItemStack stack1, ItemStack stack2) {
        String id1 = stack1.get(ModDataComponents.BLOODLINE_ID.get());
        String id2 = stack2.get(ModDataComponents.BLOODLINE_ID.get());

        return id1 != null && id1.equals(id2);
    }

    public static int getCombinedPurity(ItemStack stack1, ItemStack stack2) {
        Integer purity1 = stack1.get(ModDataComponents.PURITY.get());
        Integer purity2 = stack2.get(ModDataComponents.PURITY.get());

        if (purity1 == null) purity1 = 0;
        if (purity2 == null) purity2 = 0;

        return Math.min(purity1 + purity2, 100);
    }
}