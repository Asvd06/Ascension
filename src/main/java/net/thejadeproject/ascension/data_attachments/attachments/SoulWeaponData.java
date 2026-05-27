package net.thejadeproject.ascension.data_attachments.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

public class SoulWeaponData {
    public static final Codec<SoulWeaponData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.BOOL.optionalFieldOf("bound", false).forGetter(d -> d.bound),
            Codec.STRING.optionalFieldOf("weapon_type", "").forGetter(d -> d.weaponType),
            Codec.INT.optionalFieldOf("current_grade", 0).forGetter(d -> d.currentGrade),
            Codec.INT.optionalFieldOf("current_tempering", 0).forGetter(d -> d.currentTempering),
            Codec.BOOL.optionalFieldOf("summoned", false).forGetter(d -> d.summoned),
            Codec.INT.optionalFieldOf("lifetime_marks", 0).forGetter(d -> d.lifetimeMarks),
            Codec.INT.optionalFieldOf("last_soul_major", 0).forGetter(d -> d.lastSoulMajor),
            Codec.INT.optionalFieldOf("last_soul_minor", 0).forGetter(d -> d.lastSoulMinor),
            ItemStack.OPTIONAL_CODEC.optionalFieldOf("stored_weapon", ItemStack.EMPTY)
                    .forGetter(d -> d.storedWeapon)
    ).apply(inst, SoulWeaponData::new));

    public boolean bound;
    public String weaponType;
    public int currentGrade;
    public int currentTempering;
    public boolean summoned;
    public int lifetimeMarks;
    public int lastSoulMajor;
    public int lastSoulMinor;
    public ItemStack storedWeapon;

    public SoulWeaponData(
            boolean bound,
            String weaponType,
            int currentGrade,
            int currentTempering,
            boolean summoned,
            int lifetimeMarks,
            int lastSoulMajor,
            int lastSoulMinor,
            ItemStack storedWeapon
    ) {
        this.bound = bound;
        this.weaponType = weaponType;
        this.currentGrade = currentGrade;
        this.currentTempering = currentTempering;
        this.summoned = summoned;
        this.lifetimeMarks = lifetimeMarks;
        this.lastSoulMajor = lastSoulMajor;
        this.lastSoulMinor = lastSoulMinor;
        this.storedWeapon = storedWeapon;
    }

    public SoulWeaponData() {
        this(false, "", 0, 0, false, 0, 0, 0, ItemStack.EMPTY);
    }

    public void clear() {
        bound = false;
        weaponType = "";
        currentGrade = 0;
        currentTempering = 0;
        summoned = false;
        lifetimeMarks = 0;
        lastSoulMajor = 0;
        lastSoulMinor = 0;
        storedWeapon = ItemStack.EMPTY;
    }
}