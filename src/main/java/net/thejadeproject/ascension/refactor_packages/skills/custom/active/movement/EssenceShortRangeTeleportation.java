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

public class EssenceShortRangeTeleportation extends SimpleInstantCastSkill {

    private static final double QI_COST = 28.0D;

    private static final double BASE_RANGE = 8.0D;
    private static final double RANGE_PER_MAJOR = 2.0D;
    private static final double RANGE_PER_MINOR = 0.2D;
    private static final double MAX_RANGE = 24.0D;

    private static final double STEP_SIZE = 0.25D;
    private static final int COOLDOWN_TICKS = 80;

    @Override
    public CastResult canCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) return new CastResult(CastResult.Type.FAILURE);
        if (!player.hasData(ModAttachments.ENTITY_DATA)) return new CastResult(CastResult.Type.FAILURE);

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        if (!entityData.getQiContainer().hasQi(QI_COST)) return new CastResult(CastResult.Type.FAILURE);

        Vec3 destination = resolveDestination(player);

        return MovementSkillHelper.movedFarEnough(player.position(), destination, 0.65D)
                ? new CastResult(CastResult.Type.SUCCESS)
                : new CastResult(CastResult.Type.FAILURE);
    }

    @Override
    public void initialCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;
        if (!player.hasData(ModAttachments.ENTITY_DATA)) return;

        Vec3 start = player.position();
        Vec3 destination = resolveDestination(player);

        if (!MovementSkillHelper.movedFarEnough(start, destination, 0.65D)) return;

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        if (!entityData.getQiContainer().tryConsumeQi(QI_COST)) return;

        player.serverLevel().sendParticles(
                ParticleTypes.PORTAL,
                start.x, start.y + 1.0D, start.z,
                28,
                0.25D, 0.45D, 0.25D,
                0.12D
        );

        MovementSkillHelper.spawnTrail(
                player.serverLevel(),
                ParticleTypes.PORTAL,
                start,
                destination,
                12,
                2.0D,
                1.0D
        );

        MovementSkillHelper.teleport(player, destination);

        player.setDeltaMovement(player.getDeltaMovement().scale(0.15D));

        player.serverLevel().sendParticles(
                ParticleTypes.PORTAL,
                destination.x, destination.y + 1.0D, destination.z,
                34,
                0.3D, 0.5D, 0.3D,
                0.14D
        );

    }

    private Vec3 resolveDestination(ServerPlayer player) {
        return MovementSkillHelper.resolveSafeDestination(
                player,
                player.getLookAngle(),
                calculateRange(player),
                STEP_SIZE
        );
    }

    private double calculateRange(ServerPlayer player) {
        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        IPathData essence = entityData.getPathData(ModPaths.ESSENCE.getId());
        int major = essence != null ? essence.getMajorRealm() : 0;
        int minor = essence != null ? essence.getMinorRealm() : 0;

        double range = BASE_RANGE
                + major * RANGE_PER_MAJOR
                + minor * RANGE_PER_MINOR;

        return Math.min(range, MAX_RANGE);
    }

    @Override
    public int getCooldown(CastEndData castEndData) {
        return COOLDOWN_TICKS;
    }

    @Override
    protected String getTitleKey() {
        return "ascension.skill.essence_short_range_teleportation";
    }

    @Override
    protected String getDescriptionKey() {
        return "ascension.skill.essence_short_range_teleportation.description";
    }
}