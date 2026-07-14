package net.thejadeproject.ascension.refactor_packages.events.skills;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.skills.custom.ModSkills;
import net.thejadeproject.ascension.refactor_packages.skills.custom.active.defense.TidalWardSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.elemental.AdamantSpiritSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.elemental.StormheartSkill;

@EventBusSubscriber(modid = AscensionCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class ElementalSkillEvents {

    private ElementalSkillEvents() {
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        applyStormheart(event);
        applyAdamantSpirit(event);
        applyTidalWard(event);
    }

    private static void applyStormheart(LivingIncomingDamageEvent event) {
        Entity attacker = event.getSource().getEntity();

        if (!(attacker instanceof ServerPlayer player)) {
            return;
        }

        if (player == event.getEntity()) return;

        if (!player.hasData(ModAttachments.ENTITY_DATA)) {
            return;
        }

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        if (!entityData.hasSkill(ModSkills.STORMHEART.getId())) {
            return;
        }

        float multiplier = StormheartSkill.consumeDamageMultiplier(player, entityData);

        if (multiplier <= 1.0F) return;

        event.setAmount(event.getAmount() * multiplier);
        spawnStormheartDischarge(player, event.getEntity());
    }

    private static void applyAdamantSpirit(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!player.hasData(ModAttachments.ENTITY_DATA)) {
            return;
        }

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        if (!entityData.hasSkill(ModSkills.ADAMANT_SPIRIT.getId())) {
            return;
        }

        float reduction = AdamantSpiritSkill.getDamageReduction(entityData);

        event.setAmount(event.getAmount() * (1.0F - reduction));

        player.serverLevel().sendParticles(
                ParticleTypes.ENCHANTED_HIT,
                player.getX(),
                player.getY() + player.getBbHeight() * 0.5D,
                player.getZ(),
                6,
                0.28D,
                0.4D,
                0.28D,
                0.02D
        );
    }

    private static void applyTidalWard(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!player.hasData(ModAttachments.ENTITY_DATA)) {
            return;
        }

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        if (!entityData.hasSkill(ModSkills.TIDAL_WARD.getId())) {
            return;
        }

        if (!TidalWardSkill.hasActiveWard(player)) {
            return;
        }

        float before = event.getAmount();
        float after = TidalWardSkill.absorbDamage(player, before);
        float absorbed = before - after;
        if (absorbed <= 0.0F) return;

        event.setAmount(after);

        player.serverLevel().sendParticles(
                ParticleTypes.SPLASH,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                12,
                0.45D,
                0.6D,
                0.45D,
                0.08D
        );

        player.level().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.SHIELD_BLOCK,
                SoundSource.PLAYERS,
                0.55F,
                1.35F
        );
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (player.level().isClientSide()) return;
        if (player.tickCount % 4 != 0) return;

        if (!TidalWardSkill.hasActiveWard(player)) {
            return;
        }

        spawnTidalWardRing(player);
    }

    private static void spawnTidalWardRing(ServerPlayer player) {
        ServerLevel level = player.serverLevel();

        int points = 16;
        double radius = 1.15D;

        double rotation = player.tickCount * 0.08D;

        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0D * i / points + rotation;
            double x = player.getX() + Math.cos(angle) * radius;
            double z = player.getZ() + Math.sin(angle) * radius;
            double y = player.getY() + 0.35D + Math.sin(angle * 2.0D) * 0.2D;

            level.sendParticles(
                    ParticleTypes.SPLASH,
                    x,
                    y,
                    z,
                    1,
                    0.01D,
                    0.02D,
                    0.01D,
                    0.0D
            );
        }
    }

    private static void spawnStormheartDischarge(ServerPlayer attacker, Entity target) {
        ServerLevel level = attacker.serverLevel();
        Vec3 start = attacker.getEyePosition();
        Vec3 end = target.getBoundingBox().getCenter();
        Vec3 difference = end.subtract(start);

        int steps = Math.max(8, (int) (difference.length() * 1.5D));

        for (int i = 0; i <= steps; i++) {
            double progress = i / (double) steps;
            Vec3 position = start.add(difference.scale(progress));

            level.sendParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    position.x,
                    position.y,
                    position.z,
                    1,
                    0.025D,
                    0.025D,
                    0.025D,
                    0.0D
            );
        }

        level.sendParticles(
                ParticleTypes.ELECTRIC_SPARK,
                end.x,
                end.y,
                end.z,
                18,
                0.35D,
                0.45D,
                0.35D,
                0.08D
        );
    }
}