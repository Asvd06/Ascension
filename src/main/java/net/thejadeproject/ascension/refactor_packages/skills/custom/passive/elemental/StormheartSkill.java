package net.thejadeproject.ascension.refactor_packages.skills.custom.passive.elemental;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.skills.IPersistentSkillData;
import net.thejadeproject.ascension.refactor_packages.skills.ITickingSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.SimplePassiveSkill;

public class StormheartSkill
        extends SimplePassiveSkill
        implements ITickingSkill {

    public static final String CHARGE_KEY =
            AscensionCraft.MOD_ID + ":stormheart_charge";

    private static final float MAX_CHARGE = 100.0F;

    private static final float BASE_CHARGE_PER_TICK = 0.30F;
    private static final float CHARGE_PER_MAJOR = 0.04F;
    private static final float CHARGE_PER_MINOR = 0.005F;

    private static final float BASE_DAMAGE_MULTIPLIER = 1.45F;
    private static final float MULTIPLIER_PER_MAJOR = 0.07F;
    private static final float MULTIPLIER_PER_MINOR = 0.008F;
    private static final float MAX_DAMAGE_MULTIPLIER = 1.90F;

    @Override
    public void onPlayerTick(
            ServerPlayer player,
            IEntityData entityData
    ) {
        Vec3 movement = player.getDeltaMovement();
        double horizontalMovementSqr = movement.x * movement.x + movement.z * movement.z;
        boolean moving = horizontalMovementSqr > 0.003D || player.isSprinting() || player.isFallFlying();

        if (!moving) return;

        float currentCharge = player.getPersistentData().getFloat(CHARGE_KEY);

        if (currentCharge >= MAX_CHARGE) {
            spawnChargedParticles(player);
            return;
        }

        float chargeGain = calculateChargeGain(entityData);
        float newCharge = Math.min(MAX_CHARGE, currentCharge + chargeGain);

        player.getPersistentData().putFloat(CHARGE_KEY, newCharge);

        if (newCharge >= MAX_CHARGE) {
            spawnChargedParticles(player);
        }
    }

    private float calculateChargeGain(
            IEntityData entityData
    ) {
        IPathData lightning = entityData.getPathData(ModPaths.LIGHTNING.getId());
        int major = lightning != null ? lightning.getMajorRealm() : 0;
        int minor = lightning != null ? lightning.getMinorRealm() : 0;

        return BASE_CHARGE_PER_TICK + major * CHARGE_PER_MAJOR + minor * CHARGE_PER_MINOR;
    }

    public static boolean isFullyCharged(
            ServerPlayer player
    ) {
        return player.getPersistentData().getFloat(CHARGE_KEY) >= MAX_CHARGE;
    }

    public static float consumeDamageMultiplier(
            ServerPlayer player,
            IEntityData entityData
    ) {
        if (!isFullyCharged(player)) {
            return 1.0F;
        }

        player.getPersistentData().remove(CHARGE_KEY);
        IPathData lightning = entityData.getPathData(ModPaths.LIGHTNING.getId());

        int major = lightning != null ? lightning.getMajorRealm() : 0;
        int minor = lightning != null ? lightning.getMinorRealm() : 0;

        return Math.min(BASE_DAMAGE_MULTIPLIER + major * MULTIPLIER_PER_MAJOR + minor * MULTIPLIER_PER_MINOR, MAX_DAMAGE_MULTIPLIER);
    }

    private void spawnChargedParticles(
            ServerPlayer player
    ) {
        if (player.tickCount % 10 != 0) return;

        player.serverLevel().sendParticles(
                ParticleTypes.ELECTRIC_SPARK,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                4,
                0.3D,
                0.55D,
                0.3D,
                0.03D
        );
    }

    @Override
    public void onRemoved(IEntityData attachedEntityData, IPersistentSkillData persistentData) {
        if (attachedEntityData.getAttachedEntity() instanceof ServerPlayer player) {
            player.getPersistentData().remove(CHARGE_KEY);
        }
    }

    @Override
    protected String getTitleKey() {
        return "ascension.skill.stormheart";
    }

    @Override
    protected String getDescriptionKey() {
        return "ascension.skill.stormheart.description";
    }
}