package net.thejadeproject.ascension.datagen.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.List;

/**
 * A loot modifier that conditionally adds one of several weighted items to the drop pool.
 * Used by {@link net.thejadeproject.ascension.datagen.ModMobRankLootTable} to add
 * rank-scaled loot drops.
 *
 * The conditions array (inherited from LootModifier) must ALL pass before any item is added.
 * One item is chosen from the pool using weighted random selection.
 */
public class AddWeightedItemModifier extends LootModifier {

    public static final MapCodec<AddWeightedItemModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst)
                    .and(WeightedDrop.CODEC.listOf().fieldOf("drops").forGetter(m -> m.drops))
                    .apply(inst, AddWeightedItemModifier::new)
    );

    private final List<WeightedDrop> drops;

    public AddWeightedItemModifier(LootItemCondition[] conditionsIn, List<WeightedDrop> drops) {
        super(conditionsIn);
        this.drops = drops;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (drops.isEmpty()) return generatedLoot;

        int totalWeight = 0;
        for (WeightedDrop drop : drops) {
            totalWeight += drop.weight();
        }

        int roll = context.getRandom().nextInt(totalWeight);
        int running = 0;
        for (WeightedDrop drop : drops) {
            running += drop.weight();
            if (roll < running) {
                generatedLoot.add(drop.stack().copy());
                break;
            }
        }

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    public record WeightedDrop(ItemStack stack, int weight) {
        public static final Codec<WeightedDrop> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ItemStack.CODEC.fieldOf("item").forGetter(WeightedDrop::stack),
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("weight").forGetter(WeightedDrop::weight)
        ).apply(inst, WeightedDrop::new));
    }
}