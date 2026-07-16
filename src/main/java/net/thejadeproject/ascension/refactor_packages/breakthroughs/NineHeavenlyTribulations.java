package net.thejadeproject.ascension.refactor_packages.breakthroughs;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;

public class NineHeavenlyTribulations implements IBreakthroughInstance {

    public static final int NETWORK_TYPE = 0;

    private static final int TOTAL_TRIBULATIONS = 9;
    private static final int STRIKE_INTERVAL_TICKS = 40;

    private int currentTribulation;
    private int ticksSinceFired;
    private final double baseDamage;

    public NineHeavenlyTribulations(double baseDamage) {
        this(baseDamage, 0, 0);
    }

    private NineHeavenlyTribulations(double baseDamage, int currentTribulation, int ticksSinceFired) {
        this.baseDamage = Math.max(0.0D, baseDamage);
        this.currentTribulation = Math.max(0, currentTribulation);
        this.ticksSinceFired = Math.max(0, ticksSinceFired);
    }

    @Override
    public void tick(IEntityData entityData, ResourceLocation path) {
        IPathData pathData = entityData.getPathData(path);

        if (pathData == null || pathData.getBreakthroughInstance() != this) {
            return;
        }

        Entity attachedEntity = entityData.getAttachedEntity();

        if (!(attachedEntity instanceof LivingEntity livingEntity) || !(livingEntity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (!livingEntity.isAlive()) {
            onEntityDeath(entityData, path);
            return;
        }

        if (currentTribulation >= TOTAL_TRIBULATIONS) {
            completeBreakthrough(entityData, path);
            return;
        }

        ticksSinceFired = Math.min(STRIKE_INTERVAL_TICKS, ticksSinceFired + 1);

        if (ticksSinceFired < STRIKE_INTERVAL_TICKS) {
            return;
        }

        if (!canReceiveTribulation(serverLevel, livingEntity)) {
            return;
        }

        fireTribulation(serverLevel, livingEntity, path);

        if (!livingEntity.isAlive() || pathData.getBreakthroughInstance() != this) {
            return;
        }

        ticksSinceFired = 0;
        currentTribulation++;

        if (currentTribulation >= TOTAL_TRIBULATIONS) {
            completeBreakthrough(entityData, path);
        } else {
            sync(pathData, livingEntity);
        }
    }

    private boolean canReceiveTribulation(ServerLevel level, LivingEntity entity) {
        return level.canSeeSky(entity.blockPosition().above());
    }

    private void fireTribulation(ServerLevel level, LivingEntity entity, ResourceLocation path) {
        int strikeNumber = currentTribulation + 1;

        double damage = baseDamage * strikeNumber;

        DamageSource source = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.LIGHTNING_BOLT));
        entity.hurt(source, (float) damage);

        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);

        if (lightning != null) {
            lightning.moveTo(entity.getX(), entity.getY(), entity.getZ());
            lightning.setVisualOnly(true);
            level.addFreshEntity(lightning);
        }
    }

    private void completeBreakthrough(IEntityData entityData, ResourceLocation path) {
        IPathData pathData = entityData.getPathData(path);

        if (pathData == null || pathData.getBreakthroughInstance() != this) {
            return;
        }

        int oldMajorRealm = pathData.getMajorRealm();
        int nextMajorRealm = oldMajorRealm + 1;

        pathData.setBreakthroughInstance(null);
        pathData.handleRealmChange(nextMajorRealm, 0, entityData);
        Entity attachedEntity = entityData.getAttachedEntity();

        if (nextMajorRealm > pathData.getMaxMajorRealm()) {
            AscensionCraft.LOGGER.warn(
                    "[Tribulation] Refused to advance {} beyond maximum realm {} on path {}",
                    attachedEntity.getName().getString(),
                    pathData.getMaxMajorRealm(),
                    path
            );

            pathData.setBreakthroughInstance(null);
            return;
        }


        if (attachedEntity instanceof ServerPlayer player && player.connection != null) {
            pathData.sync(player);
        }
    }

    @Override
    public void onEntityDeath(IEntityData entityData, ResourceLocation path) {
        IPathData pathData = entityData.getPathData(path);

        if (pathData == null && pathData.getBreakthroughInstance() != this) {
            return;
        }

        int targetRealm = pathData.getMajorRealm() + 1;

        TribulationFailureHelper.createNineHeavenlyEruption(entityData, targetRealm);
        pathData.setBreakthroughInstance(null);

    }

    private void sync(IPathData pathData, LivingEntity entity) {
        if (entity instanceof ServerPlayer player && player.connection != null) {
            pathData.sync(player);
        }
    }

    @Override
    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();

        tag.putDouble("base_damage", baseDamage);
        tag.putInt("current_tribulation", currentTribulation);
        tag.putInt("ticks_since_fired", ticksSinceFired);

        return tag;
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(NETWORK_TYPE);
        buf.writeDouble(baseDamage);
        buf.writeInt(currentTribulation);
        buf.writeInt(ticksSinceFired);
    }

    public static NineHeavenlyTribulations fromCompound(CompoundTag tag) {
        return new NineHeavenlyTribulations(
                tag.getDouble("base_damage"),
                tag.getInt("current_tribulation"),
                tag.getInt("ticks_since_fired")
        );
    }

    public static NineHeavenlyTribulations fromNetworkBody(RegistryFriendlyByteBuf buf) {
        return new NineHeavenlyTribulations(
                buf.readDouble(),
                buf.readInt(),
                buf.readInt()
        );
    }

    public int getCurrentTribulation() {
        return currentTribulation;
    }

    public int getTotalTribulations() {
        return TOTAL_TRIBULATIONS;
    }
}