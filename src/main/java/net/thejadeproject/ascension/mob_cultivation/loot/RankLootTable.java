package net.thejadeproject.ascension.mob_cultivation.loot;

import net.minecraft.world.item.ItemStack;
import net.thejadeproject.ascension.datagen.loot.functions.SetRandomIntComponentFunction;
import net.thejadeproject.ascension.datagen.loot.functions.SetTechniquePageFunction;

import java.util.List;

/**
 * Defines loot drops for a specific cultivation rank.
 */
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
            SetTechniquePageFunction techniquePageFunction
    ) {
        // Compact canonical constructor for validation
        public RankLootEntry {
            if (randomComponents == null && techniquePageFunction == null) {
                // Standard entry — OK
            }
            // No validation needed, just ensure fields are set
        }

        // Static factory: Standard item entry
        public static RankLootEntry of(ItemStack stack, int weight, float quantityScale, int minCount, int maxCount) {
            return new RankLootEntry(stack, weight, quantityScale, minCount, maxCount, null, null);
        }

        // Static factory: Pill entry with random component builders
        public static RankLootEntry ofPill(ItemStack stack, int weight, float quantityScale, int minCount, int maxCount, List<SetRandomIntComponentFunction> randomComponents) {
            return new RankLootEntry(stack, weight, quantityScale, minCount, maxCount, randomComponents, null);
        }

        // Static factory: Technique page entry with SetTechniquePageFunction
        public static RankLootEntry ofPage(ItemStack stack, int weight, float quantityScale, int minCount, int maxCount, SetTechniquePageFunction techniquePageFunction) {
            return new RankLootEntry(stack, weight, quantityScale, minCount, maxCount, null, techniquePageFunction);
        }
    }
}