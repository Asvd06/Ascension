package net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.elemental;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;

public class FireCultivationSkill extends ElementalCultivationSkill {
    @Override
    protected ResourceLocation getElementPath() {
        return ModPaths.FIRE.getId();
    }

    @Override
    protected double getEnvironmentMultiplier(Entity caster) {
        if (caster.isInLava()) return 2.25D;
        if (caster.isOnFire()) return 1.50D;
        return 0.75D;
    }
}