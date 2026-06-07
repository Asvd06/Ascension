package net.thejadeproject.ascension.refactor_packages.skills.custom.active.attack.weapon;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.handlers.AscensionDamageHandler;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastEndData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastResult;
import net.thejadeproject.ascension.refactor_packages.skills.custom.SkillTargetingHelper;
import net.thejadeproject.ascension.refactor_packages.skills.custom.active.SimpleInstantCastSkill;
import net.thejadeproject.ascension.refactor_packages.skills.castable.IPreCastData;
import net.thejadeproject.ascension.util.ModTags;

import java.util.HashSet;
import java.util.List;

public class BladeCleave extends SimpleInstantCastSkill {

    private static final double QI_COST = 38.0D;

    private static final double BASE_RANGE = 4.5D;
    private static final double RANGE_PER_MAJOR = 0.75D;
    private static final double RANGE_PER_MINOR = 0.07D;
    private static final double MAX_RANGE = 8.5D;

    private static final double CONE_DOT = 0.45D;

    private static final float BASE_DAMAGE = 5.0F;
    private static final int COOLDOWN_TICKS = 130;

    @Override
    public CastResult canCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) {
            return new CastResult(CastResult.Type.FAILURE);
        }

        if (!player.hasData(ModAttachments.ENTITY_DATA)) {
            return new CastResult(CastResult.Type.FAILURE);
        }

        if (!player.getMainHandItem().is(ModTags.Items.BLADE)) {
            return new CastResult(CastResult.Type.FAILURE);
        }

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        return entityData.getQiContainer().hasQi(QI_COST)
                ? new CastResult(CastResult.Type.SUCCESS)
                : new CastResult(CastResult.Type.FAILURE);
    }

    @Override
    public void initialCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;
        if (!player.hasData(ModAttachments.ENTITY_DATA)) return;
        if (!player.getMainHandItem().is(ModTags.Items.BLADE)) return;

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        if (!entityData.getQiContainer().tryConsumeQi(QI_COST)) {
            return;
        }

        damageTargetsInCone(player);
        spawnCleaveParticles(player);

        player.swing(InteractionHand.MAIN_HAND, true);
    }

    private void damageTargetsInCone(ServerPlayer player) {
        Vec3 look = player.getLookAngle().normalize();

        List<LivingEntity> targets = SkillTargetingHelper.findLivingTargetsInHorizontalCone(
                player,
                calculateRange(player),
                CONE_DOT,
                true
        );

        float damage = calculateDamage(player);

        HashSet<ResourceLocation> paths = new HashSet<>();
        paths.add(ModPaths.BLADE.getId());

        AscensionDamageHandler.AscensionDamageSource source =
                new AscensionDamageHandler.AscensionDamageSource(
                        paths,
                        player.damageSources().playerAttack(player)
                );

        for (LivingEntity target : targets) {
            target.hurt(source, damage);

            Vec3 push = look.scale(0.65D);
            target.push(push.x, 0.18D, push.z);
            target.hurtMarked = true;
        }
    }

    private double calculateRange(ServerPlayer player) {
        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        IPathData bladeData = entityData.getPathData(ModPaths.BLADE.getId());
        int major = bladeData != null ? bladeData.getMajorRealm() : 0;
        int minor = bladeData != null ? bladeData.getMinorRealm() : 0;

        double range = BASE_RANGE
                + major * RANGE_PER_MAJOR
                + minor * RANGE_PER_MINOR;

        return Math.min(range, MAX_RANGE);
    }

    private float calculateDamage(ServerPlayer player) {
        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        IPathData bladeData = entityData.getPathData(ModPaths.BLADE.getId());
        int major = bladeData != null ? bladeData.getMajorRealm() : 0;
        int minor = bladeData != null ? bladeData.getMinorRealm() : 0;

        float attackDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);

        float multiplier = 1.35F + major * 0.22F + minor * 0.035F;

        return BASE_DAMAGE + attackDamage * multiplier;
    }

    private void spawnCleaveParticles(ServerPlayer player) {
        ServerLevel level = player.serverLevel();

        Vec3 look = player.getLookAngle();
        Vec3 forward = new Vec3(look.x, 0.0D, look.z);

        if (forward.lengthSqr() < 0.001D) {
            forward = player.getViewVector(1.0F);
        }

        forward = forward.normalize();

        Vec3 right = new Vec3(-forward.z, 0.0D, forward.x).normalize();
        Vec3 origin = player.position().add(0.0D, 1.15D, 0.0D);

        double range = calculateRange(player);
        double particleRange = range * 0.75D;

        int steps = 24;
        double arcDegrees = 110.0D;
        double startAngle = -arcDegrees * 0.5D;
        double angleStep = arcDegrees / steps;

        for (int i = 0; i <= steps; i++) {
            double angle = Math.toRadians(startAngle + angleStep * i);

            Vec3 direction = forward.scale(Math.cos(angle))
                    .add(right.scale(Math.sin(angle)))
                    .normalize();

            Vec3 pos = origin.add(direction.scale(particleRange));

            level.sendParticles(
                    ParticleTypes.SWEEP_ATTACK,
                    pos.x, pos.y, pos.z,
                    1,
                    0.0D, 0.0D, 0.0D,
                    0.0D
            );

            level.sendParticles(
                    ParticleTypes.CRIT,
                    pos.x, pos.y, pos.z,
                    1,
                    0.05D, 0.05D, 0.05D,
                    0.01D
            );
        }
    }

    @Override
    public int getCooldown(CastEndData castEndData) {
        return COOLDOWN_TICKS;
    }

    @Override
    protected String getTitleKey() {
        return "ascension.skill.blade_cleave";
    }

    @Override
    protected String getDescriptionKey() {
        return "ascension.skill.blade_cleave.description";
    }
}