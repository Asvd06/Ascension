package net.thejadeproject.ascension.datagen.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.thejadeproject.ascension.common.items.ModItems;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;

public class AddBloodlineRandomPurityModifier extends LootModifier {
    public static final MapCodec<AddBloodlineRandomPurityModifier> CODEC = RecordCodecBuilder.mapCodec(
            inst -> codecStart(inst)
                    .and(ResourceLocation.CODEC.fieldOf("bloodlineId").forGetter(m -> m.bloodlineId))
                    .and(Codec.INT.optionalFieldOf("minPurity", 1).forGetter(m -> m.minPurity))
                    .and(Codec.INT.optionalFieldOf("maxPurity", 100).forGetter(m -> m.maxPurity))
                    .apply(inst, AddBloodlineRandomPurityModifier::new)
    );

    private final ResourceLocation bloodlineId;
    private final int minPurity;
    private final int maxPurity;

    public AddBloodlineRandomPurityModifier(LootItemCondition[] conditionsIn, ResourceLocation bloodlineId, int minPurity, int maxPurity) {
        super(conditionsIn);
        this.bloodlineId = bloodlineId;
        this.minPurity = Math.max(1, minPurity);
        this.maxPurity = Math.min(100, Math.max(this.minPurity, maxPurity));
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ItemStack stack = new ItemStack(ModItems.BLOODLINE_ESSENCE.get());
        stack.set(ModDataComponents.BLOODLINE_ID.get(), bloodlineId.toString());

        int purity = minPurity + context.getRandom().nextInt(maxPurity - minPurity + 1);
        stack.set(ModDataComponents.PURITY.get(), purity);

        generatedLoot.add(stack);
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}