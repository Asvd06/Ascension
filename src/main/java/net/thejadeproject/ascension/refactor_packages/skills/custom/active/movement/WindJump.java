package net.thejadeproject.ascension.refactor_packages.skills.custom.active.movement;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastEndData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastResult;
import net.thejadeproject.ascension.refactor_packages.skills.castable.IPreCastData;
import net.thejadeproject.ascension.refactor_packages.skills.custom.active.MovementSkillHelper;
import net.thejadeproject.ascension.refactor_packages.skills.custom.active.SimpleInstantCastSkill;

public class WindJump extends SimpleInstantCastSkill {

    private static final double QI_COST = 18.0D;

    private static final double BASE_UPWARD_BOOST = 0.72D;
    private static final double UPWARD_BOOST_PER_MAJOR = 0.08D;
    private static final double UPWARD_BOOST_PER_MINOR = 0.01D;
    private static final double MAX_UPWARD_BOOST = 1.35D;

    private static final double BASE_FORWARD_BOOST = 0.65D;
    private static final double FORWARD_BOOST_PER_MAJOR = 0.08D;
    private static final double FORWARD_BOOST_PER_MINOR = 0.008D;
    private static final double MAX_FORWARD_BOOST = 1.45D;

    private static final int SLOW_FALLING_TICKS = 50;
    private static final int COOLDOWN_TICKS = 70;

    @Override
    public CastResult canCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) return new CastResult(CastResult.Type.FAILURE);
        if (!player.hasData(ModAttachments.ENTITY_DATA)) return new CastResult(CastResult.Type.FAILURE);
        if (player.onGround()) return new CastResult(CastResult.Type.FAILURE);

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
        if (player.onGround()) return;

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        if (!entityData.getQiContainer().tryConsumeQi(QI_COST)) return;

        Vec3 forward = MovementSkillHelper.horizontalLook(player);
        Vec3 current = player.getDeltaMovement();

        double upwardBoost = calculateUpwardBoost(player);
        double forwardBoost = calculateForwardBoost(player);

        Vec3 newMovement = new Vec3(
                current.x * 0.25D,
                Math.max(current.y, 0.0D),
                current.z * 0.25D
        ).add(forward.scale(forwardBoost)).add(0.0D, upwardBoost, 0.0D);

        player.setDeltaMovement(newMovement);
        player.fallDistance = 0.0F;
        player.hurtMarked = true;

        player.addEffect(new MobEffectInstance(
                MobEffects.SLOW_FALLING,
                SLOW_FALLING_TICKS,
                0,
                true,
                false,
                false
        ));

        player.serverLevel().sendParticles(
                ParticleTypes.CLOUD,
                player.getX(), player.getY() + 0.15D, player.getZ(),
                30,
                0.45D, 0.18D, 0.45D,
                0.1D
        );

        player.serverLevel().sendParticles(
                ParticleTypes.POOF,
                player.getX(), player.getY() + 0.3D, player.getZ(),
                14,
                0.35D, 0.15D, 0.35D,
                0.05D
        );
    }

    private double calculateUpwardBoost(ServerPlayer player) {
        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        IPathData wind = entityData.getPathData(ModPaths.WIND.getId());
        int major = wind != null ? wind.getMajorRealm() : 0;
        int minor = wind != null ? wind.getMinorRealm() : 0;

        double boost = BASE_UPWARD_BOOST
                + major * UPWARD_BOOST_PER_MAJOR
                + minor * UPWARD_BOOST_PER_MINOR;

        return Math.min(boost, MAX_UPWARD_BOOST);
    }

    private double calculateForwardBoost(ServerPlayer player) {
        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        IPathData wind = entityData.getPathData(ModPaths.WIND.getId());
        int major = wind != null ? wind.getMajorRealm() : 0;
        int minor = wind != null ? wind.getMinorRealm() : 0;

        double boost = BASE_FORWARD_BOOST
                + major * FORWARD_BOOST_PER_MAJOR
                + minor * FORWARD_BOOST_PER_MINOR;

        return Math.min(boost, MAX_FORWARD_BOOST);
    }

    @Override
    public int getCooldown(CastEndData castEndData) {
        return COOLDOWN_TICKS;
    }

    @Override
    protected String getTitleKey() {
        return "ascension.skill.wind_jump";
    }

    @Override
    protected String getDescriptionKey() {
        return "ascension.skill.wind_jump.description";
    }
}