package net.thejadeproject.ascension.data_attachments.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PhysiqueAcquisitionCounters {

    public static class T1Counters {
        public static final Codec<T1Counters> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.INT.optionalFieldOf("fire_damage_hits",       0).forGetter(c -> c.fireDamageHits),
                Codec.INT.optionalFieldOf("water_drowning_hits",    0).forGetter(c -> c.waterDrowningHits),
                Codec.INT.optionalFieldOf("earth_blocks_mined",     0).forGetter(c -> c.earthBlocksMined),
                Codec.INT.optionalFieldOf("wood_logs_chopped",      0).forGetter(c -> c.woodLogsChopped),
                Codec.INT.optionalFieldOf("metal_ores_mined",       0).forGetter(c -> c.metalOresMined),
                Codec.INT.optionalFieldOf("sword_kills",            0).forGetter(c -> c.swordKills),
                Codec.INT.optionalFieldOf("fist_kills",             0).forGetter(c -> c.fistKills),
                Codec.INT.optionalFieldOf("spear_kills",            0).forGetter(c -> c.spearKills),
                Codec.INT.optionalFieldOf("bow_kills",              0).forGetter(c -> c.bowKills),
                Codec.INT.optionalFieldOf("distinct_weapon_types",  0).forGetter(c -> c.distinctWeaponKillTypes),
                Codec.INT.optionalFieldOf("axe_kills",              0).forGetter(c -> c.axeKills),
                Codec.INT.optionalFieldOf("warden_kills",           0).forGetter(c -> c.wardenKills),
                Codec.INT.optionalFieldOf("weak_soul_hits",         0).forGetter(c -> c.weakSoulHits),
                WeaponCounters.CODEC.optionalFieldOf("weapons", new WeaponCounters()).forGetter(c -> c.weapons)
        ).apply(inst, T1Counters::new));

        public int fireDamageHits;
        public int waterDrowningHits;
        public int earthBlocksMined;
        public int woodLogsChopped;
        public int metalOresMined;
        public int swordKills;
        public int fistKills;
        public int spearKills;
        public int bowKills;
        public int distinctWeaponKillTypes;
        public int axeKills;
        public int wardenKills;
        public int weakSoulHits;
        public WeaponCounters weapons;

        public T1Counters(int fireDamageHits, int waterDrowningHits, int earthBlocksMined,
                          int woodLogsChopped, int metalOresMined,
                          int swordKills, int fistKills, int spearKills, int bowKills,
                          int distinctWeaponKillTypes, int axeKills, int wardenKills,
                          int weakSoulHits, WeaponCounters weapons) {
            this.fireDamageHits          = fireDamageHits;
            this.waterDrowningHits       = waterDrowningHits;
            this.earthBlocksMined        = earthBlocksMined;
            this.woodLogsChopped         = woodLogsChopped;
            this.metalOresMined          = metalOresMined;
            this.swordKills              = swordKills;
            this.fistKills               = fistKills;
            this.spearKills              = spearKills;
            this.bowKills                = bowKills;
            this.distinctWeaponKillTypes = distinctWeaponKillTypes;
            this.axeKills                = axeKills;
            this.wardenKills             = wardenKills;
            this.weakSoulHits            = weakSoulHits;
            this.weapons = weapons;
        }

        public T1Counters() {
            this.weapons = new WeaponCounters();
        }
    }

    public static class T2Counters {
        public static final Codec<T2Counters> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.INT .optionalFieldOf("lightning_strikes",         0    ).forGetter(c -> c.lightningStrikesReceived),
                Codec.INT .optionalFieldOf("near_death_fire_hits",      0    ).forGetter(c -> c.nearDeathFireHits),
                Codec.INT .optionalFieldOf("flame_touched_near_death_hits", 0).forGetter(c -> c.flameTouchedNearDeathHits),
                Codec.INT .optionalFieldOf("poison_hits",               0    ).forGetter(c -> c.poisonHitsReceived),
                Codec.INT .optionalFieldOf("wither_magic_damage",       0    ).forGetter(c -> c.witherMagicDamageTotal),
                Codec.INT .optionalFieldOf("books_used",                0    ).forGetter(c -> c.booksUsed),
                Codec.INT .optionalFieldOf("soul_kills_low_hp",         0    ).forGetter(c -> c.soulKillsLowHp),
                Codec.INT .optionalFieldOf("deaths_this_window",        0    ).forGetter(c -> c.deathsThisWindow),
                Codec.LONG.optionalFieldOf("first_death_timestamp",     0L   ).forGetter(c -> c.firstDeathTimestamp),
                Codec.INT .optionalFieldOf("shield_blocks",             0    ).forGetter(c -> c.shieldBlocks),
                Codec.INT .optionalFieldOf("underground_blocks_mined",  0    ).forGetter(c -> c.undergroundBlocksMined),
                Codec.INT .optionalFieldOf("boss_kills_sword",          0    ).forGetter(c -> c.bossKillsSword),
                Codec.INT .optionalFieldOf("boss_kills_low_hp",         0    ).forGetter(c -> c.bossKillsLowHp),
                Codec.INT .optionalFieldOf("twisted_vessel_hits",       0    ).forGetter(c -> c.twistedVesselHits)
        ).apply(inst, T2Counters::new));

        public int     lightningStrikesReceived;
        public int     nearDeathFireHits;
        public int     flameTouchedNearDeathHits;
        public int     poisonHitsReceived;
        public int     witherMagicDamageTotal;
        public int     booksUsed;
        public int     soulKillsLowHp;
        public int     deathsThisWindow;
        public long    firstDeathTimestamp;
        public int     shieldBlocks;
        public int     undergroundBlocksMined;
        public int     bossKillsSword;
        public int     bossKillsLowHp;
        public int     twistedVesselHits;

        public T2Counters(int lightningStrikesReceived,
                          int nearDeathFireHits, int flameTouchedNearDeathHits, int poisonHitsReceived, int witherMagicDamageTotal,
                          int booksUsed, int soulKillsLowHp, int deathsThisWindow,
                          long firstDeathTimestamp, int shieldBlocks,
                          int undergroundBlocksMined, int bossKillsSword, int bossKillsLowHp,
                          int twistedVesselHits) {
            this.lightningStrikesReceived = lightningStrikesReceived;
            this.nearDeathFireHits        = nearDeathFireHits;
            this.flameTouchedNearDeathHits = flameTouchedNearDeathHits;
            this.poisonHitsReceived       = poisonHitsReceived;
            this.witherMagicDamageTotal   = witherMagicDamageTotal;
            this.booksUsed                = booksUsed;
            this.soulKillsLowHp           = soulKillsLowHp;
            this.deathsThisWindow         = deathsThisWindow;
            this.firstDeathTimestamp      = firstDeathTimestamp;
            this.shieldBlocks             = shieldBlocks;
            this.undergroundBlocksMined   = undergroundBlocksMined;
            this.bossKillsSword           = bossKillsSword;
            this.bossKillsLowHp           = bossKillsLowHp;
            this.twistedVesselHits        = twistedVesselHits;
        }

        public T2Counters() {}
    }

    public static class T3Counters {
        public static final Codec<T3Counters> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.BOOL.optionalFieldOf("lava_soak_complete",      false).forGetter(c -> c.lavaSoakComplete),
                Codec.LONG.optionalFieldOf("entered_lava_time",       0L   ).forGetter(c -> c.enteredLavaTime),
                Codec.INT .optionalFieldOf("poison_cycles_completed", 0    ).forGetter(c -> c.poisonCyclesCompleted)
        ).apply(inst, T3Counters::new));

        public boolean lavaSoakComplete;
        public long    enteredLavaTime;
        public int     poisonCyclesCompleted;

        public T3Counters(boolean lavaSoakComplete, long enteredLavaTime, int poisonCyclesCompleted) {
            this.lavaSoakComplete      = lavaSoakComplete;
            this.enteredLavaTime       = enteredLavaTime;
            this.poisonCyclesCompleted = poisonCyclesCompleted;
        }

        public T3Counters() {}
    }

    public static class WeaponCounters {
        public static final Codec<WeaponCounters> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.INT.optionalFieldOf("next_sword_apprentice_roll", 100)
                        .forGetter(c -> c.nextSwordApprenticeRoll),
                Codec.INT.optionalFieldOf("next_soul_sword_heart_roll", 200)
                        .forGetter(c -> c.nextSoulSwordHeartRoll),
                Codec.INT.optionalFieldOf("next_thin_sword_pulse_roll", 250)
                        .forGetter(c -> c.nextThinSwordPulseRoll),
                Codec.INT.optionalFieldOf("next_sword_bone_roll", 300)
                        .forGetter(c -> c.nextSwordBoneRoll),

                Codec.INT.optionalFieldOf("next_thuggish_form_roll", 80)
                        .forGetter(c -> c.nextThuggishFormRoll),
                Codec.INT.optionalFieldOf("next_bruised_knuckle_body_roll", 180)
                        .forGetter(c -> c.nextBruisedKnuckleBodyRoll),
                Codec.INT.optionalFieldOf("next_tyrant_body_roll", 200)
                        .forGetter(c -> c.nextTyrantBodyRoll),

                Codec.INT.optionalFieldOf("next_hardened_general_roll", 80)
                        .forGetter(c -> c.nextHardenedGeneralRoll),
                Codec.INT.optionalFieldOf("next_spear_soul_mark_roll", 180)
                        .forGetter(c -> c.nextSpearSoulMarkRoll),
                Codec.INT.optionalFieldOf("next_pointed_eyes_roll", 200)
                        .forGetter(c -> c.nextPointedEyesRoll)
        ).apply(inst, WeaponCounters::new));

        public int nextSwordApprenticeRoll;
        public int nextSoulSwordHeartRoll;
        public int nextThinSwordPulseRoll;
        public int nextSwordBoneRoll;

        public int nextThuggishFormRoll;
        public int nextBruisedKnuckleBodyRoll;
        public int nextTyrantBodyRoll;

        public int nextHardenedGeneralRoll;
        public int nextSpearSoulMarkRoll;
        public int nextPointedEyesRoll;

        public WeaponCounters(int nextSwordApprenticeRoll,
                              int nextSoulSwordHeartRoll,
                              int nextThinSwordPulseRoll,
                              int nextSwordBoneRoll,
                              int nextThuggishFormRoll,
                              int nextBruisedKnuckleBodyRoll,
                              int nextTyrantBodyRoll,
                              int nextHardenedGeneralRoll,
                              int nextSpearSoulMarkRoll,
                              int nextPointedEyesRoll) {
            this.nextSwordApprenticeRoll = nextSwordApprenticeRoll;
            this.nextSoulSwordHeartRoll = nextSoulSwordHeartRoll;
            this.nextThinSwordPulseRoll = nextThinSwordPulseRoll;
            this.nextSwordBoneRoll = nextSwordBoneRoll;

            this.nextThuggishFormRoll = nextThuggishFormRoll;
            this.nextBruisedKnuckleBodyRoll = nextBruisedKnuckleBodyRoll;
            this.nextTyrantBodyRoll = nextTyrantBodyRoll;

            this.nextHardenedGeneralRoll = nextHardenedGeneralRoll;
            this.nextSpearSoulMarkRoll = nextSpearSoulMarkRoll;
            this.nextPointedEyesRoll = nextPointedEyesRoll;
        }

        public WeaponCounters() {
            this(100, 200, 250, 300,
                    80, 180, 200,
                    80, 180, 200);
        }
    }

    public static final Codec<PhysiqueAcquisitionCounters> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            T1Counters.CODEC.optionalFieldOf("t1", new T1Counters()).forGetter(c -> c.t1),
            T2Counters.CODEC.optionalFieldOf("t2", new T2Counters()).forGetter(c -> c.t2),
            T3Counters.CODEC.optionalFieldOf("t3", new T3Counters()).forGetter(c -> c.t3)
    ).apply(inst, PhysiqueAcquisitionCounters::new));

    public T1Counters t1;
    public T2Counters t2;
    public T3Counters t3;

    public PhysiqueAcquisitionCounters(T1Counters t1, T2Counters t2, T3Counters t3) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    public PhysiqueAcquisitionCounters() {
        this.t1 = new T1Counters();
        this.t2 = new T2Counters();
        this.t3 = new T3Counters();
    }
}