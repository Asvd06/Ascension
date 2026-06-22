package net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.elemental;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;

public class WindCultivationSkill extends ElementalCultivationSkill {
    @Override
    protected ResourceLocation getElementPath() {
        return ModPaths.WIND.getId();
    }

    @Override
    protected double getEnvironmentMultiplier(Entity caster) {
        int seaLevel = caster.level().getSeaLevel();
        int heightAboveSea = caster.blockPosition().getY() - seaLevel;
        boolean openSky = caster.level().canSeeSky(caster.blockPosition());

        if (!openSky) {
            return 0.75D;
        }

        if (heightAboveSea >= 240) {
            return 2.50D;
        }

        if (heightAboveSea >= 80) {
            return 1.65D;
        }

        if (heightAboveSea >= 32) {
            return 1.15D;
        }

        return 1.00D;
    }
}