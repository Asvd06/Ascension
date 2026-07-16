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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;

public class ThreeNinesHeavenlyTribulations implements IBreakthroughInstance {

    public static final int NETWORK_TYPE = 1;

    private static final int TOTAL_WAVES = 9;
    private static final int BOLTS_PER_WAVE = 3;

    private static final int INITIAL_DELAY_TICKS = 40;
    private static final int BOLT_DELAY_TICKS = 20;
    private static final int WAVE_DELAY_TICKS = 50;

    private int currentWave;
    private int currentBolt;
    private int ticksUntilNextBolt;

    private final double baseDamage;

    public ThreeNinesHeavenlyTribulations(double baseDamage) {
        this(
                baseDamage,
                0,
                0,
                INITIAL_DELAY_TICKS
        );
    }

    private ThreeNinesHeavenlyTribulations(
            double baseDamage,
            int currentWave,
            int currentBolt,
            int ticksUntilNextBolt
    ) {
        this.baseDamage = Math.max(0.0D, baseDamage);
        this.currentWave = Math.max(0, currentWave);
        this.currentBolt = Math.max(0, currentBolt);
        this.ticksUntilNextBolt = Math.max(0, ticksUntilNextBolt);
    }

    public static ThreeNinesHeavenlyTribulations createScaled(
            IEntityData entityData,
            ResourceLocation path
    ) {
        IPathData pathData = entityData.getPathData(path);

        int targetRealm = pathData == null
                ? 1
                : pathData.getMajorRealm() + 1;

        double maximumHealth = Math.max(
                1.0D,
                entityData.getAttributeValue(Attributes.MAX_HEALTH)
        );

        double baseFraction =
                0.005D + 0.0005D * targetRealm;

        return new ThreeNinesHeavenlyTribulations(
                maximumHealth * baseFraction
        );
    }

