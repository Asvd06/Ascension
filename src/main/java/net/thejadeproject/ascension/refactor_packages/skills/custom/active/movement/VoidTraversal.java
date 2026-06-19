package net.thejadeproject.ascension.refactor_packages.skills.custom.active.movement;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.skills.SyncVoidTraversalState;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastEndData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastResult;
import net.thejadeproject.ascension.refactor_packages.skills.ITickingSkill;
import net.thejadeproject.ascension.refactor_packages.skills.castable.IPreCastData;
import net.thejadeproject.ascension.refactor_packages.skills.custom.active.SimpleInstantCastSkill;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoidTraversal extends SimpleInstantCastSkill implements ITickingSkill {

    private static final double BASE_QI_COST = 90.0D;

    private static final int BASE_DURATION_TICKS = 50;
    private static final int DURATION_PER_MAJOR = 5;
    private static final int MAX_DURATION_TICKS = 140;

    private static final double BASE_MOVEMENT_MULTIPLIER = 4.0D;
    private static final double MULTIPLIER_PER_MAJOR = 0.5D;
    private static final double MULTIPLIER_PER_MINOR = 0.05D;
    private static final double MAX_MOVEMENT_MULTIPLIER = 12.0D;

    private static final double STEP_SIZE = 0.25D;
    private static final double MIN_MOVEMENT_SQR = 1.0E-5D;

    private static final boolean DEBUG_ACTION_BAR = true;
    private static final int DEBUG_MESSAGE_INTERVAL_TICKS = 10;

    private static final int COOLDOWN_TICKS = 220;

    private static final Map<UUID, VoidTraversalState> ACTIVE_TRAVERSALS = new HashMap<>();

    @Override
    public CastResult canCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) {
            return new CastResult(CastResult.Type.FAILURE);
        }

        if (!player.hasData(ModAttachments.ENTITY_DATA)) {
            return new CastResult(CastResult.Type.FAILURE);
        }

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        if (ACTIVE_TRAVERSALS.containsKey(player.getUUID())) {
            return new CastResult(CastResult.Type.SUCCESS);
        }

        return entityData.getQiContainer().hasQi(BASE_QI_COST)
                ? new CastResult(CastResult.Type.SUCCESS)
                : new CastResult(CastResult.Type.FAILURE);
    }

    @Override
    public void initialCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;
        if (!player.hasData(ModAttachments.ENTITY_DATA)) return;

        UUID uuid = player.getUUID();

        if (ACTIVE_TRAVERSALS.containsKey(uuid)) {
            exitTraversal(player, true);
            return;
        }

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        if (!entityData.getQiContainer().tryConsumeQi(BASE_QI_COST)) {
            return;
        }

        int duration = calculateDuration(player);
        double multiplier = calculateMovementMultiplier(player);

        ACTIVE_TRAVERSALS.put(
                uuid,
                new VoidTraversalState(
                        player.position(),
                        player.position(),
                        duration,
                        multiplier
                )
        );

        sendVoidTraversalState(player, true, duration);

        sendActionBar(
                player,
                Component.literal("Entered Void Traversal | ")
                        .withStyle(ChatFormatting.DARK_PURPLE)
                        .append(Component.literal("duration: " + duration + "t, multiplier: " + format(multiplier) + "x")
                                .withStyle(ChatFormatting.LIGHT_PURPLE))
        );

        player.fallDistance = 0.0F;
        player.hurtMarked = true;

        player.serverLevel().sendParticles(
                ParticleTypes.PORTAL,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                40,
                0.3D,
                0.55D,
                0.3D,
                0.18D
        );

        player.serverLevel().sendParticles(
                ParticleTypes.REVERSE_PORTAL,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                28,
                0.25D,
                0.45D,
                0.25D,
                0.08D
        );

        player.level().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.END_PORTAL_SPAWN,
                SoundSource.PLAYERS,
                0.45F,
                1.65F
        );
    }

    @Override
    public void onPlayerTick(ServerPlayer player, IEntityData entityData) {
        if (player.level().isClientSide()) return;

        VoidTraversalState state = ACTIVE_TRAVERSALS.get(player.getUUID());
        if (state == null) return;

        if (!player.isAlive() || player.isRemoved()) {
            ACTIVE_TRAVERSALS.remove(player.getUUID());
            sendVoidTraversalState(player, false, 0);
            return;
        }

        player.fallDistance = 0.0F;

        Vec3 currentPosition = player.position();

        if (isPositionSafe(player, currentPosition)) {
            state.lastSafePosition = currentPosition;
        }

        spawnAmbientVoidParticles(player, state);

        if (DEBUG_ACTION_BAR && state.ticksRemaining % DEBUG_MESSAGE_INTERVAL_TICKS == 0) {
            Vec3 traveled = currentPosition.subtract(state.originPosition);
            Vec3 horizontalTraveled = new Vec3(traveled.x, 0.0D, traveled.z);

            double walkedBlocks = horizontalTraveled.length();
            double projectedTotalBlocks = walkedBlocks * state.movementMultiplier;
            double extraBlocks = walkedBlocks * (state.movementMultiplier - 1.0D);

            sendActionBar(
                    player,
                    Component.literal("Void Traversal | ")
                            .withStyle(ChatFormatting.DARK_PURPLE)
                            .append(Component.literal("left: " + state.ticksRemaining + "t ")
                                    .withStyle(ChatFormatting.GRAY))
                            .append(Component.literal("walked: " + format(walkedBlocks) + " ")
                                    .withStyle(ChatFormatting.AQUA))
                            .append(Component.literal("extra: " + format(extraBlocks) + " ")
                                    .withStyle(ChatFormatting.LIGHT_PURPLE))
                            .append(Component.literal("total: " + format(projectedTotalBlocks))
                                    .withStyle(ChatFormatting.DARK_AQUA))
            );
        }

        state.ticksRemaining--;

        if (state.ticksRemaining <= 0) {
            exitTraversal(player, false);
        }
    }

    private void exitTraversal(ServerPlayer player, boolean manualExit) {
        VoidTraversalState state = ACTIVE_TRAVERSALS.remove(player.getUUID());
        if (state == null) return;

        sendVoidTraversalState(player, false, 0);

        Vec3 currentPosition = player.position();

        Vec3 traveled = currentPosition.subtract(state.originPosition);
        Vec3 horizontalTraveled = new Vec3(traveled.x, 0.0D, traveled.z);

        double walkedBlocks = horizontalTraveled.length();
        double intendedExtraBlocks = walkedBlocks * (state.movementMultiplier - 1.0D);

        Vec3 exitPosition = resolveFinalTraversalDestination(player, state, currentPosition);
        double actualExtraBlocks = exitPosition.distanceTo(currentPosition);

        player.teleportTo(exitPosition.x, exitPosition.y, exitPosition.z);
        player.setDeltaMovement(player.getDeltaMovement().scale(0.15D));
        player.fallDistance = 0.0F;
        player.hurtMarked = true;

        spawnMovementVoidParticles(player, currentPosition, exitPosition);

        sendActionBar(
                player,
                Component.literal("Exited Void Traversal | ")
                        .withStyle(ChatFormatting.DARK_PURPLE)
                        .append(Component.literal("walked: " + format(walkedBlocks) + " ")
                                .withStyle(ChatFormatting.AQUA))
                        .append(Component.literal("intended extra: " + format(intendedExtraBlocks) + " ")
                                .withStyle(ChatFormatting.LIGHT_PURPLE))
                        .append(Component.literal("actual extra: " + format(actualExtraBlocks))
                                .withStyle(actualExtraBlocks < 0.25D && intendedExtraBlocks > 0.25D
                                        ? ChatFormatting.RED
                                        : ChatFormatting.GREEN))
        );

        player.serverLevel().sendParticles(
                ParticleTypes.REVERSE_PORTAL,
                exitPosition.x,
                exitPosition.y + 1.0D,
                exitPosition.z,
                manualExit ? 38 : 26,
                0.32D,
                0.55D,
                0.32D,
                0.12D
        );

        player.serverLevel().sendParticles(
                ParticleTypes.SONIC_BOOM,
                exitPosition.x,
                exitPosition.y + 1.0D,
                exitPosition.z,
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );

        player.level().playSound(
                null,
                exitPosition.x,
                exitPosition.y,
                exitPosition.z,
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS,
                0.65F,
                manualExit ? 1.25F : 0.95F
        );
    }

    private Vec3 resolveSafeDestination(
            ServerPlayer player,
            Vec3 origin,
            Vec3 direction,
            double distance
    ) {
        Level level = player.level();
        Vec3 lastSafe = origin;

        int steps = Math.max(1, (int) Math.ceil(distance / STEP_SIZE));

        for (int i = 1; i <= steps; i++) {
            double stepDistance = Math.min(i * STEP_SIZE, distance);
            Vec3 candidate = origin.add(direction.scale(stepDistance));

            AABB testBox = playerBoxAt(player, candidate).deflate(0.001D);

            if (level.getBlockCollisions(player, testBox).iterator().hasNext()) {
                break;
            }

            lastSafe = candidate;
        }

        return lastSafe;
    }

    private Vec3 resolveFinalTraversalDestination(
            ServerPlayer player,
            VoidTraversalState state,
            Vec3 currentPosition
    ) {
        Vec3 traveled = currentPosition.subtract(state.originPosition);
        Vec3 horizontalTraveled = new Vec3(traveled.x, 0.0D, traveled.z);

        if (horizontalTraveled.lengthSqr() < MIN_MOVEMENT_SQR) {
            return isPositionSafe(player, currentPosition)
                    ? currentPosition
                    : state.lastSafePosition;
        }

        Vec3 direction = horizontalTraveled.normalize();

        double extraDistance = horizontalTraveled.length() * (state.movementMultiplier - 1.0D);

        Vec3 destination = resolveSafeDestination(
                player,
                currentPosition,
                direction,
                extraDistance
        );

        if (!isPositionSafe(player, destination)) {
            return state.lastSafePosition;
        }

        return destination;
    }

    private boolean isPositionSafe(ServerPlayer player, Vec3 position) {
        AABB testBox = playerBoxAt(player, position).deflate(0.001D);
        return !player.level().getBlockCollisions(player, testBox).iterator().hasNext();
    }

    private AABB playerBoxAt(ServerPlayer player, Vec3 position) {
        double halfWidth = player.getBbWidth() / 2.0D;
        double height = player.getBbHeight();

        return new AABB(
                position.x - halfWidth,
                position.y,
                position.z - halfWidth,
                position.x + halfWidth,
                position.y + height,
                position.z + halfWidth
        );
    }

    private void spawnAmbientVoidParticles(ServerPlayer player, VoidTraversalState state) {
        if (state.ticksRemaining % 5 != 0) return;

        player.serverLevel().sendParticles(
                ParticleTypes.REVERSE_PORTAL,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                5,
                0.22D,
                0.45D,
                0.22D,
                0.025D
        );
    }

    private void spawnMovementVoidParticles(ServerPlayer player, Vec3 start, Vec3 end) {
        Vec3 diff = end.subtract(start);
        int steps = Math.max(2, (int) (diff.length() * 1.25D));

        for (int i = 0; i <= steps; i++) {
            double progress = i / (double) steps;
            Vec3 pos = start.add(diff.scale(progress)).add(0.0D, 1.0D, 0.0D);

            player.serverLevel().sendParticles(
                    ParticleTypes.PORTAL,
                    pos.x,
                    pos.y,
                    pos.z,
                    1,
                    0.02D,
                    0.02D,
                    0.02D,
                    0.0D
            );
        }
    }

    private int calculateDuration(ServerPlayer player) {
        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        IPathData essence = entityData.getPathData(ModPaths.ESSENCE.getId());
        int major = essence != null ? essence.getMajorRealm() : 0;

        int duration = BASE_DURATION_TICKS + major * DURATION_PER_MAJOR;

        return Math.min(duration, MAX_DURATION_TICKS);
    }

    private double calculateMovementMultiplier(ServerPlayer player) {
        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        IPathData essence = entityData.getPathData(ModPaths.ESSENCE.getId());
        int major = essence != null ? essence.getMajorRealm() : 0;
        int minor = essence != null ? essence.getMinorRealm() : 0;

        double multiplier = BASE_MOVEMENT_MULTIPLIER
                + major * MULTIPLIER_PER_MAJOR
                + minor * MULTIPLIER_PER_MINOR;

        return Math.min(multiplier, MAX_MOVEMENT_MULTIPLIER);
    }

    private void sendVoidTraversalState(ServerPlayer player, boolean active, int durationTicks) {
        PacketDistributor.sendToPlayer(
                player,
                new SyncVoidTraversalState(active, durationTicks)
        );
    }

    @Override
    public int getCooldown(CastEndData castEndData) {
        return COOLDOWN_TICKS;
    }

    @Override
    protected String getTitleKey() {
        return "ascension.skill.void_traversal";
    }

    @Override
    protected String getDescriptionKey() {
        return "ascension.skill.void_traversal.description";
    }

    private static final class VoidTraversalState {
        private final Vec3 originPosition;
        private Vec3 lastSafePosition;
        private int ticksRemaining;
        private final double movementMultiplier;

        private VoidTraversalState(
                Vec3 originPosition,
                Vec3 lastSafePosition,
                int ticksRemaining,
                double movementMultiplier
        ) {
            this.originPosition = originPosition;
            this.lastSafePosition = lastSafePosition;
            this.ticksRemaining = ticksRemaining;
            this.movementMultiplier = movementMultiplier;
        }
    }

    private void sendActionBar(ServerPlayer player, Component message) {
        if (!DEBUG_ACTION_BAR) return;
        player.displayClientMessage(message, true);
    }

    private String format(double value) {
        return String.format("%.2f", value);
    }
}