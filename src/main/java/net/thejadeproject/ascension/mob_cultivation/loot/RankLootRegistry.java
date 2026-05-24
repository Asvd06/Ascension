package net.thejadeproject.ascension.mob_cultivation.loot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.common.blocks.ModBlocks;
import net.thejadeproject.ascension.common.items.ModItems;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.datagen.loot.functions.SetRandomIntComponentFunction;
import net.thejadeproject.ascension.datagen.loot.functions.SetTechniquePageFunction;
import net.thejadeproject.ascension.mob_cultivation.MobCultivationList;

import java.util.*;

/**
 * Central registry for rank-based loot tables.
 *
 * ANY mob from ANY mod that has a cultivation rank will pull from these tables.
 * No per-mob hardcoding needed — fully mod compatible.
 */
public final class RankLootRegistry {

    private RankLootRegistry() {}

    private static final Map<String, RankLootTable> TABLES = new HashMap<>();

    static {
        registerAll();
    }

    private static void registerAll() {
        // ═════════════════════════════════════════════════════════════════════
        // MORTAL RANKS (realmIndex 0) — Weak drops, mostly trash/crafting mats
        // ═════════════════════════════════════════════════════════════════════
        register("mortal", 1, 0.20f, List.of(
                entry(new ItemStack(Items.IRON_NUGGET), 40, 1.0f, 2, 6),
                entry(new ItemStack(Items.GOLD_NUGGET), 25, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.JADE_NUGGET.get()), 10, 1.0f, 1, 2)
        ));

