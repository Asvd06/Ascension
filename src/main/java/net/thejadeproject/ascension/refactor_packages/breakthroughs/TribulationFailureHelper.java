package net.thejadeproject.ascension.refactor_packages.breakthroughs;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;

public final class TribulationFailureHelper {

    private TribulationFailureHelper() {
    }

    public static void createNineHeavenlyEruption(IEntityData entityData, int targetRealm) {
        float radius = Mth.clamp(
                1.25F + 0.25F * targetRealm,
                1.5F,
                2.5F
        );

        createEnergyEruption(entityData, radius);
    }

    public static void createThreeNinesEruption(IEntityData entityData, int targetRealm) {

        float radius = Mth.clamp(2.0F + 0.35F * targetRealm, 2.35F, 3.5F);

        createEnergyEruption(entityData, radius);
    }

    private static void createEnergyEruption(IEntityData entityData, float radius) {
        if (entityData == null) {
            return;
        }

        Entity entity = entityData.getAttachedEntity();

        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        serverLevel.explode(
                entity,
                entity.getX(),
                entity.getY() + 0.5D,
                entity.getZ(),
                radius,
                false,
                Level.ExplosionInteraction.BLOCK
        );
    }
}