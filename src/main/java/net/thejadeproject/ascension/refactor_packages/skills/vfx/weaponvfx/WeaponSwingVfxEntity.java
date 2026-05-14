package net.thejadeproject.ascension.refactor_packages.skills.vfx.weaponvfx;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class WeaponSwingVfxEntity extends Entity {

    private static final EntityDataAccessor<Float> ROTATION_Z = SynchedEntityData.defineId(WeaponSwingVfxEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RADIUS_X = SynchedEntityData.defineId(WeaponSwingVfxEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RADIUS_Y = SynchedEntityData.defineId(WeaponSwingVfxEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RADIUS_Z = SynchedEntityData.defineId(WeaponSwingVfxEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<String> VFX_TYPE = SynchedEntityData.defineId(WeaponSwingVfxEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TEX_PATH = SynchedEntityData.defineId(WeaponSwingVfxEntity.class, EntityDataSerializers.STRING);


    // ── VFX type constants (logic tags) ─────────────────────────────────────

    public static final String TYPE_SWORD = "sword_swing";
    public static final String TYPE_AXE   = "axe_swing";
    public static final String TYPE_SPEAR = "spear_thrust";
    public static final String TYPE_MACE  = "mace_smash";


    // ── Server-only fields ───────────────────────────────────────────────────

    private double damage = 4.0;
    private double knockback = 0.5;
    private int duration = 12;
    private LivingEntity owner = null;
    private final List<Integer> hitIds = new ArrayList<>();
    private boolean damageApplied = false;

    // ── Constructor ──────────────────────────────────────────────────────────

    public WeaponSwingVfxEntity(EntityType<? extends WeaponSwingVfxEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }


    // ── Synced data setup ────────────────────────────────────────────────────

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ROTATION_Z, 0.0f);
        builder.define(RADIUS_X,   2.0f);
        builder.define(RADIUS_Y,   2.0f);
        builder.define(RADIUS_Z,   2.0f);
        builder.define(VFX_TYPE,   TYPE_SWORD);
        builder.define(TEX_PATH,   "entity/vfx/" + TYPE_SWORD + "/blue");
    }

    // ── Tick ─────────────────────────────────────────────────────────────────

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (!damageApplied) {
                if (TYPE_SPEAR.equals(getVfxType())) {
                    applyRayDamage();
                } else {
                    applyAreaDamage();
                }
                damageApplied = getDeltaMovement().lengthSqr() < 1e-6;
            }
        }

        Vec3 delta = getDeltaMovement();
        this.move(MoverType.SELF, delta);
        this.setDeltaMovement(delta.scale(0.92));

        if (this.tickCount >= duration) {
            this.discard();
        }
    }

    // ── Damage helpers ───────────────────────────────────────────────────────

    private void applyAreaDamage() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;
        Vector3f r   = getRadius();
        float    avg = (r.x + r.y + r.z) / 5.0f;
        AABB box = this.getBoundingBox().inflate(avg);
        for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, box, this::isValidTarget)) {
            if (hitIds.contains(target.getId())) continue;
            applyKnockback(target);
            applyDamage(target);
            hitIds.add(target.getId());
        }
    }

    private void applyRayDamage() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;
        Vec3   dir      = this.getLookAngle();
        double halfLen  = getRadius().z * 0.5;
        Vec3   rayStart = this.position().add(dir.scale(halfLen));
        Vec3   rayEnd   = this.position().add(dir.scale(-halfLen));
        for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(halfLen), this::isValidTarget)) {
            if (hitIds.contains(target.getId())) continue;
            if (!target.getBoundingBox().intersects(rayStart, rayEnd)) continue;
            applyDamage(target);
            if (owner != null) {
                Vec3 kb = this.position().subtract(owner.position()).normalize().scale(knockback);
                target.setDeltaMovement(kb.x, 0.3 * knockback, kb.z);
            }
            hitIds.add(target.getId());
        }
    }

    private void applyDamage(LivingEntity target) {
        if (TYPE_MACE.equals(getVfxType())) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
        }
        target.hurt(this.damageSources().mobAttack(owner), (float) damage);
        target.invulnerableTime = getDeltaMovement().lengthSqr() < 1e-6 ? 0 : 8;
    }

    private void applyKnockback(LivingEntity target) {
        if (owner == null || target.invulnerableTime != 0) return;
        Vec3 dir = target.position().subtract(this.position()).normalize().scale(knockback);
        target.setDeltaMovement(dir.x, 0.3 * knockback, dir.z);
    }

    private boolean isValidTarget(LivingEntity e) {
        if (e == owner) return false;
        if (owner == null) return true;
        return !e.isAlliedTo(owner);
    }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setVfxType(String type) { this.entityData.set(VFX_TYPE,   type); }
    public void setTexPath(String path) { this.entityData.set(TEX_PATH,   path); }
    public void setRotationZ(float rot) { this.entityData.set(ROTATION_Z, rot);  }
    public void setRadius(Vector3f r) {
        this.entityData.set(RADIUS_X, r.x);
        this.entityData.set(RADIUS_Y, r.y);
        this.entityData.set(RADIUS_Z, r.z);
    }
    public void setOwner(LivingEntity owner) { this.owner = owner;}
    public void setDamage(double damage) { this.damage = damage;}
    public void setKnockback(double knockback) { this.knockback = knockback;}
    public void setDuration(int ticks) { this.duration = ticks;}

    // ── Getters ──────────────────────────────────────────────────────────────

    /** Logic tag — used server-side for hit detection mode. */
    public String getVfxType() { return this.entityData.get(VFX_TYPE);}
    /** Resolved texture sub-path — used client-side by the renderer. */
    public String getTexPath() { return this.entityData.get(TEX_PATH);}
    public float getRotationZ() { return this.entityData.get(ROTATION_Z);}
    public Vector3f getRadius() {
        return new Vector3f(
                this.entityData.get(RADIUS_X),
                this.entityData.get(RADIUS_Y),
                this.entityData.get(RADIUS_Z));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }
}
