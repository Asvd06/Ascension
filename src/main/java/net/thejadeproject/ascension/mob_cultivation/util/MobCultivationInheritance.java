package net.thejadeproject.ascension.mob_cultivation.util;

import net.minecraft.world.entity.LivingEntity;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.mob_cultivation.MobCultivationData;
import net.thejadeproject.ascension.mob_cultivation.MobCultivationDefinition;
import net.thejadeproject.ascension.mob_cultivation.MobCultivationList;
import net.thejadeproject.ascension.mob_cultivation.MobCultivationResolver;

public final class MobCultivationInheritance {
    private MobCultivationInheritance() {}

    public static MobCultivationData copyOf(MobCultivationData source) {
        if (source == null || !source.isInitialized() || source.isUnranked()) {
            return null;
        }

        return new MobCultivationData(
                source.getRealmId(),
                source.getStage(),
                true
        );
    }

    public static MobCultivationData inheritFromParents(LivingEntity parentA, LivingEntity parentB) {
        MobCultivationData dataA = parentA.getData(ModAttachments.MOB_RANK);
        MobCultivationData dataB = parentB.getData(ModAttachments.MOB_RANK);

        boolean validA = isValid(dataA);
        boolean validB = isValid(dataB);

        if (!validA && !validB) {
            return null;
        }

        if (validA && !validB) {
            return copyOf(dataA);
        }

        if (!validA) {
            return copyOf(dataB);
        }

        int compare = MobCultivationList.compare(
                dataA.getRealmId(),
                dataA.getStage(),
                dataB.getRealmId(),
                dataB.getStage()
        );

        MobCultivationData stronger = compare >= 0 ? dataA : dataB;
        MobCultivationData weaker = compare >= 0 ? dataB : dataA;

        // Chance of matching the stronger parent = 60%
        MobCultivationData chosen = parentA.getRandom().nextFloat() < 0.60F ? stronger : weaker;

        return copyOf(chosen);
    }

    public static MobCultivationData inheritFromOrigin(LivingEntity origin) {
        MobCultivationData originData = origin.getData(ModAttachments.MOB_RANK);
        if (!isValid(originData)) {
            return null;
        }

        return copyReducedByOneStage(originData);
    }

    private static MobCultivationData copyReducedByOneStage(MobCultivationData source) {
        int power = MobCultivationResolver.getRankPower(
                source.getRealmId(),
                source.getStage()
        );

        int reducedPower = Math.max(0, power - 1);

        MobCultivationDefinition reduced = MobCultivationResolver.resolveFromPower(reducedPower);

        return new MobCultivationData(
                reduced.realmId(),
                reduced.stage(),
                true
        );
    }

    private static boolean isValid(MobCultivationData data) {
        return data != null && data.isInitialized() && !data.isUnranked();
    }
}