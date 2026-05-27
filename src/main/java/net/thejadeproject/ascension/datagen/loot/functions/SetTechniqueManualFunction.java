package net.thejadeproject.ascension.datagen.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.common.items.techniques.TechniqueTransferItem;

public class SetTechniqueManualFunction implements LootItemFunction {

    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTION_TYPES =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, AscensionCraft.MOD_ID);

    public static final MapCodec<SetTechniqueManualFunction> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("techniqueId").forGetter(f -> f.techniqueId)
    ).apply(inst, SetTechniqueManualFunction::new));

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SetTechniqueManualFunction>> TYPE =
            LOOT_FUNCTION_TYPES.register("set_technique_manual", () -> new LootItemFunctionType<>(CODEC));

    private final ResourceLocation techniqueId;

    public SetTechniqueManualFunction(ResourceLocation techniqueId) {
        this.techniqueId = techniqueId;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        ItemStack result = TechniqueTransferItem.createWithTechnique(techniqueId.toString());
        result.setCount(stack.getCount());
        return result;
    }

    @Override
    public LootItemFunctionType<SetTechniqueManualFunction> getType() {
        return TYPE.get();
    }

    public static Builder builder(ResourceLocation techniqueId) {
        return new Builder(techniqueId);
    }

    public static class Builder implements LootItemFunction.Builder {
        private final ResourceLocation techniqueId;

        public Builder(ResourceLocation techniqueId) {
            this.techniqueId = techniqueId;
        }

        @Override
        public LootItemFunction build() {
            return new SetTechniqueManualFunction(techniqueId);
        }
    }
}