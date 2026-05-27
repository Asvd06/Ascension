package net.thejadeproject.ascension.mob_cultivation.loot;

import net.minecraft.world.item.ItemStack;
import net.thejadeproject.ascension.datagen.loot.functions.SetRandomIntComponentFunction;
import net.thejadeproject.ascension.datagen.loot.functions.SetTechniqueManualFunction;
import net.thejadeproject.ascension.datagen.loot.functions.SetTechniquePageFunction;

import java.util.List;

public record RankLootTable(
        String realmId,
        int stage,
        float baseChance,
        List<RankLootEntry> entries
) {
    public record RankLootEntry(
            ItemStack stack,
            int weight,
            float quantityScale,
            int minCount,
            int maxCount,
            List<SetRandomIntComponentFunction> randomComponents,
            SetTechniquePageFunction techniquePageFunction,
            SetTechniqueManualFunction techniqueManualFunction
    ) {
        public static RankLootEntry of(ItemStack stack, int weight, float quantityScale, int minCount, int maxCount) {
            return new RankLootEntry(stack, weight, quantityScale, minCount, maxCount, null, null, null);
        }

        public static RankLootEntry ofPill(ItemStack stack, int weight, float quantityScale, int minCount, int maxCount, List<SetRandomIntComponentFunction> randomComponents) {
            return new RankLootEntry(stack, weight, quantityScale, minCount, maxCount, randomComponents, null, null);
        }

        public static RankLootEntry ofPage(ItemStack stack, int weight, float quantityScale, int minCount, int maxCount, SetTechniquePageFunction techniquePageFunction) {
            return new RankLootEntry(stack, weight, quantityScale, minCount, maxCount, null, techniquePageFunction, null);
        }

        public static RankLootEntry ofManual(ItemStack stack, int weight, float quantityScale, int minCount, int maxCount, SetTechniqueManualFunction techniqueManualFunction) {
            return new RankLootEntry(stack, weight, quantityScale, minCount, maxCount, null, null, techniqueManualFunction);
        }
    }
}