package net.thejadeproject.ascension.refactor_packages.techniques;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.stats.custom.ModStats;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.body.BodyElementTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.body.CombinedBodyElementTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.body.FiveElementBodyTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.essence.FiveElementCultivationTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.GenericTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.soul.PaleMoonTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.soul.ScholarlySoulTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.body.WhiteLightningTenStageTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.essence.*;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.stat_change_handlers.BasicStatChangeHandler;
import net.thejadeproject.ascension.refactor_packages.util.value_modifiers.ModifierOperation;
import net.thejadeproject.ascension.refactor_packages.util.value_modifiers.ValueContainerModifier;

import java.util.Set;

public class ModTechniques {
    public static final DeferredRegister<ITechnique> TECHNIQUES = DeferredRegister.create(AscensionRegistries.Techniques.TECHNIQUES_REGISTRY, AscensionCraft.MOD_ID);

    // --- Resource location keys for modifiers ---
    public static final ResourceLocation test = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "test");
    public static final ResourceLocation FIRE_BODY_KEY   = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "fire_body_tech");
    public static final ResourceLocation WATER_BODY_KEY  = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "water_body_tech");
    public static final ResourceLocation WOOD_BODY_KEY   = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "wood_body_tech");
    public static final ResourceLocation EARTH_BODY_KEY  = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "earth_body_tech");
    public static final ResourceLocation METAL_BODY_KEY  = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "metal_body_tech");
    public static final ResourceLocation TIER2_BODY_KEY  = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "tier2_body_tech");
    public static final ResourceLocation TIER3_BODY_KEY  = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "tier3_body_tech");
    public static final ResourceLocation TIER4_BODY_KEY  = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "tier4_body_tech");
    public static final ResourceLocation TIER5_BODY_KEY  = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "tier5_body_tech");

    // --- Placeholder handler ---
    public static BasicStatChangeHandler testHandler = new BasicStatChangeHandler()
            .addMinorRealmStatModifier(ModStats.VITALITY.getId(),new ValueContainerModifier(2, ModifierOperation.ADD_BASE, test))
            .addMajorRealmStatModifier(ModStats.VITALITY.getId(),new ValueContainerModifier(0.2,ModifierOperation.MULTIPLY_FINAL,test))
            .addMinorRealmStatModifier(ModStats.AGILITY.getId(),new ValueContainerModifier(5,ModifierOperation.ADD_BASE,test));


    // --- Single-element body technique handlers ---
    // Fire: Vitality + Strength focus, max health
    public static BasicStatChangeHandler fireBodyHandler = new BasicStatChangeHandler()
            .addMinorRealmStatModifier(ModStats.VITALITY.getId(), new ValueContainerModifier(3, ModifierOperation.ADD_BASE, FIRE_BODY_KEY))
            .addMinorRealmStatModifier(ModStats.STRENGTH.getId(), new ValueContainerModifier(2, ModifierOperation.ADD_BASE, FIRE_BODY_KEY))
            .addMinorRealmAttributeModifier(Attributes.MAX_HEALTH, new ValueContainerModifier(4, ModifierOperation.ADD_BASE, FIRE_BODY_KEY))
            .addMajorRealmStatModifier(ModStats.VITALITY.getId(), new ValueContainerModifier(0.15, ModifierOperation.MULTIPLY_FINAL, FIRE_BODY_KEY));

    // Water: Vitality focus, max health, no strength
    public static BasicStatChangeHandler waterBodyHandler = new BasicStatChangeHandler()
            .addMinorRealmStatModifier(ModStats.VITALITY.getId(), new ValueContainerModifier(5, ModifierOperation.ADD_BASE, WATER_BODY_KEY))
            .addMinorRealmAttributeModifier(Attributes.MAX_HEALTH, new ValueContainerModifier(4, ModifierOperation.ADD_BASE, WATER_BODY_KEY))
            .addMajorRealmStatModifier(ModStats.VITALITY.getId(), new ValueContainerModifier(0.2, ModifierOperation.MULTIPLY_FINAL, WATER_BODY_KEY));

    // Wood: Agility focus, some vitality
    public static BasicStatChangeHandler woodBodyHandler = new BasicStatChangeHandler()
            .addMinorRealmStatModifier(ModStats.AGILITY.getId(), new ValueContainerModifier(4, ModifierOperation.ADD_BASE, WOOD_BODY_KEY))
            .addMinorRealmStatModifier(ModStats.VITALITY.getId(), new ValueContainerModifier(2, ModifierOperation.ADD_BASE, WOOD_BODY_KEY))
            .addMajorRealmStatModifier(ModStats.AGILITY.getId(), new ValueContainerModifier(0.15, ModifierOperation.MULTIPLY_FINAL, WOOD_BODY_KEY));

    // Earth: Vitality + Strength, highest max health
    public static BasicStatChangeHandler earthBodyHandler = new BasicStatChangeHandler()
            .addMinorRealmStatModifier(ModStats.VITALITY.getId(), new ValueContainerModifier(4, ModifierOperation.ADD_BASE, EARTH_BODY_KEY))
            .addMinorRealmStatModifier(ModStats.STRENGTH.getId(), new ValueContainerModifier(3, ModifierOperation.ADD_BASE, EARTH_BODY_KEY))
            .addMinorRealmAttributeModifier(Attributes.MAX_HEALTH, new ValueContainerModifier(4, ModifierOperation.ADD_BASE, EARTH_BODY_KEY))
            .addMajorRealmStatModifier(ModStats.VITALITY.getId(), new ValueContainerModifier(0.2, ModifierOperation.MULTIPLY_FINAL, EARTH_BODY_KEY));

    // Metal: Strength focus, some vitality
    public static BasicStatChangeHandler metalBodyHandler = new BasicStatChangeHandler()
            .addMinorRealmStatModifier(ModStats.STRENGTH.getId(), new ValueContainerModifier(4, ModifierOperation.ADD_BASE, METAL_BODY_KEY))
            .addMinorRealmStatModifier(ModStats.VITALITY.getId(), new ValueContainerModifier(2, ModifierOperation.ADD_BASE, METAL_BODY_KEY))
            .addMajorRealmStatModifier(ModStats.STRENGTH.getId(), new ValueContainerModifier(0.15, ModifierOperation.MULTIPLY_FINAL, METAL_BODY_KEY));

    // --- Combined body technique handlers (tier-based) ---
    // Tier 2 (2-element): noticeably stronger than single
    public static BasicStatChangeHandler tier2BodyHandler = new BasicStatChangeHandler()
            .addMinorRealmStatModifier(ModStats.VITALITY.getId(), new ValueContainerModifier(6, ModifierOperation.ADD_BASE, TIER2_BODY_KEY))
            .addMinorRealmStatModifier(ModStats.STRENGTH.getId(), new ValueContainerModifier(4, ModifierOperation.ADD_BASE, TIER2_BODY_KEY))
            .addMinorRealmAttributeModifier(Attributes.MAX_HEALTH, new ValueContainerModifier(6, ModifierOperation.ADD_BASE, TIER2_BODY_KEY))
            .addMajorRealmStatModifier(ModStats.VITALITY.getId(), new ValueContainerModifier(0.2, ModifierOperation.MULTIPLY_FINAL, TIER2_BODY_KEY));

    // Tier 3 (3-element)
    public static BasicStatChangeHandler tier3BodyHandler = new BasicStatChangeHandler()
            .addMinorRealmStatModifier(ModStats.VITALITY.getId(), new ValueContainerModifier(10, ModifierOperation.ADD_BASE, TIER3_BODY_KEY))
            .addMinorRealmStatModifier(ModStats.STRENGTH.getId(), new ValueContainerModifier(6, ModifierOperation.ADD_BASE, TIER3_BODY_KEY))
            .addMinorRealmAttributeModifier(Attributes.MAX_HEALTH, new ValueContainerModifier(8, ModifierOperation.ADD_BASE, TIER3_BODY_KEY))
            .addMajorRealmStatModifier(ModStats.VITALITY.getId(), new ValueContainerModifier(0.25, ModifierOperation.MULTIPLY_FINAL, TIER3_BODY_KEY));

    // Tier 4 (4-element)
    public static BasicStatChangeHandler tier4BodyHandler = new BasicStatChangeHandler()
            .addMinorRealmStatModifier(ModStats.VITALITY.getId(), new ValueContainerModifier(14, ModifierOperation.ADD_BASE, TIER4_BODY_KEY))
            .addMinorRealmStatModifier(ModStats.STRENGTH.getId(), new ValueContainerModifier(8, ModifierOperation.ADD_BASE, TIER4_BODY_KEY))
            .addMinorRealmStatModifier(ModStats.AGILITY.getId(), new ValueContainerModifier(5, ModifierOperation.ADD_BASE, TIER4_BODY_KEY))
            .addMinorRealmAttributeModifier(Attributes.MAX_HEALTH, new ValueContainerModifier(10, ModifierOperation.ADD_BASE, TIER4_BODY_KEY))
            .addMajorRealmStatModifier(ModStats.VITALITY.getId(), new ValueContainerModifier(0.3, ModifierOperation.MULTIPLY_FINAL, TIER4_BODY_KEY));

    // Tier 5 (5-element / Five Harmony)
    public static BasicStatChangeHandler tier5BodyHandler = new BasicStatChangeHandler()
            .addMinorRealmStatModifier(ModStats.VITALITY.getId(), new ValueContainerModifier(20, ModifierOperation.ADD_BASE, TIER5_BODY_KEY))
            .addMinorRealmStatModifier(ModStats.STRENGTH.getId(), new ValueContainerModifier(12, ModifierOperation.ADD_BASE, TIER5_BODY_KEY))
            .addMinorRealmStatModifier(ModStats.AGILITY.getId(), new ValueContainerModifier(8, ModifierOperation.ADD_BASE, TIER5_BODY_KEY))
            .addMinorRealmAttributeModifier(Attributes.MAX_HEALTH, new ValueContainerModifier(14, ModifierOperation.ADD_BASE, TIER5_BODY_KEY))
            .addMajorRealmStatModifier(ModStats.VITALITY.getId(), new ValueContainerModifier(0.4, ModifierOperation.MULTIPLY_FINAL, TIER5_BODY_KEY))
            .addMajorRealmStatModifier(ModStats.STRENGTH.getId(), new ValueContainerModifier(0.25, ModifierOperation.MULTIPLY_FINAL, TIER5_BODY_KEY));


    // ──── ESSENCE TECHNIQUES ────────────────────────────────────────────
    public static final DeferredHolder<ITechnique, ? extends GenericTechnique> BASIC_CULTIVATION_TECHNIQUE = TECHNIQUES.register("basic_cultivation_technique", () ->
            new GenericTechnique(ModPaths.ESSENCE.getId(), Component.translatable("ascension.technique.basic_cultivation_technique"), 2.0, Set.of())
                    .setStatChangeHandler(testHandler));
    public static final DeferredHolder<ITechnique, ? extends GenericTechnique> ADVANCED_CULTIVATION_TECHNIQUE = TECHNIQUES.register("advanced_cultivation_technique",()->
            new GenericTechnique(ModPaths.ESSENCE.getId(),Component.translatable("ascension.technique.advanced_cultivation_technique"),10.0,Set.of())
                    .setStatChangeHandler(testHandler));



    // ──── ESSENCE-ELEMENTAL HYBRID TECHNIQUES ────────────────────────────────────────────
    public static final DeferredHolder<ITechnique, ? extends FireEssenceTechnique> FIRE_ESSENCE_TECHNIQUE =
            TECHNIQUES.register("fire_essence_technique",
                    () -> new FireEssenceTechnique(testHandler));
    public static final DeferredHolder<ITechnique, ? extends WaterEssenceTechnique> WATER_ESSENCE_TECHNIQUE =
            TECHNIQUES.register("water_essence_technique",
                    () -> new WaterEssenceTechnique(testHandler));
    public static final DeferredHolder<ITechnique, ? extends WoodEssenceTechnique> WOOD_ESSENCE_TECHNIQUE =
            TECHNIQUES.register("wood_essence_technique",
                    () -> new WoodEssenceTechnique(testHandler));
    public static final DeferredHolder<ITechnique, ? extends EarthEssenceTechnique> EARTH_ESSENCE_TECHNIQUE =
            TECHNIQUES.register("earth_essence_technique",
                    () -> new EarthEssenceTechnique(testHandler));
    public static final DeferredHolder<ITechnique, ? extends MetalEssenceTechnique> METAL_ESSENCE_TECHNIQUE =
            TECHNIQUES.register("metal_essence_technique",
                    () -> new MetalEssenceTechnique(testHandler));
    public static final DeferredHolder<ITechnique, ? extends LightningEssenceTechnique> LIGHTNING_ESSENCE_TECHNIQUE =
            TECHNIQUES.register("lightning_essence_technique",
                    () -> new LightningEssenceTechnique(testHandler));
    public static final DeferredHolder<ITechnique, ? extends WindEssenceTechnique> WIND_ESSENCE_TECHNIQUE =
            TECHNIQUES.register("wind_essence_technique",
                    () -> new WindEssenceTechnique(testHandler));
    public static final DeferredHolder<ITechnique, ? extends FiveElementCultivationTechnique> FIVE_ELEMENT_CIRCULATION_METHOD =
            TECHNIQUES.register("five_element_cultivation_technique",
                    () -> new FiveElementCultivationTechnique(testHandler));



    // ──── BODY TECHNIQUES ────────────────────────────────────────────
    public static final DeferredHolder<ITechnique, ? extends WhiteLightningTenStageTechnique> WHITE_LIGHTNING_TEN_STAGE_TECHNIQUE =
            TECHNIQUES.register("white_lightning_ten_stage_technique",
                    () -> new WhiteLightningTenStageTechnique(testHandler));



    // ──── BODY-ELEMENTAL HYBRID TECHNIQUES ────────────────────────────────────────────
    // T1
    public static final DeferredHolder<ITechnique, ? extends BodyElementTechnique> HEART_FIRE_TECHNIQUE =
            TECHNIQUES.register("heart_fire_technique", () ->
                    new BodyElementTechnique(ModPaths.FIRE.getId(), Component.translatable("ascension.technique.heart_fire_technique"), 3.0, fireBodyHandler));
    public static final DeferredHolder<ITechnique, ? extends BodyElementTechnique> KIDNEY_WATER_TECHNIQUE =
            TECHNIQUES.register("kidney_water_technique", () ->
                    new BodyElementTechnique(ModPaths.WATER.getId(), Component.translatable("ascension.technique.kidney_water_technique"), 3.0, waterBodyHandler));
    public static final DeferredHolder<ITechnique, ? extends BodyElementTechnique> LIVER_WOOD_TECHNIQUE =
            TECHNIQUES.register("liver_wood_technique", () ->
                    new BodyElementTechnique(ModPaths.WOOD.getId(), Component.translatable("ascension.technique.liver_wood_technique"), 3.0, woodBodyHandler));
    public static final DeferredHolder<ITechnique, ? extends BodyElementTechnique> SPLEEN_EARTH_TECHNIQUE =
            TECHNIQUES.register("spleen_earth_technique", () ->
                    new BodyElementTechnique(ModPaths.EARTH.getId(), Component.translatable("ascension.technique.spleen_earth_technique"), 3.0, earthBodyHandler));
    public static final DeferredHolder<ITechnique, ? extends BodyElementTechnique> LUNG_METAL_TECHNIQUE =
            TECHNIQUES.register("lung_metal_technique", () ->
                    new BodyElementTechnique(ModPaths.METAL.getId(), Component.translatable("ascension.technique.lung_metal_technique"), 3.0, metalBodyHandler));
    // T2
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> WOOD_FIRE_BODY_TECHNIQUE =
            TECHNIQUES.register("wood_fire_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.wood_fire_body_technique"),
                            6.0, Set.of(ModPaths.WOOD.getId(), ModPaths.FIRE.getId()), tier2BodyHandler));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> FIRE_EARTH_BODY_TECHNIQUE =
            TECHNIQUES.register("fire_earth_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.fire_earth_body_technique"),
                            6.0, Set.of(ModPaths.FIRE.getId(), ModPaths.EARTH.getId()), tier2BodyHandler));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> EARTH_METAL_BODY_TECHNIQUE =
            TECHNIQUES.register("earth_metal_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.earth_metal_body_technique"),
                            6.0, Set.of(ModPaths.EARTH.getId(), ModPaths.METAL.getId()), tier2BodyHandler));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> METAL_WATER_BODY_TECHNIQUE =
            TECHNIQUES.register("metal_water_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.metal_water_body_technique"),
                            6.0, Set.of(ModPaths.METAL.getId(), ModPaths.WATER.getId()), tier2BodyHandler));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> WATER_WOOD_BODY_TECHNIQUE =
            TECHNIQUES.register("water_wood_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.water_wood_body_technique"),
                            6.0, Set.of(ModPaths.WATER.getId(), ModPaths.WOOD.getId()), tier2BodyHandler));
    // T3
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> WOOD_FIRE_EARTH_BODY_TECHNIQUE =
            TECHNIQUES.register("wood_fire_earth_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.wood_fire_earth_body_technique"),
                            9.0, Set.of(ModPaths.WOOD.getId(), ModPaths.FIRE.getId(), ModPaths.EARTH.getId()), tier3BodyHandler));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> FIRE_EARTH_METAL_BODY_TECHNIQUE =
            TECHNIQUES.register("fire_earth_metal_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.fire_earth_metal_body_technique"),
                            9.0, Set.of(ModPaths.FIRE.getId(), ModPaths.EARTH.getId(), ModPaths.METAL.getId()), tier3BodyHandler));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> EARTH_METAL_WATER_BODY_TECHNIQUE =
            TECHNIQUES.register("earth_metal_water_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.earth_metal_water_body_technique"),
                            9.0, Set.of(ModPaths.EARTH.getId(), ModPaths.METAL.getId(), ModPaths.WATER.getId()), tier3BodyHandler));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> METAL_WATER_WOOD_BODY_TECHNIQUE =
            TECHNIQUES.register("metal_water_wood_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.metal_water_wood_body_technique"),
                            9.0, Set.of(ModPaths.METAL.getId(), ModPaths.WATER.getId(), ModPaths.WOOD.getId()), tier3BodyHandler));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> WATER_WOOD_FIRE_BODY_TECHNIQUE =
            TECHNIQUES.register("water_wood_fire_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.water_wood_fire_body_technique"),
                            9.0, Set.of(ModPaths.WATER.getId(), ModPaths.WOOD.getId(), ModPaths.FIRE.getId()), tier3BodyHandler));
    // T4
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> WOOD_FIRE_EARTH_METAL_BODY_TECHNIQUE =
            TECHNIQUES.register("wood_fire_earth_metal_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.wood_fire_earth_metal_body_technique"),
                            12.0, Set.of(ModPaths.WOOD.getId(), ModPaths.FIRE.getId(), ModPaths.EARTH.getId(), ModPaths.METAL.getId()), tier4BodyHandler));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> FIRE_EARTH_METAL_WATER_BODY_TECHNIQUE =
            TECHNIQUES.register("fire_earth_metal_water_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.fire_earth_metal_water_body_technique"),
                            12.0, Set.of(ModPaths.FIRE.getId(), ModPaths.EARTH.getId(), ModPaths.METAL.getId(), ModPaths.WATER.getId()), tier4BodyHandler));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> EARTH_METAL_WATER_WOOD_BODY_TECHNIQUE =
            TECHNIQUES.register("earth_metal_water_wood_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.earth_metal_water_wood_body_technique"),
                            12.0, Set.of(ModPaths.EARTH.getId(), ModPaths.METAL.getId(), ModPaths.WATER.getId(), ModPaths.WOOD.getId()), tier4BodyHandler));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> METAL_WATER_WOOD_FIRE_BODY_TECHNIQUE =
            TECHNIQUES.register("metal_water_wood_fire_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.metal_water_wood_fire_body_technique"),
                            12.0, Set.of(ModPaths.METAL.getId(), ModPaths.WATER.getId(), ModPaths.WOOD.getId(), ModPaths.FIRE.getId()), tier4BodyHandler));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> WATER_WOOD_FIRE_EARTH_BODY_TECHNIQUE =
            TECHNIQUES.register("water_wood_fire_earth_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.water_wood_fire_earth_body_technique"),
                            12.0, Set.of(ModPaths.WATER.getId(), ModPaths.WOOD.getId(), ModPaths.FIRE.getId(), ModPaths.EARTH.getId()), tier4BodyHandler));
    // T5
    public static final DeferredHolder<ITechnique, ? extends FiveElementBodyTechnique> FIVE_ELEMENT_BODY_TECHNIQUE =
            TECHNIQUES.register("five_element_body_technique", () ->
                    new FiveElementBodyTechnique(tier5BodyHandler));



    // ──── SOUL TECHNIQUES ────────────────────────────────────────────
    public static final DeferredHolder<ITechnique, ? extends ScholarlySoulTechnique> SCHOLARLY_SOUL_TECHNIQUE =
            TECHNIQUES.register("scholarly_soul_technique",
                    () -> new ScholarlySoulTechnique(testHandler));
    public static final DeferredHolder<ITechnique, ? extends PaleMoonTechnique> PALE_MOON_SUTRA =
            TECHNIQUES.register("pale_moon_sutra", () -> new PaleMoonTechnique(testHandler));



    // ──── WEAPON TECHNIQUES ────────────────────────────────────────────
    public static final DeferredHolder<ITechnique, ? extends GenericTechnique> SWORD_COMPREHENSION_TECHNIQUE =
            TECHNIQUES.register("sword_comprehension_technique",
                    ()-> new GenericTechnique(ModPaths.SWORD.getId(),
                            Component.translatable("ascension.technique.sword_comprehension_technique"),
                            10.0,Set.of())
            );



    /* ──── TECHNIQUE IDEAS | Fill in as you please ──────────────────────────────────────────── //

        public static final DeferredHolder<ITechnique, ? extends IndestructibleVajraTechnique> INDESTRUCTIBLE_VAJRA_SCRIPTURE
        public static final DeferredHolder<ITechnique, ? extends LotusHeartTechnique> LOTUS_HEART_SUTRA
        public static final DeferredHolder<ITechnique, ? extends TripleSoulTechnique> TRIPLE_SOUL_LIFE_SUTRA

        public static final DeferredHolder<ITechnique, ? extends AbyssDwellerTechnique> ABYSS_DWELLERS_MANUAL
        public static final DeferredHolder<ITechnique, ? extends HellboundMarrowTechnique> HELLBOUND_MARROW_SCRIPTURE
        public static final DeferredHolder<ITechnique, ? extends NetherQiTechnique> NETHER_QI_DEVOURING_ART

        public static final DeferredHolder<ITechnique, ? extends MirageArrowTechnique> MIRAGE_ARROW_MANUAL
        public static final DeferredHolder<ITechnique, ? extends GreatWallTechnique> BASTION_WALL_TECHNIQUE
        public static final DeferredHolder<ITechnique, ? extends MortalNineSaberTechnique> NINE_BLADES_SABER
        public static final DeferredHolder<ITechnique, ? extends EdgeTemperingTechnique> EDGE_TEMPERING_METHOD
        public static final DeferredHolder<ITechnique, ? extends FallingLeafBladeTechnique> FALLING_LEAF_BLADE
        public static final DeferredHolder<ITechnique, ? extends >
        public static final DeferredHolder<ITechnique, ? extends >
        public static final DeferredHolder<ITechnique, ? extends >
        public static final DeferredHolder<ITechnique, ? extends >

        public static final DeferredHolder<ITechnique, ? extends SoulThreadTechnique> SOUL_THREAD_TECHNIQUE
        public static final DeferredHolder<ITechnique, ? extends MirrorSoulTechnique> MIRROR_SOUL_TECHNIQUE

        public static final DeferredHolder<ITechnique, ? extends NineEchoSoulTechnique> NINE_ECHOS_SOUL_ART
        public static final DeferredHolder<ITechnique, ? extends JadeSpiritTechnique> JADE_SPIRIT_SCRIPTURE
        public static final DeferredHolder<ITechnique, ? extends GhostLanternTechnique> GHOST_LANTERN_METHOD
        public static final DeferredHolder<ITechnique, ? extends SoulSeveringTechnique> SOUL_SEVERING_TECHNIQUE
        public static final DeferredHolder<ITechnique, ? extends >
        public static final DeferredHolder<ITechnique, ? extends >
        public static final DeferredHolder<ITechnique, ? extends >

        P.S. I don't really mean we should make this many individual classes to extend, maybe just a couple general ones?
        Unless someone wants to or the technique actually requires it :)
        I'm just jotting down ideas - sortofsmart

    // ──── TECHNIQUE IDEAS | Fill in as you please ──────────────────────────────────────────── */


    public static void register(IEventBus modEventBus){
        TECHNIQUES.register(modEventBus);
    }
}
