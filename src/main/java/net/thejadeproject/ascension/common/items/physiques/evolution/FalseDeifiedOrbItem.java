package net.thejadeproject.ascension.common.items.physiques.evolution;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.refactor_packages.physiques.ModPhysiques;
import net.thejadeproject.ascension.refactor_packages.physiques.custom.helpers.PhysiqueEvolutionHelper;

import java.util.List;

public class FalseDeifiedOrbItem extends Item {
    public static final int REQUIRED_VITAL_ASSERTIONS = 33;

    public FalseDeifiedOrbItem(Properties properties) {
        super(properties);
    }

    public static int getVitalAssertions(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.VITAL_ASSERTIONS.get(), 0);
    }

    public static void addVitalAssertion(ItemStack stack) {
        int current = getVitalAssertions(stack);
        int next = Math.min(REQUIRED_VITAL_ASSERTIONS, current + 1);
        stack.set(ModDataComponents.VITAL_ASSERTIONS.get(), next);
    }

    public static boolean isComplete(ItemStack stack) {
        return getVitalAssertions(stack) >= REQUIRED_VITAL_ASSERTIONS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!isComplete(stack)) {
            serverPlayer.displayClientMessage(Component.translatable(
                    "ascension.item.false_deified_orb.unstable", getVitalAssertions(stack), REQUIRED_VITAL_ASSERTIONS
            ).withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        if (serverPlayer.getHealth() < serverPlayer.getMaxHealth()) {
            serverPlayer.displayClientMessage(Component.translatable(
                    "ascension.item.false_deified_orb.body_not_whole"
            ).withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        boolean evolved = PhysiqueEvolutionHelper.tryEvolveInto(
                serverPlayer,
                ModPhysiques.EMBODIED_ENTITY.getId()
        );

        if (!evolved) {
            serverPlayer.displayClientMessage(Component.translatable(
                    "ascension.item.corrupted_entity_completion.cannot_complete"
            ).withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        if (!serverPlayer.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int progress = getVitalAssertions(stack);

        tooltip.add(Component.translatable("ascension.item.false_deified_orb.tooltip.vital_assertions")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(progress + " / " + REQUIRED_VITAL_ASSERTIONS)
                        .withStyle(progress >= REQUIRED_VITAL_ASSERTIONS ? ChatFormatting.GREEN : ChatFormatting.GOLD)));

        if (progress >= REQUIRED_VITAL_ASSERTIONS) {
            tooltip.add(Component.translatable("ascension.item.false_deified_orb.tooltip.ready")
                    .withStyle(ChatFormatting.GREEN));
        }
    }
}