package net.thejadeproject.ascension.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.thejadeproject.ascension.AscensionCraft;

public class lang extends LanguageProvider {
    public lang(PackOutput output, String locale) {
        super(output, AscensionCraft.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {

        // Paths
        add("ascension.path.essence", "Essence");
        add("ascension.path.body", "Body");
        add("ascension.path.soul", "Soul");

        add("ascension.path.fire", "Fire");
        add("ascension.path.water","Water");
        add("ascension.path.wood","Wood");
        add("ascension.path.earth","Earth");
        add("ascension.path.metal","Metal");

        add("ascension.path.lightning","Lightning");
        add("ascension.path.wind","Wind");

        add("ascension.path.sword", "Sword");
        add("ascension.path.blade", "Blade");
        add("ascension.path.axe", "Axe");
        add("ascension.path.spear", "Spear");
        add("ascension.path.mace", "Mace");
        add("ascension.path.bow", "Bow");
        add("ascension.path.shield", "Shield");
        add("ascension.path.trident", "Trident");
        add("ascension.path.fist", "Fist");

        add("ascension.path.virtuous", "Virtuous");
        add("ascension.path.demonic", "Demonic");
        add("ascension.path.buddhist", "Buddhist");




        // Essence realms
        add("ascension.path.essence.mortal", "Mortal");
        add("ascension.path.essence.qi_condensation", "Qi Condensation");
        add("ascension.path.essence.formation_establishment", "Formation Establishment");
        add("ascension.path.essence.golden_core", "Golden Core");
        add("ascension.path.essence.nascent_core", "Nascent Core");

        // Body realms
        add("ascension.path.body.mortal", "Mortal Body");
        add("ascension.path.body.skin_tempering", "Skin Tempering");
        add("ascension.path.body.sinew_weaving", "Sinew Weaving");
        add("ascension.path.body.bone_forging", "Bone Forging");
        add("ascension.path.body.heart_kindling", "Heart Kindling");

        // Soul realms
        add("ascension.path.soul.mortal", "Mortal Soul");
        add("ascension.path.soul.battle_soul", "Battle Soul");
        add("ascension.path.soul.azure_soul", "Azure Soul");
        add("ascension.path.soul.silver_soul", "Silver Soul");
        add("ascension.path.soul.gold_battle_soul", "Gold Battle Soul");

        // Fire realms
        add("ascension.path.fire.kindling", "Kindling");
        add("ascension.path.fire.ignition", "Ignition");
        add("ascension.path.fire.true_flame", "True Flame");
        add("ascension.path.fire.origin_flame", "Origin Flame");

        // Water realms
        add("ascension.path.water.tidepool", "Tidepool");
        add("ascension.path.water.current", "Current");
        add("ascension.path.water.true_water", "True Water");
        add("ascension.path.water.origin_water", "Origin Water");

        // Wood realms
        add("ascension.path.wood.sprout", "Sprout");
        add("ascension.path.wood.growth", "Growth");
        add("ascension.path.wood.true_wood", "True Wood");
        add("ascension.path.wood.origin_wood", "Origin Wood");

        // Earth realms
        add("ascension.path.earth.pebble", "Pebble");
        add("ascension.path.earth.stone", "Stone");
        add("ascension.path.earth.true_earth", "True Earth");
        add("ascension.path.earth.origin_earth", "Origin Earth");

        // Metal realms
        add("ascension.path.metal.ore", "Ore");
        add("ascension.path.metal.ingot", "Ingot");
        add("ascension.path.metal.true_metal", "True Metal");
        add("ascension.path.metal.origin_metal", "Origin Metal");

        // Wind realms
        add("ascension.path.wind.gust", "Gust");
        add("ascension.path.wind.lesser_breeze", "Lesser Breeze");
        add("ascension.path.wind.true_wind", "True Wind");
        add("ascension.path.wind.origin_wind", "Origin Wind");

        // Lightning realms
        add("ascension.path.lightning.spark", "Spark");
        add("ascension.path.lightning.lesser_lightning", "Lesser Lightning");
        add("ascension.path.lightning.true_lightning", "True Lightning");
        add("ascension.path.lightning.origin_lightning", "Origin Lightning");

        // Sword realms
        add("ascension.path.sword.initiate", "Initiate");
        add("ascension.path.sword.intent", "Intent");
        add("ascension.path.sword.aura", "Aura");
        add("ascension.path.sword.unity", "Unity");

        // Axe realms
        add("ascension.path.axe.initiate", "Initiate");
        add("ascension.path.axe.intent", "Intent");
        add("ascension.path.axe.aura", "Aura");
        add("ascension.path.axe.unity", "Unity");

        // Blade realms
        add("ascension.path.blade.initiate", "Initiate");
        add("ascension.path.blade.intent", "Intent");
        add("ascension.path.blade.aura", "Aura");
        add("ascension.path.blade.unity", "Unity");

        // Spear realms
        add("ascension.path.spear.initiate", "Initiate");
        add("ascension.path.spear.intent", "Intent");
        add("ascension.path.spear.aura", "Aura");
        add("ascension.path.spear.unity", "Unity");

        // Bow realms
        add("ascension.path.bow.initiate", "Initiate");
        add("ascension.path.bow.intent", "Intent");
        add("ascension.path.bow.aura", "Aura");
        add("ascension.path.bow.unity", "Unity");

        // Trident realms
        add("ascension.path.trident.initiate", "Initiate");
        add("ascension.path.trident.intent", "Intent");
        add("ascension.path.trident.aura", "Aura");
        add("ascension.path.trident.unity", "Unity");

        // Mace realms
        add("ascension.path.mace.initiate", "Initiate");
        add("ascension.path.mace.intent", "Intent");
        add("ascension.path.mace.aura", "Aura");
        add("ascension.path.mace.unity", "Unity");

        // Shield realms
        add("ascension.path.shield.initiate", "Initiate");
        add("ascension.path.shield.intent", "Intent");
        add("ascension.path.shield.aura", "Aura");
        add("ascension.path.shield.unity", "Unity");

        // Fist realms
        add("ascension.path.fist.initiate", "Initiate");
        add("ascension.path.fist.intent", "Intent");
        add("ascension.path.fist.aura", "Aura");
        add("ascension.path.fist.unity", "Unity");

        // Buddhist realms
        add("ascension.path.buddhist.sramana", "Sramana");
        add("ascension.path.buddhist.arhat", "Arhat");
        add("ascension.path.buddhist.bodhisattva", "Bodhisattva");
        add("ascension.path.buddhist.tathagata", "Tathagata");

        // Demonic realms
        add("ascension.path.demonic.seed", "Seed");
        add("ascension.path.demonic.heart", "Heart");
        add("ascension.path.demonic.demon", "Demon");
        add("ascension.path.demonic.demonic", "Demonic");

        // Virtuous realms
        add("ascension.path.virtuous.self", "Self");
        add("ascension.path.virtuous.family", "Family");
        add("ascension.path.virtuous.state", "State");
        add("ascension.path.virtuous.universal", "Universal");



        // Physiques
        add("ascension.physiques.mortal", "Mortal Physique");
        add("ascension.physiques.mortal.description","so fragile yet... full of potential");//i know cringe but needed smth
        add("ascension.physiques.cripple", "Cripple");
        add("ascension.physiques.severed_meridians", "Severed Meridians");
        add("ascension.physiques.sword_bone", "Sword Bone");
        add("ascension.physiques.flame_touched", "Flame Touched");
        add("ascension.physiques.tyrant_body", "Tyrant Body");
        add("ascension.physiques.academic_spirit", "Academic Soul");
        add("ascension.physiques.stone_monkey", "Stone Monkey");
        add("ascension.physiques.blood_fiend", "Blood Fiend");
        add("ascension.physiques.blood_wraith", "Blood Wraith");
        add("ascension.physiques.virtuoso_buddha", "Virtuoso Buddha");
        add("ascension.physiques.pointed_eyes", "Pointed Eyes");
        add("ascension.physiques.bruised_knuckle_body", "Bruised Knuckle Body");
        add("ascension.physiques.iron_bulwark_spine", "Iron Bulwark Spine");
        add("ascension.physiques.arrow_blessed", "Arrow Blessed");
        add("ascension.physiques.wild_cleaver_veteran", "Wild Cleaver Veteran");
        add("ascension.physiques.thin_sword_pulse", "Thin Sword Pulse");
        add("ascension.physiques.flow_severing_eyes", "Flow Severing Eyes");
        add("ascension.physiques.divine_essence_bone", "Divine Essence Bone");
        add("ascension.physiques.heavenly_essence_bone", "Heavenly Essence Bone");
        add("ascension.physiques.special_essence_bone", "Special Essence Bone");
        add("ascension.physiques.mortal_essence_bone", "Mortal Essence Bone");
        add("ascension.physiques.wind_attuned", "Wind Attuned");
        add("ascension.physiques.lightning_attuned", "Lightning Attuned");
        add("ascension.physiques.metal_attuned", "Metal Attuned");
        add("ascension.physiques.wood_attuned", "Wood Attuned");
        add("ascension.physiques.earth_attuned", "Earth Attuned");
        add("ascension.physiques.water_attuned", "Water Attuned");
        add("ascension.physiques.fire_attuned", "Fire Attuned");
        add("ascension.physiques.blessed", "Blessed One");
        add("ascension.physiques.metal_body", "Metal Body");
        add("ascension.physiques.water_body", "Water Body");
        add("ascension.physiques.fire_body", "Fire Body");
        add("ascension.physiques.wood_body", "Wood Body");
        add("ascension.physiques.earth_body", "Earth Body");
        add("ascension.physiques.clear_spirit", "Clear Spirit");
        add("ascension.physiques.dreaming_soul", "Dreaming Soul");
        add("ascension.physiques.soul_gaze", "Soul Gazing Eyes");
        add("ascension.physiques.ashen_soul_flame", "Ashen Soul Flame");
        add("ascension.physiques.thundering_soul_core", "Thundering Soul Core");
        add("ascension.physiques.soul_sword_heart", "Sword Heart Soul");
        add("ascension.physiques.spear_soul_mark", "Spear Marked Soul");

        // Elemental body merged physiques (2-element)
        add("ascension.physiques.liver_heart_awakening", "Liver-Heart Awakening Physique");
        add("ascension.physiques.heart_spleen_awakening", "Heart-Spleen Awakening Physique");
        add("ascension.physiques.spleen_lung_awakening", "Spleen-Lung Awakening Physique");
        add("ascension.physiques.lung_kidney_awakening", "Lung-Kidney Awakening Physique");
        add("ascension.physiques.kidney_liver_awakening", "Kidney-Liver Awakening Physique");

        // Elemental body merged physiques (3-element)
        add("ascension.physiques.vital_blood", "Vital Blood Physique");
        add("ascension.physiques.refined_qi", "Refined Qi Physique");
        add("ascension.physiques.flowing_tide", "Flowing Tide Physique");
        add("ascension.physiques.deep_root", "Deep Root Physique");
        add("ascension.physiques.rising_yang", "Rising Yang Physique");

        // Elemental body merged physiques (4-element)
        add("ascension.physiques.unquenched_palace", "Unquenched Palace Physique");
        add("ascension.physiques.unrooted_palace", "Unrooted Palace Physique");
        add("ascension.physiques.unlit_palace", "Unlit Palace Physique");
        add("ascension.physiques.ungrounded_palace", "Ungrounded Palace Physique");
        add("ascension.physiques.unrefined_palace", "Unrefined Palace Physique");

        // Elemental body merged physiques (5-element)
        add("ascension.physiques.five_palace_immortal", "Five Palace Immortal Physique");

        add("ascension.physiques.world_dominator", "World Dominator");
        add("ascension.message.physique.world_dominator.acquired", "%s has become the %s. Bear witness to their Ascension!");
        add("ascension.physiques.soul_crowned_king", "Crowned Soul King");
        add("ascension.message.physique.soul_crowned_king.acquired", "%s has become the %s. Tremble beneath their Throne!");



        // Physique Evolution Messages
        add("ascension.message.physique_evolution.pacifist_mortal_reset", "Your peaceful mortal streak has been broken.");
        add("ascension.message.physique_evolution.pacifist_blessed_broken", "The Blessed path has been stained by blood.");



        //Techniques
        add("ascension.learnt_technique","You have learned the %s");
        add("ascension.technique.basic_cultivation_technique","Basic Cultivation Technique");
        add("ascension.technique.advanced_cultivation_technique","Advanced Cultivation Technique");
        add("ascension.technique.sword_comprehension_technique","Sword Comprehension Technique");
        add("ascension.technique.five_element","Five Element Circulation Method");
        add("ascension.technique.scholarly_soul_technique","Scholar's Soul Transcendence");
        add("ascension.technique.white_lightning_ten_stage_technique","White Lightning Ten Stages");

        add("ascension.technique.fire_essence_technique","Fire Essence Technique");
        add("ascension.technique.water_essence_technique","Water Essence Technique");
        add("ascension.technique.earth_essence_technique","Earth Essence Technique");
        add("ascension.technique.metal_essence_technique","Metal Essence Technique");
        add("ascension.technique.wood_essence_technique","Wood Essence Technique");
        add("ascension.technique.lightning_essence_technique","Lightning Essence Technique");
        add("ascension.technique.wind_essence_technique","Wind Essence Technique");

        // Single-element body techniques
        add("ascension.technique.heart_fire_technique", "Heart Flame Cultivation");
        add("ascension.technique.kidney_water_technique", "Kidney Tide Cultivation");
        add("ascension.technique.liver_wood_technique", "Liver Grove Cultivation");
        add("ascension.technique.spleen_earth_technique", "Spleen Mountain Cultivation");
        add("ascension.technique.lung_metal_technique", "Lung Forge Cultivation");

        // 2-element body techniques
        add("ascension.technique.wood_fire_body_technique", "Rising Flame Method");
        add("ascension.technique.fire_earth_body_technique", "Smoldering Earth Method");
        add("ascension.technique.earth_metal_body_technique", "Hidden Vein Method");
        add("ascension.technique.metal_water_body_technique", "Jade Spring Method");
        add("ascension.technique.water_wood_body_technique", "Evergreen Current Method");

        // 3-element body techniques
        add("ascension.technique.wood_fire_earth_body_technique", "Ascending Pyre Method");
        add("ascension.technique.fire_earth_metal_body_technique", "Forge and Harvest Method");
        add("ascension.technique.earth_metal_water_body_technique", "Deep Vein Spring Method");
        add("ascension.technique.metal_water_wood_body_technique", "Iron Root Current Method");
        add("ascension.technique.water_wood_fire_body_technique", "Morning Mist Method");

        // 4-element body techniques
        add("ascension.technique.wood_fire_earth_metal_body_technique", "Earthbound Cycle Method");
        add("ascension.technique.fire_earth_metal_water_body_technique", "Sunken Forge Method");
        add("ascension.technique.earth_metal_water_wood_body_technique", "Rooted Tide Method");
        add("ascension.technique.metal_water_wood_fire_body_technique", "Untempered Blaze Method");
        add("ascension.technique.water_wood_fire_earth_body_technique", "Smoldering Grove Method");

        // 5-element body technique
        add("ascension.technique.five_element_body_technique", "Five Harmony Body Method");
        add("ascension.technique.pale_moon_scripture", "Pale Moon Scripture");
        add("ascension.technique.gibbous_moon_scripture", "Gibbous Moon Scripture");



        // Multi Part Technique Items
        add("item.ascension.scholarly_soul_rectification_of_names", "Rectification of Names");
        add("item.ascension.scholarly_soul_great_learning", "Great Learning");
        add("item.ascension.scholarly_soul_thousand_commentaries", "Hundred Thousand Commentaries");
        add("item.ascension.scholarly_soul_sage_mandate", "Mandate of the Sage");



        // Demonic Techniques
        add("ascension.technique.bloodfeast_soul_refining_scripture", "§4Bloodfeast Soul-Refining Scripture");
        add("ascension.technique.bloodfeast_soul_refining_scripture.short_desc", "§4A heretical scripture that refines the soul through the blood of the slain.");
        add("ascension.technique.bloodfeast_soul_refining_scripture.desc", "§4By feasting on the vital essence of nearby creatures the cultivator advances at an\nextraordinary pace. Yet the path is stained crimson each higher realm demands\nproof of the hunt, and those who hunt only their fellow cultivators grow ever more\nfearsome with each slaughter.");



        //Skills
        add("ascension.skill.regeneration_boost", "Regenerative Body");
        add("ascension.skill.regeneration_boost.description", "Your refined body slowly restores lost health over time.");
        add("ascension.skill.qi_sustained_body", "Qi-Sustained Body.");
        add("ascension.skill.qi_sustained_body.description", "Your body is sustained by Qi, removing the need for ordinary food.");
        add("ascension.skill.true_flight", "True Flight");
        add("ascension.skill.true_flight.description", "Grants the ability to fly freely.");
        add("ascension.skill.air_step", "Air Step");
        add("ascension.skill.air_step.description", "Circulate qi through your body to step onto the air.");
        add("ascension.skill.enter_spirit_form", "Enter Spirit Form");
        add("ascension.skill.enter_spirit_form.description", "Project your Spirit from your Body.");
        add("ascension.skill.cultivation_skill", "%s Cultivation Skill");
        add("ascension.skill.cultivation_skill.description", "Allows you to cultivate the %s path.");
        add("ascension.skill.elemental_essence_cultivation", "%s Essence Cultivation");
        add("ascension.skill.fire_essence_cultivation.description", "Cultivates Essence through fire. 75% base speed, 150% while burning, and 225% while in lava.");
        add("ascension.skill.water_essence_cultivation.description", "Cultivates Essence through water. 80% base speed and 130% while submerged in water");
        add("ascension.skill.wood_essence_cultivation.description", "Cultivates Essence through nearby plants, trees, and living growth.");
        add("ascension.skill.earth_essence_cultivation.description", "Cultivates Essence through stone, soil, depth, and grounded stillness.");
        add("ascension.skill.metal_essence_cultivation.description", "Cultivates Essence through nearby ores and condensed mineral power.");
        add("ascension.skill.lightning_essence_cultivation.description", "Cultivates Essence through thunderous resonance. It cultivates normally at rest, faster beneath storms, and surges after being struck by lightning outside tribulations.");
        add("ascension.skill.wind_essence_cultivation.description", "Cultivates Essence through open sky and high altitude. It weakens in enclosed spaces and strengthens the higher you stand beneath the wind.");
        add("ascension.skill.five_element_circulation", "Five Element Circulation");
        add("ascension.skill.five_element_circulation.description", "Cultivate the Essence through the Five Elements, harmonising and uniting them.");
        add("ascension.skill.flame_tempered_body", "Flame Tempered Body");
        add("ascension.skill.flame_tempered_body.description", "Reduces fire and lava damage.");
        add("ascension.skill.aquatic_circulation", "Aquatic Circulation");
        add("ascension.skill.aquatic_circulation.description", "Grants conduit-like power while underwater.");
        add("ascension.skill.verdant_recovery", "Verdant Recovery");
        add("ascension.skill.verdant_recovery.description", "Slowly regenerates health while surrounded by plant life.");
        add("ascension.skill.fire_spray", "Fire Spray");
        add("ascension.skill.fire_spray.description", "Condense a beam of Fire Qi and advance fearlessly.");
        add("ascension.skill.white_lightning_cultivation_skill", "White Lightning Cultivation");
        add("ascension.skill.white_lightning_cultivation_skill.description", "Cultivates the Body through white martial lightning, gaining speed while unarmed or purifying harmful effects.");
        add("ascension.skill.turbid_energy_purge", "Turbid Energy Purge");
        add("ascension.skill.turbid_energy_purge.description", "Slowly burns away harmful effects through purified white martial energy.");
        add("ascension.skill.white_lightning_fist", "White Lightning Fist");
        add("ascension.skill.white_lightning_fist.description", "An unarmed strike that releases purified white martial energy into the target's body.");
        add("ascension.skill.scholarly_soul_cultivation_skill", "Scholarly Soul Cultivation");
        add("ascension.skill.scholarly_soul_cultivation_skill.description", "Cultivates the Soul path through study and understanding. Higher realms require recovered chapters.");
        add("ascension.skill.sword_cultivation_skill", "Sword Comprehension");
        add("ascension.skill.sword_cultivation_skill.description", """
                        One swing splits air.
                        Ten swings shape the blade.
                        A hundred swings carve skill.
                        A thousand swings awaken intent.
                        Ten thousand swings… heaven yields to the sword.
                        """);
        add("ascension.skill.axe_mastery_skill", "Axe Mastery");
        add("ascension.skill.axe_mastery_skill.description", "As your worldview and realm increase, so to does your command over the Axe.");
        add("ascension.skill.blade_mastery_skill", "Blade Mastery");
        add("ascension.skill.blade_mastery_skill.description", "As your worldview and realm increase, so to does your command over the Blade.");
        add("ascension.skill.bow_mastery_skill", "Bow Mastery");
        add("ascension.skill.bow_mastery_skill.description", "As your worldview and realm increase, so to does your command over the Bow.");
        add("ascension.skill.mace_mastery_skill", "Mace Mastery");
        add("ascension.skill.mace_mastery_skill.description", "As your worldview and realm increase, so to does your command over the Mace.");
        add("ascension.skill.spear_mastery_skill", "Spear Mastery");
        add("ascension.skill.spear_mastery_skill.description", "As your worldview and realm increase, so to does your command over the Spear.");
        add("ascension.skill.sword_mastery_skill", "Sword Mastery");
        add("ascension.skill.sword_mastery_skill.description", "As your worldview and realm increase, so to does your command over the Sword.");
        add("ascension.skill.trident_mastery_skill", "Trident Mastery");
        add("ascension.skill.trident_mastery_skill.description", "As your worldview and realm increase, so to does your command over the Trident.");
        add("ascension.skill.pale_moon_cultivation_skill", "Pale Moon Cultivation");
        add("ascension.skill.pale_moon_cultivation_skill.description", "Cultivates the Soul by drawing upon pale lunar essence. Must be practised under open sky. Stronger when gazing directly at the moon. Harmful beneath direct sunlight.");
        add("ascension.skill.pale_moon_cultivation_skill.blocked_indoors", "You cannot cultivate the Pale Moon Scripture indoors.");
        add("ascension.skill.gibbous_moon_cultivation_skill", "Gibbous Moon Cultivation");
        add("ascension.skill.gibbous_moon_cultivation_skill.description", "Cultivates the Soul by drawing upon the swelling power of the gibbous moon. Must be practised under open sky. Greatly empowered when gazing directly at the moon. Harmful beneath direct sunlight.");
        add("ascension.skill.gibbous_moon_cultivation_skill.blocked_indoors", "You cannot cultivate the Gibbous Moon Scripture indoors.");

        add("ascension.skill.bloodfeast_banquet", "§4Bloodfeast Banquet");
        add("ascension.skill.bloodfeast_banquet.description", "§4Draw upon the life-force of nearby creatures, feasting on their blood to fuel your cultivation. The greater your realm, the wider your table.");

        // Body cultivation — T1
        add("ascension.skill.body_cultivation_fire", "§6Fire Body Tempering");
        add("ascension.skill.body_cultivation_fire.description", "§6The heart burns with every strike, its heat sinking into the flesh and tempering it from within.");
        add("ascension.skill.body_cultivation_water", "§6Water Body Cleansing");
        add("ascension.skill.body_cultivation_water.description", "§6The kidneys draw away what should not remain, each wound leaving the body clearer than before.");
        add("ascension.skill.body_cultivation_wood", "§6Wood Body Conditioning");
        add("ascension.skill.body_cultivation_wood.description", "§6The liver endures strain without breaking, the body growing stronger each time it is forced to bend.");
        add("ascension.skill.body_cultivation_earth", "§6Earth Body Hardening");
        add("ascension.skill.body_cultivation_earth.description", "§6The spleen settles deep and unmoving, each blow sinking into the body without shaking its foundation.");
        add("ascension.skill.body_cultivation_metal", "§6Metal Body Refinement");
        add("ascension.skill.body_cultivation_metal.description", "§6The lungs refine with every breath, each strike shaving away weakness and leaving something sharper behind.");
        // Body cultivation — T2
        add("ascension.skill.body_cultivation_wood_fire", "§6Scorched Root Tempering");
        add("ascension.skill.body_cultivation_wood_fire.description", "§6Liver feeds heart, and heart answers with flame. Growth and fire take hold together, the body enduring without withering.");
        add("ascension.skill.body_cultivation_fire_earth", "§6Forge Foundation Tempering");
        add("ascension.skill.body_cultivation_fire_earth.description", "§6The heart burns while the spleen holds firm beneath it. Heat settles into flesh that does not give, thickening under each strike.");
        add("ascension.skill.body_cultivation_earth_metal", "§6Mountain Iron Refinement");
        add("ascension.skill.body_cultivation_earth_metal.description", "§6Spleen anchors as lungs compress. Each blow is taken in and condensed, the body growing dense and unyielding.");
        add("ascension.skill.body_cultivation_metal_water", "§6Tempered Current Refinement");
        add("ascension.skill.body_cultivation_metal_water.description", "§6The lungs refine as the kidneys carry away. What remains is cleaner and sharper, the body honed through loss and renewal.");
        add("ascension.skill.body_cultivation_water_wood", "§6Rising Root Cleansing");
        add("ascension.skill.body_cultivation_water_wood.description", "§6Kidneys nourish what the liver shapes. What sinks into the body returns as quiet strength, growing beneath the surface.");
        // Body cultivation — T3
        add("ascension.skill.body_cultivation_wood_fire_earth", "§6Blazing Cycle Tempering");
        add("ascension.skill.body_cultivation_wood_fire_earth.description", "§6Liver kindles the heart, and the flame settles into the spleen. Growth feeds fire, and fire is given form, layering strength with every trial.");
        add("ascension.skill.body_cultivation_fire_earth_metal", "§6Smelted Earth Refinement");
        add("ascension.skill.body_cultivation_fire_earth_metal.description", "§6The heart burns inward, held fast by the spleen, until the lungs refine what remains. The body becomes like ore — only what endures is left behind.");
        add("ascension.skill.body_cultivation_earth_metal_water", "§6Sunken Steel Hardening");
        add("ascension.skill.body_cultivation_earth_metal_water.description", "§6The spleen bears the weight as the lungs compress it, the kidneys drawing everything deeper. What is suffered no longer leaves — it settles within.");
        add("ascension.skill.body_cultivation_metal_water_wood", "§6Iron Growth Refinement");
        add("ascension.skill.body_cultivation_metal_water_wood.description", "§6The lungs sharpen, the kidneys carry, and the liver reshapes what flows through them. The body yields where needed and hardens where it must.");
        add("ascension.skill.body_cultivation_water_wood_fire", "§6Cycle of Renewal");
        add("ascension.skill.body_cultivation_water_wood_fire.description", "§6From kidney to liver to heart, the cycle turns without pause. What is damaged passes through it and returns transformed.");
        // Body cultivation — T4
        add("ascension.skill.body_cultivation_wood_fire_earth_metal", "§6Four Cycle Refinement");
        add("ascension.skill.body_cultivation_wood_fire_earth_metal.description", "§6It begins in the liver and passes forward — heart, spleen, then lung — each taking its turn. The body is carried through the sequence, each cycle leaving it harder to break.");
        add("ascension.skill.body_cultivation_fire_earth_metal_water", "§6Endless Forging");
        add("ascension.skill.body_cultivation_fire_earth_metal_water.description", "§6The heart ignites it, the spleen holds it, the lungs refine it, and the kidneys carry it onward. The body is forged again and again without rest.");
        add("ascension.skill.body_cultivation_earth_metal_water_wood", "§6Ancient Circuit Hardening");
        add("ascension.skill.body_cultivation_earth_metal_water_wood.description", "§6What the spleen endures, the lungs condense, the kidneys sink, and the liver reshapes. The body follows a rhythm it no longer resists.");
        add("ascension.skill.body_cultivation_metal_water_wood_fire", "§6Four Pillar Tempering");
        add("ascension.skill.body_cultivation_metal_water_wood_fire.description", "§6The lungs carve the edge, the kidneys bear the flow, the liver gives it form, and the heart drives it forward. Each force supports the others, holding the body together.");
        add("ascension.skill.body_cultivation_water_wood_fire_earth", "§6Flowing Foundation Tempering");
        add("ascension.skill.body_cultivation_water_wood_fire_earth.description", "§6The kidneys set it in motion, the liver raises it, the heart ignites it, and the spleen brings it to rest. Change flows cleanly, each phase reinforcing the next.");
        // Body cultivation — T5
        add("ascension.skill.body_cultivation", "§6Five Element Body Refinement");
        add("ascension.skill.body_cultivation.description", "§6The cycle closes — liver, heart, spleen, lung, kidney — each giving way to the next without interruption. Nothing is lost between them; every trial is carried forward and returned as strength.");



        //Debuff Passive Skills
        add("ascension.skill.blinded_senses_debuff", "Blinded Senses");
        add("ascension.skill.blinded_senses_debuff.description", "A fog veils the world from your sight, restricting and limiting you.");
        add("ascension.skill.cracked_meridians_debuff", "Cracked Meridians");
        add("ascension.skill.cracked_meridians_debuff.description", "As opposing energy floods your Meridians, they crack and break, weakening your body.");
        add("ascension.skill.paralyzed_body_debuff", "Paralyzed Body");
        add("ascension.skill.paralyzed_body_debuff.description", "An opposing energy floods your Body, halting your movement.");
        add("ascension.skill.venomous_meridians_debuff", "Venomous Meridians");
        add("ascension.skill.venomous_meridians_debuff.description", "As opposing energy floods your Meridians, corroding your flesh and blood.");
        add("ascension.skill.qi_devouring_parasite_debuff", "Qi Devouring Parasite");
        add("ascension.skill.qi_devouring_parasite_debuff.description", "Slowly Devours your internal energy until empty, Then it takes your life energy.");
        add("ascension.skill.corrosive_miasma_debuff", "Corrosive Poison");
        add("ascension.skill.corrosive_miasma_debuff.description", "Slowly drains your gear of durability.");
        add("ascension.skill.scorching_yang_debuff", "Scorching Yang Poison");
        add("ascension.skill.scorching_yang_debuff.description", "Scorches the target with Extreme Yang that bypasses Immunities!");
        add("ascension.skill.frost_silkworm_poison_debuff", "Frost Silkworm Poison");
        add("ascension.skill.frost_silkworm_poison_debuff.description", "A silkworm's frost seeps into your meridians, freezing Qi and body alike. Cannot be cleansed. Leads only to death.");
        add("ascension.skill.frost_silkworm_poison_temp_debuff", "Frost Silkworm Venom");
        add("ascension.skill.frost_silkworm_poison_temp_debuff.description", "A concentrated burst of silkworm frost. The cold builds quickly — flee before it reaches your core.");





        //Pill effects
        add("ascension.pill_effects.body_cultivation_pill_effect.name","Body Cultivation");
        add("ascension.pill_effects.body_cultivation_pill_effect.description","Increases Body cultivation");

        add("ascension.pill_effects.essence_cultivation_pill_effect.name","Essence Cultivation");
        add("ascension.pill_effects.essence_cultivation_pill_effect.description","Increases Essence cultivation");

        add("ascension.pill_effects.soul_cultivation_pill_effect.name","Soul Cultivation");
        add("ascension.pill_effects.soul_cultivation_pill_effect.description","Increases Soul cultivation");

        add("ascension.pill_effects.antidote_pill_effect.name","Antidote");
        add("ascension.pill_effects.antidote_pill_effect.description","Cures Parasites");

        add("ascension.pill_effects.parasite_pill_effect.name","Parasite");
        add("ascension.pill_effects.parasite_pill_effect.description","Infects targets with a qi devouring parasite");

        add("ascension.pill_effects.qi_enhanced_regen_effect.name","Qi Enhanced Regeneration");
        add("ascension.pill_effects.qi_enhanced_regen_effect.description","Regeneration enhanced with Qi");




        //Commands
        add("command.ascension.cultivation.info.header", "=== Cultivation Info for %s ===");
        add("command.ascension.cultivation.info.path", "%s: Realm %d.%d");
        add("command.ascension.cultivation.info.progress", "  Progress: %s");
        add("command.ascension.cultivation.info.technique", "  Technique: %s");
        add("command.ascension.cultivation.info.cultivating.yes", "  Status: Cultivating");
        add("command.ascension.cultivation.info.cultivating.no", "  Status: Idle");
        add("command.ascension.cultivation.info.no_paths", "No active cultivation paths.");
        add("command.ascension.cultivation.info.physique", "Physique: %s");
        add("command.ascension.cultivation.info.path_header", "%s — %s %d.%d");

        add("command.ascension.reset.all", "Your Ascension data was reset.");
        add("command.ascension.reset.attributes", "Your Ascension attributes were reset.");
        add("command.ascension.reset.attributes.no_form", "Cannot reset Ascension attributes: no active form.");
        add("command.ascension.reset.paths", "Your cultivation paths were reset.");
        add("command.ascension.reset.error.invalid_path", "Invalid path: %s");
        add("command.ascension.reset.error.player_no_path", "%s does not have path %s");
        add("command.ascension.reset.path", "Your path was reset: %s");
        add("command.ascension.reset.skills", "Your skills were reset.");
        add("command.ascension.reset.techniques", "Your techniques were reset.");
        add("command.ascension.reset.error.player_no_technique", "%s has no technique for path %s");
        add("command.ascension.reset.technique", "Your technique was reset for path: %s");
        add("command.ascension.reset.physique", "Your physique was reset.");
        add("command.ascension.reset.count.all", "Reset all Ascension data for %s player(s).");
        add("command.ascension.reset.count.attributes", "Reset attributes for %s player(s).");
        add("command.ascension.reset.count.paths", "Reset paths for %s player(s).");
        add("command.ascension.reset.count.path", "Reset path %s for %s player(s).");
        add("command.ascension.reset.count.skills", "Reset skills for %s player(s).");
        add("command.ascension.reset.count.techniques", "Reset techniques for %s player(s).");
        add("command.ascension.reset.count.technique", "Reset technique for path %s for %s player(s).");
        add("command.ascension.reset.count.physique", "Reset physique for %s player(s).");



        //Menus
        add("container.ascension.spatial_stones", "§5Spatial Stones");
        add("container.ascension.other_upgrades", "§6Upgrades");



        //Advancements
        add("advancements.ascension.root.install", "Ascension Loaded");
        add("advancements.ascension.root.welcome", "Welcome to the world of Ascension! We hope you will cultivate to immortality!");



        //Death msgs
        add("death.attack.pill_residue", "%1$s finally learned not to eat Pill Residue leftover in the Pill Cauldron");



        //Tools and Swords and Armor
        add("item.ascension.wooden_blade", "Wooden Blade");
        add("item.ascension.stone_blade", "Stone Blade");
        add("item.ascension.iron_blade", "Iron Blade");
        add("item.ascension.gold_blade", "Gold Blade");
        add("item.ascension.diamond_blade", "Diamond Blade");
        add("item.ascension.netherite_blade", "Netherite Blade");

        add("item.ascension.wooden_spear", "Wooden Spear");
        add("item.ascension.stone_spear", "Stone Spear");
        add("item.ascension.iron_spear", "Iron Spear");
        add("item.ascension.gold_spear", "Gold Spear");
        add("item.ascension.diamond_spear", "Diamond Spear");
        add("item.ascension.netherite_spear", "Netherite Spear");

        add("item.ascension.spiritual_stone_pickaxe", "Spiritual Stone Pickaxe");
        add("item.ascension.spiritual_stone_axe", "Spiritual Stone Axe");
        add("item.ascension.spiritual_stone_shovel", "Spiritual Stone Shovel");
        add("item.ascension.spiritual_stone_hoe", "Spiritual Stone Hoe");

        add("item.ascension.fan", "Fan");


        // Needles
        add("item.ascension.silver_needle", "Silver Needle");

        // Jade Needles — tooltip
        add("tooltip.ascension.needle_plain", "A sleek hidden weapon. Can be tipped with a coating.");
        add("tooltip.ascension.needle_tipped", "Coated: %s");
        //Needle Poison Coat
        add("needle_effect.ascension.needle_poison", "Poison");
        add("needle_effect.ascension.needle_blindness", "Blindness");
        add("needle_effect.ascension.needle_slowness", "Slowness");
        add("needle_effect.ascension.needle_weakness", "Weakness");


        add("needle_effect.ascension.needle_qi_devouring_poison", "Qi Devouring Parasite");
        add("needle_effect.ascension.needle_cracked_meridians", "Cracked Meridians");
        add("needle_effect.ascension.needle_blinded_senses", "Blinded Senses");
        add("needle_effect.ascension.needle_paralyzed_body", "Paralyzed Body");
        add("needle_effect.ascension.needle_venomous_meridians", "Venomous Meridians");
        add("needle_effect.ascension.needle_corrosive_poison", "Corrosive Poison");
        add("needle_effect.ascension.needle_scorching_yang_poison", "Scorching Yang Poison");
        add("needle_effect.ascension.needle_frost_silkworm_poison_temp", "Frost Silkworm Venom");
        add("needle_effect.ascension.needle_frost_silkworm_poison", "Frost Silkworm Poison");




        //Misc
        add("item.ascension.spiritual_stone", "Spiritual Stone");
        add("item.ascension.spatial_stone_tier_1", "Spatial Stone I");
        add("item.ascension.spatial_stone_tier_2", "Spatial Stone II");
        add("item.ascension.raw_black_iron", "Raw Black Iron");
        add("item.ascension.black_iron_ingot", "Black Iron Ingot");
        add("item.ascension.black_iron_nugget", "Black Iron Nugget");

        add("item.ascension.raw_frost_silver", "Raw Frost Silver");
        add("item.ascension.frost_silver_ingot", "Frost Silver Ingot");
        add("item.ascension.frost_silver_nugget", "Frost Silver Nugget");

        add("item.ascension.jade", "Jade");
        add("item.ascension.jade_nugget", "Jade Nugget");
        add("item.ascension.ascension_icon", "Ascension Icon");
        add("item.ascension.undead_core", "Undead Core");
        add("item.ascension.living_core", "Living Core");


        //Spiritual Fires
        add("item.ascension.crimson_lotus_flame", "Crimson Lotus Flame");
        add("item.ascension.flame", "Flame");
        add("item.ascension.soul_flame", "Soul Flame");


        add("item.ascension.rat_spawn_egg", "Treasure Rat Spawn Egg");

        //Mobs
        add("entity.ascension.treasure_rat", "Treasure Rat");


        //Pills
        add("item.ascension.pill_residue", "Pill Residue");
        add("item.ascension.regeneration_pill", "Regeneration Pill");
        add("item.ascension.neutrality_pill", "Neutrality Pill");
        add("item.ascension.rebirth_pill", "Rebirth Pill");
        add("item.ascension.body_amnesia_pill", "Body Amnesia Pill");
        add("item.ascension.antidote_pill_qdppill", "Antidote Pill");
        add("item.ascension.antidote_pill_t2", "Antidote Pill");
        add("item.ascension.antidote_pill_t3", "Antidote Pill");
        add("item.ascension.fasting_pill_t1", "Fasting Pill");
        add("item.ascension.fasting_pill_t2", "Fasting Pill");
        add("item.ascension.fasting_pill_t3", "Fasting Pill");
        add("item.ascension.cleansing_pill_t1", "Cleansing Pill");
        add("item.ascension.cleansing_pill_t2", "Cleansing Pill");
        add("item.ascension.cleansing_pill_t3", "Cleansing Pill");
        add("item.ascension.cleansing_pill_t4", "Cleansing Pill");

        add("item.ascension.inner_reinforcement_pill_t", "Inner Reinforcement Pill");
        add("item.ascension.essence_gathering_pill", "Essence Gathering Pill");
        add("item.ascension.soul_focus_pill", "Soul Focus Pill");




        //Technique Manual Stuff
        add("item.ascension.technique_binder", "Technique Binder");
        add("item.ascension.technique_page", "Technique Page");

        add("ascension.item.technique_page", "%s");
        add("ascension.item.technique_page.blank", "Blank Technique Page");

        add("ascension.binder.technique", "Technique: %s");
        add("ascension.binder.progress", "Progress: %s/%s");
        add("ascension.binder.empty", "Empty binder. Add technique pages.");

        add("ascension.toast.invalid_page", "Invalid Page");
        add("ascension.toast.no_manual_data", "No manual data found");
        add("ascension.toast.mismatch", "Mismatch");
        add("ascension.toast.different_technique", "Page is for a different technique");
        add("ascension.toast.duplicate_page", "Duplicate Page");
        add("ascension.toast.already_collected", "This chapter is already in the binder");
        add("ascension.toast.manual_complete", "Manual Complete!");
        add("ascension.toast.page_added", "%s Added (%s/%s)");

        add("ascension.chapter.bloodfeast_soul_refining_scripture.1", "Chapter 1: The Hunger");
        add("ascension.chapter.bloodfeast_soul_refining_scripture.2", "Chapter 2: First Blood");
        add("ascension.chapter.bloodfeast_soul_refining_scripture.3", "Chapter 3: The Feast");
        add("ascension.chapter.bloodfeast_soul_refining_scripture.4", "Chapter 4: Soul Devouring");
        add("ascension.chapter.bloodfeast_soul_refining_scripture.5", "Chapter 5: Crimson Ascension");
        add("ascension.chapter.bloodfeast_soul_refining_scripture.6", "Chapter 6: The Eternal Banquet");

        add("ascension.chapter.white_lightning_ten_stage_technique.1", "Stage 1: Awakening the Spark");
        add("ascension.chapter.white_lightning_ten_stage_technique.2", "Stage 2: Meridian Ignition");
        add("ascension.chapter.white_lightning_ten_stage_technique.3", "Stage 3: Tempering the Flesh");
        add("ascension.chapter.white_lightning_ten_stage_technique.4", "Stage 4: Bone Forging");
        add("ascension.chapter.white_lightning_ten_stage_technique.5", "Stage 5: The Conductor");
        add("ascension.chapter.white_lightning_ten_stage_technique.6", "Stage 6: Lightning Heart");
        add("ascension.chapter.white_lightning_ten_stage_technique.7", "Stage 7: White Flash");
        add("ascension.chapter.white_lightning_ten_stage_technique.8", "Stage 8: Thunderclap Vessel");
        add("ascension.chapter.white_lightning_ten_stage_technique.9", "Stage 9: Heaven's Wrath");
        add("ascension.chapter.white_lightning_ten_stage_technique.10", "Stage 10: The Fist King's Rebirth");


        //Tooltips
        add("ascension.tooltip.waste", "Waste");
        add("ascension.tooltip.medicinal", "Medicinal");
        add("ascension.tooltip.cultivation", "Cultivation");
        add("ascension.tooltip.poisonous", "Poisonous");
        add("ascension.tooltip.qdppill", "Qi Devouring Parasite Antidote");



        // BaseTeleportTalisman messages
        add("ascension.teleport.countdown", "§eTeleporting in %d seconds");
        add("ascension.teleport.cancelled", "§cTeleport cancelled: %s");
        add("ascension.teleport.cancel.movement", "§aMovement detected");
        add("ascension.teleport.cancel.damage", "§cDamage taken");
        add("ascension.teleport.cancel.no_item", "§aItem not found");
        add("ascension.teleport.failed.no_safe_location", "§cNo safe teleport location found!");

        add("item.ascension.death_recall_talisman.permanent", "Death Recall Talisman (Eternal)");
        add("item.ascension.death_recall_talisman.cooldown", "Death Recall Talisman [%s:%s]");
        add("ascension.deathrecall.no_location", "§cNo death location bound to this talisman");
        add("ascension.deathrecall.dimension_invalid", "§cThe death dimension is no longer accessible");
        add("ascension.deathrecall.success", "§aReturned to the site of your demise");
        add("ascension.deathrecall.tooltip.bound", "§4§oBound to Death Location");
        add("ascension.deathrecall.tooltip.unbound", "§7Will bind to death location automatically");
        add("ascension.deathrecall.tooltip.coords", "§cCoords: §7%s, %s, %s");
        add("ascension.deathrecall.tooltip.dimension", "§cDimension: §7%s");
        add("ascension.deathrecall.bind_message", "§5§oThe talisman absorbs the essence of your death...");
        add("ascension.deathrecall.bound_on_respawn", "§5Death Recall Talisman has bound to your demise...");

        add("ascension.soulsteadreturn.teleported", "§aTeleported to your spawn point!");
        add("ascension.teleport.success.random", "§aTeleported to a random location!");
        add("ascension.worldaxis.teleported", "§aTeleported to world spawn!");
        add("ascension.voidmarking.teleported", "§aTeleported to saved location!");
        add("item.ascension.soulstead_return_talisman.cooldown", "Soulstead Return Talisman §7(%dm %ds§7)");
        add("item.ascension.void_marking_talisman.cooldown", "Void Marking Talisman §7(%dm %ds§7)");
        add("item.ascension.world_axis_talisman.cooldown", "World Axis Talisman §7(%dm %ds§7)");


        // Soul Anchor Test
        add("item.ascension.soul_anchor_talisman", "Soul Anchor Talisman");
        add("ascension.tooltip.sat", "Single-use │ Pulls death drops to anchor");

        // In-world messages (action bar)
        add("ascension.soulanchor.anchor_set",          "§5Anchor bound to this location.");
        add("ascension.soulanchor.no_anchor",           "§7No anchor set. Shift-right-click to bind one.");
        add("ascension.soulanchor.anchor_info",         "§5Anchor: §7%d, %d, %d");
        add("ascension.soulanchor.already_used",        "§cThis talisman has already spent its soul.");
        add("ascension.soulanchor.dimension_invalid",   "§cAnchor dimension no longer exists — talisman failed.");
        add("ascension.soulanchor.activated",
                "§5Soul Anchor pulled §d%d §5item(s) to §7%d, %d, %d§5.");

        // Tooltip lines
        add("ascension.soulanchor.tooltip.anchored",    "§aAnchor Set");
        add("ascension.soulanchor.tooltip.coords",      "§7Location: %d, %d, %d");
        add("ascension.soulanchor.tooltip.dimension",   "§7Dimension: %s");
        add("ascension.soulanchor.tooltip.unset",       "§7No anchor set.");
        add("ascension.soulanchor.tooltip.hint",        "§8Shift-right-click to set anchor.");
        add("ascension.soulanchor.tooltip.consumed",    "§4§oSpent — this talisman is inert.");







        //Consumable Talismans
        add("ascension.tooltip.srtt1", "2.5k x 2.5k Range │ 60 min cooldown");
        add("ascension.tooltip.srtt2", "5k x 5k Range │ 40 min cooldown");
        add("ascension.tooltip.srtt3", "7.5k x 7.5k Range │ 20 min cooldown");
        add("ascension.tooltip.srt", "5 min cooldown");
        add("ascension.tooltip.wat", "5 min cooldown");
        add("ascension.tooltip.vmt", "30 sec cooldown");

        //Permanent Talismans
        add("ascension.tooltip.srtt1.permanent", "2.5k x 2.5k Range │ Permanent (Recharges)");
        add("ascension.tooltip.srtt2.permanent", "5k x 5k Range │ Permanent (Recharges)");
        add("ascension.tooltip.srtt3.permanent", "7.5k x 7.5k Range │ Permanent (Recharges)");
        add("ascension.tooltip.srt.permanent",   "Teleport to Spawn │ Permanent (Recharges)");
        add("ascension.tooltip.wat.permanent",   "Teleport to World Spawn │ Permanent (Recharges)");
        add("ascension.tooltip.vmt.permanent",   "Teleport to Saved Location │ Permanent (Recharges)");


        add("item.ascension.spatial_rupture_talisman_t1.cooldown", "Spatial Rupture Talisman §7(%dm %ds§7)");
        add("item.ascension.spatial_rupture_talisman_t2.cooldown", "Spatial Rupture Talisman §7(%dm %ds§7)");
        add("item.ascension.spatial_rupture_talisman_t3.cooldown", "Spatial Rupture Talisman §7(%dm %ds§7)");

        add("ascension.voidmarking.location_saved", "§aLocation saved!");
        add("ascension.voidmarking.no_location", "§cNo location saved! Shift-right-click to save current position.");
        add("ascension.voidmarking.dimension_invalid", "§cCannot teleport to saved dimension!");
        add("ascension.voidmarking.tooltip.saved", "§7Saved Location:");
        add("ascension.voidmarking.tooltip.coords", "§7X: %s §7Y: %s §7Z: %s");
        add("ascension.voidmarking.tooltip.dimension", "§7Dimension: %s");

        add("item.ascension.spatial_rupture_talisman_t1.permanent", "Permanent Spatial Rupture Talisman Lesser");
        add("item.ascension.spatial_rupture_talisman_t2.permanent", "Permanent Spatial Rupture Talisman Standard");
        add("item.ascension.spatial_rupture_talisman_t3.permanent", "Permanent Spatial Rupture Talisman Greater");
        add("item.ascension.soulstead_return_talisman.permanent", "Permanent Soulstead Return Talisman");
        add("item.ascension.world_axis_talisman.permanent", "Permanent World Axis Talisman");
        add("item.ascension.void_marking_talisman.permanent", "Permanent Void Marking Talisman");

        add("item.ascension.permanent_talisman.recharging", "Recharging: %s%% (Draining Qi...)");
        add("ascension.talisman.recharged", "%s is ready!");

        add("ascension.tablet.cooldown", "§cTablet is on cooldown!");
        add("ascension.tablet.human.cooldown", "§cHuman Tablet needs 20 seconds to recharge...");
        add("item.ascension.tablet_of_destruction_heaven.cooldown", "§cHeaven Tablet needs 5 seconds to recharge...");
        add("ascension.tablet.drop_blocks", "§7Drop Blocks: ");
        add("item.ascension.tablet_of_destruction_heaven.drop_blocks", "§7Drop Blocks: ");
        add("ascension.tablet.toggle_mode_info", "§8Press [M] to toggle mode");
        add("item.ascension.tablet_of_destruction_heaven.link_info", "§8Shift+Right-Click container to link");
        add("item.ascension.tablet.link_invalid", "§cInvalid container for linking!");
        add("item.ascension.tablet.unlink_success", "§aUnlinked from container!");
        add("item.ascension.tablet.link_success", "§aLinked to %s at [%d, %d, %d]");
        add("item.ascension.tablet_of_destruction_heaven.linked_to", "§7Linked to §f%s §7at %s");
        add("ascension.tooltip.tablet.coordinates", "§7X: %s, Y: %s, Z: %s");
        add("item.ascension.tablet_of_destruction_human", "Tablet of Destruction (Human Tier)");
        add("item.ascension.tablet_of_destruction_earth", "Tablet of Destruction (Earth Tier)");
        add("item.ascension.tablet_of_destruction_heaven", "Tablet of Destruction (Heaven Tier)");

        add("ascension.tooltip.rgb.jade_slip", "Formation Identification");
        add("ascension.tooltip.rgb.rebirth_warning", "Be Cautious This Pill Resets Everything!");



        //Poison Pills
        add("item.ascension.qi_devouring_parasite_pill", "Qi Devouring Parasite Pill");



        //Herbs
        add("item.ascension.golden_sun_leaf", "Golden Sun Leaf");
        add("item.ascension.ironwood_sprout", "Ironwood Sprout");
        add("item.ascension.white_jade_orchid", "White Jade Orchid");

        add("item.ascension.hundred_year_ginseng", "Hundred Year Ginseng");
        add("item.ascension.hundred_year_snow_ginseng", "Hundred Year Snow Ginseng");
        add("item.ascension.hundred_year_fire_ginseng", "Hundred Year Fire Ginseng");
        add("item.ascension.jade_bamboo_of_serenity", "Jade Bamboo of Serenity");

        add("item.ascension.peach", "Peach");

        add("block.ascension.hundred_year_ginseng_crop", "Hundred Year Ginseng");
        add("block.ascension.hundred_year_fire_ginseng_crop", "Hundred Year Fire Ginseng");
        add("block.ascension.hundred_year_snow_ginseng_crop", "Hundred Year Snow Ginseng");
        add("block.ascension.ironwood_sprout_crop", "Ironwood Sprout");
        add("block.ascension.white_jade_orchid_crop", "White Jade Orchid");



        //Decorational Blocks
        add("block.ascension.cushion_white", "White Cushion");
        add("block.ascension.cushion_light_gray", "Light Gray Cushion");
        add("block.ascension.cushion_gray", "Gray Cushion");
        add("block.ascension.cushion_black", "Black Cushion");
        add("block.ascension.cushion_brown", "Brown Cushion");
        add("block.ascension.cushion_red", "Red Cushion");
        add("block.ascension.cushion_orange", "Orange Cushion");
        add("block.ascension.cushion_yellow", "Yellow Cushion");
        add("block.ascension.cushion_lime", "Lime Cushion");
        add("block.ascension.cushion_green", "Green Cushion");
        add("block.ascension.cushion_cyan", "Cyan Cushion");
        add("block.ascension.cushion_light_blue", "Light Blue Cushion");
        add("block.ascension.cushion_blue", "Blue Cushion");
        add("block.ascension.cushion_purple", "Purple Cushion");
        add("block.ascension.cushion_magenta", "Magenta Cushion");
        add("block.ascension.cushion_pink", "Pink Cushion");



        //Blocks
        add("block.ascension.pill_cauldron", "Pill Cauldron");
        add("block.ascension.cauldron_pedestal", "Cauldron Pedestal");
        add("block.ascension.flame_stand", "Flame Stand");
        add("block.ascension.spirit_condenser", "Spirit Condenser");

        add("block.ascension.golden_palm_log", "Golden Palm Log");
        add("block.ascension.golden_palm_wood", "Golden Palm Wood");
        add("block.ascension.stripped_golden_palm_log", "Stripped Golden Palm Log");
        add("block.ascension.stripped_golden_palm_wood", "Stripped Golden Palm Wood");
        add("block.ascension.golden_palm_planks", "Golden Palm Planks");
        add("block.ascension.golden_palm_leaves", "Golden Palm Leaves");
        add("block.ascension.golden_palm_sapling", "Golden Palm Sapling");
        add("block.ascension.golden_palm_slab", "Golden Palm Slab");
        add("block.ascension.golden_palm_stairs", "Golden Palm Stair");
        add("block.ascension.golden_palm_button", "Golden Palm Button");
        add("block.ascension.golden_palm_pressure_plate", "Golden Palm Pressure Plate");
        add("block.ascension.golden_palm_fence", "Golden Palm Fence");
        add("block.ascension.golden_palm_fence_gate", "Golden Palm Fence Gate");
        add("block.ascension.golden_palm_door", "Golden Palm Door");
        add("block.ascension.golden_palm_trapdoor", "Golden Palm Trapdoor");
        add("block.ascension.ironwood_log", "Ironwood Log");
        add("block.ascension.ironwood_wood", "Ironwood Wood");
        add("block.ascension.stripped_ironwood_log", "Stripped Ironwood Log");
        add("block.ascension.stripped_ironwood_wood", "Stripped Ironwood Wood");
        add("block.ascension.ironwood_planks", "Ironwood Planks");
        add("block.ascension.ironwood_leaves", "Ironwood Leaves");
        add("block.ascension.ironwood_sapling", "Ironwood Sapling");
        add("block.ascension.ironwood_slab", "Ironwood Slab");
        add("block.ascension.ironwood_stairs", "Ironwood Stair");
        add("block.ascension.ironwood_button", "Ironwood Button");
        add("block.ascension.ironwood_pressure_plate", "Ironwood Pressure Plate");
        add("block.ascension.ironwood_fence", "Ironwood Fence");
        add("block.ascension.ironwood_fence_gate", "Ironwood Fence Gate");
        add("block.ascension.ironwood_door", "Ironwood Door");
        add("block.ascension.ironwood_trapdoor", "Ironwood Trapdoor");



        // Marble
        add("block.ascension.raw_marble", "Marble");
        add("block.ascension.polished_marble", "Polished Marble");
        add("block.ascension.marble_bricks", "Marble Bricks");
        add("block.ascension.marble_chiseled", "Chiseled Marble");
        add("block.ascension.marble_tiles", "Marble Tiles");
        add("block.ascension.marble_burned", "Black Marble");
        add("block.ascension.polished_burned_marble", "Polished Black Marble");
        add("block.ascension.marble_burned_bricks", "Black Marble Bricks");
        add("block.ascension.marble_burned_chiseled", "Black Chiseled Marble");
        add("block.ascension.marble_burned_tiles", "Black Marble Tiles");
        add("block.ascension.blue_marble", "Blue Marble");
        add("block.ascension.blue_marble_bricks", "Blue Marble Bricks");
        add("block.ascension.blue_marble_chiseled", "Chiseled Blue Marble");
        add("block.ascension.blue_marble_tiles", "Blue Marble Tiles");
        add("block.ascension.blue_polished_marble", "Polished Blue Marble");
        add("block.ascension.brown_marble", "Brown Marble");
        add("block.ascension.brown_marble_bricks", "Brown Marble Bricks");
        add("block.ascension.brown_marble_chiseled", "Chiseled Brown Marble");
        add("block.ascension.brown_marble_tiles", "Brown Marble Tiles");
        add("block.ascension.brown_polished_marble", "Polished Brown Marble");
        add("block.ascension.cyan_marble", "Cyan Marble");
        add("block.ascension.cyan_marble_bricks", "Cyan Marble Bricks");
        add("block.ascension.cyan_marble_chiseled", "Chiseled Cyan Marble");
        add("block.ascension.cyan_marble_tiles", "Cyan Marble Tiles");
        add("block.ascension.cyan_polished_marble", "Polished Cyan Marble");
        add("block.ascension.gray_marble", "Gray Marble");
        add("block.ascension.gray_marble_bricks", "Gray Marble Bricks");
        add("block.ascension.gray_marble_chiseled", "Chiseled Gray Marble");
        add("block.ascension.gray_marble_tiles", "Gray Marble Tiles");
        add("block.ascension.gray_polished_marble", "Polished Gray Marble");
        add("block.ascension.green_marble", "Green Marble");
        add("block.ascension.green_marble_bricks", "Green Marble Bricks");
        add("block.ascension.green_marble_chiseled", "Chiseled Green Marble");
        add("block.ascension.green_marble_tiles", "Green Marble Tiles");
        add("block.ascension.green_polished_marble", "Polished Green Marble");
        add("block.ascension.light_blue_marble", "Light Blue Marble");
        add("block.ascension.light_blue_marble_bricks", "Light Blue Marble Bricks");
        add("block.ascension.light_blue_marble_chiseled", "Chiseled Light Blue Marble");
        add("block.ascension.light_blue_marble_tiles", "Light Blue Marble Tiles");
        add("block.ascension.light_blue_polished_marble", "Polished Light Blue Marble");
        add("block.ascension.light_gray_marble", "Light Gray Marble");
        add("block.ascension.light_gray_marble_bricks", "Light Gray Marble Bricks");
        add("block.ascension.light_gray_marble_chiseled", "Chiseled Light Gray Marble");
        add("block.ascension.light_gray_marble_tiles", "Light Gray Marble Tiles");
        add("block.ascension.polished_light_gray_marble", "Polished Light Gray Marble");
        add("block.ascension.lime_marble", "Lime Marble");
        add("block.ascension.lime_marble_bricks", "Lime Marble Bricks");
        add("block.ascension.lime_marble_chiseled", "Chiseled Lime Marble");
        add("block.ascension.lime_marble_tiles", "Lime Marble Tiles");
        add("block.ascension.lime_polished_marble", "Polished Lime Marble");
        add("block.ascension.magenta_marble", "Magenta Marble");
        add("block.ascension.magenta_marble_bricks", "Magenta Marble Bricks");
        add("block.ascension.magenta_marble_chiseled", "Chiseled Magenta Marble");
        add("block.ascension.magenta_marble_tiles", "Magenta Marble Tiles");
        add("block.ascension.magenta_polished_marble", "Polished Magenta Marble");
        add("block.ascension.orange_marble", "Orange Marble");
        add("block.ascension.orange_marble_bricks", "Orange Marble Bricks");
        add("block.ascension.orange_marble_chiseled", "Chiseled Orange Marble");
        add("block.ascension.orange_marble_tiles", "Orange Marble Tiles");
        add("block.ascension.orange_polished_marble", "Polished Orange Marble");
        add("block.ascension.pink_marble", "Pink Marble");
        add("block.ascension.pink_marble_bricks", "Pink Marble Bricks");
        add("block.ascension.pink_marble_chiseled", "Chiseled Pink Marble");
        add("block.ascension.pink_marble_tiles", "Pink Marble Tiles");
        add("block.ascension.pink_polished_marble", "Polished Pink Marble");
        add("block.ascension.purple_marble", "Purple Marble");
        add("block.ascension.purple_marble_bricks", "Purple Marble Bricks");
        add("block.ascension.purple_marble_chiseled", "Chiseled Purple Marble");
        add("block.ascension.purple_marble_tiles", "Purple Marble Tiles");
        add("block.ascension.purple_polished_marble", "Polished Purple Marble");
        add("block.ascension.red_marble", "Red Marble");
        add("block.ascension.red_marble_bricks", "Red Marble Bricks");
        add("block.ascension.red_marble_chiseled", "Chiseled Red Marble");
        add("block.ascension.red_marble_tiles", "Red Marble Tiles");
        add("block.ascension.red_polished_marble", "Polished Red Marble");
        add("block.ascension.yellow_marble", "Yellow Marble");
        add("block.ascension.yellow_marble_bricks", "Yellow Marble Bricks");
        add("block.ascension.yellow_marble_chiseled", "Chiseled Yellow Marble");
        add("block.ascension.yellow_marble_tiles", "Yellow Marble Tiles");
        add("block.ascension.yellow_polished_marble", "Polished Yellow Marble");



        // Slabs
        add("block.ascension.marble_brick_slabs", "Marble Brick Slab");
        add("block.ascension.marble_tile_slabs", "Marble Tile Slab");
        add("block.ascension.burned_marble_brick_slabs", "Black Marble Brick Slab");
        add("block.ascension.burned_marble_tile_slabs", "Black Marble Tile Slab");
        add("block.ascension.blue_marble_brick_slabs", "Blue Marble Brick Slab");
        add("block.ascension.blue_marble_tile_slabs", "Blue Marble Tile Slab");
        add("block.ascension.brown_marble_brick_slabs", "Brown Marble Brick Slab");
        add("block.ascension.brown_marble_tile_slabs", "Brown Marble Tile Slab");
        add("block.ascension.cyan_marble_brick_slabs", "Cyan Marble Brick Slab");
        add("block.ascension.cyan_marble_tile_slabs", "Cyan Marble Tile Slab");
        add("block.ascension.gray_marble_brick_slabs", "Gray Marble Brick Slab");
        add("block.ascension.gray_marble_tile_slabs", "Gray Marble Tile Slab");
        add("block.ascension.green_marble_brick_slabs", "Green Marble Brick Slab");
        add("block.ascension.green_marble_tile_slabs", "Green Marble Tile Slab");
        add("block.ascension.light_blue_marble_brick_slabs", "Light Blue Marble Brick Slab");
        add("block.ascension.light_blue_marble_tile_slabs", "Light Blue Marble Tile Slab");
        add("block.ascension.light_gray_marble_brick_slabs", "Light Gray Marble Brick Slab");
        add("block.ascension.light_gray_marble_tile_slabs", "Light Gray Marble Tile Slab");
        add("block.ascension.lime_marble_brick_slabs", "Lime Marble Brick Slab");
        add("block.ascension.lime_marble_tile_slabs", "Lime Marble Tile Slab");
        add("block.ascension.magenta_marble_brick_slabs", "Magenta Marble Brick Slab");
        add("block.ascension.magenta_marble_tile_slabs", "Magenta Marble Tile Slab");
        add("block.ascension.orange_marble_brick_slabs", "Orange Marble Brick Slab");
        add("block.ascension.orange_marble_tile_slabs", "Orange Marble Tile Slab");
        add("block.ascension.pink_marble_brick_slabs", "Pink Marble Brick Slab");
        add("block.ascension.pink_marble_tile_slabs", "Pink Marble Tile Slab");
        add("block.ascension.purple_marble_brick_slabs", "Purple Marble Brick Slab");
        add("block.ascension.purple_marble_tile_slabs", "Purple Marble Tile Slab");
        add("block.ascension.red_marble_brick_slabs", "Red Marble Brick Slab");
        add("block.ascension.red_marble_tile_slabs", "Red Marble Tile Slab");
        add("block.ascension.yellow_marble_brick_slabs", "Yellow Marble Brick Slab");
        add("block.ascension.yellow_marble_tile_slabs", "Yellow Marble Tile Slab");

        /* Stairs */
        add("block.ascension.marble_brick_stairs", "Marble Brick Stair");
        add("block.ascension.marble_tile_stairs", "Marble Tile Stair");
        add("block.ascension.burned_marble_brick_stairs", "Black Marble Brick Stair");
        add("block.ascension.burned_marble_tile_stairs", "Black Marble Tile Stair");
        add("block.ascension.blue_marble_brick_stairs", "Blue Marble Brick Stairs");
        add("block.ascension.blue_marble_tile_stairs", "Blue Marble Tile Stairs");
        add("block.ascension.brown_marble_brick_stairs", "Brown Marble Brick Stairs");
        add("block.ascension.brown_marble_tile_stairs", "Brown Marble Tile Stairs");
        add("block.ascension.cyan_marble_brick_stairs", "Cyan Marble Brick Stairs");
        add("block.ascension.cyan_marble_tile_stairs", "Cyan Marble Tile Stairs");
        add("block.ascension.gray_marble_brick_stairs", "Gray Marble Brick Stairs");
        add("block.ascension.gray_marble_tile_stairs", "Gray Marble Tile Stairs");
        add("block.ascension.green_marble_brick_stairs", "Green Marble Brick Stairs");
        add("block.ascension.green_marble_tile_stairs", "Green Marble Tile Stairs");
        add("block.ascension.light_blue_marble_brick_stairs", "Light Blue Marble Brick Stairs");
        add("block.ascension.light_blue_marble_tile_stairs", "Light Blue Marble Tile Stairs");
        add("block.ascension.light_gray_marble_brick_stairs", "Light Gray Marble Brick Stairs");
        add("block.ascension.light_gray_marble_tile_stairs", "Light Gray Marble Tile Stairs");
        add("block.ascension.lime_marble_brick_stairs", "Lime Marble Brick Stairs");
        add("block.ascension.lime_marble_tile_stairs", "Lime Marble Tile Stairs");
        add("block.ascension.magenta_marble_brick_stairs", "Magenta Marble Brick Stairs");
        add("block.ascension.magenta_marble_tile_stairs", "Magenta Marble Tile Stairs");
        add("block.ascension.orange_marble_brick_stairs", "Orange Marble Brick Stairs");
        add("block.ascension.orange_marble_tile_stairs", "Orange Marble Tile Stairs");
        add("block.ascension.pink_marble_brick_stairs", "Pink Marble Brick Stairs");
        add("block.ascension.pink_marble_tile_stairs", "Pink Marble Tile Stairs");
        add("block.ascension.purple_marble_brick_stairs", "Purple Marble Brick Stairs");
        add("block.ascension.purple_marble_tile_stairs", "Purple Marble Tile Stairs");
        add("block.ascension.red_marble_brick_stairs", "Red Marble Brick Stairs");
        add("block.ascension.red_marble_tile_stairs", "Red Marble Tile Stairs");
        add("block.ascension.yellow_marble_brick_stairs", "Yellow Marble Brick Stairs");
        add("block.ascension.yellow_marble_tile_stairs", "Yellow Marble Tile Stairs");



        // Walls
        add("block.ascension.marble_brick_wall", "Marble Brick Wall");
        add("block.ascension.marble_tile_wall", "Marble Tile Wall");
        add("block.ascension.burned_marble_brick_wall", "Black Marble Brick Wall");
        add("block.ascension.burned_marble_tile_wall", "Black Marble Tile Wall");
        add("block.ascension.blue_marble_brick_wall", "Blue Marble Brick Wall");
        add("block.ascension.blue_marble_tile_wall", "Blue Marble Tile Wall");
        add("block.ascension.brown_marble_brick_wall", "Brown Marble Brick Wall");
        add("block.ascension.brown_marble_tile_wall", "Brown Marble Tile Wall");
        add("block.ascension.cyan_marble_brick_wall", "Cyan Marble Brick Wall");
        add("block.ascension.cyan_marble_tile_wall", "Cyan Marble Tile Wall");
        add("block.ascension.gray_marble_brick_wall", "Gray Marble Brick Wall");
        add("block.ascension.gray_marble_tile_wall", "Gray Marble Tile Wall");
        add("block.ascension.green_marble_brick_wall", "Green Marble Brick Wall");
        add("block.ascension.green_marble_tile_wall", "Green Marble Tile Wall");
        add("block.ascension.light_blue_marble_brick_wall", "Light Blue Marble Brick Wall");
        add("block.ascension.light_blue_marble_tile_wall", "Light Blue Marble Tile Wall");
        add("block.ascension.light_gray_marble_brick_wall", "Light Gray Marble Brick Wall");
        add("block.ascension.light_gray_marble_tile_wall", "Light Gray Marble Tile Wall");
        add("block.ascension.lime_marble_brick_wall", "Lime Marble Brick Wall");
        add("block.ascension.lime_marble_tile_wall", "Lime Marble Tile Wall");
        add("block.ascension.magenta_marble_brick_wall", "Magenta Marble Brick Wall");
        add("block.ascension.magenta_marble_tile_wall", "Magenta Marble Tile Wall");
        add("block.ascension.orange_marble_brick_wall", "Orange Marble Brick Wall");
        add("block.ascension.orange_marble_tile_wall", "Orange Marble Tile Wall");
        add("block.ascension.pink_marble_brick_wall", "Pink Marble Brick Wall");
        add("block.ascension.pink_marble_tile_wall", "Pink Marble Tile Wall");
        add("block.ascension.purple_marble_brick_wall", "Purple Marble Brick Wall");
        add("block.ascension.purple_marble_tile_wall", "Purple Marble Tile Wall");
        add("block.ascension.red_marble_brick_wall", "Red Marble Brick Wall");
        add("block.ascension.red_marble_tile_wall", "Red Marble Tile Wall");
        add("block.ascension.yellow_marble_brick_wall", "Yellow Marble Brick Wall");
        add("block.ascension.yellow_marble_tile_wall", "Yellow Marble Tile Wall");



        // Ore Blocks
        add("block.ascension.black_iron_ore", "Black Iron Ore");
        add("block.ascension.black_iron_block", "Black Iron Block");
        add("block.ascension.frost_silver_ore", "Frost Silver Ore");
        add("block.ascension.frost_silver_block", "Frost Silver Block");

        add("block.ascension.jade_ore", "Jade Ore");
        add("block.ascension.spirit_vein", "Spirit Vein");
        add("block.ascension.spiritual_stone_cluster", "Spiritual Stone Cluster");
        add("block.ascension.jade_block", "Jade Block");
        add("block.ascension.spiritual_stone_block", "Spiritual Stone Block");

        add("block.ascension.pill_cauldron_low_human", "Pill Cauldron");



        //Fires
        add("block.ascension.crimson_lotus_fire", "Crimson Lotus Fire");



        //Artifacts
        add("item.ascension.spatial_ring", "Spatial Ring");
        add("item.ascension.spirit_sealing_ring", "Spirit Sealing Ring");

        add("item.ascension.fire_gourd", "Gourd O Fire");
        add("item.ascension.spatial_rupture_talisman_t1", "Lesser Spatial Rupture Talisman");
        add("item.ascension.spatial_rupture_talisman_t2", "Standard Spatial Rupture Talisman");
        add("item.ascension.spatial_rupture_talisman_t3", "Greater Spatial Rupture Talisman");
        add("item.ascension.soulstead_return_talisman", "Soulstead Return Talisman");
        add("item.ascension.world_axis_talisman", "World Axis Talisman");
        add("item.ascension.void_marking_talisman", "Void-Marking Talisman");
        add("item.ascension.death_recall_talisman", "Death Recall Talisman");
        add("item.ascension.todh", "Tablet Of Destruction");
        add("item.ascension.tode", "Tablet Of Destruction");
        add("item.ascension.todhe", "Tablet Of Destruction");
        add("item.ascension.repair_slip", "Repair Slip");
        add("item.ascension.ender_pouch_gui", "Ender Pouch");
        add("item.ascension.ender_pouch", "Ender Pouch");



        //Crafting Ingredient
        add("item.ascension.talisman_paper", "Blank Talisman");



        //GUI & Other Stuff
        add("creativetab.ascension.items", "Ascension Items");
        add("creativetab.ascension.artifacts", "Ascension Artifacts");
        add("creativetab.ascension.tools", "Ascension Tools");
        add("creativetab.ascension.blocks", "Ascension Blocks");
        add("creativetab.ascension.herbs", "Ascension Herbs");
        add("creativetab.ascension.pills", "Ascension Pills");
        add("creativetab.ascension.manuals", "Ascension Manuals");



        //JADE
        add("tooltip.ascension.heat_level", "Heat: %s°C / %s°C");
        add("tooltip.ascension.progress", "Progress: %s%%");
        add("tooltip.ascension.time_remaining", "Time remaining: %ss");
        add("tooltip.ascension.finishing", "Finishing...");
        add("tooltip.ascension.output", "Output: %s");
        add("tooltip.ascension.inputs", "Inputs:");
        add("config.jade.plugin_ascension.pill_cauldron", "Pill Cauldron");
        add("config.jade.plugin_ascension.flame_stand", "Flame Stand");
        add("config.jade.plugin_ascension.cauldron_pedestal", "Cauldron Pedestal");



        //JEI
        add("jei.ascension.pill_cauldron_low_human", "Pill Crafting");



        //effects
        add("effect.ascension.qi_enhanced_regeneration", "Qi Enhanced Regen");
        add("effect.ascension.cleansing", "Cleansing");
        add("effect.ascension.neutrality", "Neutrality");
        add("effect.ascension.qi_devouring_parasite", "Devouring Parasite");
        add("effect.ascension.heart_demon", "Heart Demon");
        add("effect.ascension.heart_demon.minor_realm_decrease", "§cHeart Demon corrodes your cultivation! Minor realm regressed to %s");



        //controls
        add("category.ascension.cultivation", "Ascension");
        add("key.ascension.introspection", "Introspection");
        add("key.ascension.open_spatial_ring", "Open Spatial Ring");
        add("key.ascension.toggle_artifact_mode", "Toggle Mode");
        add("key.ascension.cast_skill", "Cast Skill");
        add("key.ascension.skill_wheel", "Skill Wheel");


        //CONFIG
        add("ascension.configuration.PillCauldron","Pill Cauldron");
        add("ascension.configuration.heat_items","Heat Items");
        add("ascension.configuration.heat_loss_interval","Heat Loss Interval");
        add("ascension.configuration.heat_loss_amount","Heat Loss Per Interval");
        add("ascension.configuration.max_heat","Max Heat");

        add("ascension.configuration.Artifacts","Artifacts");
        add("ascension.configuration.repairInterval","Repair Interval");
        add("ascension.configuration.repairAmount","Repair Amount");









        // Messages
        add("message.ascension.milk_denied","§cMilk buckets are not drinkable in this mod. Use antidote pills instead!");



    }
}