package net.thejadeproject.ascension.refactor_packages.skills.custom;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.events.skills.SkillTickManager;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skills.ISkill;
import net.thejadeproject.ascension.refactor_packages.skills.ITickingSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.active.attack.body.WhiteLightningFist;
import net.thejadeproject.ascension.refactor_packages.skills.custom.active.attack.fire.FireSpray;
import net.thejadeproject.ascension.refactor_packages.skills.custom.active.utility.QiFlightSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.BodyCultivationSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.GenericCultivationSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.ScholarlySoulCultivationSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.SwordCultivationSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.WhiteLightningCultivationSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.elemental.*;
import net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.five_element.FiveElementCirculation;
import net.thejadeproject.ascension.refactor_packages.skills.custom.form_change.EnterSpiritForm;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.QiSustainedBodySkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.RegenerationBoostSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.TurbidEnergyPurgeSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.elemental.AquaticCirculationSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.elemental.FlameTemperedBodySkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.elemental.VerdantRecoverySkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.flight.TrueFlightSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.weapon.*;
import net.thejadeproject.ascension.refactor_packages.skills.custom.qi.QiRelease;

public class ModSkills {
    public static final DeferredRegister<ISkill> SKILLS =DeferredRegister.create(AscensionRegistries.Skills.SKILL_REGISTRY, AscensionCraft.MOD_ID);

    //TODO update to not include any of these details (except path) and the technique defines the data

    // ──── Universal Skills ────────────────────────────────────────────
    // Passives
    public static final DeferredHolder<ISkill, RegenerationBoostSkill> REGENERATION_BOOST =
            SKILLS.register("regeneration_boost", RegenerationBoostSkill::new);
    public static final DeferredHolder<ISkill, QiSustainedBodySkill> QI_SUSTAINED_BODY =
            SKILLS.register("qi_sustained_body", QiSustainedBodySkill::new);
    public static final DeferredHolder<ISkill, TrueFlightSkill> TRUE_FLIGHT =
            SKILLS.register("true_flight", TrueFlightSkill::new);

    // Actives
    public static final DeferredHolder<ISkill, ? extends QiFlightSkill> AIR_STEP =
            SKILLS.register("air_step", QiFlightSkill::new);
    public static final DeferredHolder<ISkill,? extends EnterSpiritForm> ENTER_SPIRIT_FORM =
            SKILLS.register("enter_spirit_form", EnterSpiritForm::new);


    // ──── ESSENCE SKILLS ────────────────────────────────────────────
    // Cultivation
    public static final DeferredHolder<ISkill,? extends GenericCultivationSkill> BASIC_CULTIVATION_SKILL =
            SKILLS.register("basic_essence_cultivation_skill", ()->new GenericCultivationSkill(2.0,ModPaths.ESSENCE.getId()));

    public static final DeferredHolder<ISkill, ? extends FireEssenceCultivationSkill> FIRE_ESSENCE_CULTIVATION_SKILL =
            SKILLS.register("fire_essence_cultivation_skill", FireEssenceCultivationSkill::new);
    public static final DeferredHolder<ISkill, ? extends WaterEssenceCultivationSkill> WATER_ESSENCE_CULTIVATION_SKILL =
            SKILLS.register("water_essence_cultivation_skill", WaterEssenceCultivationSkill::new);
    public static final DeferredHolder<ISkill, ? extends WoodEssenceCultivationSkill> WOOD_ESSENCE_CULTIVATION_SKILL =
            SKILLS.register("wood_essence_cultivation_skill", WoodEssenceCultivationSkill::new);
    public static final DeferredHolder<ISkill, ? extends EarthEssenceCultivationSkill> EARTH_ESSENCE_CULTIVATION_SKILL =
            SKILLS.register("earth_essence_cultivation_skill", EarthEssenceCultivationSkill::new);
    public static final DeferredHolder<ISkill, ? extends MetalEssenceCultivationSkill> METAL_ESSENCE_CULTIVATION_SKILL =
            SKILLS.register("metal_essence_cultivation_skill", MetalEssenceCultivationSkill::new);

    public static final DeferredHolder<ISkill, ? extends LightningEssenceCultivationSkill> LIGHTNING_ESSENCE_CULTIVATION_SKILL =
            SKILLS.register("lightning_essence_cultivation_skill", LightningEssenceCultivationSkill::new);
    public static final DeferredHolder<ISkill, ? extends WindEssenceCultivationSkill> WIND_ESSENCE_CULTIVATION_SKILL =
            SKILLS.register("wind_essence_cultivation_skill", WindEssenceCultivationSkill::new);

    public static final DeferredHolder<ISkill,? extends FiveElementCirculation> FIVE_ELEMENT_CIRCULATION =
            SKILLS.register("five_element_circulation", FiveElementCirculation::new);
    // Passives

    // Actives


    // ──── ELEMENTAL SKILLS ────────────────────────────────────────────
    // Cultivation

