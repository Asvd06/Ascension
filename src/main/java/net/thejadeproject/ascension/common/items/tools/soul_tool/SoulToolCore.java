package net.thejadeproject.ascension.common.items.tools.soul_tool;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.thejadeproject.ascension.common.items.ModItems;
import net.thejadeproject.ascension.common.items.ModToolTiers;
import net.thejadeproject.ascension.util.ModTags;

import java.util.Locale;

public enum SoulToolCore {
    NONE(
            "none",
            0,
            0.0F,
            1.00D,
            1.00D
    ),

    BLACK_IRON(
            "black_iron",
            1,
            0.1F,
            1.00D,
            1.00D
    ),

    FROST_SILVER(
            "frost_silver",
            2,
            0.2F,
            1.12D,
            0.92D
    ),

    JADE(
            "jade",
            3,
            0.3F,
            1.25D,
            0.85D
    );

    public static final Codec<SoulToolCore> CODEC =
            Codec.STRING.xmap(SoulToolCore::fromId, SoulToolCore::id);

    private final String id;
    private final int rank;
    private final float modelPredicate;
    private final double speedMultiplier;
    private final double qiCostMultiplier;

    SoulToolCore(
            String id,
            int rank,
            float modelPredicate,
            double speedMultiplier,
            double qiCostMultiplier
    ) {
        this.id = id;
        this.rank = rank;
        this.modelPredicate = modelPredicate;
        this.speedMultiplier = speedMultiplier;
        this.qiCostMultiplier = qiCostMultiplier;
    }

    public String id() {
        return id;
    }

    public int rank() {
        return rank;
    }

    public float modelPredicate() {
        return modelPredicate;
    }

    public double speedMultiplier() {
        return speedMultiplier;
    }

    public double qiCostMultiplier() {
        return qiCostMultiplier;
    }

    public boolean isFormed() {
        return this != NONE;
    }

    public boolean canReplace(SoulToolCore current) {
        return this != NONE && rank > current.rank;
    }

    public Tier tier() {
        return switch (this) {
            case NONE, BLACK_IRON -> ModToolTiers.BLACK_IRON;
            case FROST_SILVER -> ModToolTiers.FROST_SILVER;
            case JADE -> ModToolTiers.JADE;
        };
    }

    public boolean matches(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        return switch (this) {
            case NONE -> false;
            case BLACK_IRON -> stack.is(ModTags.Items.INGOTS_BLACK_IRON);
            case FROST_SILVER -> stack.is(ModTags.Items.INGOTS_FROST_SILVER);
            case JADE -> stack.is(ModItems.JADE.get());
        };
    }

    public String translationKey() {
        return "ascension.soul_tool.core." + id;
    }

    public static SoulToolCore fromId(String id) {
        if (id == null || id.isBlank()) {
            return NONE;
        }

        String normalized = id.toLowerCase(Locale.ROOT);

        for (SoulToolCore core : values()) {
            if (core.id.equals(normalized)) {
                return core;
            }
        }

        return NONE;
    }

    public static SoulToolCore fromIngredient(ItemStack stack) {
        for (SoulToolCore core : values()) {
            if (core.matches(stack)) {
                return core;
            }
        }

        return NONE;
    }
}