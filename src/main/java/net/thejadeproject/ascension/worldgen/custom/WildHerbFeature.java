package net.thejadeproject.ascension.worldgen.custom;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.thejadeproject.ascension.common.blocks.custom.crops.CropAgeCache;
import net.thejadeproject.ascension.common.blocks.custom.crops.GenericSlowCropBlock;
import net.thejadeproject.ascension.common.blocks.custom.crops.StemSlowCropBlock;
import net.thejadeproject.ascension.common.items.herbs.HerbQuality;

public class WildHerbFeature extends Feature<WildHerbFeatureConfig> {

    public WildHerbFeature(Codec<WildHerbFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WildHerbFeatureConfig> ctx) {
        WildHerbFeatureConfig config = ctx.config();
        var level  = ctx.level();
        var origin = ctx.origin();
        var random = ctx.random();

        // Walk up from origin to find the surface (up to 16 blocks)
        BlockPos surface = origin;
        for (int i = 0; i < 16; i++) {
            if (!level.isEmptyBlock(surface)) break;
            surface = surface.below();
        }

        BlockState groundState = level.getBlockState(surface);
        BlockPos plantPos = surface.above();

        // Validate ground
        if (config.validGround().stream().noneMatch(b -> groundState.is(b))) return false;
        if (!level.isEmptyBlock(plantPos)) return false;

        // Build the fully-grown block state — supports both crop types
        BlockState cropState;
        if (config.cropBlock() instanceof GenericSlowCropBlock generic) {
            cropState = generic.getStateForAge(GenericSlowCropBlock.MAX_AGE);
        } else if (config.cropBlock() instanceof StemSlowCropBlock stem) {
            cropState = stem.getStateForAge(StemSlowCropBlock.MAX_AGE);
        } else {
            // Fallback: try the default state (won't have age property set)
            cropState = config.cropBlock().defaultBlockState();
        }

        if (!cropState.canSurvive(level, plantPos)) return false;

        level.setBlock(plantPos, cropState, 2);

        // Stamp age: random point between Mature and Elder
        long ageRange = HerbQuality.AGE_ELDER - HerbQuality.AGE_MATURE;
        long wildAge  = HerbQuality.AGE_MATURE + (long)(random.nextDouble() * ageRange);
        int  quality  = HerbQuality.rollQuality();

        ServerLevel serverLevel = level.getLevel();
        long grownSince = serverLevel.getGameTime() - wildAge;
        CropAgeCache.store(serverLevel, plantPos, grownSince, quality);

        return true;
    }
}
