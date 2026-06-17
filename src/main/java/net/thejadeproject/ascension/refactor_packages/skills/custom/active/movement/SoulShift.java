package net.thejadeproject.ascension.refactor_packages.skills.custom.active.movement;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

public class SoulShift extends SimpleInstantCastSkill {

    private static final double QI_COST = 20.0D;

    private static final double BASE_DISTANCE = 4.0D;
    private static final double DISTANCE_PER_MAJOR = 0.65D;
    private static final double DISTANCE_PER_MINOR = 0.06D;
    private static final double MAX_DISTANCE = 10.0D;

    private static final double STEP_SIZE = 0.25D;
    private static final int COOLDOWN_TICKS = 60;

    @Override
    public CastResult canCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) return new CastResult(CastResult.Type.FAILURE);
        if (!player.hasData(ModAttachments.ENTITY_DATA)) return new CastResult(CastResult.Type.FAILURE);

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        if (!entityData.getQiContainer().hasQi(QI_COST)) return new CastResult(CastResult.Type.FAILURE);

        Vec3 destination = resolveShiftDestination(player);

        return MovementSkillHelper.movedFarEnough(player.position(), destination, 0.75D)
                ? new CastResult(CastResult.Type.SUCCESS)
                : new CastResult(CastResult.Type.FAILURE);
    }

    @Override
    public void initialCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;
        if (!player.hasData(ModAttachments.ENTITY_DATA)) return;

        Vec3 start = player.position();
        Vec3 destination = resolveShiftDestination(player);

        if (!MovementSkillHelper.movedFarEnough(start, destination, 0.75D)) return;

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        if (!entityData.getQiContainer().tryConsumeQi(QI_COST)) return;

        player.serverLevel().sendParticles(
                ParticleTypes.SOUL,
                start.x, start.y + 1.0D, start.z,
                16,
                0.2D, 0.35D, 0.2D,
                0.04D
        );

        MovementSkillHelper.spawnTrail(
                player.serverLevel(),
                ParticleTypes.SOUL,
                start,
                destination,
                8,
                1.6D,
                1.0D
        );

        MovementSkillHelper.teleport(player, destination);
        player.setDeltaMovement(player.getDeltaMovement().scale(0.05D));

        player.serverLevel().sendParticles(
                ParticleTypes.SOUL,
                destination.x, destination.y + 1.0D, destination.z,
                24,
                0.25D, 0.45D, 0.25D,
                0.05D
        );

        player.level().playSound(
                null,
                destination.x, destination.y, destination.z,
                SoundEvents.SOUL_ESCAPE.value(),
                SoundSource.PLAYERS,
                0.75F,
                1.55F
        );
    }

    private Vec3 resolveShiftDestination(ServerPlayer player) {
        Vec3 start = player.position();

        Vec3 forward = MovementSkillHelper.horizontalLook(player);
        Vec3 right = new Vec3(-forward.z, 0.0D, forward.x).normalize();

        Vec3[] directions;

        if (player.isShiftKeyDown()) {
            directions = new Vec3[] {
                    forward.scale(-1.0D),
                    right,
                    right.scale(-1.0D),
                    forward
            };
        } else {
            directions = new Vec3[] {
                    right,
                    right.scale(-1.0D),
                    forward.scale(-1.0D),
                    forward
            };
        }

        double distance = calculateDistance(player);

        for (Vec3 direction : directions) {
            Vec3 destination = MovementSkillHelper.resolveSafeDestination(
                    player,
                    direction,
                    distance,
                    STEP_SIZE
            );

            if (MovementSkillHelper.movedFarEnough(start, destination, 0.75D)) {
                return destination;
            }
        }

        return start;
    }

    private double calculateDistance(ServerPlayer player) {
        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        IPathData soul = entityData.getPathData(ModPaths.SOUL.getId());
        int major = soul != null ? soul.getMajorRealm() : 0;
        int minor = soul != null ? soul.getMinorRealm() : 0;

        double distance = BASE_DISTANCE
                + major * DISTANCE_PER_MAJOR
                + minor * DISTANCE_PER_MINOR;

        return Math.min(distance, MAX_DISTANCE);
    }

    @Override
    public int getCooldown(CastEndData castEndData) {
        return COOLDOWN_TICKS;
    }

    @Override
    protected String getTitleKey() {
        return "ascension.skill.soul_shift";
    }

    @Override
    protected String getDescriptionKey() {
        return "ascension.skill.soul_shift.description";
    }
}