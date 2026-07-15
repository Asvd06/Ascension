package net.thejadeproject.ascension.common.items.data_components.herb_pouch;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.common.items.herbs.HerbQuality;
import net.thejadeproject.ascension.refactor_packages.util.ByteBufUtil;
import net.thejadeproject.ascension.util.ModTags;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record HerbPouchComponent(int capacity, List<ItemStack> herbs) {
    public static final int DEFAULT_CAPACITY = 54 * 64;

    public HerbPouchComponent(int capacity) {
        this(capacity, List.of());
    }

    public HerbPouchComponent {
        capacity = Math.max(0, capacity);

        List<ItemStack> copied = new ArrayList<>();

        for (ItemStack stack : herbs) {
            if (!stack.isEmpty()) {
                copied.add(stack.copy());
            }
        }

        herbs = List.copyOf(copied);
    }

    public int getTotalCount() {
        int total = 0;

        for (ItemStack stack : herbs) {
            total += stack.getCount();
        }

        return total;
    }

    public int getRemainingCapacity() {
        return Math.max(0, capacity - getTotalCount());
    }

    // ── Insertion ─────────────────────────────────────────────────

    public InsertResult insert(ItemStack input) {
        if (input.isEmpty() || !input.is(ModTags.Items.HERBS)) {
            return new InsertResult(this, input.copy());
        }

        int space = getRemainingCapacity();

        if (space <= 0) {
            return new InsertResult(this, input.copy());
        }

        int toInsert = Math.min(space, input.getCount());

        List<ItemStack> newHerbs = copyStoredHerbs();

        ItemStack inserted = input.copy();
        inserted.setCount(toInsert);

        for (ItemStack stored : newHerbs) {
            if (!ItemStack.isSameItemSameComponents(stored, inserted)) {
                continue;
            }

            int room = stored.getMaxStackSize() - stored.getCount();
            int move = Math.min(room, inserted.getCount());

            if (move > 0) {
                stored.grow(move);
                inserted.shrink(move);
            }

            if (inserted.isEmpty()) {
                break;
            }
        }

        if (!inserted.isEmpty()) {
            newHerbs.add(inserted);
        }

        ItemStack remainder = input.copy();
        remainder.shrink(toInsert);

        return new InsertResult(new HerbPouchComponent(capacity, newHerbs), remainder);
    }

    // ── Single-item extraction ────────────────────────────────────

    public ExtractResult extractOneByAgeGroup(ItemStack clickedSummary, boolean fromEnd) {
        if (clickedSummary.isEmpty()) {
            return new ExtractResult(
                    this,
                    ItemStack.EMPTY
            );
        }

        List<ItemStack> newHerbs = copyStoredHerbs();

        int selectedIndex = -1;

        if (fromEnd) {
            for (int i = newHerbs.size() - 1; i >= 0; i--) {
                if (matchesAgeGroup(newHerbs.get(i), clickedSummary)) {
                    selectedIndex = i;
                    break;
                }
            }
        } else {
            for (int i = 0; i < newHerbs.size(); i++) {
                if (matchesAgeGroup(newHerbs.get(i), clickedSummary)) {
                    selectedIndex = i;
                    break;
                }
            }
        }

        if (selectedIndex < 0) {
            return new ExtractResult(
                    this,
                    ItemStack.EMPTY
            );
        }

        ItemStack stored = newHerbs.get(selectedIndex);
        ItemStack extracted = stored.copy();

        extracted.setCount(1);
        stored.shrink(1);

        if (stored.isEmpty()) {
            newHerbs.remove(selectedIndex);
        }

        return new ExtractResult(new HerbPouchComponent(capacity, newHerbs), extracted);
    }

    // ── Bulk extraction ───────────────────────────────────────────

    public ExtractManyResult extractAllByAgeGroup(ItemStack clickedSummary) {
        if (clickedSummary.isEmpty()) {
            return new ExtractManyResult(
                    this,
                    List.of()
            );
        }

        List<ItemStack> newHerbs = new ArrayList<>();
        List<ItemStack> extracted = new ArrayList<>();

        for (ItemStack stored : herbs) {
            ItemStack copy = stored.copy();

            if (matchesAgeGroup(copy, clickedSummary)) {
                extracted.add(copy);
            } else {
                newHerbs.add(copy);
            }
        }

        return new ExtractManyResult(
                new HerbPouchComponent(capacity, newHerbs),
                List.copyOf(extracted)
        );
    }

    public ExtractManyResult extractAllByItem(ItemStack clickedSummary) {
        if (clickedSummary.isEmpty()) {
            return new ExtractManyResult(
                    this,
                    List.of()
            );
        }

        List<ItemStack> newHerbs = new ArrayList<>();
        List<ItemStack> extracted = new ArrayList<>();

        for (ItemStack stored : herbs) {
            ItemStack copy = stored.copy();

            if (ItemStack.isSameItem(copy, clickedSummary)) {
                extracted.add(copy);
            } else {
                newHerbs.add(copy);
            }
        }

        return new ExtractManyResult(new HerbPouchComponent(capacity, newHerbs), List.copyOf(extracted));
    }

    // ── Display summaries ─────────────────────────────────────────

    public List<ItemStack> getSummaryStacks() {
        List<ItemStack> summaries = new ArrayList<>();

        for (ItemStack herb : herbs) {
            ItemStack groupKey =
                    createAgeGroupKey(herb);

            ItemStack matchingSummary = null;

            for (ItemStack summary : summaries) {
                if (ItemStack.isSameItemSameComponents(
                        summary,
                        groupKey
                )) {
                    matchingSummary = summary;
                    break;
                }
            }

            if (matchingSummary != null) {
                matchingSummary.grow(herb.getCount());
            } else {
                groupKey.setCount(herb.getCount());
                summaries.add(groupKey);
            }
        }

        summaries.sort(
                Comparator
                        .comparing(
                                (ItemStack stack) ->
                                        BuiltInRegistries.ITEM
                                                .getKey(stack.getItem())
                                                .toString()
                        )
                        .thenComparingInt(
                                stack ->
                                        HerbQuality
                                                .getAgeTier(
                                                        getRawAgeTicks(stack)
                                                )
                                                .ordinal()
                        )
        );

        return summaries;
    }

    private static ItemStack createAgeGroupKey(ItemStack stack) {
        ItemStack key = stack.copy();
        key.setCount(1);
        key.remove(ModDataComponents.HERB_QUALITY.get());
        key.set(ModDataComponents.HERB_AGE_TIER.get(), HerbQuality.getCanonicalAgeTicks(getRawAgeTicks(stack)));

        return key;
    }

    private static boolean matchesAgeGroup(ItemStack stored, ItemStack summary) {
        ItemStack storedKey = createAgeGroupKey(stored);
        ItemStack summaryKey = createAgeGroupKey(summary);

        return ItemStack.isSameItemSameComponents(storedKey, summaryKey);
    }

    private static long getRawAgeTicks(ItemStack stack) {
        Long age = stack.get(ModDataComponents.HERB_AGE_TIER.get());

        return age != null ? age : 0L;
    }

    private List<ItemStack> copyStoredHerbs() {
        List<ItemStack> copied = new ArrayList<>(herbs.size());

        for (ItemStack stack : herbs) {
            copied.add(stack.copy());
        }

        return copied;
    }

    // ── Serialization ──────────────────────────────────────────────

    public static final Codec<HerbPouchComponent> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.INT
                                    .fieldOf("capacity")
                                    .forGetter(
                                            HerbPouchComponent::capacity
                                    ),
                            ItemStack.OPTIONAL_CODEC
                                    .listOf()
                                    .fieldOf("herbs")
                                    .forGetter(
                                            HerbPouchComponent::herbs
                                    )
                    ).apply(
                            instance,
                            HerbPouchComponent::new
                    )
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, HerbPouchComponent> STREAM_CODEC = StreamCodec.of(HerbPouchComponent::encode, HerbPouchComponent::decode);

    public static void encode(RegistryFriendlyByteBuf buffer, HerbPouchComponent component) {
        buffer.writeInt(component.capacity());
        ByteBufUtil.ITEM_STACK_LIST.encode(buffer, component.herbs());
    }

    public static HerbPouchComponent decode(RegistryFriendlyByteBuf buffer) {
        int capacity = buffer.readInt();
        List<ItemStack> herbs = ByteBufUtil.ITEM_STACK_LIST.decode(buffer);
        return new HerbPouchComponent(capacity, herbs);
    }

    public record InsertResult(HerbPouchComponent component, ItemStack remainder) {
    }

    public record ExtractResult(HerbPouchComponent component, ItemStack extracted) {
    }

    public record ExtractManyResult(HerbPouchComponent component, List<ItemStack> extracted) {
    }
}