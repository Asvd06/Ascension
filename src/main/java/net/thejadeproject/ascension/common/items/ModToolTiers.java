package net.thejadeproject.ascension.common.items;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import net.thejadeproject.ascension.util.ModTags;

public class ModToolTiers {

    public static final Tier BLACK_IRON = new SimpleTier(BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            2031, 9.0F, 4.0F, 15,
            () -> Ingredient.of(ModTags.Items.INGOTS_BLACK_IRON)
    );

    public static final Tier FROST_SILVER = new SimpleTier(ModTags.Blocks.INCORRECT_FOR_FROST_SILVER_TOOL,
            2400, 10.0F, 5.0F, 18,
            () -> Ingredient.of(ModTags.Items.INGOTS_FROST_SILVER)
    );


    public static final Tier JADE = new SimpleTier(ModTags.Blocks.INCORRECT_FOR_JADE_TOOL,
            2800, 11.0F, 6.0F, 22,
            () -> Ingredient.of(ModItems.JADE.get())
    );

    public static final Tier SPIRITUAL_STONE = new SimpleTier(ModTags.Blocks.INCORRECT_FOR_SPIRITUAL_STONE_TOOL,
            2048, 9.0F, 6.5F, 22,
            () -> Ingredient.EMPTY
    );

    private ModToolTiers() {
    }
}