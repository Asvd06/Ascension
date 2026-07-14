package net.thejadeproject.ascension.common.items.tools.soul_tool;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.thejadeproject.ascension.common.items.ModItems;

import java.util.Locale;

public enum SoulToolSpirit {
    NONE("none"),
    EMERALD("emerald"),
    LAPIS("lapis"),
    LIVING_CORE("living_core");

    public static final Codec<SoulToolSpirit> CODEC =
            Codec.STRING.xmap(SoulToolSpirit::fromId, SoulToolSpirit::id);

    private final String id;

    SoulToolSpirit(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public AssimilationCategory category() {
        return AssimilationCategory.SPIRIT;
    }

    public String translationKey() {
        return "ascension.soul_tool.spirit." + id;
    }

    public boolean matches(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        return switch (this) {
            case NONE -> false;
            case EMERALD -> stack.is(Items.EMERALD);
            case LAPIS -> stack.is(Items.LAPIS_LAZULI);
            case LIVING_CORE -> stack.is(ModItems.LIVING_CORE.get());
        };
    }

    public static SoulToolSpirit fromId(String id) {
        if (id == null || id.isBlank()) {
            return NONE;
        }

        String normalized = id.toLowerCase(Locale.ROOT);

        for (SoulToolSpirit value : values()) {
            if (value.id.equals(normalized)) {
                return value;
            }
        }

        return NONE;
    }

    public static SoulToolSpirit fromIngredient(ItemStack stack) {
        for (SoulToolSpirit value : values()) {
            if (value.matches(stack)) {
                return value;
            }
        }

        return NONE;
    }
}