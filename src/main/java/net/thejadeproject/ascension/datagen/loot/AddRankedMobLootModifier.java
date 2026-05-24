package net.thejadeproject.ascension.datagen.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.common.items.ModItems;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.common.items.techniques.TechniquePageItem;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.datagen.loot.functions.SetRandomIntComponentFunction;
import net.thejadeproject.ascension.mob_cultivation.MobCultivationData;
import net.thejadeproject.ascension.mob_cultivation.MobCultivationList;
import net.thejadeproject.ascension.mob_cultivation.loot.RankLootRegistry;
import net.thejadeproject.ascension.mob_cultivation.loot.RankLootTable;

import java.util.List;

/**
 * Global loot modifier that adds cultivation rank-based drops to ANY mob.
 *
 * Works with vanilla mobs, modded mobs, anything that has a cultivation rank.
 * No per-mob hardcoding — fully mod compatible.
 */
public class AddRankedMobLootModifier extends LootModifier {

    public static final MapCodec<AddRankedMobLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
    codecStart(inst).apply(inst, AddRankedMobLootModifier::new)
    );

    public AddRankedMobLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!(context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof LivingEntity entity)) {
            return generatedLoot;
        }

        MobCultivationData data = entity.getData(ModAttachments.MOB_RANK);
        if (data == null || !data.isInitialized() || data.isUnranked()) {
            return generatedLoot;
        }

        RankLootTable table = RankLootRegistry.get(data.getRealmId(), data.getStage());
        if (table == null) {
            return generatedLoot;
        }

        if (context.getRandom().nextFloat() > table.baseChance()) {
            return generatedLoot;
        }

        // Roll 1-3 times based on realm depth
        int realmIndex = MobCultivationList.getRealmIndex(data.getRealmId());
        int rolls = 1 + (realmIndex / 2);
        rolls = Math.min(rolls, 3);

        List<RankLootTable.RankLootEntry> entries = table.entries();
        int totalWeight = entries.stream().mapToInt(RankLootTable.RankLootEntry::weight).sum();

        for (int i = 0; i < rolls; i++) {
            if (totalWeight <= 0) break;

            int roll = context.getRandom().nextInt(totalWeight);
            int running = 0;

            for (RankLootTable.RankLootEntry entry : entries) {
                running += entry.weight();
                if (roll < running) {
                    ItemStack drop = processDrop(entry, context, realmIndex, data.getStage());
                    if (!drop.isEmpty()) {
                        generatedLoot.add(drop);
                    }
                    break;
                }
            }
        }

        return generatedLoot;
    }

    private ItemStack processDrop(RankLootTable.RankLootEntry entry, LootContext context, int realmIndex, int stage) {
        ItemStack base = entry.stack().copy();

        // ── Technique Pages: apply SetTechniquePageFunction at drop time ──
        if (entry.techniquePageFunction() != null) {
            return entry.techniquePageFunction().apply(base, context);
        }

        // ── Pills: apply SetRandomIntComponentFunction builders at drop time ──
        if (entry.randomComponents() != null && !entry.randomComponents().isEmpty()) {
            for (SetRandomIntComponentFunction function : entry.randomComponents()) {
                base = function.apply(base, context);
            }
        }

        // ── Quantity scaling ──
        float scale = 1.0f + (realmIndex * 0.15f) + ((stage - 1) * 0.05f);
        scale *= entry.quantityScale();

        int count = entry.minCount() + context.getRandom().nextInt(
                Math.max(1, entry.maxCount() - entry.minCount() + 1)
        );
        count = (int) (count * scale);
        count = Math.max(1, Math.min(base.getMaxStackSize(), count));

        base.setCount(count);
        return base;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}