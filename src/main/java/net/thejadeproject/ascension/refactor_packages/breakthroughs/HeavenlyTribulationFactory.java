package net.thejadeproject.ascension.refactor_packages.breakthroughs;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;

public final class HeavenlyTribulationFactory {

    private static final double BASE_THREE_NINES_CHANCE = 0.05D;
    private static final double MAX_THREE_NINES_CHANCE = 0.50D;

    private HeavenlyTribulationFactory() {
    }

    public static IBreakthroughInstance create(IEntityData entityData, ITechnique technique) {
        IPathData pathData = entityData.getPathData(technique.getPath());

        if (pathData == null) {
            return createNine(entityData, null);
        }

        if (pathData.consumeRegularTribulationForced()) {
            return createNine(entityData, pathData);
        }

        double multiplier = Math.max(
                0.0D,
                technique.getThreeNinesChanceMultiplier(entityData)
        );

        double chance = Math.clamp(
                BASE_THREE_NINES_CHANCE * multiplier,
                0.0D,
                MAX_THREE_NINES_CHANCE
        );

        boolean threeNines = entityData.getAttachedEntity().getRandom().nextDouble() < chance;

        if (threeNines) {
            AscensionCraft.LOGGER.info(
                    "[Tribulation] Three-Nines selected for {} on path {} with chance {}%",
                    entityData.getAttachedEntity().getName().getString(),
                    technique.getPath(),
                    chance * 100.0D
            );

            return createThreeNines(entityData, pathData);
        }

        return createNine(entityData, pathData);
    }

    private static IBreakthroughInstance createNine(IEntityData entityData, IPathData pathData) {
        int targetRealm = pathData == null ? 1 : pathData.getMajorRealm() + 1;
        double maximumHealth = Math.max(1.0D, entityData.getAttributeValue(Attributes.MAX_HEALTH));

        double baseFraction = 0.021D + 0.0015D * targetRealm;

        return new NineHeavenlyTribulations(
                maximumHealth * baseFraction
        );
    }

    private static IBreakthroughInstance createThreeNines(IEntityData entityData, IPathData pathData) {
        int targetRealm = pathData == null ? 1 : pathData.getMajorRealm() + 1;
        double maximumHealth = Math.max(1.0D, entityData.getAttributeValue(Attributes.MAX_HEALTH)
        );

        double baseFraction = 0.006D + 0.0005D * targetRealm;

        return new ThreeNinesHeavenlyTribulations(
                maximumHealth * baseFraction
        );
    }

    public static IBreakthroughInstance fromCompound(CompoundTag tag) {
        String type = tag.getString("tribulation_type");

        if ("three_nines".equals(type)) {
            return ThreeNinesHeavenlyTribulations.fromCompound(tag);
        }

        return NineHeavenlyTribulations.fromCompound(tag);
    }

    public static IBreakthroughInstance fromNetwork(RegistryFriendlyByteBuf buf) {
        int type = buf.readVarInt();

        return switch (type) {
            case ThreeNinesHeavenlyTribulations.NETWORK_TYPE ->
                    ThreeNinesHeavenlyTribulations.fromNetworkBody(buf);

            case NineHeavenlyTribulations.NETWORK_TYPE ->
                    NineHeavenlyTribulations.fromNetworkBody(buf);

            default -> throw new IllegalStateException(
                    "Unknown heavenly tribulation network type: " + type
            );
        };
    }
}