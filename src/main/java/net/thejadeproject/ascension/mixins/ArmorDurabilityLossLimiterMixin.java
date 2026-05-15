package net.thejadeproject.ascension.mixins;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.thejadeproject.ascension.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class ArmorDurabilityLossLimiterMixin extends LivingEntity {

    @Unique
    private static final float VANILLA_ARMOR_DAMAGE_DIVISOR = 4.0F;

    protected ArmorDurabilityLossLimiterMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "hurtArmor", at = @At("HEAD"), cancellable = true)
    private void ascension$limitArmorDurabilityLoss(
            DamageSource damageSource,
            float damage,
            CallbackInfo ci
    ) {
        if (!Config.COMMON.ARMOR_DURABILITY_LIMIT_ENABLED.get()) {
            return;
        }

        if (damage <= 0.0F) {
            ci.cancel();
            return;
        }

        double maxLossPercent = Config.COMMON.ARMOR_DURABILITY_MAX_LOSS_PERCENT.get();
        int minLoss = Config.COMMON.ARMOR_DURABILITY_MIN_LOSS.get();

        for (EquipmentSlot slot : new EquipmentSlot[] {
                EquipmentSlot.FEET,
                EquipmentSlot.LEGS,
                EquipmentSlot.CHEST,
                EquipmentSlot.HEAD
        }) {
            ItemStack stack = this.getItemBySlot(slot);

            if (stack.isEmpty() || !stack.isDamageableItem()) {
                continue;
            }

            int maxDurability = stack.getMaxDamage();

            int maxLossFromPercent = (int) Math.floor(maxDurability * maxLossPercent);

            int maxAllowedLoss = Math.max(minLoss, maxLossFromPercent);

            if (maxAllowedLoss <= 0) {
                continue;
            }

            float cappedDamageInput = Math.min(
                    damage,
                    maxAllowedLoss * VANILLA_ARMOR_DAMAGE_DIVISOR
            );

            this.doHurtEquipment(damageSource, cappedDamageInput, slot);
        }

        ci.cancel();
    }
}