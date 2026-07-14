package net.thejadeproject.ascension.common.items.tools.soul_tool;

import com.mojang.serialization.Codec;

import java.util.Locale;

public enum SoulToolType {
    PICKAXE("pickaxe"),
    SHOVEL("shovel"),
    HOE("hoe"),
    SHEARS("shears");

    public static final Codec<SoulToolType> CODEC =
            Codec.STRING.xmap(SoulToolType::fromId, SoulToolType::id);

    private final String id;

    SoulToolType(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public String translationKey() {
        return "ascension.soul_tool.form." + id;
    }

    public SoulToolType next() {
        return switch (this) {
            case PICKAXE -> SHOVEL;
            case SHOVEL -> HOE;
            case HOE -> SHEARS;
            case SHEARS -> PICKAXE;
        };
    }

    public static SoulToolType fromId(String id) {
        if (id == null || id.isBlank()) {
            return PICKAXE;
        }

        String normalized = id.toLowerCase(Locale.ROOT);

        for (SoulToolType type : values()) {
            if (type.id.equals(normalized)) {
                return type;
            }
        }

        return PICKAXE;
    }
}