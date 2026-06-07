package net.thejadeproject.ascension.refactor_packages.techniques;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.stats.custom.ModStats;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.body.*;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.GenericTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.essence.*;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.poison.MyriadVenomRefinementTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.soul.*;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.stat_change_handlers.BasicStatChangeHandler;
import net.thejadeproject.ascension.refactor_packages.skills.custom.ModSkills;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.weapon.BasicWeaponTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.weapon.SwordCultivationTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.weapon.FistCultivationTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.helpers.TechniqueManualRegistry;
import net.thejadeproject.ascension.refactor_packages.handlers.realm_change.RealmChangeHandlers;
import net.thejadeproject.ascension.refactor_packages.util.value_modifiers.ModifierOperation;
import net.thejadeproject.ascension.refactor_packages.util.value_modifiers.ValueContainerModifier;

import java.util.List;
import java.util.Set;

public class ModTechniques {
    public static final DeferredRegister<ITechnique> TECHNIQUES = DeferredRegister.create(AscensionRegistries.Techniques.TECHNIQUES_REGISTRY, AscensionCraft.MOD_ID);

    // --- Resource location keys for modifiers ---
    public static final ResourceLocation test = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "test");
    public static final ResourceLocation BLOODFEAST_KEY = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "bloodfeast_soul_refining");
    public static final ResourceLocation WHITE_LIGHTNING_KEY = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "white_lightning_ten_stage");
    public static final ResourceLocation BASE_BODY_KEY    = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "base_body");
    public static final ResourceLocation BASE_ESSENCE_KEY = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "base_essence");
    public static final ResourceLocation BASE_SOUL_KEY    = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "base_soul");
    public static final ResourceLocation BASE_WEAPON_KEY = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "base_weapon");
    public static final ResourceLocation MYRIAD_VENOM_KEY = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "myriad_venom_refinement");

    // --- Base path handlers ---
    // Body: main VIT + STR, secondary AGI, tertiary INT
    public static BasicStatChangeHandler baseBodyHandler = new BasicStatChangeHandler()
            .addMinorRealmStatModifier(ModStats.VITALITY.getId(),     new ValueContainerModifier(3,    ModifierOperation.ADD_BASE,       BASE_BODY_KEY))
            .addMinorRealmStatModifier(ModStats.STRENGTH.getId(),     new ValueContainerModifier(3,    ModifierOperation.ADD_BASE,       BASE_BODY_KEY))
            .addMinorRealmStatModifier(ModStats.AGILITY.getId(),      new ValueContainerModifier(2,    ModifierOperation.ADD_BASE,       BASE_BODY_KEY))
            .addMinorRealmStatModifier(ModStats.INTELLIGENCE.getId(), new ValueContainerModifier(1,    ModifierOperation.ADD_BASE,       BASE_BODY_KEY))
            .addMajorRealmStatModifier(ModStats.VITALITY.getId(),     new ValueContainerModifier(0.15, ModifierOperation.MULTIPLY_FINAL, BASE_BODY_KEY))
            .addMajorRealmStatModifier(ModStats.STRENGTH.getId(),     new ValueContainerModifier(0.15, ModifierOperation.MULTIPLY_FINAL, BASE_BODY_KEY))
            .addMajorRealmStatModifier(ModStats.AGILITY.getId(),      new ValueContainerModifier(0.15, ModifierOperation.MULTIPLY_FINAL, BASE_BODY_KEY));

    // Essence: main INT, secondary VIT + STR, tertiary AGI
    public static BasicStatChangeHandler baseEssenceHandler = new BasicStatChangeHandler()
            .addMinorRealmStatModifier(ModStats.INTELLIGENCE.getId(), new ValueContainerModifier(3,    ModifierOperation.ADD_BASE,       BASE_ESSENCE_KEY))
            .addMinorRealmStatModifier(ModStats.VITALITY.getId(),     new ValueContainerModifier(2,    ModifierOperation.ADD_BASE,       BASE_ESSENCE_KEY))
            .addMinorRealmStatModifier(ModStats.STRENGTH.getId(),     new ValueContainerModifier(2,    ModifierOperation.ADD_BASE,       BASE_ESSENCE_KEY))
            .addMinorRealmStatModifier(ModStats.AGILITY.getId(),      new ValueContainerModifier(1,    ModifierOperation.ADD_BASE,       BASE_ESSENCE_KEY))
            .addMajorRealmStatModifier(ModStats.INTELLIGENCE.getId(), new ValueContainerModifier(0.15, ModifierOperation.MULTIPLY_FINAL, BASE_ESSENCE_KEY))
            .addMajorRealmStatModifier(ModStats.AGILITY.getId(),      new ValueContainerModifier(0.15, ModifierOperation.MULTIPLY_FINAL, BASE_ESSENCE_KEY));

    // Soul: main INT, secondary AGI, tertiary VIT + STR
    public static BasicStatChangeHandler baseSoulHandler = new BasicStatChangeHandler()
            .addMinorRealmStatModifier(ModStats.INTELLIGENCE.getId(), new ValueContainerModifier(3,    ModifierOperation.ADD_BASE,       BASE_SOUL_KEY))
            .addMinorRealmStatModifier(ModStats.AGILITY.getId(),      new ValueContainerModifier(2,    ModifierOperation.ADD_BASE,       BASE_SOUL_KEY))
            .addMinorRealmStatModifier(ModStats.VITALITY.getId(),     new ValueContainerModifier(1,    ModifierOperation.ADD_BASE,       BASE_SOUL_KEY))
            .addMinorRealmStatModifier(ModStats.STRENGTH.getId(),     new ValueContainerModifier(1,    ModifierOperation.ADD_BASE,       BASE_SOUL_KEY))
            .addMajorRealmStatModifier(ModStats.INTELLIGENCE.getId(), new ValueContainerModifier(0.15, ModifierOperation.MULTIPLY_FINAL, BASE_SOUL_KEY))
            .addMajorRealmStatModifier(ModStats.AGILITY.getId(),      new ValueContainerModifier(0.25, ModifierOperation.MULTIPLY_FINAL, BASE_SOUL_KEY));

    // Temp Weapon Handler until Flip makes something proper
    public static BasicStatChangeHandler baseWeaponHandler = new BasicStatChangeHandler()
            .addMinorRealmStatModifier(ModStats.AGILITY.getId(), new ValueContainerModifier(2, ModifierOperation.ADD_BASE, BASE_WEAPON_KEY))
            .addMinorRealmStatModifier(ModStats.STRENGTH.getId(), new ValueContainerModifier(2, ModifierOperation.ADD_BASE, BASE_WEAPON_KEY))
            .addMinorRealmStatModifier(ModStats.INTELLIGENCE.getId(), new ValueContainerModifier(1, ModifierOperation.ADD_BASE, BASE_WEAPON_KEY))
            .addMinorRealmStatModifier(ModStats.VITALITY.getId(), new ValueContainerModifier(0.5, ModifierOperation.ADD_BASE, BASE_WEAPON_KEY))
            .addMajorRealmStatModifier(ModStats.AGILITY.getId(), new ValueContainerModifier(0.12, ModifierOperation.MULTIPLY_FINAL, BASE_WEAPON_KEY))
            .addMajorRealmStatModifier(ModStats.STRENGTH.getId(), new ValueContainerModifier(0.12, ModifierOperation.MULTIPLY_FINAL, BASE_WEAPON_KEY));

    // Temp Poison Handler until Flip makes something proper
    public static BasicStatChangeHandler basePoisonHandler = new BasicStatChangeHandler()
            .addMinorRealmStatModifier(ModStats.VITALITY.getId(),     new ValueContainerModifier(3, ModifierOperation.ADD_BASE,       MYRIAD_VENOM_KEY))
            .addMinorRealmStatModifier(ModStats.INTELLIGENCE.getId(), new ValueContainerModifier(3, ModifierOperation.ADD_BASE,       MYRIAD_VENOM_KEY))
            .addMinorRealmStatModifier(ModStats.AGILITY.getId(),      new ValueContainerModifier(1, ModifierOperation.ADD_BASE,       MYRIAD_VENOM_KEY))
            .addMajorRealmStatModifier(ModStats.VITALITY.getId(),     new ValueContainerModifier(0.15, ModifierOperation.MULTIPLY_FINAL, MYRIAD_VENOM_KEY))
            .addMajorRealmStatModifier(ModStats.INTELLIGENCE.getId(), new ValueContainerModifier(0.15, ModifierOperation.MULTIPLY_FINAL, MYRIAD_VENOM_KEY));



    // ──── ESSENCE TECHNIQUES ────────────────────────────────────────────
    public static final DeferredHolder<ITechnique, ? extends GenericTechnique> BASIC_CULTIVATION_TECHNIQUE = TECHNIQUES.register("basic_cultivation_technique", () ->
            new GenericTechnique(ModPaths.ESSENCE.getId(), Component.translatable("ascension.technique.basic_cultivation_technique"), 2.0, Set.of())
                    .setStatChangeHandler(baseEssenceHandler));
    public static final DeferredHolder<ITechnique, ? extends GenericTechnique> ADVANCED_CULTIVATION_TECHNIQUE = TECHNIQUES.register("advanced_cultivation_technique",()->
            new GenericTechnique(ModPaths.ESSENCE.getId(),Component.translatable("ascension.technique.advanced_cultivation_technique"),10.0,Set.of())
                    .setStatChangeHandler(baseEssenceHandler));
    public static final DeferredHolder<ITechnique, ? extends OpenSkyBreathingTechnique> OPEN_SKY_BREATHING_SCRIPTURE =
            TECHNIQUES.register("open_sky_breathing_scripture",
                    () -> new OpenSkyBreathingTechnique(baseEssenceHandler));

    public static final DeferredHolder<ITechnique, ? extends GenericTechnique> MORTAL_ESSENCE_SCRIPTURE = TECHNIQUES.register("mortal_essence_scripture", () ->
            new GenericTechnique(ModPaths.ESSENCE.getId(), Component.translatable("ascension.technique.mortal_essence_scripture"), 1.0, Set.of())
                    .setStatChangeHandler(baseEssenceHandler));
    public static final DeferredHolder<ITechnique, ? extends GenericTechnique> JADE_MERIDIANS_TECHNIQUE = TECHNIQUES.register("jade_meridians_technique",()->
            new GenericTechnique(ModPaths.ESSENCE.getId(),Component.translatable("ascension.technique.jade_meridians_technique"),5.0,Set.of())
                    .setStatChangeHandler(baseEssenceHandler));



    public static final DeferredHolder<ITechnique, ? extends BloodfeastSoulRefiningTechnique>
            BLOODFEAST_SOUL_REFINING_SCRIPTURE = TECHNIQUES.register(
            "bloodfeast_soul_refining_scripture",
            () -> new BloodfeastSoulRefiningTechnique(new BasicStatChangeHandler())
                    // ── Minor realm gains (per minor realm advance) ───────────────
                    .addMinorRealmStatModifier(ModStats.VITALITY.getId(),
                            new ValueContainerModifier(10, ModifierOperation.ADD_BASE, BLOODFEAST_KEY))
                    .addMinorRealmStatModifier(ModStats.STRENGTH.getId(),
                            new ValueContainerModifier(5,  ModifierOperation.ADD_BASE, BLOODFEAST_KEY))
                    .addMinorRealmStatModifier(ModStats.AGILITY.getId(),
                            new ValueContainerModifier(3,  ModifierOperation.ADD_BASE, BLOODFEAST_KEY))
                    .addMinorRealmStatModifier(ModStats.INTELLIGENCE.getId(),
                            new ValueContainerModifier(4,  ModifierOperation.ADD_BASE, BLOODFEAST_KEY))
                    .addMinorRealmAttributeModifier(Attributes.MAX_HEALTH,
                            new ValueContainerModifier(7,  ModifierOperation.ADD_BASE, BLOODFEAST_KEY))
                    // ── Major realm gains (per major realm advance) ───────────────
                    .addMajorRealmStatModifier(ModStats.VITALITY.getId(),
                            new ValueContainerModifier(0.20, ModifierOperation.MULTIPLY_FINAL, BLOODFEAST_KEY))
    );



    // ──── POISON TECHNIQUES ────────────────────────────────────────────

    public static final DeferredHolder<ITechnique, ? extends MyriadVenomRefinementTechnique> MYRIAD_VENOM_REFINEMENT_SCRIPTURE =
            TECHNIQUES.register("myriad_venom_refinement_scripture",
                    () -> new MyriadVenomRefinementTechnique(basePoisonHandler)
            );

    // ──── ESSENCE-ELEMENTAL HYBRID TECHNIQUES ────────────────────────────────────────────
    public static final DeferredHolder<ITechnique, ? extends FireEssenceTechnique> FIRE_ESSENCE_TECHNIQUE =
            TECHNIQUES.register("fire_essence_technique",
                    () -> new FireEssenceTechnique(baseEssenceHandler));
    public static final DeferredHolder<ITechnique, ? extends WaterEssenceTechnique> WATER_ESSENCE_TECHNIQUE =
            TECHNIQUES.register("water_essence_technique",
                    () -> new WaterEssenceTechnique(baseEssenceHandler));
    public static final DeferredHolder<ITechnique, ? extends WoodEssenceTechnique> WOOD_ESSENCE_TECHNIQUE =
            TECHNIQUES.register("wood_essence_technique",
                    () -> new WoodEssenceTechnique(baseEssenceHandler));
    public static final DeferredHolder<ITechnique, ? extends EarthEssenceTechnique> EARTH_ESSENCE_TECHNIQUE =
            TECHNIQUES.register("earth_essence_technique",
                    () -> new EarthEssenceTechnique(baseEssenceHandler));
    public static final DeferredHolder<ITechnique, ? extends MetalEssenceTechnique> METAL_ESSENCE_TECHNIQUE =
            TECHNIQUES.register("metal_essence_technique",
                    () -> new MetalEssenceTechnique(baseEssenceHandler));
    public static final DeferredHolder<ITechnique, ? extends LightningEssenceTechnique> LIGHTNING_ESSENCE_TECHNIQUE =
            TECHNIQUES.register("lightning_essence_technique",
                    () -> new LightningEssenceTechnique(baseEssenceHandler));
    public static final DeferredHolder<ITechnique, ? extends WindEssenceTechnique> WIND_ESSENCE_TECHNIQUE =
            TECHNIQUES.register("wind_essence_technique",
                    () -> new WindEssenceTechnique(baseEssenceHandler));
