package net.thejadeproject.ascension.datagen.loot;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.datagen.loot.conditions.AddTechniqueManualModifier;

import java.util.function.Supplier;

public class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, AscensionCraft.MOD_ID);

    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> ADD_ITEM =
            LOOT_MODIFIER_SERIALIZERS.register("add_item", () -> AddItemModifier.CODEC);

    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> ADD_WEIGHTED_ITEM =
            LOOT_MODIFIER_SERIALIZERS.register("add_weighted_item", () -> AddWeightedItemModifier.CODEC);

    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> ADD_PHYSIQUE_ITEM =
            LOOT_MODIFIER_SERIALIZERS.register("add_physique_item", () -> AddPhysiqueItemModifier.CODEC);

    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> ADD_PHYSIQUE_RANDOM_PURITY =
            LOOT_MODIFIER_SERIALIZERS.register("add_physique_random_purity",
                    () -> AddPhysiqueRandomPurityModifier.CODEC);

    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> ADD_BLOODLINE_ITEM  =
            LOOT_MODIFIER_SERIALIZERS.register("add_bloodline_item", () -> AddBloodlineItemModifier.CODEC);

    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> ADD_BLOODLINE_RANDOM_PURITY =
            LOOT_MODIFIER_SERIALIZERS.register("add_bloodline_random_purity",
                    () -> AddBloodlineRandomPurityModifier.CODEC);

    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> RANKED_MOB_LOOT =
            LOOT_MODIFIER_SERIALIZERS.register("add_ranked_mob_loot", () -> AddRankedMobLootModifier.CODEC);




    /** Drops a single technique page (identified by techniqueId + 0-based pageIndex). */
    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> ADD_TECHNIQUE_PAGE =
            LOOT_MODIFIER_SERIALIZERS.register("add_technique_page",
                    () -> AddTechniquePageModifier.CODEC);

    /** Drops a complete technique manual (the learn-on-use item). */
    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> ADD_TECHNIQUE_MANUAL =
            LOOT_MODIFIER_SERIALIZERS.register("add_technique_manual",
                    () -> AddTechniqueManualModifier.CODEC);

    public static void register(IEventBus eventBus) {
        LOOT_MODIFIER_SERIALIZERS.register(eventBus);
    }
}
