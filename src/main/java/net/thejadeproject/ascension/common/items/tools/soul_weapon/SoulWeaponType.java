package net.thejadeproject.ascension.common.items.tools.soul_weapon;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.thejadeproject.ascension.common.items.ModItems;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.util.ModTags;

import java.util.function.Supplier;

public enum SoulWeaponType {
    AXE(
            "axe",
            ModTags.Items.SOULFORGE_AXES,
            ModPaths.AXE.getId(),
            ModItems.SOULBOUND_AXE
    ),
    BLADE(
            "blade",
            ModTags.Items.SOULFORGE_BLADES,
            ModPaths.BLADE.getId(),
            ModItems.SOULBOUND_BLADE
    ),
    SPEAR(
            "spear",
            ModTags.Items.SOULFORGE_SPEARS,
            ModPaths.SPEAR.getId(),
            ModItems.SOULBOUND_SPEAR
    ),
    MACE(
            "mace",
            ModTags.Items.SOULFORGE_MACES,
            ModPaths.MACE.getId(),
            ModItems.SOULBOUND_MACE
    ),
    SWORD(
            "sword",
            ModTags.Items.SOULFORGE_SWORDS,
            ModPaths.SWORD.getId(),
            ModItems.SOULBOUND_SWORD
    );

    private final String id;
    private final TagKey<Item> inputTag;
    private final ResourceLocation path;
    private final Supplier<? extends Item> soulboundItem;

    SoulWeaponType(
            String id,
            TagKey<Item> inputTag,
            ResourceLocation path,
            Supplier<? extends Item> soulboundItem
    ) {
        this.id = id;
        this.inputTag = inputTag;
        this.path = path;
        this.soulboundItem = soulboundItem;
    }

    public String id() {
        return id;
    }

    public ResourceLocation path() {
        return path;
    }

    public ItemStack createSoulboundStack() {
        return new ItemStack(soulboundItem.get());
    }

    public static SoulWeaponType fromStack(ItemStack stack) {
        if (stack.isEmpty()) return null;

        for (SoulWeaponType type : values()) {
            if (stack.is(type.inputTag)) {
                return type;
            }
        }

        return null;
    }

    public static SoulWeaponType fromId(String id) {
        for (SoulWeaponType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }

        return null;
    }
}