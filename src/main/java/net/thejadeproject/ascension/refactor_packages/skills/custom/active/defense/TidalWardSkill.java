package net.thejadeproject.ascension.refactor_packages.skills.custom.active.defense;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastEndData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastResult;
import net.thejadeproject.ascension.refactor_packages.skills.IPersistentSkillData;
import net.thejadeproject.ascension.refactor_packages.skills.castable.IPreCastData;
import net.thejadeproject.ascension.refactor_packages.skills.custom.active.SimpleInstantCastSkill;

public class TidalWardSkill extends SimpleInstantCastSkill {

    public static final String WARD_REMAINING_KEY = AscensionCraft.MOD_ID + ":tidal_ward_remaining";

    public static final String WARD_EXPIRY_KEY = AscensionCraft.MOD_ID + ":tidal_ward_expiry";

    private static final double QI_COST = 45.0D;

    private static final int BASE_DURATION_TICKS = 160;
    private static final int DURATION_PER_MAJOR_REALM = 20;
    private static final int MAX_DURATION_TICKS = 300;

    private static final double BASE_HEALTH_FRACTION = 0.08D;
    private static final double FRACTION_PER_MAJOR_REALM = 0.025D;
    private static final double FRACTION_PER_MINOR_REALM = 0.0025D;
    private static final double MAX_HEALTH_FRACTION = 0.30D;

    private static final float DAMAGE_ABSORPTION_RATIO = 0.70F;

    private static final int COOLDOWN_TICKS = 180;

    @Override
    public CastResult canCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) {
            return new CastResult(CastResult.Type.FAILURE);
        }

        if (!player.hasData(ModAttachments.ENTITY_DATA)) {
            return new CastResult(CastResult.Type.FAILURE);
        }

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        return entityData.getQiContainer().hasQi(QI_COST) ? new CastResult(CastResult.Type.SUCCESS) : new CastResult(CastResult.Type.FAILURE);
    }

    @Override
    public void initialCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;
        if (!player.hasData(ModAttachments.ENTITY_DATA)) return;

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        if (!entityData.getQiContainer().tryConsumeQi(QI_COST)) {
            return;
        }

        CompoundTag persistentData = player.getPersistentData();

        persistentData.putFloat(WARD_REMAINING_KEY, calculateWardCapacity(player, entityData));

        persistentData.putLong(
                WARD_EXPIRY_KEY,
                player.level().getGameTime()
                        + calculateDuration(entityData)
        );

        spawnActivationParticles(player);

        player.level().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.PLAYER_SPLASH,
                SoundSource.PLAYERS,
                0.8F,
                0.75F
        );

    }

    private float calculateWardCapacity(
            ServerPlayer player,
            IEntityData entityData
    ) {
        IPathData water = entityData.getPathData(ModPaths.WATER.getId());

        int major = water != null ? water.getMajorRealm() : 0;
        int minor = water != null ? water.getMinorRealm() : 0;

        double fraction = BASE_HEALTH_FRACTION + major * FRACTION_PER_MAJOR_REALM + minor * FRACTION_PER_MINOR_REALM;

        fraction = Math.min(fraction, MAX_HEALTH_FRACTION);

        return Math.max(12.0F, (float) (player.getMaxHealth() * fraction));
    }

    private int calculateDuration(IEntityData entityData) {
        IPathData water = entityData.getPathData(ModPaths.WATER.getId());

        int major = water != null ? water.getMajorRealm() : 0;
        return Math.min(BASE_DURATION_TICKS + major * DURATION_PER_MAJOR_REALM, MAX_DURATION_TICKS);
    }

    public static boolean hasActiveWard(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();

        float remaining = data.getFloat(WARD_REMAINING_KEY);

        long expiry = data.getLong(WARD_EXPIRY_KEY);

        if (remaining <= 0.0F || expiry <= player.level().getGameTime()) {
            clearWard(player);
            return false;
        }

        return true;
    }


    public static float absorbDamage(
            ServerPlayer player,
            float incomingDamage
    ) {
        if (!hasActiveWard(player)) {
            return incomingDamage;
        }

        CompoundTag data = player.getPersistentData();

        float remaining = data.getFloat(WARD_REMAINING_KEY);
        float desiredAbsorption = incomingDamage * DAMAGE_ABSORPTION_RATIO;
        float absorbed = Math.min(remaining, desiredAbsorption);

        remaining -= absorbed;

        if (remaining <= 0.0F) {
            clearWard(player);
        } else {
            data.putFloat(WARD_REMAINING_KEY, remaining);
        }

        return Math.max(0.0F, incomingDamage - absorbed);
    }

    public static float getRemainingWard(ServerPlayer player) {
        return hasActiveWard(player) ? player.getPersistentData().getFloat(WARD_REMAINING_KEY) : 0.0F;
    }

    public static void clearWard(ServerPlayer player) {
        player.getPersistentData().remove(WARD_REMAINING_KEY);
        player.getPersistentData().remove(WARD_EXPIRY_KEY);
    }

    @Override
    public void onRemoved(
            IEntityData attachedEntityData,
            IPersistentSkillData persistentData
    ) {
        if (attachedEntityData.getAttachedEntity() instanceof ServerPlayer player) {
            clearWard(player);
        }
    }

    private void spawnActivationParticles(ServerPlayer player) {
        int points = 48;
        double radius = 1.35D;

        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0D * i / points;
            double x = player.getX() + Math.cos(angle) * radius;
            double z = player.getZ() + Math.sin(angle) * radius;
            double y = player.getY() + 0.25D + (i % 12) * 0.12D;

            player.serverLevel().sendParticles(
                    net.minecraft.core.particles.ParticleTypes.SPLASH,
                    x,
                    y,
                    z,
                    1,
                    0.02D,
                    0.04D,
                    0.02D,
                    0.0D
            );
        }

        player.serverLevel().sendParticles(
                net.minecraft.core.particles.ParticleTypes.BUBBLE_POP,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                24,
                0.65D,
                0.8D,
                0.65D,
                0.05D
        );
    }

    @Override
    public int getCooldown(CastEndData castEndData) {
        return COOLDOWN_TICKS;
    }

    @Override
    protected String getTitleKey() {
        return "ascension.skill.tidal_ward";
    }

    @Override
    protected String getDescriptionKey() {
        return "ascension.skill.tidal_ward.description";
    }
}