    @Override
    public void tick(
            IEntityData entityData,
            ResourceLocation path
    ) {
        IPathData pathData = entityData.getPathData(path);

        if (pathData == null
                || pathData.getBreakthroughInstance() != this) {
            return;
        }

        Entity attachedEntity = entityData.getAttachedEntity();

        if (!(attachedEntity instanceof LivingEntity livingEntity)
                || !(livingEntity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (!livingEntity.isAlive()) {
            onEntityDeath(entityData, path);
            return;
        }

        if (currentWave >= TOTAL_WAVES) {
            completeBreakthrough(entityData, path);
            return;
        }

        if (ticksUntilNextBolt > 0) {
            ticksUntilNextBolt--;
        }

        if (ticksUntilNextBolt > 0) {
            return;
        }

        if (!canReceiveTribulation(serverLevel, livingEntity)) {
            return;
        }

        fireBolt(
                serverLevel,
                livingEntity,
                path
        );

        if (!livingEntity.isAlive()
                || pathData.getBreakthroughInstance() != this) {
            return;
        }

        currentBolt++;

        if (currentBolt >= BOLTS_PER_WAVE) {
            currentBolt = 0;
            currentWave++;

            if (currentWave >= TOTAL_WAVES) {
                completeBreakthrough(entityData, path);
                return;
            }

            ticksUntilNextBolt = WAVE_DELAY_TICKS;

            sync(pathData, livingEntity);
        } else {
            ticksUntilNextBolt = BOLT_DELAY_TICKS;
        }
    }

    private boolean canReceiveTribulation(
            ServerLevel level,
            LivingEntity entity
    ) {
        return level.canSeeSky(
                entity.blockPosition().above()
        );
    }

    private void fireBolt(
            ServerLevel level,
            LivingEntity entity,
            ResourceLocation path
    ) {
        int waveNumber = currentWave + 1;
        int boltNumber = currentBolt + 1;

        double boltMultiplier =
                1.0D + currentBolt * 0.25D;

        double damage =
                baseDamage
                        * waveNumber
                        * boltMultiplier;

        AscensionCraft.LOGGER.info(
                "[Three-Nines Tribulation] {} wave {}/{} bolt {}/{} for {} damage={}",
                path,
                waveNumber,
                TOTAL_WAVES,
                boltNumber,
                BOLTS_PER_WAVE,
                entity.getName().getString(),
                damage
        );

        DamageSource source = new DamageSource(
                level.registryAccess()
                        .registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(DamageTypes.LIGHTNING_BOLT)
        );

        entity.hurt(source, (float) damage);

        LightningBolt lightning =
                EntityType.LIGHTNING_BOLT.create(level);

        if (lightning != null) {
            lightning.moveTo(
                    entity.getX(),
                    entity.getY(),
                    entity.getZ()
            );

            lightning.setVisualOnly(true);
            level.addFreshEntity(lightning);
        }
    }

    private void completeBreakthrough(IEntityData entityData, ResourceLocation path) {
        IPathData pathData = entityData.getPathData(path);

        if (pathData == null || pathData.getBreakthroughInstance() != this) {
            return;
        }

        Entity attachedEntity = entityData.getAttachedEntity();
        ITechnique technique = pathData.getCurrentTechnique();

        if (technique == null || pathData.getMajorRealm() >= technique.getMaxMajorRealm()) {

            pathData.setBreakthroughInstance(null);

            if (attachedEntity instanceof ServerPlayer player && player.connection != null) {
                pathData.sync(player);
            }

            return;
        }

        int oldMajorRealm = pathData.getMajorRealm();
        int expectedNewRealm = oldMajorRealm + 1;

        pathData.setBreakthroughInstance(null);

        pathData.handleRealmChange(expectedNewRealm, 0, entityData);

        if (pathData.getMajorRealm() == expectedNewRealm) {
            TribulationRewardHelper.grantThreeNinesFoundationReward(entityData, path, pathData.getMajorRealm());
        } else {
            AscensionCraft.LOGGER.warn(
                    "[Three-Nines Tribulation] {} survived, but path {} failed to advance from realm {} to {}. Reward was not granted.",
                    attachedEntity.getName().getString(),
                    path,
                    oldMajorRealm,
                    expectedNewRealm
            );
        }

        if (attachedEntity instanceof ServerPlayer player && player.connection != null) {
            pathData.sync(player);
        }
    }

    @Override
    public void onEntityDeath(IEntityData entityData, ResourceLocation path) {
        IPathData pathData = entityData.getPathData(path);

        if (pathData == null || pathData.getBreakthroughInstance() != this) {
            return;
        }

        int targetRealm = pathData.getMajorRealm() + 1;

        TribulationFailureHelper.createThreeNinesEruption(entityData, targetRealm);

        pathData.setRegularTribulationForced(true);
        pathData.setBreakthroughInstance(null);

        if (entityData.getAttachedEntity() instanceof ServerPlayer player && player.connection != null) {
            pathData.sync(player);
        }
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
        tag.putInt("current_wave", currentWave);
        tag.putInt("current_bolt", currentBolt);
        tag.putInt("ticks_until_next_bolt", ticksUntilNextBolt);
        tag.putString("tribulation_type", "three_nines");

        return tag;
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(NETWORK_TYPE);
        buf.writeDouble(baseDamage);
        buf.writeInt(currentWave);
        buf.writeInt(currentBolt);
        buf.writeInt(ticksUntilNextBolt);
    }

    public static ThreeNinesHeavenlyTribulations fromCompound(CompoundTag tag) {
        return new ThreeNinesHeavenlyTribulations(
                tag.getDouble("base_damage"),
                tag.getInt("current_wave"),
                tag.getInt("current_bolt"),
                tag.getInt("ticks_until_next_bolt")
        );
    }

    public static ThreeNinesHeavenlyTribulations fromNetworkBody(RegistryFriendlyByteBuf buf) {
        return new ThreeNinesHeavenlyTribulations(
                buf.readDouble(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
        );
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public int getCurrentBolt() {
        return currentBolt;
    }

    public int getTotalWaves() {
        return TOTAL_WAVES;
    }

    public int getBoltsPerWave() {
        return BOLTS_PER_WAVE;
    }
}