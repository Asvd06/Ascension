package net.thejadeproject.ascension.refactor_packages.skills.vfx.weaponvfx;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.thejadeproject.ascension.entity.ModEntities;
import org.joml.Vector3f;

/**
 * Static helpers for spawning WeaponSwingVfxEntity.
 * All spawning must happen server-side (level.isClientSide == false).
 */
public class WeaponVfxUtils {

    /**
     * Full-parameter spawn.
     *
     * @param techniqueId The technique the player is currently using on this path.
     *                    Passed to VfxColorRegistry to resolve the correct color folder.
     *                    Pass null to use the weapon type's default fallback color.
     * @param fallbackColor The color folder to use when techniqueId has no mapping.
     */
    public static void spawnSwingVfx(Level level, LivingEntity owner, Vec3 position, float yRotOffset, float xRotOffset,
                                     float rotZ, Vector3f radius, double damage, double knockback,
                                     int duration, String vfxType, Vec3 movement, ResourceLocation techniqueId, String fallbackColor) {
        if (level.isClientSide) return;

        WeaponSwingVfxEntity vfx = new WeaponSwingVfxEntity(ModEntities.WEAPON_SWING_VFX.get(), level);

        vfx.setPos(position.x, position.y, position.z);
        vfx.setXRot(owner.getXRot() + xRotOffset);
        vfx.setYRot(owner.getYRot() + yRotOffset);
        vfx.setRotationZ(rotZ);
        vfx.setOwner(owner);
        vfx.setRadius(radius);
        vfx.setDamage(damage);
        vfx.setKnockback(knockback);
        vfx.setDuration(duration);
        vfx.setVfxType(vfxType);

        String texPath = VfxColorRegistry.resolveTexturePath(vfxType, techniqueId, fallbackColor);
        vfx.setTexPath(texPath);

        if (!movement.equals(Vec3.ZERO)) {
            Vec3 forward = owner.getLookAngle();
            Vec3 right = forward.cross(new Vec3(0, 1, 0)).normalize();
            Vec3 up = new Vec3(0, 1, 0);
            vfx.setDeltaMovement(
                    forward.scale(movement.z)
                            .add(up.scale(movement.y))
                            .add(right.scale(movement.x))
                            .scale(0.8));
        }

        level.addFreshEntity(vfx);
    }

    /**
     * Convenience — spawns directly in front of the owner at eye-level.
     * Technique ID is resolved through VfxColorRegistry automatically.
     */
    public static void spawnSwingVfxAhead(Level level,
                                          LivingEntity owner, float rotZ, Vector3f radius, double damage, double knockback,
                                          int duration, String vfxType, ResourceLocation techniqueId, String fallbackColor) {
        Vec3 pos = owner.getEyePosition().add(owner.getLookAngle().scale(1.2));
        spawnSwingVfx(level, owner, pos,
                0, 0, rotZ,
                radius, damage, knockback, duration,
                vfxType, Vec3.ZERO,
                techniqueId, fallbackColor);
    }
}
