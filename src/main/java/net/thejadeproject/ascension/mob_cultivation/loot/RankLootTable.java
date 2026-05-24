package net.thejadeproject.ascension.mob_cultivation.loot;

import net.minecraft.world.item.ItemStack;
import net.thejadeproject.ascension.datagen.loot.functions.SetRandomIntComponentFunction;

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
            int minPage,     // -1 when unused
            int maxPage,     // -1 when unused
            List<SetRandomIntComponentFunction> randomComponents  // null when unused
    ) {
        // Standard item entry
        public RankLootEntry(ItemStack stack, int weight, float quantityScale, int minCount, int maxCount) {
            this(stack, weight, quantityScale, minCount, maxCount, -1, -1, null);
        }

        // Technique page entry
        public RankLootEntry(ItemStack stack, int weight, float quantityScale, int minCount, int maxCount, int minPage, int maxPage) {
            this(stack, weight, quantityScale, minCount, maxCount, minPage, maxPage, null);
        }

        // Pill entry with random component builders
        public RankLootEntry(ItemStack stack, int weight, float quantityScale, int minCount, int maxCount, List<SetRandomIntComponentFunction> randomComponents) {
            this(stack, weight, quantityScale, minCount, maxCount, -1, -1, randomComponents);
        }
    }
}