package net.thejadeproject.ascension.refactor_packages.skills.custom;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class SkillTargetingHelper {
    private SkillTargetingHelper() {}

    public static LivingEntity findLookTarget(Player player, double range, double inflate) {
        return findLookTarget(player, range, inflate, true);
    }

    public static LivingEntity findLookTarget(Player player, double range, double inflate, boolean stopAtBlocks) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = eye.add(look.scale(range));

        AABB box = player.getBoundingBox()
                .expandTowards(look.scale(range))
                .inflate(inflate);

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                player.level(),
                player,
                eye,
                end,
                box,
                entity -> entity instanceof LivingEntity living && isValidLivingTarget(player, living)
        );

        if (entityHit == null || !(entityHit.getEntity() instanceof LivingEntity living)) {
            return null;
        }

        if (stopAtBlocks) {
            HitResult blockHit = player.pick(range, 0.0F, false);

            if (
                    blockHit.getType() != HitResult.Type.MISS
                            && eye.distanceToSqr(blockHit.getLocation()) < eye.distanceToSqr(entityHit.getLocation())
            ) {
                return null;
            }
        }

        return living;
    }

    public static List<LivingEntity> findLivingTargetsInLine(
            Player player,
            double range,
            double radius,
            boolean requireLineOfSight
    ) {
        Vec3 start = player.getEyePosition();
        Vec3 direction = player.getLookAngle().normalize();

        AABB area = player.getBoundingBox()
                .expandTowards(direction.scale(range))
                .inflate(radius);

        List<LivingEntity> nearbyTargets = player.level().getEntitiesOfClass(
                LivingEntity.class,
                area,
                target -> isValidLivingTarget(player, target)
                        && (!requireLineOfSight || player.hasLineOfSight(target))
        );

        List<LivingEntity> targets = new ArrayList<>();

        for (LivingEntity target : nearbyTargets) {
            Vec3 targetCenter = target.getBoundingBox().getCenter();
            Vec3 toTarget = targetCenter.subtract(start);

            double projectedDistance = toTarget.dot(direction);
            if (projectedDistance < 0.0D || projectedDistance > range) continue;

            Vec3 closestPoint = start.add(direction.scale(projectedDistance));
            double distanceFromLine = targetCenter.distanceTo(closestPoint);

            if (distanceFromLine <= radius + target.getBbWidth() * 0.5D) {
                targets.add(target);
            }
        }

        return targets;
    }

    public static List<LivingEntity> findLivingTargetsInCone(
            Player player,
            double range,
            double coneDot,
            boolean requireLineOfSight
    ) {
        Vec3 origin = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();

        AABB area = player.getBoundingBox().inflate(range);

        List<LivingEntity> nearbyTargets = player.level().getEntitiesOfClass(
                LivingEntity.class,
                area,
                target -> isValidLivingTarget(player, target)
                        && (!requireLineOfSight || player.hasLineOfSight(target))
        );

        List<LivingEntity> targets = new ArrayList<>();

        for (LivingEntity target : nearbyTargets) {
            Vec3 targetCenter = target.getBoundingBox().getCenter();
            Vec3 toTarget = targetCenter.subtract(origin);

            double distance = toTarget.length();
            if (distance <= 0.001D || distance > range) continue;

            Vec3 directionToTarget = toTarget.normalize();
            double dot = look.dot(directionToTarget);

            if (dot >= coneDot) {
                targets.add(target);
            }
        }

        return targets;
    }

    public static List<LivingEntity> findLivingTargetsInHorizontalCone(
            Player player,
            double range,
            double coneDot,
            boolean requireLineOfSight
    ) {
        Vec3 look = player.getLookAngle();
        Vec3 horizontalLook = new Vec3(look.x, 0.0D, look.z);

        if (horizontalLook.lengthSqr() < 0.001D) {
            return List.of();
        }

        horizontalLook = horizontalLook.normalize();

        Vec3 origin = player.position().add(0.0D, player.getBbHeight() * 0.5D, 0.0D);

        AABB area = player.getBoundingBox().inflate(range);

        List<LivingEntity> nearbyTargets = player.level().getEntitiesOfClass(
                LivingEntity.class,
                area,
                target -> isValidLivingTarget(player, target)
                        && (!requireLineOfSight || player.hasLineOfSight(target))
        );

        List<LivingEntity> targets = new ArrayList<>();

        for (LivingEntity target : nearbyTargets) {
            Vec3 targetCenter = target.getBoundingBox().getCenter();
            Vec3 toTarget = targetCenter.subtract(origin);
            Vec3 horizontalToTarget = new Vec3(toTarget.x, 0.0D, toTarget.z);

            double distance = horizontalToTarget.length();
            if (distance <= 0.001D || distance > range) continue;

            Vec3 directionToTarget = horizontalToTarget.normalize();
            double dot = horizontalLook.dot(directionToTarget);

            if (dot >= coneDot) {
                targets.add(target);
            }
        }

        return targets;
    }

    public static List<LivingEntity> findLivingTargetsBetween(
            Player player,
            Vec3 start,
            Vec3 end,
            double radius,
            boolean requireLineOfSight
    ) {
        Vec3 diff = end.subtract(start);

        if (diff.lengthSqr() < 0.001D) {
            return List.of();
        }

        Vec3 direction = diff.normalize();
        double range = diff.length();

        AABB area = new AABB(start, end).inflate(radius);

        List<LivingEntity> nearbyTargets = player.level().getEntitiesOfClass(
                LivingEntity.class,
                area,
                target -> isValidLivingTarget(player, target)
                        && (!requireLineOfSight || player.hasLineOfSight(target))
        );

        List<LivingEntity> targets = new ArrayList<>();

        for (LivingEntity target : nearbyTargets) {
            Vec3 targetCenter = target.getBoundingBox().getCenter();
            Vec3 toTarget = targetCenter.subtract(start);

            double projectedDistance = toTarget.dot(direction);
            if (projectedDistance < 0.0D || projectedDistance > range) continue;

            Vec3 closestPoint = start.add(direction.scale(projectedDistance));
            double distanceFromLine = targetCenter.distanceTo(closestPoint);

            if (distanceFromLine <= radius + target.getBbWidth() * 0.5D) {
                targets.add(target);
            }
        }

        return targets;
    }

    private static boolean isValidLivingTarget(Player player, LivingEntity target) {
        return target.isAlive()
                && target != player
                && !target.isSpectator()
                && target.isPickable();
    }
}