package net.thejadeproject.ascension.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.common.blocks.ModBlocks;
import net.thejadeproject.ascension.common.items.ModItems;
import net.thejadeproject.ascension.util.ModTags;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper){
        super(output, lookupProvider, blockTags, AscensionCraft.MOD_ID, existingFileHelper);
    }
    @Override
    protected void addTags(HolderLookup.Provider provider) {

        tag(ModTags.Items.HERBS)
                .add(ModItems.WHITE_JADE_ORCHID.get())
                .add(ModItems.HUNDRED_YEAR_FIRE_GINSENG.get())
                .add(ModItems.HUNDRED_YEAR_SNOW_GINSENG.get())
                .add(ModItems.HUNDRED_YEAR_GINSENG.get())
                .add(ModItems.JADE_DEW_GRASS.get());


        tag(ModTags.Items.MEDICINAL)
                .add(ModItems.GOLDEN_SUN_LEAF.get())
                .add(ModItems.WHITE_JADE_ORCHID.get())
                .add(ModItems.JADE_BAMBOO_OF_SERENITY.get())
                .add(ModItems.HUNDRED_YEAR_FIRE_GINSENG.get())
                .add(ModItems.HUNDRED_YEAR_SNOW_GINSENG.get())
                .add(ModItems.HUNDRED_YEAR_GINSENG.get())
                .add(ModItems.IRONWOOD_SPROUT.get());
        this.tag(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.GOLDEN_PALM_LOG.get().asItem())
                .add(ModBlocks.GOLDEN_PALM_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_GOLDEN_PALM_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_GOLDEN_PALM_WOOD.get().asItem());

        this.tag(ItemTags.PLANKS)
                .add(ModBlocks.GOLDEN_PALM_PLANKS.asItem())
                .add(ModBlocks.IRONWOOD_PLANKS.asItem());


        tag(ItemTags.PICKAXES)
                .add(ModItems.SPIRITUAL_STONE_PICKAXE.get());
        tag(ItemTags.AXES)
                .add(ModItems.SPIRITUAL_STONE_AXE.get());
        tag(ItemTags.SHOVELS)
                .add(ModItems.SPIRITUAL_STONE_SHOVEL.get());
        tag(ItemTags.HOES)
                .add(ModItems.SPIRITUAL_STONE_HOE.get());


        tag(ModTags.Items.REPAIR_BLACKLIST)
                .add(ModItems.SOULSTEAD_RETURN_TALISMAN.get())
                .add(ModItems.WORLD_AXIS_TALISMAN.get())
                .add(ModItems.SPATIAL_RUPTURE_TALISMAN_T1.get())
                .add(ModItems.SPATIAL_RUPTURE_TALISMAN_T2.get())
                .add(ModItems.SPATIAL_RUPTURE_TALISMAN_T3.get())
                .add(ModItems.VOID_MARKING_TALISMAN.get());



        tag(ModTags.Items.WOOLABLE)
                .add(Items.WHITE_WOOL)
                .add(Items.LIGHT_GRAY_WOOL)
                .add(Items.GRAY_WOOL)
                .add(Items.BLACK_WOOL)
                .add(Items.BROWN_WOOL)
                .add(Items.RED_WOOL)
                .add(Items.ORANGE_WOOL)
                .add(Items.YELLOW_WOOL)
                .add(Items.LIME_WOOL)
                .add(Items.GREEN_WOOL)
                .add(Items.CYAN_WOOL)
                .add(Items.LIGHT_BLUE_WOOL)
                .add(Items.BLUE_WOOL)
                .add(Items.PURPLE_WOOL)
                .add(Items.MAGENTA_WOOL)
                .add(Items.PINK_WOOL);


        tag(ModTags.Items.FLAMES)
                .add(ModItems.FLAME.get())
                .add(ModItems.SOUL_FLAME.get())
                .add(ModItems.CRIMSON_LOTUS_FLAME.get());

        tag(ItemTags.SWORDS)
                .add(ModItems.CULTIVATORS_SWORD_IRON.get())
                .add(ModItems.SOULBOUND_SWORD.get());

        tag(ModTags.Items.BLADE)
                .add(ModItems.WOODEN_BLADE.get())
                .add(ModItems.STONE_BLADE.get())
                .add(ModItems.IRON_BLADE.get())
                .add(ModItems.GOLD_BLADE.get())
                .add(ModItems.DIAMOND_BLADE.get())
                .add(ModItems.NETHERITE_BLADE.get())
                .add(ModItems.SOULBOUND_BLADE.get());

        tag(ModTags.Items.SPEAR)
                .add(ModItems.WOODEN_SPEAR.get())
                .add(ModItems.STONE_SPEAR.get())
                .add(ModItems.IRON_SPEAR.get())
                .add(ModItems.GOLD_SPEAR.get())
                .add(ModItems.DIAMOND_SPEAR.get())
                .add(ModItems.NETHERITE_SPEAR.get())
                .add(ModItems.SOULBOUND_SPEAR.get());

        tag(ModTags.Items.GAUNTLET)
                .add(ModItems.WOODEN_GAUNTLET.get())
                .add(ModItems.STONE_GAUNTLET.get())
                .add(ModItems.IRON_GAUNTLET.get())
                .add(ModItems.GOLD_GAUNTLET.get())
                .add(ModItems.DIAMOND_GAUNTLET.get())
                .add(ModItems.NETHERITE_GAUNTLET.get());

        tag(ItemTags.AXES)
                .add(ModItems.SOULBOUND_AXE.get());

        tag(ModTags.Items.MACE)
                .add(Items.MACE)
                .add(ModItems.SOULBOUND_MACE.get());

        tag(ModTags.Items.FIST)
                .add(ModItems.WOODEN_GAUNTLET.get())
                .add(ModItems.STONE_GAUNTLET.get())
                .add(ModItems.IRON_GAUNTLET.get())
                .add(ModItems.GOLD_GAUNTLET.get())
                .add(ModItems.DIAMOND_GAUNTLET.get())
                .add(ModItems.NETHERITE_GAUNTLET.get())
                //.add(ModItems.SOULBOUND_GAUNTLET.get())
        ;

        tag(ModTags.Items.SOULFORGE_SWORDS)
                .add(Items.WOODEN_SWORD)
                .add(Items.STONE_SWORD)
                .add(Items.IRON_SWORD)
                .add(Items.GOLDEN_SWORD)
                .add(Items.DIAMOND_SWORD)
                .add(Items.NETHERITE_SWORD)
                .add(ModItems.CULTIVATORS_SWORD_IRON.get());

        tag(ModTags.Items.SOULFORGE_BLADES)
                .add(ModItems.WOODEN_BLADE.get())
                .add(ModItems.STONE_BLADE.get())
                .add(ModItems.IRON_BLADE.get())
                .add(ModItems.GOLD_BLADE.get())
                .add(ModItems.DIAMOND_BLADE.get())
                .add(ModItems.NETHERITE_BLADE.get());

        tag(ModTags.Items.SOULFORGE_SPEARS)
                .add(ModItems.WOODEN_SPEAR.get())
                .add(ModItems.STONE_SPEAR.get())
                .add(ModItems.IRON_SPEAR.get())
                .add(ModItems.GOLD_SPEAR.get())
                .add(ModItems.DIAMOND_SPEAR.get())
                .add(ModItems.NETHERITE_SPEAR.get());

        tag(ModTags.Items.SOULFORGE_AXES)
                .add(Items.WOODEN_AXE)
                .add(Items.STONE_AXE)
                .add(Items.IRON_AXE)
                .add(Items.GOLDEN_AXE)
                .add(Items.DIAMOND_AXE)
                .add(Items.NETHERITE_AXE);

        tag(ModTags.Items.SOULFORGE_MACES)
                .add(Items.MACE);

        tag(ItemTags.SWORD_ENCHANTABLE)
                .addTag(ItemTags.SWORDS)
                .addTag(ModTags.Items.BLADE)
                .addTag(ModTags.Items.SPEAR);

        tag(ItemTags.SHARP_WEAPON_ENCHANTABLE)
                .addTag(ItemTags.SWORDS)
                .addTag(ModTags.Items.BLADE)
                .addTag(ModTags.Items.SPEAR)
                .addTag(ModTags.Items.GAUNTLET)
                .add(ModItems.SOULBOUND_AXE.get());

        tag(ItemTags.WEAPON_ENCHANTABLE)
                .addTag(ItemTags.SWORDS)
                .addTag(ModTags.Items.BLADE)
                .addTag(ModTags.Items.SPEAR)
                .addTag(ModTags.Items.GAUNTLET)
                .add(ModItems.SOULBOUND_AXE.get())
                .add(ModItems.SOULBOUND_MACE.get());

        tag(ItemTags.FIRE_ASPECT_ENCHANTABLE)
                .addTag(ItemTags.SWORDS)
                .addTag(ModTags.Items.BLADE)
                .addTag(ModTags.Items.SPEAR);

        tag(ItemTags.MACE_ENCHANTABLE)
                .add(ModItems.SOULBOUND_MACE.get());


        tag(ItemTags.DURABILITY_ENCHANTABLE)
                .addTag(ModTags.Items.BLADE)
                .addTag(ModTags.Items.SPEAR)
                .addTag(ModTags.Items.GAUNTLET);

        tag(ItemTags.BREAKS_DECORATED_POTS)
                .addTag(ModTags.Items.BLADE)
                .addTag(ModTags.Items.SPEAR);


        tag(ModTags.Items.INGOTS_BLACK_IRON)
                .add(ModItems.BLACK_IRON_INGOT.get());
        tag(ModTags.Items.BLACK_IRON_RAW)
                .add(ModItems.RAW_BLACK_IRON.get());
        tag(ModTags.Items.NUGGETS_BLACK_IRON)
                .add(ModItems.BLACK_IRON_NUGGET.get());

        tag(ModTags.Items.INGOTS_FROST_SILVER)
                .add(ModItems.FROST_SILVER_INGOT.get());

        tag(ModTags.Items.FROST_SILVER_RAW).
                add(ModItems.RAW_FROST_SILVER.get());

        tag(ModTags.Items.NUGGETS_FROST_SILVER)
                .add(ModItems.FROST_SILVER_NUGGET.get());



        tag(ModTags.Items.CURIOS_RING).add(ModItems.SPATIAL_RING.get());

    }
}