        register("mortal", 2, 0.25f, List.of(
                entry(new ItemStack(Items.IRON_NUGGET), 35, 1.0f, 3, 8),
                entry(new ItemStack(Items.GOLD_NUGGET), 25, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.JADE_NUGGET.get()), 15, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.BLACK_IRON_NUGGET.get()), 10, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.FASTING_PILL_T1.get()), 8, 1.0f, 1, 1)
        ));

        register("mortal", 3, 0.30f, List.of(
                entry(new ItemStack(Items.IRON_NUGGET), 30, 1.0f, 4, 10),
                entry(new ItemStack(Items.GOLD_NUGGET), 25, 1.0f, 3, 6),
                entry(new ItemStack(ModItems.JADE_NUGGET.get()), 20, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.BLACK_IRON_NUGGET.get()), 15, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.FROST_SILVER_NUGGET.get()), 10, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.FASTING_PILL_T1.get()), 12, 1.0f, 1, 1)
        ));

        // ═════════════════════════════════════════════════════════════════════
        // QI GATHERING (realmIndex 1) — First real cultivation drops
        // ═════════════════════════════════════════════════════════════════════
        register("qi_gathering", 1, 0.35f, List.of(
                entry(new ItemStack(Items.IRON_INGOT), 40, 1.0f, 1, 3),
                entry(new ItemStack(Items.GOLD_INGOT), 30, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.TALISMAN_PAPER.get()), 35, 1.0f, 4, 8),
                entry(new ItemStack(ModItems.JADE_NUGGET.get()), 25, 1.0f, 3, 6),
                entry(new ItemStack(ModItems.BLACK_IRON_NUGGET.get()), 20, 1.0f, 3, 6),
                entry(new ItemStack(ModItems.FROST_SILVER_NUGGET.get()), 15, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.LIVING_CORE.get()), 15, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.UNDEAD_CORE.get()), 15, 1.0f, 1, 1),
                entry(new ItemStack(Items.EXPERIENCE_BOTTLE), 20, 1.0f, 1, 3)
        ));

        register("qi_gathering", 2, 0.38f, List.of(
                entry(new ItemStack(Items.IRON_INGOT), 35, 1.0f, 2, 4),
                entry(new ItemStack(Items.GOLD_INGOT), 30, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.TALISMAN_PAPER.get()), 35, 1.0f, 6, 10),
                entry(new ItemStack(ModItems.JADE.get()), 20, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.BLACK_IRON_NUGGET.get()), 25, 1.0f, 4, 8),
                entry(new ItemStack(ModItems.FROST_SILVER_NUGGET.get()), 20, 1.0f, 3, 5),
                entry(new ItemStack(ModItems.LIVING_CORE.get()), 18, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.UNDEAD_CORE.get()), 18, 1.0f, 1, 1),
                entry(new ItemStack(Items.EXPERIENCE_BOTTLE), 25, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.GOLDEN_SUN_LEAF.get()), 12, 1.0f, 1, 2)
        ));

        // Rank 3+ — technique drops start appearing
        register("qi_gathering", 3, 0.42f, List.of(
                entry(new ItemStack(Items.IRON_INGOT), 30, 1.0f, 3, 6),
                entry(new ItemStack(Items.GOLD_INGOT), 25, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.TALISMAN_PAPER.get()), 35, 1.0f, 8, 14),
                entry(new ItemStack(ModItems.JADE.get()), 25, 1.0f, 2, 3),
                entry(new ItemStack(ModItems.BLACK_IRON_INGOT.get()), 20, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.FROST_SILVER_NUGGET.get()), 25, 1.0f, 4, 7),
                entry(new ItemStack(ModItems.LIVING_CORE.get()), 22, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.UNDEAD_CORE.get()), 22, 1.0f, 1, 1),
                entry(new ItemStack(Items.EXPERIENCE_BOTTLE), 30, 1.0f, 3, 6),
                entry(new ItemStack(ModItems.GOLDEN_SUN_LEAF.get()), 15, 1.0f, 2, 3),
                entry(new ItemStack(ModItems.JADE_DEW_GRASS.get()), 10, 1.0f, 1, 2)
                ));

        // ═════════════════════════════════════════════════════════════════════
        // FORMATION ESTABLISHMENT (realmIndex 2) — Mid-tier, elemental cores appear
        // ═════════════════════════════════════════════════════════════════════
        register("formation_establishment", 1, 0.45f, List.of(
                entry(new ItemStack(Items.IRON_INGOT), 30, 1.0f, 3, 6),
                entry(new ItemStack(Items.GOLD_INGOT), 25, 1.0f, 2, 4),
                entry(new ItemStack(Items.DIAMOND), 15, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.TALISMAN_PAPER.get()), 30, 1.0f, 10, 16),
                entry(new ItemStack(ModItems.JADE.get()), 30, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.BLACK_IRON_INGOT.get()), 25, 1.0f, 2, 3),
                entry(new ItemStack(ModItems.FROST_SILVER_INGOT.get()), 20, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.LIVING_CORE.get()), 25, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.UNDEAD_CORE.get()), 25, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.FIRE_CORE.get()), 15, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.WATER_CORE.get()), 15, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.WOOD_CORE.get()), 15, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.EARTH_CORE.get()), 15, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.METAL_CORE.get()), 15, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.LIGHTNING_CORE.get()), 12, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.WIND_CORE.get()), 12, 1.0f, 1, 1),
                entry(new ItemStack(Items.EXPERIENCE_BOTTLE), 35, 1.0f, 4, 8),
                entry(new ItemStack(ModItems.GOLDEN_SUN_LEAF.get()), 18, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.JADE_DEW_GRASS.get()), 15, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.SOUL_ANCHOR_TALISMAN.get()), 8, 1.0f, 1, 1),
                pillEntry(ModItems.ESSENCE_GATHERING_PILL.get(), 15,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 2),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.SOUL_FOCUS_PILL.get(), 15,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 2),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.INNER_REINFORCEMENT_PILL.get(), 15,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 2),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90))
        ));

        register("formation_establishment", 2, 0.48f, List.of(
                entry(new ItemStack(Items.IRON_INGOT), 25, 1.0f, 4, 8),
                entry(new ItemStack(Items.GOLD_INGOT), 25, 1.0f, 3, 5),
                entry(new ItemStack(Items.DIAMOND), 20, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.TALISMAN_PAPER.get()), 30, 1.0f, 12, 20),
                entry(new ItemStack(ModItems.JADE.get()), 35, 1.0f, 3, 5),
                entry(new ItemStack(ModItems.BLACK_IRON_INGOT.get()), 30, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.FROST_SILVER_INGOT.get()), 25, 1.0f, 2, 3),
                entry(new ItemStack(ModItems.LIVING_CORE.get()), 28, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.UNDEAD_CORE.get()), 28, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.FIRE_CORE.get()), 18, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.WATER_CORE.get()), 18, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.WOOD_CORE.get()), 18, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.EARTH_CORE.get()), 18, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.METAL_CORE.get()), 18, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.LIGHTNING_CORE.get()), 15, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.WIND_CORE.get()), 15, 1.0f, 1, 2),
                entry(new ItemStack(Items.EXPERIENCE_BOTTLE), 40, 1.0f, 5, 10),
                entry(new ItemStack(ModItems.GOLDEN_SUN_LEAF.get()), 22, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.JADE_DEW_GRASS.get()), 18, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.JADE_BAMBOO_OF_SERENITY.get()), 12, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.SOUL_ANCHOR_TALISMAN.get()), 10, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.DEATH_RECALL_TALISMAN.get()), 6, 1.0f, 1, 1),
                pillEntry(ModItems.ESSENCE_GATHERING_PILL.get(), 15,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 2),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.SOUL_FOCUS_PILL.get(), 15,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 2),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.INNER_REINFORCEMENT_PILL.get(), 15,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 2),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),

                pillEntry(ModItems.QI_ENHANCED_REGEN_PILL.get(), 15,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 2),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.QI_REPLENISHING_PILL.get(), 15,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 2),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90))

        ));

        // Rank 3 — boosted technique chances + random page range drops
        register("formation_establishment", 3, 0.52f, List.of(
                entry(new ItemStack(Items.IRON_INGOT), 20, 1.0f, 5, 10),
                entry(new ItemStack(Items.GOLD_INGOT), 25, 1.0f, 3, 6),
                entry(new ItemStack(Items.DIAMOND), 25, 1.0f, 2, 3),
                entry(new ItemStack(Items.EMERALD), 15, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.TALISMAN_PAPER.get()), 30, 1.0f, 14, 24),
                entry(new ItemStack(ModItems.JADE.get()), 40, 1.0f, 3, 6),
                entry(new ItemStack(ModItems.BLACK_IRON_INGOT.get()), 35, 1.0f, 3, 5),
                entry(new ItemStack(ModItems.FROST_SILVER_INGOT.get()), 30, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.LIVING_CORE.get()), 32, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.UNDEAD_CORE.get()), 32, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.FIRE_CORE.get()), 22, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.WATER_CORE.get()), 22, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.WOOD_CORE.get()), 22, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.EARTH_CORE.get()), 22, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.METAL_CORE.get()), 22, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.LIGHTNING_CORE.get()), 18, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.WIND_CORE.get()), 18, 1.0f, 1, 2),
                entry(new ItemStack(Items.EXPERIENCE_BOTTLE), 45, 1.0f, 6, 12),
                entry(new ItemStack(ModItems.GOLDEN_SUN_LEAF.get()), 25, 1.0f, 2, 5),
                entry(new ItemStack(ModItems.JADE_DEW_GRASS.get()), 22, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.JADE_BAMBOO_OF_SERENITY.get()), 15, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.IRONWOOD_SPROUT.get()), 12, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.SOUL_ANCHOR_TALISMAN.get()), 12, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.DEATH_RECALL_TALISMAN.get()), 8, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.SPATIAL_RUPTURE_TALISMAN_T1.get()), 6, 1.0f, 1, 1),
                // High-chance technique drops — random page ranges like chest loot
                entry(techniqueManual("heart_fire_technique"), 18, 1.0f, 1, 1),
                entry(techniqueManual("kidney_water_technique"), 18, 1.0f, 1, 1),

                pillEntry(ModItems.ESSENCE_GATHERING_PILL.get(), 15,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 2),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.SOUL_FOCUS_PILL.get(), 15,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 2),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.INNER_REINFORCEMENT_PILL.get(), 15,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 2),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),

                pillEntry(ModItems.QI_ENHANCED_REGEN_PILL.get(), 15,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 2),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.QI_REPLENISHING_PILL.get(), 15,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 2),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90))
        ));

        // ═════════════════════════════════════════════════════════════════════
        // GOLDEN CORE (realmIndex 3) — High-tier, poison powders, rare talismans
        // ═════════════════════════════════════════════════════════════════════
        register("golden_core", 1, 0.55f, List.of(
                entry(new ItemStack(Items.IRON_INGOT), 20, 1.0f, 6, 12),
                entry(new ItemStack(Items.GOLD_INGOT), 25, 1.0f, 4, 8),
                entry(new ItemStack(Items.DIAMOND), 30, 1.0f, 2, 4),
                entry(new ItemStack(Items.EMERALD), 20, 1.0f, 3, 6),
                entry(new ItemStack(ModItems.TALISMAN_PAPER.get()), 25, 1.0f, 16, 28),
                entry(new ItemStack(ModItems.JADE.get()), 45, 1.0f, 4, 8),
                entry(new ItemStack(ModItems.BLACK_IRON_INGOT.get()), 40, 1.0f, 4, 6),
                entry(new ItemStack(ModItems.FROST_SILVER_INGOT.get()), 35, 1.0f, 3, 5),
                entry(new ItemStack(ModItems.LIVING_CORE.get()), 35, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.UNDEAD_CORE.get()), 35, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.FIRE_CORE.get()), 25, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.WATER_CORE.get()), 25, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.WOOD_CORE.get()), 25, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.EARTH_CORE.get()), 25, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.METAL_CORE.get()), 25, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.LIGHTNING_CORE.get()), 22, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.WIND_CORE.get()), 22, 1.0f, 1, 2),
                entry(new ItemStack(Items.EXPERIENCE_BOTTLE), 50, 1.0f, 8, 16),
                entry(new ItemStack(ModItems.BLINDED_SENSES_POWDER.get()), 15, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.VENOMOUS_MERIDIAN_POWDER.get()), 15, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.PARALYZED_BODY_POWDER.get()), 12, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.QI_DEVOURING_POWDER.get()), 10, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.GOLDEN_SUN_LEAF.get()), 28, 1.0f, 3, 6),
                entry(new ItemStack(ModItems.JADE_DEW_GRASS.get()), 25, 1.0f, 2, 5),
                entry(new ItemStack(ModItems.JADE_BAMBOO_OF_SERENITY.get()), 18, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.IRONWOOD_SPROUT.get()), 15, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.SOUL_ANCHOR_TALISMAN.get()), 15, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.DEATH_RECALL_TALISMAN.get()), 10, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.SPATIAL_RUPTURE_TALISMAN_T1.get()), 8, 1.0f, 1, 1),
                // Technique drops
                pageEntry("bloodfeast_soul_refining_scripture", 0, 5, 24),
                pageEntry("white_lightning_ten_stage_technique", 0, 9, 20),
                pageEntry("soul_forged_weapon_manual", 0, 3, 23),

                entry(techniqueManual("lung_metal_technique"), 15, 1.0f, 1, 1),

                pillEntry(ModItems.ESSENCE_GATHERING_PILL.get(), 15,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 3),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.SOUL_FOCUS_PILL.get(), 15,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 3),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.INNER_REINFORCEMENT_PILL.get(), 15,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 3),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),

                pillEntry(ModItems.QI_ENHANCED_REGEN_PILL.get(), 15,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 3),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.QI_REPLENISHING_PILL.get(), 15,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 3),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90))
        ));

        register("golden_core", 2, 0.58f, List.of(
                entry(new ItemStack(Items.IRON_INGOT), 15, 1.0f, 8, 14),
                entry(new ItemStack(Items.GOLD_INGOT), 25, 1.0f, 5, 10),
                entry(new ItemStack(Items.DIAMOND), 35, 1.0f, 3, 5),
                entry(new ItemStack(Items.EMERALD), 25, 1.0f, 4, 8),
                entry(new ItemStack(Items.NETHERITE_SCRAP), 10, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.TALISMAN_PAPER.get()), 25, 1.0f, 20, 32),
                entry(new ItemStack(ModItems.JADE.get()), 50, 1.0f, 5, 10),
                entry(new ItemStack(ModItems.BLACK_IRON_INGOT.get()), 45, 1.0f, 5, 8),
                entry(new ItemStack(ModItems.FROST_SILVER_INGOT.get()), 40, 1.0f, 4, 6),
                entry(new ItemStack(ModItems.LIVING_CORE.get()), 40, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.UNDEAD_CORE.get()), 40, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.FIRE_CORE.get()), 30, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.WATER_CORE.get()), 30, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.WOOD_CORE.get()), 30, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.EARTH_CORE.get()), 30, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.METAL_CORE.get()), 30, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.LIGHTNING_CORE.get()), 25, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.WIND_CORE.get()), 25, 1.0f, 1, 3),
                entry(new ItemStack(Items.EXPERIENCE_BOTTLE), 55, 1.0f, 10, 20),
                entry(new ItemStack(ModItems.BLINDED_SENSES_POWDER.get()), 18, 1.0f, 1, 4),
                entry(new ItemStack(ModItems.VENOMOUS_MERIDIAN_POWDER.get()), 18, 1.0f, 1, 4),
                entry(new ItemStack(ModItems.PARALYZED_BODY_POWDER.get()), 15, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.QI_DEVOURING_POWDER.get()), 12, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.SCORCHING_YANG_POWDER.get()), 10, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.CRACKED_MERIDIANS_POWDER.get()), 10, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.GOLDEN_SUN_LEAF.get()), 32, 1.0f, 3, 7),
                entry(new ItemStack(ModItems.JADE_DEW_GRASS.get()), 28, 1.0f, 2, 6),
                entry(new ItemStack(ModItems.JADE_BAMBOO_OF_SERENITY.get()), 22, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.IRONWOOD_SPROUT.get()), 18, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.SOUL_ANCHOR_TALISMAN.get()), 18, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.DEATH_RECALL_TALISMAN.get()), 12, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.SPATIAL_RUPTURE_TALISMAN_T1.get()), 10, 1.0f, 1, 1),
                // Technique drops — higher chances
                pageEntry("bloodfeast_soul_refining_scripture", 0, 5, 24),
                pageEntry("white_lightning_ten_stage_technique", 0, 9, 20),
                pageEntry("soul_forged_weapon_manual", 0, 3, 22),
                entry(techniqueManual("lung_metal_technique"), 20, 1.0f, 1, 1),
                entry(techniqueManual("lightning_essence_technique"), 20, 1.0f, 1, 1),

                pillEntry(ModItems.ESSENCE_GATHERING_PILL.get(), 20,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 3),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.SOUL_FOCUS_PILL.get(), 20,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 3),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.INNER_REINFORCEMENT_PILL.get(), 20,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 3),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),

                pillEntry(ModItems.QI_ENHANCED_REGEN_PILL.get(), 20,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 3),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.QI_REPLENISHING_PILL.get(), 20,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 1, 3),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90))
        ));

        // Rank 3 — max technique chances + full random page ranges
        register("golden_core", 3, 0.62f, List.of(
                entry(new ItemStack(Items.IRON_INGOT), 12, 1.0f, 10, 18),
                entry(new ItemStack(Items.GOLD_INGOT), 25, 1.0f, 6, 12),
                entry(new ItemStack(Items.DIAMOND), 40, 1.0f, 3, 6),
                entry(new ItemStack(Items.EMERALD), 30, 1.0f, 5, 10),
                entry(new ItemStack(Items.NETHERITE_SCRAP), 15, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.TALISMAN_PAPER.get()), 25, 1.0f, 24, 40),
                entry(new ItemStack(ModItems.JADE.get()), 55, 1.0f, 6, 12),
                entry(new ItemStack(ModItems.BLACK_IRON_INGOT.get()), 50, 1.0f, 6, 10),
                entry(new ItemStack(ModItems.FROST_SILVER_INGOT.get()), 45, 1.0f, 5, 8),
                entry(new ItemStack(ModItems.LIVING_CORE.get()), 45, 1.0f, 2, 3),
                entry(new ItemStack(ModItems.UNDEAD_CORE.get()), 45, 1.0f, 2, 3),
                entry(new ItemStack(ModItems.FIRE_CORE.get()), 35, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.WATER_CORE.get()), 35, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.WOOD_CORE.get()), 35, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.EARTH_CORE.get()), 35, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.METAL_CORE.get()), 35, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.LIGHTNING_CORE.get()), 30, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.WIND_CORE.get()), 30, 1.0f, 1, 3),
                entry(new ItemStack(Items.EXPERIENCE_BOTTLE), 60, 1.0f, 12, 24),
                entry(new ItemStack(ModItems.BLINDED_SENSES_POWDER.get()), 22, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.VENOMOUS_MERIDIAN_POWDER.get()), 22, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.PARALYZED_BODY_POWDER.get()), 18, 1.0f, 1, 4),
                entry(new ItemStack(ModItems.QI_DEVOURING_POWDER.get()), 15, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.SCORCHING_YANG_POWDER.get()), 12, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.CRACKED_MERIDIANS_POWDER.get()), 12, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.FROST_SILKWORM_POWDER.get()), 10, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.CORROSIVE_POISON_POWDER.get()), 10, 1.0f, 1, 2),
                entry(new ItemStack(ModItems.GOLDEN_SUN_LEAF.get()), 35, 1.0f, 4, 8),
                entry(new ItemStack(ModItems.JADE_DEW_GRASS.get()), 32, 1.0f, 3, 7),
                entry(new ItemStack(ModItems.JADE_BAMBOO_OF_SERENITY.get()), 25, 1.0f, 2, 5),
                entry(new ItemStack(ModItems.IRONWOOD_SPROUT.get()), 22, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.SOUL_ANCHOR_TALISMAN.get()), 22, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.DEATH_RECALL_TALISMAN.get()), 15, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.SPATIAL_RUPTURE_TALISMAN_T1.get()), 12, 1.0f, 1, 1),
                // Max technique drops — random page ranges (like chest loot)
                pageEntry("bloodfeast_soul_refining_scripture", 0, 5, 24),
                pageEntry("white_lightning_ten_stage_technique", 0, 9, 20),
                pageEntry("soul_forged_weapon_manual", 0, 3, 22),
                entry(techniqueManual("lung_metal_technique"), 22, 1.0f, 1, 1),
                entry(techniqueManual("lightning_essence_technique"), 22, 1.0f, 1, 1),
                entry(techniqueManual("sword_comprehension_technique"), 18, 1.0f, 1, 1),

                pillEntry(ModItems.ESSENCE_GATHERING_PILL.get(), 20,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 2, 3),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.SOUL_FOCUS_PILL.get(), 20,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 2, 3),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.INNER_REINFORCEMENT_PILL.get(), 20,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 2, 3),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),

                pillEntry(ModItems.QI_ENHANCED_REGEN_PILL.get(), 20,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 2, 3),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.QI_REPLENISHING_PILL.get(), 20,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 2, 3),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90))
        ));

        // ═════════════════════════════════════════════════════════════════════
        // NASCENT SOUL (realmIndex 4) — Endgame, everything + rare exclusives
        // ═════════════════════════════════════════════════════════════════════
        register("nascent_soul", 1, 0.65f, List.of(
                entry(new ItemStack(Items.GOLD_INGOT), 25, 1.0f, 8, 16),
                entry(new ItemStack(Items.DIAMOND), 45, 1.0f, 4, 8),
                entry(new ItemStack(Items.EMERALD), 35, 1.0f, 6, 12),
                entry(new ItemStack(Items.NETHERITE_SCRAP), 20, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.TALISMAN_PAPER.get()), 25, 1.0f, 28, 48),
                entry(new ItemStack(ModItems.JADE.get()), 60, 1.0f, 8, 14),
                entry(new ItemStack(ModItems.BLACK_IRON_INGOT.get()), 55, 1.0f, 7, 12),
                entry(new ItemStack(ModItems.FROST_SILVER_INGOT.get()), 50, 1.0f, 6, 10),
                entry(new ItemStack(ModItems.LIVING_CORE.get()), 50, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.UNDEAD_CORE.get()), 50, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.FIRE_CORE.get()), 40, 1.0f, 2, 3),
                entry(new ItemStack(ModItems.WATER_CORE.get()), 40, 1.0f, 2, 3),
                entry(new ItemStack(ModItems.WOOD_CORE.get()), 40, 1.0f, 2, 3),
                entry(new ItemStack(ModItems.EARTH_CORE.get()), 40, 1.0f, 2, 3),
                entry(new ItemStack(ModItems.METAL_CORE.get()), 40, 1.0f, 2, 3),
                entry(new ItemStack(ModItems.LIGHTNING_CORE.get()), 35, 1.0f, 2, 3),
                entry(new ItemStack(ModItems.WIND_CORE.get()), 35, 1.0f, 2, 3),
                entry(new ItemStack(Items.EXPERIENCE_BOTTLE), 65, 1.0f, 14, 28),
                entry(new ItemStack(ModItems.BLINDED_SENSES_POWDER.get()), 25, 1.0f, 2, 5),
                entry(new ItemStack(ModItems.VENOMOUS_MERIDIAN_POWDER.get()), 25, 1.0f, 2, 5),
                entry(new ItemStack(ModItems.PARALYZED_BODY_POWDER.get()), 22, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.QI_DEVOURING_POWDER.get()), 18, 1.0f, 1, 4),
                entry(new ItemStack(ModItems.SCORCHING_YANG_POWDER.get()), 15, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.CRACKED_MERIDIANS_POWDER.get()), 15, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.FROST_SILKWORM_POWDER.get()), 12, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.CORROSIVE_POISON_POWDER.get()), 12, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.GOLDEN_SUN_LEAF.get()), 40, 1.0f, 4, 10),
                entry(new ItemStack(ModItems.JADE_DEW_GRASS.get()), 35, 1.0f, 3, 8),
                entry(new ItemStack(ModItems.JADE_BAMBOO_OF_SERENITY.get()), 28, 1.0f, 2, 6),
                entry(new ItemStack(ModItems.IRONWOOD_SPROUT.get()), 25, 1.0f, 2, 5),
                entry(new ItemStack(ModItems.SOUL_ANCHOR_TALISMAN.get()), 25, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.DEATH_RECALL_TALISMAN.get()), 18, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.SPATIAL_RUPTURE_TALISMAN_T1.get()), 15, 1.0f, 1, 1),
                // Technique drops
                pageEntry("bloodfeast_soul_refining_scripture", 0, 5, 24),
                pageEntry("white_lightning_ten_stage_technique", 0, 9, 20),
                pageEntry("soul_forged_weapon_manual", 0, 3, 22),
                entry(techniqueManual("sword_comprehension_technique"), 22, 1.0f, 1, 1),
                entry(techniqueManual("dawning_sun_scripture"), 22, 1.0f, 1, 1),

                pillEntry(ModItems.ESSENCE_GATHERING_PILL.get(), 20,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 2, 4),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.SOUL_FOCUS_PILL.get(), 20,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 2, 4),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.INNER_REINFORCEMENT_PILL.get(), 20,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 2, 4),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),

                pillEntry(ModItems.QI_ENHANCED_REGEN_PILL.get(), 20,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 2, 4),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.QI_REPLENISHING_PILL.get(), 20,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 2, 4),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90))
        ));

        register("nascent_soul", 2, 0.68f, List.of(
                entry(new ItemStack(Items.GOLD_INGOT), 25, 1.0f, 10, 20),
                entry(new ItemStack(Items.DIAMOND), 50, 1.0f, 5, 10),
                entry(new ItemStack(Items.EMERALD), 40, 1.0f, 8, 16),
                entry(new ItemStack(Items.NETHERITE_SCRAP), 25, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.TALISMAN_PAPER.get()), 25, 1.0f, 32, 56),
                entry(new ItemStack(ModItems.JADE.get()), 65, 1.0f, 10, 18),
                entry(new ItemStack(ModItems.BLACK_IRON_INGOT.get()), 60, 1.0f, 8, 14),
                entry(new ItemStack(ModItems.FROST_SILVER_INGOT.get()), 55, 1.0f, 7, 12),
                entry(new ItemStack(ModItems.LIVING_CORE.get()), 55, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.UNDEAD_CORE.get()), 55, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.FIRE_CORE.get()), 45, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.WATER_CORE.get()), 45, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.WOOD_CORE.get()), 45, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.EARTH_CORE.get()), 45, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.METAL_CORE.get()), 45, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.LIGHTNING_CORE.get()), 40, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.WIND_CORE.get()), 40, 1.0f, 2, 4),
                entry(new ItemStack(Items.EXPERIENCE_BOTTLE), 70, 1.0f, 16, 32),
                entry(new ItemStack(ModItems.BLINDED_SENSES_POWDER.get()), 28, 1.0f, 2, 6),
                entry(new ItemStack(ModItems.VENOMOUS_MERIDIAN_POWDER.get()), 28, 1.0f, 2, 6),
                entry(new ItemStack(ModItems.PARALYZED_BODY_POWDER.get()), 25, 1.0f, 2, 5),
                entry(new ItemStack(ModItems.QI_DEVOURING_POWDER.get()), 22, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.SCORCHING_YANG_POWDER.get()), 18, 1.0f, 1, 4),
                entry(new ItemStack(ModItems.CRACKED_MERIDIANS_POWDER.get()), 18, 1.0f, 1, 4),
                entry(new ItemStack(ModItems.FROST_SILKWORM_POWDER.get()), 15, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.CORROSIVE_POISON_POWDER.get()), 15, 1.0f, 1, 3),
                entry(new ItemStack(ModItems.GOLDEN_SUN_LEAF.get()), 45, 1.0f, 5, 12),
                entry(new ItemStack(ModItems.JADE_DEW_GRASS.get()), 40, 1.0f, 4, 10),
                entry(new ItemStack(ModItems.JADE_BAMBOO_OF_SERENITY.get()), 32, 1.0f, 3, 7),
                entry(new ItemStack(ModItems.IRONWOOD_SPROUT.get()), 28, 1.0f, 2, 6),
                entry(new ItemStack(ModItems.SOUL_ANCHOR_TALISMAN.get()), 28, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.DEATH_RECALL_TALISMAN.get()), 22, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.SPATIAL_RUPTURE_TALISMAN_T1.get()), 18, 1.0f, 1, 1),
                // Technique drops — higher chances
                pageEntry("bloodfeast_soul_refining_scripture", 0, 5, 24),
                pageEntry("white_lightning_ten_stage_technique", 0, 9, 20),
                pageEntry("soul_forged_weapon_manual", 0, 3, 22),
                entry(techniqueManual("sword_comprehension_technique"), 25, 1.0f, 1, 1),
                entry(techniqueManual("dawning_sun_scripture"), 25, 1.0f, 1, 1),
                entry(techniqueManual("pale_moon_scripture"), 22, 1.0f, 1, 1),

                pillEntry(ModItems.ESSENCE_GATHERING_PILL.get(), 22,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 2, 4),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.SOUL_FOCUS_PILL.get(), 22,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 2, 4),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.INNER_REINFORCEMENT_PILL.get(), 22,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 2, 4),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),

                pillEntry(ModItems.QI_ENHANCED_REGEN_PILL.get(), 22,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 2, 4),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.QI_REPLENISHING_PILL.get(), 22,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 2, 4),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90))
        ));

        // Rank 3 — max everything
        register("nascent_soul", 3, 0.72f, List.of(
                entry(new ItemStack(Items.GOLD_INGOT), 25, 1.0f, 12, 24),
                entry(new ItemStack(Items.DIAMOND), 55, 1.0f, 6, 12),
                entry(new ItemStack(Items.EMERALD), 45, 1.0f, 10, 20),
                entry(new ItemStack(Items.NETHERITE_SCRAP), 30, 1.0f, 2, 5),
                entry(new ItemStack(ModItems.TALISMAN_PAPER.get()), 25, 1.0f, 36, 64),
                entry(new ItemStack(ModItems.JADE.get()), 70, 1.0f, 12, 22),
                entry(new ItemStack(ModItems.BLACK_IRON_INGOT.get()), 65, 1.0f, 10, 16),
                entry(new ItemStack(ModItems.FROST_SILVER_INGOT.get()), 60, 1.0f, 8, 14),
                entry(new ItemStack(ModItems.LIVING_CORE.get()), 60, 1.0f, 3, 5),
                entry(new ItemStack(ModItems.UNDEAD_CORE.get()), 60, 1.0f, 3, 5),
                entry(new ItemStack(ModItems.FIRE_CORE.get()), 50, 1.0f, 2, 5),
                entry(new ItemStack(ModItems.WATER_CORE.get()), 50, 1.0f, 2, 5),
                entry(new ItemStack(ModItems.WOOD_CORE.get()), 50, 1.0f, 2, 5),
                entry(new ItemStack(ModItems.EARTH_CORE.get()), 50, 1.0f, 2, 5),
                entry(new ItemStack(ModItems.METAL_CORE.get()), 50, 1.0f, 2, 5),
                entry(new ItemStack(ModItems.LIGHTNING_CORE.get()), 45, 1.0f, 2, 5),
                entry(new ItemStack(ModItems.WIND_CORE.get()), 45, 1.0f, 2, 5),
                entry(new ItemStack(Items.EXPERIENCE_BOTTLE), 75, 1.0f, 20, 40),
                entry(new ItemStack(ModItems.BLINDED_SENSES_POWDER.get()), 32, 1.0f, 3, 7),
                entry(new ItemStack(ModItems.VENOMOUS_MERIDIAN_POWDER.get()), 32, 1.0f, 3, 7),
                entry(new ItemStack(ModItems.PARALYZED_BODY_POWDER.get()), 28, 1.0f, 2, 6),
                entry(new ItemStack(ModItems.QI_DEVOURING_POWDER.get()), 25, 1.0f, 2, 5),
                entry(new ItemStack(ModItems.SCORCHING_YANG_POWDER.get()), 22, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.CRACKED_MERIDIANS_POWDER.get()), 22, 1.0f, 2, 4),
                entry(new ItemStack(ModItems.FROST_SILKWORM_POWDER.get()), 18, 1.0f, 1, 4),
                entry(new ItemStack(ModItems.CORROSIVE_POISON_POWDER.get()), 18, 1.0f, 1, 4),
                entry(new ItemStack(ModItems.GOLDEN_SUN_LEAF.get()), 50, 1.0f, 6, 14),
                entry(new ItemStack(ModItems.JADE_DEW_GRASS.get()), 45, 1.0f, 5, 12),
                entry(new ItemStack(ModItems.JADE_BAMBOO_OF_SERENITY.get()), 38, 1.0f, 3, 8),
                entry(new ItemStack(ModItems.IRONWOOD_SPROUT.get()), 32, 1.0f, 3, 7),
                entry(new ItemStack(ModItems.SOUL_ANCHOR_TALISMAN.get()), 32, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.DEATH_RECALL_TALISMAN.get()), 25, 1.0f, 1, 1),
                entry(new ItemStack(ModItems.SPATIAL_RUPTURE_TALISMAN_T1.get()), 22, 1.0f, 1, 1),
                // Max technique drops — full random page ranges
                pageEntry("bloodfeast_soul_refining_scripture", 0, 5, 24),
                pageEntry("white_lightning_ten_stage_technique", 0, 9, 20),
                pageEntry("soul_forged_weapon_manual", 0, 3, 22),
                entry(techniqueManual("sword_comprehension_technique"), 28, 1.0f, 1, 1),
                entry(techniqueManual("dawning_sun_scripture"), 28, 1.0f, 1, 1),
                entry(techniqueManual("pale_moon_scripture"), 25, 1.0f, 1, 1),
                entry(techniqueManual("scholarly_soul_technique"), 22, 1.0f, 1, 1),

                pillEntry(ModItems.ESSENCE_GATHERING_PILL.get(), 22,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 3, 4),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.SOUL_FOCUS_PILL.get(), 22,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 3, 4),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.INNER_REINFORCEMENT_PILL.get(), 22,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 3, 4),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),

                pillEntry(ModItems.QI_ENHANCED_REGEN_PILL.get(), 22,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 3, 4),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90)),
                pillEntry(ModItems.QI_REPLENISHING_PILL.get(), 22,
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_MAJOR_REALM.get(), 3, 4),
                        SetRandomIntComponentFunction.builder(ModDataComponents.PILL_PURITY.get(), 10, 90))
        ));

        // Future realms — placeholder scaling
        for (int realmIdx = 5; realmIdx < MobCultivationList.getRealmIds().size(); realmIdx++) {
            String realmId = MobCultivationList.getRealmIds().get(realmIdx);
            for (int stage = 1; stage <= 3; stage++) {
                float chance = 0.55f + (realmIdx * 0.05f) + (stage * 0.02f);
                registerFutureRealm(realmId, stage, chance, realmIdx);
            }
        }
    }

    private static void registerFutureRealm(String realmId, int stage, float chance, int realmIdx) {
        float scale = 1.0f + ((realmIdx - 4) * 0.25f);
        register(realmId, stage, Math.min(0.95f, chance), List.of(
                entry(new ItemStack(Items.GOLD_INGOT), 25, scale, 15, 30),
                entry(new ItemStack(Items.DIAMOND), 55, scale, 8, 16),
                entry(new ItemStack(Items.EMERALD), 45, scale, 12, 24),
                entry(new ItemStack(Items.NETHERITE_SCRAP), 35, scale, 3, 6),
                entry(new ItemStack(ModItems.TALISMAN_PAPER.get()), 25, scale, 40, 80),
                entry(new ItemStack(ModItems.JADE.get()), 70, scale, 15, 28),
                entry(new ItemStack(ModItems.BLACK_IRON_INGOT.get()), 65, scale, 12, 20),
                entry(new ItemStack(ModItems.FROST_SILVER_INGOT.get()), 60, scale, 10, 18),
                entry(new ItemStack(ModItems.LIVING_CORE.get()), 60, scale, 3, 6),
                entry(new ItemStack(ModItems.UNDEAD_CORE.get()), 60, scale, 3, 6),
                entry(new ItemStack(ModItems.FIRE_CORE.get()), 50, scale, 3, 6),
                entry(new ItemStack(ModItems.WATER_CORE.get()), 50, scale, 3, 6),
                entry(new ItemStack(ModItems.WOOD_CORE.get()), 50, scale, 3, 6),
                entry(new ItemStack(ModItems.EARTH_CORE.get()), 50, scale, 3, 6),
                entry(new ItemStack(ModItems.METAL_CORE.get()), 50, scale, 3, 6),
                entry(new ItemStack(ModItems.LIGHTNING_CORE.get()), 45, scale, 3, 6),
                entry(new ItemStack(ModItems.WIND_CORE.get()), 45, scale, 3, 6),
                entry(new ItemStack(Items.EXPERIENCE_BOTTLE), 75, scale, 24, 48),
                entry(new ItemStack(ModItems.BLINDED_SENSES_POWDER.get()), 35, scale, 3, 8),
                entry(new ItemStack(ModItems.VENOMOUS_MERIDIAN_POWDER.get()), 35, scale, 3, 8),
                entry(new ItemStack(ModItems.PARALYZED_BODY_POWDER.get()), 30, scale, 2, 7),
                entry(new ItemStack(ModItems.QI_DEVOURING_POWDER.get()), 28, scale, 2, 6),
                entry(new ItemStack(ModItems.SCORCHING_YANG_POWDER.get()), 25, scale, 2, 5),
                entry(new ItemStack(ModItems.CRACKED_MERIDIANS_POWDER.get()), 25, scale, 2, 5),
                entry(new ItemStack(ModItems.FROST_SILKWORM_POWDER.get()), 20, scale, 1, 5),
                entry(new ItemStack(ModItems.CORROSIVE_POISON_POWDER.get()), 20, scale, 1, 5),
                entry(new ItemStack(ModItems.GOLDEN_SUN_LEAF.get()), 50, scale, 7, 16),
                entry(new ItemStack(ModItems.JADE_DEW_GRASS.get()), 45, scale, 6, 14),
                entry(new ItemStack(ModItems.JADE_BAMBOO_OF_SERENITY.get()), 38, scale, 4, 10),
                entry(new ItemStack(ModItems.IRONWOOD_SPROUT.get()), 32, scale, 3, 8),
                entry(new ItemStack(ModItems.SOUL_ANCHOR_TALISMAN.get()), 32, scale, 1, 1),
                entry(new ItemStack(ModItems.DEATH_RECALL_TALISMAN.get()), 25, scale, 1, 1),
                entry(new ItemStack(ModItems.SPATIAL_RUPTURE_TALISMAN_T1.get()), 22, scale, 1, 1),
                // Scaled page entries — weight and max page grow with realm depth
                entry(techniqueManual("sword_comprehension_technique"), 30, scale, 1, 1)
        ));
    }

    // ── Helper Methods ──────────────────────────────────────────────────────

    private static void register(String realmId, int stage, float chance, List<RankLootTable.RankLootEntry> entries) {
        TABLES.put(key(realmId, stage), new RankLootTable(realmId, stage, chance, entries));
    }

    private static String key(String realmId, int stage) {
        return realmId + ":" + stage;
    }

    public static RankLootTable get(String realmId, int stage) {
        return TABLES.getOrDefault(key(realmId, stage), null);
    }

    public static boolean hasLoot(String realmId, int stage) {
        return TABLES.containsKey(key(realmId, stage));
    }

    private static RankLootTable.RankLootEntry entry(ItemStack stack, int weight, float quantityScale, int min, int max) {
        return RankLootTable.RankLootEntry.of(stack.copy(), weight, quantityScale, min, max);
    }

    /** Pill entry using SetRandomIntComponentFunction builders */
    @SafeVarargs
    private static RankLootTable.RankLootEntry pillEntry(net.minecraft.world.item.Item pillItem, int weight, SetRandomIntComponentFunction.Builder... componentBuilders) {
        ItemStack stack = new ItemStack(pillItem);
        List<SetRandomIntComponentFunction> functions = new ArrayList<>();
        for (SetRandomIntComponentFunction.Builder builder : componentBuilders) {
            functions.add(builder.build());
        }
        return RankLootTable.RankLootEntry.ofPill(stack, weight, 1.0f, 1, 1, functions);
    }

    /** Pill entry with count range */
    @SafeVarargs
    private static RankLootTable.RankLootEntry pillEntry(net.minecraft.world.item.Item pillItem, int weight, int minCount, int maxCount, SetRandomIntComponentFunction.Builder... componentBuilders) {
        ItemStack stack = new ItemStack(pillItem);
        List<SetRandomIntComponentFunction> functions = new ArrayList<>();
        for (SetRandomIntComponentFunction.Builder builder : componentBuilders) {
            functions.add(builder.build());
        }
        return RankLootTable.RankLootEntry.ofPill(stack, weight, 1.0f, minCount, maxCount, functions);
    }

    /** Technique page entry using SetTechniquePageFunction */
    private static RankLootTable.RankLootEntry pageEntry(String techniqueId, int minPage, int maxPage, int weight) {
        ItemStack stack = new ItemStack(ModItems.TECHNIQUE_PAGE.get());
        stack.set(ModDataComponents.TECHNIQUE_ID.get(), techniqueId);
        SetTechniquePageFunction function = (SetTechniquePageFunction) SetTechniquePageFunction.builder(
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, techniqueId),
                minPage,
                maxPage
        ).build();
        return RankLootTable.RankLootEntry.ofPage(stack, weight, 1.0f, 1, 1, function);
    }

    /** Creates a technique manual stack */
    private static ItemStack techniqueManual(String techniqueId) {
        return net.thejadeproject.ascension.common.items.techniques.TechniqueTransferItem.createWithTechnique(techniqueId);
    }

    /** Creates a technique page with specific page */
    private static ItemStack techniquePage(String techniqueId, int page) {
        return net.thejadeproject.ascension.common.items.techniques.TechniquePageItem.createWithTechnique(techniqueId, page);
    }

}