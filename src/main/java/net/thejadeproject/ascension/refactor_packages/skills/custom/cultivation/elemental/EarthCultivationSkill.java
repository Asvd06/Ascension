package net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.elemental;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;

public class EarthCultivationSkill extends ElementalCultivationSkill {
    @Override
    protected ResourceLocation getElementPath() {
        return ModPaths.EARTH.getId();
    }

    @Override
    protected double getEnvironmentMultiplier(Entity caster) {
        BlockState below = caster.level().getBlockState(caster.blockPosition().below());
        boolean onEarth = isEarthResonantBlock(below);
        boolean underground = caster.blockPosition().getY() < caster.level().getSeaLevel();

        if (onEarth && underground) {
            return 1.65D;
        }

        if (onEarth || underground) {
            return 1.0D;
        }

        return 0.70D;
    }

    private boolean isEarthResonantBlock(BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(BlockTags.SAND)
                || state.is(BlockTags.TERRACOTTA);
    }
}