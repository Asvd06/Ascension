package net.thejadeproject.ascension.refactor_packages.skills.custom.active.movement;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
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

public class BodyFlashStep extends SimpleInstantCastSkill {

    private static final double QI_COST = 16.0D;

    private static final double BASE_SPEED = 1.65D;
    private static final double SPEED_PER_MAJOR = 0.22D;
    private static final double SPEED_PER_MINOR = 0.025D;
    private static final double MAX_SPEED = 3.2D;

    private static final int COOLDOWN_TICKS = 45;

    @Override
    public CastResult canCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) return new CastResult(CastResult.Type.FAILURE);
        if (!player.hasData(ModAttachments.ENTITY_DATA)) return new CastResult(CastResult.Type.FAILURE);

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

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        if (!entityData.getQiContainer().tryConsumeQi(QI_COST)) return;

        Vec3 direction = MovementSkillHelper.horizontalLook(player);
        double speed = calculateSpeed(player);

        Vec3 currentMovement = player.getDeltaMovement();

        player.setDeltaMovement(
                direction.scale(speed).add(
                        0.0D,
                        Math.max(currentMovement.y, 0.08D),
                        0.0D
                )
        );

        player.fallDistance = 0.0F;
        player.hurtMarked = true;

        player.serverLevel().sendParticles(
                ParticleTypes.CLOUD,
                player.getX(), player.getY() + 0.2D, player.getZ(),
                24,
                0.35D, 0.15D, 0.35D,
                0.08D
        );

        player.serverLevel().sendParticles(
                ParticleTypes.CRIT,
                player.getX(), player.getY() + 0.8D, player.getZ(),
                12,
                0.25D, 0.35D, 0.25D,
                0.04D
        );
    }

    private double calculateSpeed(ServerPlayer player) {
        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        IPathData body = entityData.getPathData(ModPaths.BODY.getId());
        int major = body != null ? body.getMajorRealm() : 0;
        int minor = body != null ? body.getMinorRealm() : 0;

        double speed = BASE_SPEED
                + major * SPEED_PER_MAJOR
                + minor * SPEED_PER_MINOR;

        return Math.min(speed, MAX_SPEED);
    }

    @Override
    public int getCooldown(CastEndData castEndData) {
        return COOLDOWN_TICKS;
    }

    @Override
    protected String getTitleKey() {
        return "ascension.skill.body_flash_step";
    }

    @Override
    protected String getDescriptionKey() {
        return "ascension.skill.body_flash_step.description";
    }
}