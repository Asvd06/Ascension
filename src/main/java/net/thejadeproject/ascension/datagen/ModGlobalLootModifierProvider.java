package net.thejadeproject.ascension.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.common.items.ModItems;
import net.thejadeproject.ascension.datagen.loot.AddItemModifier;
import net.thejadeproject.ascension.datagen.loot.AddTechniquePageModifier;
import net.thejadeproject.ascension.datagen.loot.conditions.AddTechniqueManualModifier;
import net.thejadeproject.ascension.datagen.loot.conditions.MobRankLootCondition;

import java.util.concurrent.CompletableFuture;

public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, AscensionCraft.MOD_ID);
    }
    @Override
    protected void start() {

        //Technique Pages

        add("bloodfeast_ch5_from_ender_dragon", new AddTechniquePageModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.parse("minecraft:entities/ender_dragon")).build(),
                        LootItemRandomChanceCondition.randomChance(0.03f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "bloodfeast_soul_refining_scripture"),
                5
        ));

        //Techniques
        add("basic_cultivation_technique_from_village", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/village/village_plains_house")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "basic_cultivation_technique")
        ));

        add("advanced_cultivation_technique_from_zombie_villager", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/zombie_villager")).build(),
                        LootItemKilledByPlayerCondition.killedByPlayer().build(),
                        LootItemRandomChanceCondition.randomChance(0.04f).build(),
                        MobRankLootCondition.between("qi_gathering", 5, "formation_establishment", 9).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "advanced_cultivation_technique")
        ));

        add("advanced_cultivation_technique_from_stronghold_library", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/stronghold_library")).build(),
                        LootItemRandomChanceCondition.randomChance(0.06f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "advanced_cultivation_technique")
        ));

        add("fire_essence_technique_from_blaze", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/blaze")).build(),
                        LootItemKilledByPlayerCondition.killedByPlayer().build(),
                        LootItemRandomChanceCondition.randomChance(0.04f).build(),
                        MobRankLootCondition.between("formation_establishment", 5, "formation_establishment", 9).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "fire_essence_technique")
        ));

        add("water_essence_technique_from_elder_guardian", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/elder_guardian")).build(),
                        LootItemKilledByPlayerCondition.killedByPlayer().build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build(),
                        MobRankLootCondition.between("formation_establishment", 6, "formation_establishment", 9).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "water_essence_technique")
        ));

        add("wood_essence_technique_from_jungle_temple", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/jungle_temple")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "wood_essence_technique")
        ));

        add("earth_essence_technique_from_trail_ruins", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_rare")).build(),
                        LootItemRandomChanceCondition.randomChance(0.04f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "earth_essence_technique")
        ));

        add("metal_essence_technique_from_iron_golem", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/iron_golem")).build(),
                        LootItemKilledByPlayerCondition.killedByPlayer().build(),
                        LootItemRandomChanceCondition.randomChance(0.04f).build(),
                        MobRankLootCondition.between("formation_establishment", 4, "formation_establishment", 9).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "metal_essence_technique")
        ));

        add("lightning_essence_technique_from_creeper", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/creeper")).build(),
                        LootItemKilledByPlayerCondition.killedByPlayer().build(),
                        LootItemRandomChanceCondition.randomChance(0.04f).build(),
                        MobRankLootCondition.between("formation_establishment", 6, "formation_establishment", 9).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "lightning_essence_technique")
        ));

        add("wind_essence_technique_from_phantom", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/phantom")).build(),
                        LootItemKilledByPlayerCondition.killedByPlayer().build(),
                        LootItemRandomChanceCondition.randomChance(0.04f).build(),
                        MobRankLootCondition.between("formation_establishment", 7, "formation_establishment", 9).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "wind_essence_technique")
        ));

        add("five_element_cultivation_technique_from_end_city", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                        LootItemRandomChanceCondition.randomChance(0.03f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "five_element_cultivation_technique")
        ));

        add("heart_fire_technique_from_nether_fortress", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "heart_fire_technique")
        ));

        add("heart_fire_technique_from_wither_skeleton", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/wither_skeleton")).build(),
                        LootItemKilledByPlayerCondition.killedByPlayer().build(),
                        LootItemRandomChanceCondition.randomChance(0.03f).build(),
                        MobRankLootCondition.between("qi_gathering", 1, "formation_establishment", 9).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "heart_fire_technique")
        ));

        add("kidney_water_technique_from_drowned", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/drowned")).build(),
                        LootItemKilledByPlayerCondition.killedByPlayer().build(),
                        LootItemRandomChanceCondition.randomChance(0.03f).build(),
                        MobRankLootCondition.between("qi_gathering", 1, "formation_establishment", 9).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "kidney_water_technique")
        ));

        add("kidney_water_technique_from_ocean_ruin", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/underwater_ruin_big")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "kidney_water_technique")
        ));

        add("liver_wood_technique_from_jungle_temple", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/jungle_temple")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "liver_wood_technique")
        ));

        add("spleen_earth_technique_from_desert_pyramid", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/desert_pyramid")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "spleen_earth_technique")
        ));

        add("lung_metal_technique_from_mineshaft", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "lung_metal_technique")
        ));

        add("lung_metal_technique_from_iron_golem", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/iron_golem")).build(),
                        LootItemKilledByPlayerCondition.killedByPlayer().build(),
                        LootItemRandomChanceCondition.randomChance(0.03f).build(),
                        MobRankLootCondition.between("qi_gathering", 1, "formation_establishment", 9).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "lung_metal_technique")
        ));

        add("wood_fire_body_technique_from_mansion", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                        LootItemRandomChanceCondition.randomChance(0.04f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "wood_fire_body_technique")
        ));

        add("fire_earth_body_technique_from_bastion", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/bastion_treasure")).build(),
                        LootItemRandomChanceCondition.randomChance(0.04f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "fire_earth_body_technique")
        ));

        add("earth_metal_body_technique_from_ancient_city", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                        LootItemRandomChanceCondition.randomChance(0.04f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "earth_metal_body_technique")
        ));

        add("metal_water_body_technique_from_shipwreck", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/shipwreck_treasure")).build(),
                        LootItemRandomChanceCondition.randomChance(0.04f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "metal_water_body_technique")
        ));

        add("water_wood_body_technique_from_dungeon", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                        LootItemRandomChanceCondition.randomChance(0.04f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "water_wood_body_technique")
        ));

        add("wood_fire_earth_body_technique_from_mansion", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                        LootItemRandomChanceCondition.randomChance(0.03f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "wood_fire_earth_body_technique")
        ));

        add("fire_earth_metal_body_technique_from_bastion", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/bastion_treasure")).build(),
                        LootItemRandomChanceCondition.randomChance(0.03f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "fire_earth_metal_body_technique")
        ));

        add("earth_metal_water_body_technique_from_ancient_city", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                        LootItemRandomChanceCondition.randomChance(0.03f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "earth_metal_water_body_technique")
        ));

        add("metal_water_wood_body_technique_from_end_city", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                        LootItemRandomChanceCondition.randomChance(0.03f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "metal_water_wood_body_technique")
        ));

        add("water_wood_fire_body_technique_from_stronghold", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/stronghold_library")).build(),
                        LootItemRandomChanceCondition.randomChance(0.03f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "water_wood_fire_body_technique")
        ));

        add("wood_fire_earth_metal_body_technique_from_ancient_city", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                        LootItemRandomChanceCondition.randomChance(0.02f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "wood_fire_earth_metal_body_technique")
        ));

        add("fire_earth_metal_water_body_technique_from_bastion", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/bastion_treasure")).build(),
                        LootItemRandomChanceCondition.randomChance(0.02f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "fire_earth_metal_water_body_technique")
        ));

        add("earth_metal_water_wood_body_technique_from_end_city", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                        LootItemRandomChanceCondition.randomChance(0.02f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "earth_metal_water_wood_body_technique")
        ));

        add("metal_water_wood_fire_body_technique_from_mansion", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                        LootItemRandomChanceCondition.randomChance(0.02f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "metal_water_wood_fire_body_technique")
        ));

        add("water_wood_fire_earth_body_technique_from_stronghold", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/stronghold_library")).build(),
                        LootItemRandomChanceCondition.randomChance(0.02f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "water_wood_fire_earth_body_technique")
        ));

        add("scholarly_soul_technique_from_stronghold_library", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/stronghold_library")).build(),
                        LootItemRandomChanceCondition.randomChance(0.04f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "scholarly_soul_technique")
        ));

        add("scholarly_soul_technique_from_zombie_villager", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/zombie_villager")).build(),
                        LootItemKilledByPlayerCondition.killedByPlayer().build(),
                        LootItemRandomChanceCondition.randomChance(0.03f).build(),
                        MobRankLootCondition.between("formation_establishment", 6, "formation_establishment", 9).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "scholarly_soul_technique")
        ));

        add("pale_moon_scripture_from_ancient_city", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                        LootItemRandomChanceCondition.randomChance(0.04f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "pale_moon_scripture")
        ));

        add("pale_moon_scripture_from_phantom", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/phantom")).build(),
                        LootItemKilledByPlayerCondition.killedByPlayer().build(),
                        LootItemRandomChanceCondition.randomChance(0.03f).build(),
                        MobRankLootCondition.between("formation_establishment", 8, "formation_establishment", 9).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "pale_moon_scripture")
        ));

        add("gibbous_moon_scripture_from_end_city", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                        LootItemRandomChanceCondition.randomChance(0.03f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "gibbous_moon_scripture")
        ));

        add("gibbous_moon_scripture_from_enderman", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/enderman")).build(),
                        LootItemKilledByPlayerCondition.killedByPlayer().build(),
                        LootItemRandomChanceCondition.randomChance(0.02f).build(),
                        MobRankLootCondition.between("formation_establishment", 8, "formation_establishment", 9).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "gibbous_moon_scripture")
        ));

        add("sword_comprehension_technique_from_pillager_outpost", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/pillager_outpost")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "sword_comprehension_technique")
        ));

        add("sword_comprehension_technique_from_vindicator", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/vindicator")).build(),
                        LootItemKilledByPlayerCondition.killedByPlayer().build(),
                        LootItemRandomChanceCondition.randomChance(0.03f).build(),
                        MobRankLootCondition.between("formation_establishment", 4, "formation_establishment", 9).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "sword_comprehension_technique")
        ));

        add("sword_comprehension_technique_from_mansion", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                        LootItemRandomChanceCondition.randomChance(0.04f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "sword_comprehension_technique")
        ));

        add("dawning_sun_scripture_from_nether_fortress", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                        LootItemRandomChanceCondition.randomChance(0.07f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "dawning_sun_scripture")
        ));

        add("dawning_sun_scripture_from_blaze", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/blaze")).build(),
                        LootItemKilledByPlayerCondition.killedByPlayer().build(),
                        LootItemRandomChanceCondition.randomChance(0.03f).build(),
                        MobRankLootCondition.between("formation_establishment", 6, "formation_establishment", 9).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "dawning_sun_scripture")
        ));

        add("zenith_sun_scripture_from_bastion", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/bastion_treasure")).build(),
                        LootItemRandomChanceCondition.randomChance(0.03f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "zenith_sun_scripture")
        ));

        add("zenith_sun_scripture_from_wither", new AddTechniqueManualModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/wither")).build(),
                        LootItemKilledByPlayerCondition.killedByPlayer().build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build()
                },
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "zenith_sun_scripture")
        ));

        //Herbs

        this.add("jade_dew_grass_from_village", new AddItemModifier(new LootItemCondition[] {
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/village/village_plains_house")).build(),
                LootItemRandomChanceCondition.randomChance(0.27F).build()
        }, ModItems.JADE_DEW_GRASS_SEEDS.get()));
        this.add("hundred_year_ginseng_from_village", new AddItemModifier(new LootItemCondition[] {
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/village/village_plains_house")).build(),
                LootItemRandomChanceCondition.randomChance(0.16F).build()
        }, ModItems.HUNDRED_YEAR_GINSENG.get()));
        this.add("hundred_year_ginseng_from_dungeon", new AddItemModifier(new LootItemCondition[] {
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                LootItemRandomChanceCondition.randomChance(0.19F).build()
        }, ModItems.HUNDRED_YEAR_GINSENG.get()));
        this.add("hundred_year_snow_ginseng_from_igloo", new AddItemModifier(new LootItemCondition[] {
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/igloo_chest")).build(),
                LootItemRandomChanceCondition.randomChance(0.18F).build()
        }, ModItems.HUNDRED_YEAR_SNOW_GINSENG.get()));
        this.add("hundred_year_snow_ginseng_from_snowy_village", new AddItemModifier(new LootItemCondition[] {
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/village/village_snowy_house")).build(),
                LootItemRandomChanceCondition.randomChance(0.19F).build()
        }, ModItems.HUNDRED_YEAR_SNOW_GINSENG.get()));
        this.add("hundred_year_fire_ginseng_from_desert_temple", new AddItemModifier(new LootItemCondition[] {
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/desert_pyramid")).build(),
                LootItemRandomChanceCondition.randomChance(0.14F).build()
        }, ModItems.HUNDRED_YEAR_FIRE_GINSENG.get()));
        this.add("hundred_year_fire_ginseng_from_bastion", new AddItemModifier(new LootItemCondition[] {
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/bastion_treasure")).build(),
                LootItemRandomChanceCondition.randomChance(0.19F).build()
        }, ModItems.HUNDRED_YEAR_FIRE_GINSENG.get()));
        this.add("white_jade_orchid_from_mineshaft", new AddItemModifier(new LootItemCondition[] {
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                LootItemRandomChanceCondition.randomChance(0.20F).build()
        }, ModItems.WHITE_JADE_ORCHID.get()));
        this.add("white_jade_orchid_from_stronghold", new AddItemModifier(new LootItemCondition[] {
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")).build(),
                LootItemRandomChanceCondition.randomChance(0.18F).build()
        }, ModItems.WHITE_JADE_ORCHID.get()));

        this.add("jade_bamboo_of_serenity_from_bamboo", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("blocks/bamboo")).build(),
                LootItemRandomChanceCondition.randomChance(0.12F).build()}, ModItems.JADE_BAMBOO_OF_SERENITY.get()));

        //Living Core Drop Regular Mobs
        this.add("living_core_from_strider", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/strider")).build(),
                LootItemRandomChanceCondition.randomChance(0.15F).build()}, ModItems.LIVING_CORE.get()));
        this.add("living_core_from_piglin", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/piglin")).build(),
                LootItemRandomChanceCondition.randomChance(0.15F).build()}, ModItems.LIVING_CORE.get()));
        this.add("living_core_from_hoglin", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/hoglin")).build(),
                LootItemRandomChanceCondition.randomChance(0.15F).build()}, ModItems.LIVING_CORE.get()));
        this.add("living_core_from_zoglin", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zoglin")).build(),
                LootItemRandomChanceCondition.randomChance(0.15F).build()}, ModItems.LIVING_CORE.get()));
        this.add("living_core_from_vex", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/vex")).build(),
                LootItemRandomChanceCondition.randomChance(0.15F).build()}, ModItems.LIVING_CORE.get()));
        this.add("living_core_from_allay", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/allay")).build(),
                LootItemRandomChanceCondition.randomChance(0.15F).build()}, ModItems.LIVING_CORE.get()));
        this.add("living_core_from_golem", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/iron_golem")).build(),
                LootItemRandomChanceCondition.randomChance(0.15F).build()}, ModItems.LIVING_CORE.get()));

        //Living Core Drop Boss Undead
        this.add("living_core_from_ender_dragon", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/ender_dragon")).build(),
                LootItemRandomChanceCondition.randomChance(0.29F).build()}, ModItems.LIVING_CORE.get()));

        //Undead Core Drop Regular Undead Mobs
        this.add("undead_core_from_zombie", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombie")).build(),
                LootItemRandomChanceCondition.randomChance(0.15F).build()}, ModItems.UNDEAD_CORE.get()));
        this.add("undead_core_from_skeleton", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/skeleton")).build(),
                LootItemRandomChanceCondition.randomChance(0.15F).build()}, ModItems.UNDEAD_CORE.get()));
        this.add("undead_core_from_wither_skeleton", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/wither_skeleton")).build(),
                LootItemRandomChanceCondition.randomChance(0.15F).build()}, ModItems.UNDEAD_CORE.get()));
        this.add("undead_core_from_stray", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/stray")).build(),
                LootItemRandomChanceCondition.randomChance(0.15F).build()}, ModItems.UNDEAD_CORE.get()));
        this.add("undead_core_from_husk", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/husk")).build(),
                LootItemRandomChanceCondition.randomChance(0.15F).build()}, ModItems.UNDEAD_CORE.get()));
        this.add("undead_core_from_drowned", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/drowned")).build(),
                LootItemRandomChanceCondition.randomChance(0.15F).build()}, ModItems.UNDEAD_CORE.get()));
        this.add("undead_core_from_zombie_villager", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombie_villager")).build(),
                LootItemRandomChanceCondition.randomChance(0.15F).build()}, ModItems.UNDEAD_CORE.get()));
        this.add("undead_core_from_zombified_piglin", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombified_piglin")).build(),
                LootItemRandomChanceCondition.randomChance(0.15F).build()}, ModItems.UNDEAD_CORE.get()));
        this.add("undead_core_from_zombie_horse", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zombie_horse")).build(),
                LootItemRandomChanceCondition.randomChance(0.15F).build()}, ModItems.UNDEAD_CORE.get()));
        this.add("undead_core_from_phantom", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/phantom")).build(),
                LootItemRandomChanceCondition.randomChance(0.15F).build()}, ModItems.UNDEAD_CORE.get()));
        this.add("undead_core_from_zoglin", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/zoglin")).build(),
                LootItemRandomChanceCondition.randomChance(0.15F).build()}, ModItems.UNDEAD_CORE.get()));
        //Undead Core Drop Boss Undead
        this.add("undead_core_from_wither", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/wither")).build(),
                LootItemRandomChanceCondition.randomChance(0.29F).build()}, ModItems.UNDEAD_CORE.get()));

        // Elemental Cores
        this.add("fire_core_from_blaze", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/blaze")).build(),
                LootItemKilledByPlayerCondition.killedByPlayer().build(),
                LootItemRandomChanceCondition.randomChance(0.12F).build()}, ModItems.FIRE_CORE.get()));
        this.add("water_core_from_drowned", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/drowned")).build(),
                LootItemKilledByPlayerCondition.killedByPlayer().build(),
                LootItemRandomChanceCondition.randomChance(0.12F).build()}, ModItems.WATER_CORE.get()));
        this.add("wood_core_from_creeper", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/creeper")).build(),
                LootItemKilledByPlayerCondition.killedByPlayer().build(),
                LootItemRandomChanceCondition.randomChance(0.12F).build()}, ModItems.WOOD_CORE.get()));
        this.add("earth_core_from_husk", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/husk")).build(),
                LootItemKilledByPlayerCondition.killedByPlayer().build(),
                LootItemRandomChanceCondition.randomChance(0.12F).build()}, ModItems.EARTH_CORE.get()));
        this.add("metal_core_from_skeleton", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/skeleton")).build(),
                LootItemKilledByPlayerCondition.killedByPlayer().build(),
                LootItemRandomChanceCondition.randomChance(0.12F).build()}, ModItems.METAL_CORE.get()));
        this.add("lightning_core_from_witch", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/witch")).build(),
                LootItemKilledByPlayerCondition.killedByPlayer().build(),
                LootItemRandomChanceCondition.randomChance(0.12F).build()}, ModItems.LIGHTNING_CORE.get()));
        this.add("wind_core_from_phantom", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/phantom")).build(),
                LootItemKilledByPlayerCondition.killedByPlayer().build(),
                LootItemRandomChanceCondition.randomChance(0.12F).build()}, ModItems.WIND_CORE.get()));

        // Example implementation of rank conditional drops (P.S. this can also be used in ModEntityLootTables (different pattern))

        /*
                Example of at least (if golden core or greater, drops a hundred-year ginseng when killed by a player)
        this.add("hundred_year_ginseng_from_ranked_zombie", new AddItemModifier(new LootItemCondition[] {
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/zombie")).build(),
                LootItemKilledByPlayerCondition.killedByPlayer().build(),
                LootItemRandomChanceCondition.randomChance(0.10F).build(),
                MobRankLootCondition.atLeast("golden_core", 1).build()
        }, ModItems.HUNDRED_YEAR_GINSENG.get()));

                Example of exact (if exactly nascent soul, drops a white jade orchid when killed by a player)
        this.add("white_jade_orchid_from_ranked_zombie", new AddItemModifier(new LootItemCondition[] {
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/zombie")).build(),
                LootItemKilledByPlayerCondition.killedByPlayer().build(),
                LootItemRandomChanceCondition.randomChance(0.25F).build(),
                MobRankLootCondition.exact("nascent_soul", 2).build()
        }, ModItems.WHITE_JADE_ORCHID.get()));

                Example of between (if between soul formation and tribulation transcendence, drops a hundred-year fire ginseng when killed by a player)
        this.add("hundred_year_fire_ginseng_from_ranked_zombie", new AddItemModifier(new LootItemCondition[] {
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/zombie")).build(),
                LootItemKilledByPlayerCondition.killedByPlayer().build(),
                LootItemRandomChanceCondition.randomChance(0.18F).build(),
                MobRankLootCondition.between("soul_formation", 1, "tribulation_transcendence", 3).build()
        }, ModItems.HUNDRED_YEAR_FIRE_GINSENG.get()));
        */

    }
}