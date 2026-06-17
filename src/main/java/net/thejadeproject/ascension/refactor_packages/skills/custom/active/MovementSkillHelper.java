package net.thejadeproject.ascension.refactor_packages.skills.custom.active;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class MovementSkillHelper {

    private MovementSkillHelper() {
    }

    public static Vec3 horizontalLook(ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        Vec3 horizontal = new Vec3(look.x, 0.0D, look.z);

        if (horizontal.lengthSqr() > 1.0E-4D) {
            return horizontal.normalize();
        }

        double yawRadians = Math.toRadians(player.getYRot());

        return new Vec3(
                -Math.sin(yawRadians),
                0.0D,
                Math.cos(yawRadians)
        ).normalize();
    }

    public static Vec3 resolveSafeDestination(ServerPlayer player, Vec3 direction, double range, double stepSize) {
        Level level = player.level();

        Vec3 normalizedDirection = direction.normalize();
        if (normalizedDirection.lengthSqr() < 1.0E-4D) {
            return player.position();
        }

        Vec3 origin = player.position();
        Vec3 lastSafe = origin;

        int steps = Math.max(1, (int) Math.ceil(range / stepSize));

        for (int i = 1; i <= steps; i++) {
            double distance = Math.min(i * stepSize, range);
            Vec3 candidate = origin.add(normalizedDirection.scale(distance));

            AABB testBox = playerBoxAt(player, candidate).deflate(0.001D);

            if (level.getBlockCollisions(player, testBox).iterator().hasNext()) {
                break;
            }

            lastSafe = candidate;
        }

        return lastSafe;
    }

    public static boolean movedFarEnough(Vec3 start, Vec3 end, double minimumDistance) {
        return start.distanceToSqr(end) >= minimumDistance * minimumDistance;
    }

    public static void teleport(ServerPlayer player, Vec3 destination) {
        player.teleportTo(destination.x, destination.y, destination.z);
        player.fallDistance = 0.0F;
        player.hurtMarked = true;
    }

    public static void spawnTrail(
            ServerLevel level,
            ParticleOptions particle,
            Vec3 start,
            Vec3 end,
            int minimumSteps,
            double stepsPerBlock,
            double yOffset
    ) {
        Vec3 diff = end.subtract(start);
        int steps = Math.max(minimumSteps, (int) (diff.length() * stepsPerBlock));

        for (int i = 0; i <= steps; i++) {
            double progress = i / (double) steps;
            Vec3 pos = start.add(diff.scale(progress)).add(0.0D, yOffset, 0.0D);

            level.sendParticles(
                    particle,
                    pos.x, pos.y, pos.z,
                    1,
                    0.02D, 0.02D, 0.02D,
                    0.0D
            );
        }
    }

    private static AABB playerBoxAt(ServerPlayer player, Vec3 position) {
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
}