package net.thejadeproject.ascension.common.items.herbs;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;
/**
 * Base class for herb items that are also block items (plantable via ItemNameBlockItem).
 *
 * Custom eat effects are passed as a lambda — no subclass needed per herb.
 *
 * Usage — no special effect:
 *   new HerbBlockItem(ModBlocks.HUNDRED_YEAR_GINSENG_CROP.get(), new Item.Properties()...)
 *
 * Usage — with eat effect:
 *   new HerbBlockItem(ModBlocks.HUNDRED_YEAR_SNOW_GINSENG_CROP.get(), new Item.Properties()...,
 *       (stack, level, entity) -> {
 *           Player p = (Player) entity;
 *           p.setTicksFrozen(300);
 *           p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
 *       })
 */
public class HerbBlockItem extends ItemNameBlockItem {

    /**
     * Called server-side after the herb is eaten.
     * entity is always a Player (checked before calling).
     */
    @FunctionalInterface
    public interface EatEffect {
        void apply(ItemStack stack, Level level, LivingEntity entity);
    }

    @Nullable
    private final EatEffect eatEffect;

    /** No special eat effect. */
    public HerbBlockItem(Block block, Properties properties) {
        this(block, properties, null);
    }

    /** With a custom eat effect. */
    public HerbBlockItem(Block block, Properties properties, @Nullable EatEffect eatEffect) {
        super(block, properties);
        this.eatEffect = eatEffect;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (eatEffect != null && !level.isClientSide && entity instanceof Player) {
            eatEffect.apply(stack, level, entity);
        }
        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        HerbQuality.appendHerbTooltip(stack, tooltip);
    }
}
