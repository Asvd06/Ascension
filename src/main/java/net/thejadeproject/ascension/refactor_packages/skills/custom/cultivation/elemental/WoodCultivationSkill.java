package net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.elemental;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;

public class WoodCultivationSkill extends ElementalCultivationSkill {
    @Override
    protected ResourceLocation getElementPath() {
        return ModPaths.WOOD.getId();
    }

    @Override
    protected double getEnvironmentMultiplier(Entity caster) {
        int plants = countNearbyBlocks(caster, 4, this::isWoodResonantBlock);

        if (plants >= 24) {
            return 1.65D;
        }

        if (plants >= 8) {
            return 1.25D;
        }

        return 0.95D;
    }

    private boolean isWoodResonantBlock(BlockState state) {
        return state.is(BlockTags.SAPLINGS)
                || state.is(BlockTags.LEAVES)
                || state.is(BlockTags.LOGS)
                || state.is(BlockTags.FLOWERS)
                || state.is(BlockTags.CROPS);
    }
}