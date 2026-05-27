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
import org.jetbrains.annotations.NotNull;

public class AddBloodlineItemModifier extends LootModifier {
    public static final MapCodec<AddBloodlineItemModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst)
                    .and(ResourceLocation.CODEC.fieldOf("bloodlineId").forGetter(m -> m.bloodlineId))
                    .and(Codec.INT.optionalFieldOf("purity", 100).forGetter(m -> m.purity))
                    .apply(inst, AddBloodlineItemModifier::new)
    );

    private final ResourceLocation bloodlineId;
    private final int purity;

    public AddBloodlineItemModifier(LootItemCondition[] conditionsIn, ResourceLocation bloodlineId, int purity) {
        super(conditionsIn);
        this.bloodlineId = bloodlineId;
        this.purity = Math.min(Math.max(purity, 1), 100);
    }

    public AddBloodlineItemModifier(LootItemCondition[] conditionsIn, String bloodlineId) {
        this(conditionsIn, ResourceLocation.parse(bloodlineId), 100);
    }

    public AddBloodlineItemModifier(LootItemCondition[] conditionsIn, String bloodlineId, int purity) {
        this(conditionsIn, ResourceLocation.parse(bloodlineId), purity);
    }

    public AddBloodlineItemModifier(LootItemCondition[] conditionsIn, ResourceLocation bloodlineId) {
        this(conditionsIn, bloodlineId, 100);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ItemStack stack = new ItemStack(ModItems.BLOODLINE_ESSENCE.get());
        stack.set(ModDataComponents.BLOODLINE_ID.get(), bloodlineId.toString());
        stack.set(ModDataComponents.PURITY.get(), purity);
        generatedLoot.add(stack);
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}