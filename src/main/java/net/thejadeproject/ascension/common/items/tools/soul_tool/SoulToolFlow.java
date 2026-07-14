package net.thejadeproject.ascension.common.items.tools.soul_tool;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.thejadeproject.ascension.common.items.ModItems;

import java.util.Locale;

public enum SoulToolFlow {
    NONE("none"),
    REDSTONE("redstone"),
    COPPER("copper"),
    UNDEAD_CORE("undead_core");

    public static final Codec<SoulToolFlow> CODEC =
            Codec.STRING.xmap(SoulToolFlow::fromId, SoulToolFlow::id);

    private final String id;

    SoulToolFlow(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public AssimilationCategory category() {
        return AssimilationCategory.FLOW;
    }

    public String translationKey() {
        return "ascension.soul_tool.flow." + id;
    }

    public boolean matches(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        return switch (this) {
            case NONE -> false;
            case REDSTONE -> stack.is(Items.REDSTONE);
            case COPPER -> stack.is(Tags.Items.INGOTS_COPPER);
            case UNDEAD_CORE -> stack.is(ModItems.UNDEAD_CORE.get());
        };
    }

    public static SoulToolFlow fromId(String id) {
        if (id == null || id.isBlank()) {
            return NONE;
        }

        String normalized = id.toLowerCase(Locale.ROOT);

        for (SoulToolFlow value : values()) {
            if (value.id.equals(normalized)) {
                return value;
            }
        }

        return NONE;
    }

    public static SoulToolFlow fromIngredient(ItemStack stack) {
        for (SoulToolFlow value : values()) {
            if (value.matches(stack)) {
                return value;
            }
        }

        return NONE;
    }
}