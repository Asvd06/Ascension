package net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.elemental;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;

public class WaterCultivationSkill extends ElementalCultivationSkill {
    @Override
    protected ResourceLocation getElementPath() {
        return ModPaths.WATER.getId();
    }

    @Override
    protected double getEnvironmentMultiplier(Entity caster) {
        if (caster.isUnderWater()) {
            return 1.50D;
        }

        return 0.90D;
    }
}