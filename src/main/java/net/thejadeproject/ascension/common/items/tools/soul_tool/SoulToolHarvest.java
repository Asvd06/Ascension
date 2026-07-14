package net.thejadeproject.ascension.common.items.tools.soul_tool;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.thejadeproject.ascension.common.items.ModItems;

import java.util.Locale;

public enum SoulToolHarvest {
    NONE("none"),
    DIAMOND("diamond"),
    QUARTZ("quartz"),
    SPIRIT_STONE("spirit_stone");

    public static final Codec<SoulToolHarvest> CODEC =
            Codec.STRING.xmap(SoulToolHarvest::fromId, SoulToolHarvest::id);

    private final String id;

    SoulToolHarvest(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public AssimilationCategory category() {
        return AssimilationCategory.HARVEST;
    }

    public String translationKey() {
        return "ascension.soul_tool.harvest." + id;
    }

    public boolean matches(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        return switch (this) {
            case NONE -> false;
            case DIAMOND -> stack.is(Items.DIAMOND);
            case QUARTZ -> stack.is(Items.QUARTZ);
            case SPIRIT_STONE -> stack.is(ModItems.SPIRITUAL_STONE.get());
        };
    }

    public static SoulToolHarvest fromId(String id) {
        if (id == null || id.isBlank()) {
            return NONE;
        }

        String normalized = id.toLowerCase(Locale.ROOT);

        for (SoulToolHarvest value : values()) {
            if (value.id.equals(normalized)) {
                return value;
            }
        }

        return NONE;
    }

    public static SoulToolHarvest fromIngredient(ItemStack stack) {
        for (SoulToolHarvest value : values()) {
            if (value.matches(stack)) {
                return value;
            }
        }

        return NONE;
    }
}