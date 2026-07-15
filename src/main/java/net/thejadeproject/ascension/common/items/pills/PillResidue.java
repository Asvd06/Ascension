package net.thejadeproject.ascension.common.items.pills;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.physiques.ModPhysiques;
import net.thejadeproject.ascension.util.ModDamageTypes;

public class PillResidue extends Item {

    private static final float DAMAGED_PHYSIQUE_CHANCE = 0.015F;

    private static final float CRIPPLE_WEIGHT = 0.40F;
    private static final float SEVERED_MERIDIANS_WEIGHT = 0.40F;

    public PillCooldownItem.onItemUse consumer;

    public PillResidue(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide() && livingEntity instanceof ServerPlayer player) {

            tryInflictDamagedPhysique(player);
            DamageSource damageSource = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ModDamageTypes.PILL_RESIDUE));

            if (player.isAlive()) {
                player.hurt(damageSource, 1000.0F);
            }

            if (consumer != null) {
                consumer.accept(stack, level, livingEntity);
            }
        }

        return super.finishUsingItem(stack, level, livingEntity);
    }

    private static void tryInflictDamagedPhysique(ServerPlayer player) {
        RandomSource random = player.getRandom();

        if (random.nextFloat() >= DAMAGED_PHYSIQUE_CHANCE) {
            return;
        }

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        ResourceLocation damagedPhysique = selectDamagedPhysique(random);

        if (!entityData.setPhysique(damagedPhysique)) {
            return;
        }

        player.displayClientMessage(getDamageMessage(damagedPhysique), false);
    }

    private static ResourceLocation selectDamagedPhysique(RandomSource random) {
        float roll = random.nextFloat();

        if (roll < CRIPPLE_WEIGHT) {
            return ModPhysiques.CRIPPLE.getId();
        }

        if (roll < CRIPPLE_WEIGHT + SEVERED_MERIDIANS_WEIGHT) {
            return ModPhysiques.SEVERED_MERIDIANS.getId();
        }

        return ModPhysiques.DULL_MIND.getId();
    }

    private static Component getDamageMessage(ResourceLocation physique) {
        if (ModPhysiques.CRIPPLE.getId().equals(physique)) {
            return Component.translatable(
                    "ascension.message.pill_residue.cripple"
            ).withStyle(ChatFormatting.DARK_RED);
        }

        if (ModPhysiques.SEVERED_MERIDIANS.getId().equals(physique)) {
            return Component.translatable(
                    "ascension.message.pill_residue.severed_meridians"
            ).withStyle(ChatFormatting.RED);
        }

        return Component.translatable(
                "ascension.message.pill_residue.dull_mind"
        ).withStyle(ChatFormatting.DARK_PURPLE);
    }
}