package net.thejadeproject.ascension.common.blocks.custom.crops.jadedew;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.thejadeproject.ascension.common.blocks.custom.crops.CropAgeCache;
import net.thejadeproject.ascension.common.items.ModItems;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.common.items.herbs.HerbQuality;

import java.util.ArrayList;
import java.util.List;

public class JadeDewGrassCropBlock extends CropBlock {

    public static final int MAX_AGE = 7;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 7);

    // Shapes match wheat's 8-stage heights scaled to our range
    private static final VoxelShape[] SHAPE_BY_AGE = {
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0, 0, 0, 16, 3, 16),
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(0, 0, 0, 16, 5, 16),
            Block.box(0, 0, 0, 16, 6, 16),
            Block.box(0, 0, 0, 16, 7, 16),
            Block.box(0, 0, 0, 16, 8, 16),
            Block.box(0, 0, 0, 16, 9, 16)
    };

    private final ItemLike seedItem;
    private final float    growthChance;

    public JadeDewGrassCropBlock(Properties properties, ItemLike seedItem, float growthChance) {
        super(properties);
        this.seedItem     = seedItem;
        this.growthChance = growthChance;
    }

    // ── Shape / Age ───────────────────────────────────────────────

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE_BY_AGE[state.getValue(AGE)];
    }

    @Override protected ItemLike getBaseSeedId()       { return seedItem; }
    @Override public  IntegerProperty getAgeProperty() { return AGE; }
    @Override public  int getMaxAge()                  { return MAX_AGE; }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    // ── Growth ────────────────────────────────────────────────────

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;
        if (level.getRawBrightness(pos, 0) < 9) return;

        int currentAge = getAge(state);
        if (currentAge >= getMaxAge()) return;

        if (random.nextFloat() < growthChance) {
            int nextAge = currentAge + 1;
            level.setBlock(pos, getStateForAge(nextAge), 2);
            if (nextAge == getMaxAge()) {
                CropAgeCache.store(level, pos, level.getGameTime(), HerbQuality.rollQuality());
            }
        }
    }

    // ── Drops ─────────────────────────────────────────────────────

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = new ArrayList<>(super.getDrops(state, builder));
        if (getAge(state) < getMaxAge()) return drops;

        ServerLevel level = null;
        BlockPos pos = null;
        try {
            if (builder.getLevel() instanceof ServerLevel sl) level = sl;
            var origin = builder.getOptionalParameter(
                    net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN);
            if (origin != null) pos = BlockPos.containing(origin);
        } catch (Exception ignored) {}

        long ageTicks = 0L;
        int  quality  = HerbQuality.BASIC;

        if (level != null && pos != null) {
            CropAgeCache.CropData data = CropAgeCache.retrieve(level, pos);
            if (data != null) {
                ageTicks = Math.max(0, level.getGameTime() - data.grownSince());
                quality  = data.quality();
                CropAgeCache.remove(level, pos);
            }
        }

        for (ItemStack drop : drops) {
            // Only stamp the herb item (JADE_DEW_GRASS), not the seeds
            if (!drop.isEmpty() && drop.getItem() == ModItems.JADE_DEW_GRASS.get()) {
                drop.set(ModDataComponents.HERB_AGE_TICKS.get(), ageTicks);
                drop.set(ModDataComponents.HERB_QUALITY.get(), quality);
            }
        }

        return drops;
    }

    // ── Bonemeal disabled ─────────────────────────────────────────

    @Override public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) { return false; }
    @Override public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) { return false; }
    @Override protected int getBonemealAgeIncrease(Level level) { return 0; }
}
