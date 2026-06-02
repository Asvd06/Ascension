package net.thejadeproject.ascension.common.items.tools.soul_weapon;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.common.items.ModItems;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.common.items.tools.data.soul_weapon.SoulWeaponComponent;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.data_attachments.attachments.SoulWeaponData;
import net.thejadeproject.ascension.util.ModTags;

public final class SoulWeaponHelper {

    private static final ResourceLocation SOUL_DAMAGE_BONUS_ID = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "soul_weapon_damage_bonus");

    private SoulWeaponHelper() {}

    public static boolean isSoulWeapon(ItemStack stack) {
        return !stack.isEmpty()
                && (
                stack.getItem() instanceof ISoulboundItem
                        || stack.is(ModItems.SOULBOUND_WEAPON.get())
        )
                && stack.has(ModDataComponents.SOUL_WEAPON.get());
    }

    public static boolean isOwner(ItemStack stack, Player player) {
        SoulWeaponComponent component = stack.get(ModDataComponents.SOUL_WEAPON.get());
        return component != null && component.owner().equals(player.getUUID());
    }

    public static float calculateSoulWeaponBonus(
            int soulMajor,
            int soulMinor,
            int grade,
            int forgedMarks,
            String type
    ) {
        float soulPower = soulMajor * 2.5F
                + soulMinor * 0.25F
                + grade * 2.0F
                + forgedMarks * 1.25F;

        return soulPower * getTypeMultiplier(type);
    }



    public static int getTemperingGain(LivingEntity killed) {
        int gain = 1;

        if (killed.hasData(ModAttachments.MOB_RANK)) {
            var rank = killed.getData(ModAttachments.MOB_RANK);

            if (!rank.isUnranked() && rank.isInitialized()) {
                gain += rank.getStage();
                gain += getRealmWeight(rank.getRealmId());
                return gain;
            }
        }

        if (killed.getType().is(ModTags.EntityTypes.BOSS)) {
            return 25;
        }

        return gain;
    }

    private static int getRealmWeight(String realmId) {
        return switch (realmId) {
            case "qi_gathering" -> 2;
            case "formation_establishment" -> 5;
            case "golden_core" -> 9;
            case "nascent_soul" -> 14;
            case "soul_formation" -> 20;
            case "void_refinement" -> 27;
            case "body_integration" -> 35;
            case "tribulation_transcendence" -> 44;
            case "mahayana" -> 54;
            case "earth_immortal" -> 65;
            default -> 0;
        };
    }

    private static float getTypeMultiplier(String type) {
        return switch (type == null ? "" : type) {
            case "axe", "mace" -> 1.25F;
            case "blade" -> 1.15F;
            case "spear" -> 1.05F;
            default -> 1.0F;
        };
    }

    public static void writeSoulWeaponComponent(ItemStack stack, Player player, SoulWeaponData data) {
        stack.set(ModDataComponents.SOUL_WEAPON.get(), new SoulWeaponComponent(
                player.getUUID(),
                data.weaponType,
                data.currentGrade,
                data.currentTempering,
                data.lifetimeMarks,
                data.lastSoulMajor,
                data.lastSoulMinor
        ));
    }

    public static void syncSoulRealmProgress(SoulWeaponData data, int soulMajor, int soulMinor) {
        if (!data.bound) return;

        boolean advancedMajorRealm = soulMajor > data.lastSoulMajor;

        if (advancedMajorRealm) {
            data.currentGrade = 0;
            data.currentTempering = 0;
        }

        data.lastSoulMajor = soulMajor;
        data.lastSoulMinor = soulMinor;
    }


    public static int getRequiredTempering(int currentGrade, int lifetimeMarks) {
        return 25 + ((currentGrade * 15 + lifetimeMarks * 8) * 3);
    }

    public static int removeOwnedSoulWeapons(Player player) {
        int removed = 0;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (!isSoulWeapon(stack)) continue;
            if (!isOwner(stack, player)) continue;

            player.getInventory().setItem(i, ItemStack.EMPTY);
            removed++;
        }

        return removed;
    }

    public static void copyForgedComponents(ItemStack original, ItemStack soulbound) {
        copyComponent(original, soulbound, DataComponents.ATTRIBUTE_MODIFIERS);
        copyComponent(original, soulbound, DataComponents.ENCHANTMENTS);
        copyComponent(original, soulbound, DataComponents.CUSTOM_NAME);
        copyComponent(original, soulbound, DataComponents.LORE);
    }

    private static <T> void copyComponent(
            ItemStack from,
            ItemStack to,
            DataComponentType<T> component
    ) {
        T value = from.get(component);

        if (value != null) {
            to.set(component, value);
        }
    }

    public static void saveSummonedSoulWeapon(
            ItemStack stack,
            Player player,
            SoulWeaponData data
    ) {
        if (!isSoulWeapon(stack)) return;
        if (!isOwner(stack, player)) return;

        updateSoulWeaponAttributes(stack, data);
        writeSoulWeaponComponent(stack, player, data);
        data.storedWeapon = stack.copyWithCount(1);
    }

    public static ItemStack migrateLegacySoulWeapon(
            ItemStack stack,
            ServerPlayer player,
            SoulWeaponData data
    ) {
        if (!stack.is(ModItems.SOULBOUND_WEAPON.get())) {
            return stack;
        }

        SoulWeaponType type = SoulWeaponType.fromId(data.weaponType);
        if (type == null) {
            return stack;
        }

        ItemStack migrated = type.createSoulboundStack();

        SoulWeaponHelper.copyForgedComponents(stack, migrated);
        SoulWeaponHelper.writeSoulWeaponComponent(migrated, player, data);

        data.storedWeapon = migrated.copyWithCount(1);

        return migrated;
    }

    public static void updateSoulWeaponAttributes(ItemStack stack, SoulWeaponData data) {
        ItemAttributeModifiers current = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);

        if (current == null) {current = ItemAttributeModifiers.EMPTY;}

        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        for (ItemAttributeModifiers.Entry entry : current.modifiers()) {
            boolean isOldSoulDamageBonus =
                    entry.attribute().equals(Attributes.ATTACK_DAMAGE)
                            && entry.modifier().id().equals(SOUL_DAMAGE_BONUS_ID);

            if (isOldSoulDamageBonus) {continue;}

            builder.add(
                    entry.attribute(),
                    entry.modifier(),
                    entry.slot()
            );
        }

        float soulBonus = calculateSoulWeaponBonus(
                data.lastSoulMajor,
                data.lastSoulMinor,
                data.currentGrade,
                data.lifetimeMarks,
                data.weaponType
        );

        if (soulBonus > 0.0F) {
            builder.add(
                    Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(
                            SOUL_DAMAGE_BONUS_ID,
                            soulBonus,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    EquipmentSlotGroup.MAINHAND
            );
        }

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS,
                builder.build().withTooltip(current.showInTooltip()));
    }


}