package net.thejadeproject.ascension.common.items.physiques;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.common.items.pills.PillRealmData;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.physiques.ModPhysiques;
import net.thejadeproject.ascension.refactor_packages.physiques.custom.EvolvingPhysique;
import net.thejadeproject.ascension.refactor_packages.events.physiques.ElementalBodyTransformationEvents;

import java.util.List;

public class FiveElementHarmonyPillItem extends Item {

    public FiveElementHarmonyPillItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.EAT; }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) { return 32; }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);
        if (level.isClientSide() || !(livingEntity instanceof ServerPlayer player)) return result;

        var entityData = player.getData(ModAttachments.ENTITY_DATA);

        if (!(entityData.getPhysique() instanceof EvolvingPhysique evolvingPhysique)) {
            player.sendSystemMessage(Component.translatable("ascension.harmony_pill.not_elemental_body").withStyle(ChatFormatting.RED));
            return result;
        }

        ResourceLocation fivePalaceId = ModPhysiques.FIVE_PALACE_IMMORTAL.getId();
        if (!evolvingPhysique.canEvolveInto(fivePalaceId)) {
            player.sendSystemMessage(Component.translatable("ascension.harmony_pill.not_elemental_body").withStyle(ChatFormatting.RED));
            return result;
        }

        if (ElementalBodyTransformationEvents.isTransforming(player)) {
            player.sendSystemMessage(Component.translatable("ascension.harmony_pill.already_transforming").withStyle(ChatFormatting.RED));
            return result;
        }

        if (!player.getAbilities().instabuild) stack.shrink(1);

        // PILL_PURITY now stores a grade (0-3). Pass the grade directly.
        // ElementalBodyTransformationEvents.beginTransformation should accept
        // a grade in place of the old raw purity; adjust its signature if needed.
        Integer grade = stack.get(ModDataComponents.PILL_PURITY.get());
        ElementalBodyTransformationEvents.beginTransformation(player, grade != null ? grade : PillRealmData.GRADE_BASIC);
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        Integer majorRealm = stack.get(ModDataComponents.PILL_MAJOR_REALM.get());
        Integer grade      = stack.get(ModDataComponents.PILL_PURITY.get());

        if (majorRealm == null && grade == null) {
            tooltip.add(Component.literal("Unrefined").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        } else if (majorRealm != null && grade != null) {
            tooltip.add(Component.literal("Pill Realm: ").withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(majorRealm + " — " + PillRealmData.getMajorRealmName(majorRealm)).withStyle(ChatFormatting.WHITE)));
            tooltip.add(Component.literal("Purity: ").withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal(PillRealmData.getPurityGradeName(grade)).withStyle(PillRealmData.getPurityGradeColor(grade))));
        }

        tooltip.add(Component.literal("✦ Effects: ").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("ascension.item.five_element_harmony_pill.tooltip").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("ascension.item.five_element_harmony_pill.tooltip2").withStyle(ChatFormatting.GRAY));
    }
}