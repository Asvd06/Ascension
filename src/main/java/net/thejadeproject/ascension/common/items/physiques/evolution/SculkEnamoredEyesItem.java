package net.thejadeproject.ascension.common.items.physiques.evolution;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.thejadeproject.ascension.common.items.ModItems;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.refactor_packages.physiques.ModPhysiques;
import net.thejadeproject.ascension.refactor_packages.physiques.custom.helpers.PhysiqueEvolutionHelper;

import java.util.List;
import java.util.function.Predicate;

public class SculkEnamoredEyesItem extends Item {
    public static final int REQUIRED_ABERRANT_INSIGHT = 3333;
    private static final int INSIGHT_PER_XP_LEVEL = 1;
    private static final int INSIGHT_PER_ECHO_SHARD = 33;

    public SculkEnamoredEyesItem(Properties properties) {
        super(properties);
    }

    public static int getAberrantInsight(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.ABERRANT_INSIGHT.get(), 0);
    }

    public static boolean isAwakened(ItemStack stack) {
        return getAberrantInsight(stack) >= REQUIRED_ABERRANT_INSIGHT;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(context.getPlayer() instanceof ServerPlayer player)) {
            return InteractionResult.FAIL;
        }

        ItemStack stack = context.getItemInHand();

        if (!level.getBlockState(context.getClickedPos()).is(Blocks.REINFORCED_DEEPSLATE)) {
            return InteractionResult.PASS;
        }

        if (isAwakened(stack)) {
            player.displayClientMessage(Component.translatable("ascension.item.sculk_enamored_eyes.already_awakened")
                    .withStyle(ChatFormatting.GRAY), true);
            return InteractionResult.SUCCESS;
        }

        if (player.getAbilities().instabuild) {
            stack.set(ModDataComponents.ABERRANT_INSIGHT.get(), REQUIRED_ABERRANT_INSIGHT);

            player.displayClientMessage(Component.translatable("ascension.item.sculk_enamored_eyes.awakened")
                    .withStyle(ChatFormatting.DARK_AQUA), true);

            return InteractionResult.CONSUME;
        }

        int before = getAberrantInsight(stack);
        int remaining = REQUIRED_ABERRANT_INSIGHT - before;

        int gained = 0;

        int shardsToConsume = Math.min(
                countItems(player, Items.ECHO_SHARD),
                remaining / INSIGHT_PER_ECHO_SHARD
        );

        if (shardsToConsume > 0) {
            consumeItems(player, Items.ECHO_SHARD, shardsToConsume);
            gained += shardsToConsume * INSIGHT_PER_ECHO_SHARD;
            remaining -= shardsToConsume * INSIGHT_PER_ECHO_SHARD;
        }

        int levelsToConsume = Math.min(player.experienceLevel, remaining / INSIGHT_PER_XP_LEVEL);

        if (levelsToConsume > 0) {
            player.giveExperienceLevels(-levelsToConsume);
            gained += levelsToConsume * INSIGHT_PER_XP_LEVEL;
            remaining -= levelsToConsume * INSIGHT_PER_XP_LEVEL;
        }

        if (gained == 0 && remaining > 0 && countItems(player, Items.ECHO_SHARD) > 0) {
            consumeItems(player, Items.ECHO_SHARD, 1);
            gained += remaining;
        }

        if (gained <= 0) {
            player.displayClientMessage(Component.translatable("ascension.item.sculk_enamored_eyes.needs_offering")
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }

        int after = Math.min(REQUIRED_ABERRANT_INSIGHT, before + gained);
        stack.set(ModDataComponents.ABERRANT_INSIGHT.get(), after);

        if (after >= REQUIRED_ABERRANT_INSIGHT) {
            player.displayClientMessage(Component.translatable("ascension.item.sculk_enamored_eyes.awakened")
                    .withStyle(ChatFormatting.DARK_AQUA), true);
        } else {
            player.displayClientMessage(Component.translatable(
                    "ascension.item.sculk_enamored_eyes.progress",
                    after,
                    REQUIRED_ABERRANT_INSIGHT
            ).withStyle(ChatFormatting.DARK_AQUA), true);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack eyes = player.getItemInHand(hand);

        if (level.isClientSide()) {
            return InteractionResultHolder.success(eyes);
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.fail(eyes);
        }

        if (!isAwakened(eyes)) {
            serverPlayer.displayClientMessage(Component.translatable(
                    "ascension.item.sculk_enamored_eyes.not_awakened"
            ).withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(eyes);
        }

        ItemStack soul = findFirst(serverPlayer, stack ->
                stack.is(ModItems.DISEMBODIED_NETHER_SOUL.get())
                        && DisembodiedNetherSoulItem.isComplete(stack)
        );

        ItemStack orb = findFirst(serverPlayer, stack ->
                stack.is(ModItems.FALSE_DEIFIED_ORB.get())
                        && FalseDeifiedOrbItem.isComplete(stack)
        );

        if (soul.isEmpty() || orb.isEmpty()) {
            serverPlayer.displayClientMessage(Component.translatable(
                    "ascension.item.sculk_enamored_eyes.missing_completion_items"
            ).withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(eyes);
        }

        boolean evolved = PhysiqueEvolutionHelper.tryEvolveInto(
                serverPlayer,
                ModPhysiques.PERFECTED_ABERRANT_ENTITY.getId()
        );

        if (!evolved) {
            serverPlayer.displayClientMessage(Component.translatable(
                    "ascension.item.sculk_enamored_eyes.cannot_perfect"
            ).withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(eyes);
        }

        if (!serverPlayer.getAbilities().instabuild) {
            eyes.shrink(1);
            soul.shrink(1);
            orb.shrink(1);
        }

        return InteractionResultHolder.consume(eyes);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int insight = getAberrantInsight(stack);

        tooltip.add(Component.translatable("ascension.item.sculk_enamored_eyes.tooltip.aberrant_insight")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(insight + " / " + REQUIRED_ABERRANT_INSIGHT)
                        .withStyle(insight >= REQUIRED_ABERRANT_INSIGHT ? ChatFormatting.GREEN : ChatFormatting.GOLD)));

        if (insight >= REQUIRED_ABERRANT_INSIGHT) {
            tooltip.add(Component.translatable("ascension.item.sculk_enamored_eyes.tooltip.ready")
                    .withStyle(ChatFormatting.GREEN));
        } else {
            tooltip.add(Component.translatable("ascension.item.sculk_enamored_eyes.tooltip.awaken_hint")
                    .withStyle(ChatFormatting.DARK_AQUA));
        }
    }

    private static ItemStack findFirst(ServerPlayer player, Predicate<ItemStack> predicate) {
        for (ItemStack stack : player.getInventory().items) {
            if (!stack.isEmpty() && predicate.test(stack)) {
                return stack;
            }
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (!stack.isEmpty() && predicate.test(stack)) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    private static boolean consumeItems(ServerPlayer player, Item item, int count) {
        int found = 0;

        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(item)) {
                found += stack.getCount();
            }
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (stack.is(item)) {
                found += stack.getCount();
            }
        }

        if (found < count) {
            return false;
        }

        int remaining = count;

        for (ItemStack stack : player.getInventory().items) {
            if (remaining <= 0) break;
            if (!stack.is(item)) continue;

            int toRemove = Math.min(remaining, stack.getCount());
            stack.shrink(toRemove);
            remaining -= toRemove;
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (remaining <= 0) break;
            if (!stack.is(item)) continue;

            int toRemove = Math.min(remaining, stack.getCount());
            stack.shrink(toRemove);
            remaining -= toRemove;
        }

        return true;
    }

    private static int countItems(ServerPlayer player, Item item) {
        int found = 0;

        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(item)) {
                found += stack.getCount();
            }
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (stack.is(item)) {
                found += stack.getCount();
            }
        }

        return found;
    }
}