//    public static final DeferredHolder<ITechnique, ? extends FiveElementCultivationTechnique> FIVE_ELEMENT_CIRCULATION_METHOD =
//            TECHNIQUES.register("five_element_cultivation_technique",
//                    () -> new FiveElementCultivationTechnique(baseEssenceHandler));



    // ──── BODY TECHNIQUES ────────────────────────────────────────────
    public static final DeferredHolder<ITechnique, ? extends WhiteLightningTenStageTechnique> WHITE_LIGHTNING_TEN_STAGE_TECHNIQUE =
            TECHNIQUES.register("white_lightning_ten_stage_technique",
                    () -> new WhiteLightningTenStageTechnique(new BasicStatChangeHandler()
                            .addMinorRealmStatModifier(ModStats.VITALITY.getId(),
                                    new ValueContainerModifier(15, ModifierOperation.ADD_BASE, WHITE_LIGHTNING_KEY))
                            .addMinorRealmStatModifier(ModStats.STRENGTH.getId(),
                                    new ValueContainerModifier(11,  ModifierOperation.ADD_BASE, WHITE_LIGHTNING_KEY))
                            .addMinorRealmStatModifier(ModStats.AGILITY.getId(),
                                    new ValueContainerModifier(6,  ModifierOperation.ADD_BASE, WHITE_LIGHTNING_KEY))
                            .addMinorRealmStatModifier(ModStats.INTELLIGENCE.getId(),
                                    new ValueContainerModifier(4,  ModifierOperation.ADD_BASE, WHITE_LIGHTNING_KEY))
                            .addMajorRealmAttributeModifier(Attributes.OXYGEN_BONUS,
                                    new ValueContainerModifier(5,  ModifierOperation.ADD_BASE, WHITE_LIGHTNING_KEY))
                            .addMajorRealmStatModifier(ModStats.STRENGTH.getId(),
                                    new ValueContainerModifier(0.2,  ModifierOperation.MULTIPLY_FINAL, WHITE_LIGHTNING_KEY))
                    )
            );

    public static final DeferredHolder<ITechnique, ? extends HellBoundMarrowTechnique> HELLBOUND_MARROW_SCRIPTURE =
            TECHNIQUES.register("hellbound_marrow_scripture",
                    () -> new HellBoundMarrowTechnique(baseBodyHandler));



    // ──── BODY-ELEMENTAL HYBRID TECHNIQUES ────────────────────────────────────────────
    // T1
    public static final DeferredHolder<ITechnique, ? extends BodyElementTechnique> HEART_FIRE_TECHNIQUE =
            TECHNIQUES.register("heart_fire_technique", () ->
                    new BodyElementTechnique(ModPaths.FIRE.getId(), Component.translatable("ascension.technique.heart_fire_technique"), 3.0, baseBodyHandler, ModSkills.BODY_CULTIVATION_FIRE.getId()));
    public static final DeferredHolder<ITechnique, ? extends BodyElementTechnique> KIDNEY_WATER_TECHNIQUE =
            TECHNIQUES.register("kidney_water_technique", () ->
                    new BodyElementTechnique(ModPaths.WATER.getId(), Component.translatable("ascension.technique.kidney_water_technique"), 3.0, baseBodyHandler, ModSkills.BODY_CULTIVATION_WATER.getId()));
    public static final DeferredHolder<ITechnique, ? extends BodyElementTechnique> LIVER_WOOD_TECHNIQUE =
            TECHNIQUES.register("liver_wood_technique", () ->
                    new BodyElementTechnique(ModPaths.WOOD.getId(), Component.translatable("ascension.technique.liver_wood_technique"), 3.0, baseBodyHandler, ModSkills.BODY_CULTIVATION_WOOD.getId()));
    public static final DeferredHolder<ITechnique, ? extends BodyElementTechnique> SPLEEN_EARTH_TECHNIQUE =
            TECHNIQUES.register("spleen_earth_technique", () ->
                    new BodyElementTechnique(ModPaths.EARTH.getId(), Component.translatable("ascension.technique.spleen_earth_technique"), 3.0, baseBodyHandler, ModSkills.BODY_CULTIVATION_EARTH.getId()));
    public static final DeferredHolder<ITechnique, ? extends BodyElementTechnique> LUNG_METAL_TECHNIQUE =
            TECHNIQUES.register("lung_metal_technique", () ->
                    new BodyElementTechnique(ModPaths.METAL.getId(), Component.translatable("ascension.technique.lung_metal_technique"), 3.0, baseBodyHandler, ModSkills.BODY_CULTIVATION_METAL.getId()));
    // T2
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> WOOD_FIRE_BODY_TECHNIQUE =
            TECHNIQUES.register("wood_fire_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.wood_fire_body_technique"),
                            6.0, Set.of(ModPaths.WOOD.getId(), ModPaths.FIRE.getId()), baseBodyHandler, ModSkills.BODY_CULTIVATION_WOOD_FIRE.getId()));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> FIRE_EARTH_BODY_TECHNIQUE =
            TECHNIQUES.register("fire_earth_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.fire_earth_body_technique"),
                            6.0, Set.of(ModPaths.FIRE.getId(), ModPaths.EARTH.getId()), baseBodyHandler, ModSkills.BODY_CULTIVATION_FIRE_EARTH.getId()));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> EARTH_METAL_BODY_TECHNIQUE =
            TECHNIQUES.register("earth_metal_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.earth_metal_body_technique"),
                            6.0, Set.of(ModPaths.EARTH.getId(), ModPaths.METAL.getId()), baseBodyHandler, ModSkills.BODY_CULTIVATION_EARTH_METAL.getId()));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> METAL_WATER_BODY_TECHNIQUE =
            TECHNIQUES.register("metal_water_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.metal_water_body_technique"),
                            6.0, Set.of(ModPaths.METAL.getId(), ModPaths.WATER.getId()), baseBodyHandler, ModSkills.BODY_CULTIVATION_METAL_WATER.getId()));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> WATER_WOOD_BODY_TECHNIQUE =
            TECHNIQUES.register("water_wood_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.water_wood_body_technique"),
                            6.0, Set.of(ModPaths.WATER.getId(), ModPaths.WOOD.getId()), baseBodyHandler, ModSkills.BODY_CULTIVATION_WATER_WOOD.getId()));
    // T3
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> WOOD_FIRE_EARTH_BODY_TECHNIQUE =
            TECHNIQUES.register("wood_fire_earth_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.wood_fire_earth_body_technique"),
                            9.0, Set.of(ModPaths.WOOD.getId(), ModPaths.FIRE.getId(), ModPaths.EARTH.getId()), baseBodyHandler, ModSkills.BODY_CULTIVATION_WOOD_FIRE_EARTH.getId()));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> FIRE_EARTH_METAL_BODY_TECHNIQUE =
            TECHNIQUES.register("fire_earth_metal_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.fire_earth_metal_body_technique"),
                            9.0, Set.of(ModPaths.FIRE.getId(), ModPaths.EARTH.getId(), ModPaths.METAL.getId()), baseBodyHandler, ModSkills.BODY_CULTIVATION_FIRE_EARTH_METAL.getId()));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> EARTH_METAL_WATER_BODY_TECHNIQUE =
            TECHNIQUES.register("earth_metal_water_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.earth_metal_water_body_technique"),
                            9.0, Set.of(ModPaths.EARTH.getId(), ModPaths.METAL.getId(), ModPaths.WATER.getId()), baseBodyHandler, ModSkills.BODY_CULTIVATION_EARTH_METAL_WATER.getId()));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> METAL_WATER_WOOD_BODY_TECHNIQUE =
            TECHNIQUES.register("metal_water_wood_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.metal_water_wood_body_technique"),
                            9.0, Set.of(ModPaths.METAL.getId(), ModPaths.WATER.getId(), ModPaths.WOOD.getId()), baseBodyHandler, ModSkills.BODY_CULTIVATION_METAL_WATER_WOOD.getId()));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> WATER_WOOD_FIRE_BODY_TECHNIQUE =
            TECHNIQUES.register("water_wood_fire_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.water_wood_fire_body_technique"),
                            9.0, Set.of(ModPaths.WATER.getId(), ModPaths.WOOD.getId(), ModPaths.FIRE.getId()), baseBodyHandler, ModSkills.BODY_CULTIVATION_WATER_WOOD_FIRE.getId()));
    // T4
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> WOOD_FIRE_EARTH_METAL_BODY_TECHNIQUE =
            TECHNIQUES.register("wood_fire_earth_metal_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.wood_fire_earth_metal_body_technique"),
                            12.0, Set.of(ModPaths.WOOD.getId(), ModPaths.FIRE.getId(), ModPaths.EARTH.getId(), ModPaths.METAL.getId()), baseBodyHandler, ModSkills.BODY_CULTIVATION_WOOD_FIRE_EARTH_METAL.getId()));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> FIRE_EARTH_METAL_WATER_BODY_TECHNIQUE =
            TECHNIQUES.register("fire_earth_metal_water_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.fire_earth_metal_water_body_technique"),
                            12.0, Set.of(ModPaths.FIRE.getId(), ModPaths.EARTH.getId(), ModPaths.METAL.getId(), ModPaths.WATER.getId()), baseBodyHandler, ModSkills.BODY_CULTIVATION_FIRE_EARTH_METAL_WATER.getId()));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> EARTH_METAL_WATER_WOOD_BODY_TECHNIQUE =
            TECHNIQUES.register("earth_metal_water_wood_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.earth_metal_water_wood_body_technique"),
                            12.0, Set.of(ModPaths.EARTH.getId(), ModPaths.METAL.getId(), ModPaths.WATER.getId(), ModPaths.WOOD.getId()), baseBodyHandler, ModSkills.BODY_CULTIVATION_EARTH_METAL_WATER_WOOD.getId()));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> METAL_WATER_WOOD_FIRE_BODY_TECHNIQUE =
            TECHNIQUES.register("metal_water_wood_fire_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.metal_water_wood_fire_body_technique"),
                            12.0, Set.of(ModPaths.METAL.getId(), ModPaths.WATER.getId(), ModPaths.WOOD.getId(), ModPaths.FIRE.getId()), baseBodyHandler, ModSkills.BODY_CULTIVATION_METAL_WATER_WOOD_FIRE.getId()));
    public static final DeferredHolder<ITechnique, ? extends CombinedBodyElementTechnique> WATER_WOOD_FIRE_EARTH_BODY_TECHNIQUE =
            TECHNIQUES.register("water_wood_fire_earth_body_technique", () ->
                    new CombinedBodyElementTechnique(Component.translatable("ascension.technique.water_wood_fire_earth_body_technique"),
                            12.0, Set.of(ModPaths.WATER.getId(), ModPaths.WOOD.getId(), ModPaths.FIRE.getId(), ModPaths.EARTH.getId()), baseBodyHandler, ModSkills.BODY_CULTIVATION_WATER_WOOD_FIRE_EARTH.getId()));
    // T5
    public static final DeferredHolder<ITechnique, ? extends FiveElementBodyTechnique> FIVE_ELEMENT_BODY_TECHNIQUE =
            TECHNIQUES.register("five_element_body_technique", () ->
                    new FiveElementBodyTechnique(baseBodyHandler, ModSkills.BODY_CULTIVATION.getId()));



    // ──── SOUL TECHNIQUES ────────────────────────────────────────────
    public static final DeferredHolder<ITechnique, ? extends ScholarlySoulTechnique> SCHOLARLY_SOUL_TECHNIQUE =
            TECHNIQUES.register("scholarly_soul_technique", () -> new ScholarlySoulTechnique(baseSoulHandler));
    public static final DeferredHolder<ITechnique, ? extends PaleMoonTechnique> PALE_MOON_SCRIPTURE =
            TECHNIQUES.register("pale_moon_scripture", () -> new PaleMoonTechnique(baseSoulHandler));
    public static final DeferredHolder<ITechnique, ? extends GibbousMoonTechnique> GIBBOUS_MOON_SCRIPTURE =
            TECHNIQUES.register("gibbous_moon_scripture", () -> new GibbousMoonTechnique(baseSoulHandler));
    public static final DeferredHolder<ITechnique, ? extends DawningSunTechnique> DAWNING_SUN_SCRIPTURE =
            TECHNIQUES.register("dawning_sun_scripture", () -> new DawningSunTechnique(baseSoulHandler));
    public static final DeferredHolder<ITechnique, ? extends ZenithSunTechnique> ZENITH_SUN_SCRIPTURE =
            TECHNIQUES.register("zenith_sun_scripture", () -> new ZenithSunTechnique(baseSoulHandler));
    public static final DeferredHolder<ITechnique, ? extends SoulForgedWeaponTechnique> SOUL_FORGED_WEAPON_MANUAL =
            TECHNIQUES.register("soul_forged_weapon_manual", () -> new SoulForgedWeaponTechnique(baseSoulHandler));
    public static final DeferredHolder<ITechnique, ? extends JadeSpiritTechnique> JADE_SPIRIT_SCRIPTURE =
            TECHNIQUES.register("jade_spirit_scripture", () -> new JadeSpiritTechnique(baseSoulHandler));



    // ──── WEAPON TECHNIQUES ────────────────────────────────────────────
    public static final DeferredHolder<ITechnique, ? extends BasicWeaponTechnique> SWORD_COMPREHENSION_TECHNIQUE =
            TECHNIQUES.register("sword_comprehension_technique",
                    () -> new BasicWeaponTechnique(
                            ModPaths.SWORD.getId(),
                            Component.translatable("ascension.technique.sword_comprehension_technique"),
                            10.0D,
                            Set.of(),
                            List.of(
                                    ModSkills.SWORD_CULTIVATION_SKILL.getId(),
                                    ModSkills.SWORD_MASTERY_SKILL.getId()
                            ),
                            List.of(
                                    new BasicWeaponTechnique.RealmSkillUnlock(
                                            ModSkills.SWORD_DRAW.getId(),
                                            2
                                    )
                            )
                    ).setStatChangeHandler(baseWeaponHandler).setRealmChangeHandler(RealmChangeHandlers.HANDLER_1)
            );

    public static final DeferredHolder<ITechnique, ? extends BasicWeaponTechnique> SPEAR_COMPERHENSION_TECHNIQUE =
            TECHNIQUES.register("spear_comprehension_technique",
                    () -> new BasicWeaponTechnique(
                            ModPaths.SPEAR.getId(),
                            Component.translatable("ascension.technique.spear_comprehension_technique"),
                            10.0D,
                            Set.of(),
                            List.of(
                                    ModSkills.SPEAR_CULTIVATION_SKILL.getId(),
                                    ModSkills.SPEAR_MASTERY_SKILL.getId()
                            ),
                            List.of(
                                    new BasicWeaponTechnique.RealmSkillUnlock(
                                            ModSkills.SPEAR_THRUST.getId(),
                                            2
                                    )
                            )
                    ).setStatChangeHandler(baseWeaponHandler).setRealmChangeHandler(RealmChangeHandlers.SPEAR_HANDLER)
            );

    public static final DeferredHolder<ITechnique, ? extends BasicWeaponTechnique> BLADE_COMPERHENSION_TECHNIQUE =
            TECHNIQUES.register("blade_comprehension_technique",
                    () -> new BasicWeaponTechnique(
                            ModPaths.BLADE.getId(),
                            Component.translatable("ascension.technique.blade_comprehension_technique"),
                            10.0D,
                            Set.of(),
                            List.of(
                                    ModSkills.BLADE_CULTIVATION_SKILL.getId(),
                                    ModSkills.BLADE_MASTERY_SKILL.getId()
                            ),
                            List.of(
                                    new BasicWeaponTechnique.RealmSkillUnlock(
                                            ModSkills.BLADE_CLEAVE.getId(),
                                            2
                                    )
                            )
                    ).setStatChangeHandler(baseWeaponHandler).setRealmChangeHandler(RealmChangeHandlers.BLADE_HANDLER)
            );

    public static final DeferredHolder<ITechnique, ? extends BasicWeaponTechnique> SIMPLE_SWORD_MANUAL =
            TECHNIQUES.register("simple_sword_manual",
                    () -> new BasicWeaponTechnique(
                            ModPaths.SWORD.getId(),
                            Component.translatable("ascension.technique.simple_sword_manual"),
                            10.0D,
                            Set.of(),
                            List.of(
                                    ModSkills.SWORD_CULTIVATION_SKILL.getId(),
                                    ModSkills.SWORD_MASTERY_SKILL.getId()
                            ),
                            List.of()
                    ).setStatChangeHandler(baseWeaponHandler).setRealmChangeHandler(RealmChangeHandlers.HANDLER_1)
            );

    public static final DeferredHolder<ITechnique, ? extends BasicWeaponTechnique> FIST_COMPREHENSION_TECHNIQUE =
            TECHNIQUES.register("fist_comprehension_technique",
                    () -> new BasicWeaponTechnique(
                            ModPaths.FIST.getId(),
                            Component.translatable("ascension.technique.fist_comprehension_technique"),
                            10.0D,
                            Set.of(),
                            List.of(
                                    ModSkills.FIST_CULTIVATION_SKILL.getId()
                                    //ModSkills.FIST_MASTERY_SKILL.getId()
                            ),
                            List.of()
                    ).setStatChangeHandler(baseWeaponHandler)
            );

    public static final DeferredHolder<ITechnique, ? extends BasicWeaponTechnique> SIMPLE_AXE_MANUAL =
            TECHNIQUES.register("simple_axe_manual",
                    () -> new BasicWeaponTechnique(
                            ModPaths.SWORD.getId(),
                            Component.translatable("ascension.technique.simple_axe_manual"),
                            10.0D,
                            Set.of(),
                            List.of(
                                    ModSkills.AXE_CULTIVATION_SKILL.getId(),
                                    ModSkills.AXE_MASTERY_SKILL.getId()
                            ),
                            List.of()
                    ).setStatChangeHandler(baseWeaponHandler).setRealmChangeHandler(RealmChangeHandlers.AXEMACE_HANDLER)
            );

    public static final DeferredHolder<ITechnique, ? extends BasicWeaponTechnique> SIMPLE_SPEAR_MANUAL =
            TECHNIQUES.register("simple_spear_manual",
                    () -> new BasicWeaponTechnique(
                            ModPaths.SWORD.getId(),
                            Component.translatable("ascension.technique.simple_spear_manual"),
                            10.0D,
                            Set.of(),
                            List.of(
                                    ModSkills.SPEAR_CULTIVATION_SKILL.getId(),
                                    ModSkills.SPEAR_MASTERY_SKILL.getId()
                            ),
                            List.of()
                    ).setStatChangeHandler(baseWeaponHandler).setRealmChangeHandler(RealmChangeHandlers.SPEAR_HANDLER)
            );

    public static final DeferredHolder<ITechnique, ? extends BasicWeaponTechnique> SIMPLE_MACE_MANUAL =
            TECHNIQUES.register("simple_mace_manual",
                    () -> new BasicWeaponTechnique(
                            ModPaths.SWORD.getId(),
                            Component.translatable("ascension.technique.simple_mace_manual"),
                            10.0D,
                            Set.of(),
                            List.of(
                                    ModSkills.MACE_CULTIVATION_SKILL.getId(),
                                    ModSkills.MACE_MASTERY_SKILL.getId()
                            ),
                            List.of()
                    ).setStatChangeHandler(baseWeaponHandler).setRealmChangeHandler(RealmChangeHandlers.AXEMACE_HANDLER)
            );

    public static final DeferredHolder<ITechnique, ? extends BasicWeaponTechnique> SIMPLE_BLADE_MANUAL =
            TECHNIQUES.register("simple_blade_manual",
                    () -> new BasicWeaponTechnique(
                            ModPaths.SWORD.getId(),
                            Component.translatable("ascension.technique.simple_blade_manual"),
                            10.0D,
                            Set.of(),
                            List.of(
                                    ModSkills.BLADE_CULTIVATION_SKILL.getId(),
                                    ModSkills.BLADE_MASTERY_SKILL.getId()
                            ), List.of())
                            .setStatChangeHandler(baseWeaponHandler).setRealmChangeHandler(RealmChangeHandlers.BLADE_HANDLER)
            );


    public static void register(IEventBus modEventBus){
        TECHNIQUES.register(modEventBus);

        modEventBus.addListener(FMLCommonSetupEvent.class, event -> event.enqueueWork(() -> {
            TechniqueManualRegistry.register(
                    BLOODFEAST_SOUL_REFINING_SCRIPTURE.getId(),
                    6,
                    List.of(
                            "ascension.chapter.bloodfeast_soul_refining_scripture.1",
                            "ascension.chapter.bloodfeast_soul_refining_scripture.2",
                            "ascension.chapter.bloodfeast_soul_refining_scripture.3",
                            "ascension.chapter.bloodfeast_soul_refining_scripture.4",
                            "ascension.chapter.bloodfeast_soul_refining_scripture.5",
                            "ascension.chapter.bloodfeast_soul_refining_scripture.6"
                    )
            );
            TechniqueManualRegistry.register(
                    WHITE_LIGHTNING_TEN_STAGE_TECHNIQUE.getId(),
                    10,
                    List.of(
                            "ascension.chapter.white_lightning_ten_stage_technique.1",
                            "ascension.chapter.white_lightning_ten_stage_technique.2",
                            "ascension.chapter.white_lightning_ten_stage_technique.3",
                            "ascension.chapter.white_lightning_ten_stage_technique.4",
                            "ascension.chapter.white_lightning_ten_stage_technique.5",
                            "ascension.chapter.white_lightning_ten_stage_technique.6",
                            "ascension.chapter.white_lightning_ten_stage_technique.7",
                            "ascension.chapter.white_lightning_ten_stage_technique.8",
                            "ascension.chapter.white_lightning_ten_stage_technique.9",
                            "ascension.chapter.white_lightning_ten_stage_technique.10"
                    )
            );
            TechniqueManualRegistry.register(
                    SOUL_FORGED_WEAPON_MANUAL.getId(),
                    4,
                    List.of(
                            "ascension.chapter.soul_forged_weapon_manual.1",
                            "ascension.chapter.soul_forged_weapon_manual.2",
                            "ascension.chapter.soul_forged_weapon_manual.3",
                            "ascension.chapter.soul_forged_weapon_manual.4"
                    )
            );

        }));
    }
}