    // Passives
    public static final DeferredHolder<ISkill, ? extends FlameTemperedBodySkill> FLAME_TEMPERED_BODY =
            SKILLS.register("flame_tempered_body", FlameTemperedBodySkill::new);
    public static final DeferredHolder<ISkill, ? extends AquaticCirculationSkill> AQUATIC_CIRCULATION =
            SKILLS.register("aquatic_circulation", AquaticCirculationSkill::new);
    public static final DeferredHolder<ISkill, ? extends VerdantRecoverySkill> VERDANT_RECOVERY =
            SKILLS.register("verdant_recovery", VerdantRecoverySkill::new);
    // TODO: Metal and Earth Skill

    // Actives
    // TODO: Wood and Metal and Lightning and Wind Skills
    public static final DeferredHolder<ISkill,? extends FireSpray> FIRE_SPRAY = SKILLS.register("fire_spray", FireSpray::new);


    // ──── BODY SKILLS ────────────────────────────────────────────
    // Cultivation
    public static final DeferredHolder<ISkill, ? extends BodyCultivationSkill> BODY_CULTIVATION_SKILL =
            SKILLS.register("body_cultivation_skill", BodyCultivationSkill::new);
    public static final DeferredHolder<ISkill, ? extends WhiteLightningCultivationSkill> WHITE_LIGHTNING_CULTIVATION_SKILL =
            SKILLS.register("white_lightning_cultivation_skill", WhiteLightningCultivationSkill::new);

    // Passives
    public static final DeferredHolder<ISkill, ? extends TurbidEnergyPurgeSkill> TURBID_ENERGY_PURGE =
            SKILLS.register("turbid_energy_purge", TurbidEnergyPurgeSkill::new);

    // Actives
    public static final DeferredHolder<ISkill, ? extends WhiteLightningFist> WHITE_LIGHTNING_FIST =
            SKILLS.register("white_lightning_fist", WhiteLightningFist::new);



    // ──── SOUL SKILLS ────────────────────────────────────────────
    // Scholarly Soul Skills
    public static final DeferredHolder<ISkill, ? extends ScholarlySoulCultivationSkill> SCHOLARLY_SOUL_CULTIVATION_SKILL =
            SKILLS.register("scholarly_soul_cultivation_skill", ScholarlySoulCultivationSkill::new);

    // Passives

    // Actives

    // ──── WEAPON SKILLS ────────────────────────────────────────────
    // Cultivation
    public static final DeferredHolder<ISkill,? extends SwordCultivationSkill> SWORD_CULTIVATION_SKILL =
            SKILLS.register("sword_cultivation_skill", SwordCultivationSkill::new);

    // Passives TODO: Give different weapons different damage multi
    public static final DeferredHolder<ISkill, ? extends SwordMasterySkill> SWORD_MASTERY_SKILL =
            SKILLS.register("sword_mastery_skill", SwordMasterySkill::new);
    public static final DeferredHolder<ISkill, ? extends SpearMasterySkill> SPEAR_MASTERY_SKILL =
            SKILLS.register("spear_mastery_skill", SpearMasterySkill::new);
    public static final DeferredHolder<ISkill, ? extends AxeMasterySkill> AXE_MASTERY_SKILL =
            SKILLS.register("axe_mastery_skill", AxeMasterySkill::new);
    public static final DeferredHolder<ISkill, ? extends MaceMasterySkill> MACE_MASTERY_SKILL =
            SKILLS.register("mace_mastery_skill", MaceMasterySkill::new);
    public static final DeferredHolder<ISkill, ? extends TridentMasterySkill> TRIDENT_MASTERY_SKILL =
            SKILLS.register("trident_mastery_skill", TridentMasterySkill::new);
    public static final DeferredHolder<ISkill, ? extends BowMasterySkill> BOW_MASTERY_SKILL =
            SKILLS.register("bow_mastery_skill", BowMasterySkill::new);
    public static final DeferredHolder<ISkill, ? extends BladeMasterySkill> BLADE_MASTERY_SKILL =
            SKILLS.register("blade_mastery_skill", BladeMasterySkill::new);

    // Actives



    // ──── Register Skills with onPlayerTick methods ────────────────────────────────────────────
    public static void registerTickingSkills() {
        registerTickingSkill(TURBID_ENERGY_PURGE);
        registerTickingSkill(AQUATIC_CIRCULATION);
        registerTickingSkill(VERDANT_RECOVERY);
        registerTickingSkill(REGENERATION_BOOST);
        registerTickingSkill(QI_SUSTAINED_BODY);
        registerTickingSkill(TRUE_FLIGHT);
    }

    private static void registerTickingSkill(DeferredHolder<ISkill, ? extends ISkill> skillHolder) {
        ISkill skill = skillHolder.get();

        if (skill instanceof ITickingSkill tickingSkill) {
            SkillTickManager.register(skillHolder.getId(), tickingSkill);
        } else {
            throw new IllegalStateException(skillHolder.getId() + " is not an ITickingSkill.");
        }
    }

    public static final DeferredHolder<ISkill,? extends QiRelease> QI_RELEASE = SKILLS.register("qi_release",
            QiRelease::new);

    public static void register(IEventBus modEventBus){
        SKILLS.register(modEventBus);
    }
}
