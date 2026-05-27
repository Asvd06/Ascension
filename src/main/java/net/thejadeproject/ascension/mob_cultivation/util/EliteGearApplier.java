package net.thejadeproject.ascension.mob_cultivation.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.thejadeproject.ascension.mob_cultivation.MobCultivationDefinition;
import net.thejadeproject.ascension.mob_cultivation.MobCultivationList;

import java.util.List;
import java.util.Random;

/**
 * Applies enchanted gear to elite mobs on spawn.
 *
 * Gear tier scales with cultivation realm:
 *   qi_gathering            → leather / chainmail
 *   formation_establishment → chainmail / iron
 *   golden_core             → iron / gold
 *   nascent_soul            → diamond
 *   (future)                → netherite
 *
 * Skeletons always receive only an enchanted bow regardless of realm.
 * All other mobs randomly receive either a weapon (sword/axe) or a tool
 * (pickaxe/shovel/hoe) at equal chance, plus a chance for armor.
 *
 * Enchantment levels scale with realm index so higher-realm mobs have
 * meaningfully stronger enchants.
 */
public final class EliteGearApplier {

    private EliteGearApplier() {
    }

    private static final Random RANDOM = new Random();

    // -------------------------------------------------------------------------
    // Entry point
    // -------------------------------------------------------------------------

    public static void applyGear(ServerLevel level, LivingEntity entity,
                                 MobCultivationDefinition definition) {
        int realmIndex = MobCultivationList.getRealmIndex(definition.realmId());

        if (entity instanceof Skeleton) {
            applySkeletonGear(level, entity, realmIndex);
        } else {
            applyGenericGear(level, entity, realmIndex);
        }
    }

    // -------------------------------------------------------------------------
    // Skeleton — enchanted bow only
    // -------------------------------------------------------------------------

    private static void applySkeletonGear(ServerLevel level, LivingEntity entity, int realmIndex) {
        ItemStack bow = new ItemStack(Items.BOW);

        // Power scales with realm
        int powerLevel = Math.min(5, 1 + realmIndex);
        // Infinity on higher realms
        boolean infinity = realmIndex >= 3; // golden_core+
        // Flame on formation+
        boolean flame = realmIndex >= 2;
        // Punch on qi_gathering+
        int punchLevel = realmIndex >= 1 ? Math.min(2, realmIndex) : 0;

        enchant(level, bow, Enchantments.POWER, powerLevel);
        if (flame)    enchant(level, bow, Enchantments.FLAME, 1);
        if (punchLevel > 0) enchant(level, bow, Enchantments.PUNCH, punchLevel);
        if (infinity) enchant(level, bow, Enchantments.INFINITY, 1);

        entity.setItemSlot(EquipmentSlot.MAINHAND, bow);
    }

    // -------------------------------------------------------------------------
    // Generic mobs — weapon or tool + optional armor
    // -------------------------------------------------------------------------

    private static void applyGenericGear(ServerLevel level, LivingEntity entity, int realmIndex) {
        // 50% chance weapon, 50% chance tool
        boolean giveTool = RANDOM.nextBoolean();

        ItemStack mainhand = giveTool
                ? pickTool(realmIndex)
                : pickWeapon(realmIndex);

        enchantMainhand(level, mainhand, realmIndex, giveTool);
        entity.setItemSlot(EquipmentSlot.MAINHAND, mainhand);

        // Armor — chance scales with realm (qi: 40%, form: 55%, gold: 70%, nascent: 85%)
        float armorChance = 0.25f + (realmIndex * 0.15f);
        if (RANDOM.nextFloat() < armorChance) {
            applyArmorSet(level, entity, realmIndex);
        }
    }

    // -------------------------------------------------------------------------
    // Weapon selection by realm
    // -------------------------------------------------------------------------

    private static ItemStack pickWeapon(int realmIndex) {
        // Weapons: sword or axe
        boolean axe = RANDOM.nextBoolean();
        return switch (realmIndex) {
            case 1  -> axe ? new ItemStack(Items.STONE_AXE)    : new ItemStack(Items.STONE_SWORD);
            case 2  -> axe ? new ItemStack(Items.IRON_AXE)     : new ItemStack(Items.IRON_SWORD);
            case 3  -> axe ? new ItemStack(Items.GOLDEN_AXE)   : new ItemStack(Items.GOLDEN_SWORD);
            case 4  -> axe ? new ItemStack(Items.DIAMOND_AXE)  : new ItemStack(Items.DIAMOND_SWORD);
            // soul_formation+ when added
            default -> realmIndex >= 5
                    ? (axe ? new ItemStack(Items.NETHERITE_AXE) : new ItemStack(Items.NETHERITE_SWORD))
                    : new ItemStack(Items.WOODEN_SWORD);
        };
    }

    private static ItemStack pickTool(int realmIndex) {
        // Tools: pickaxe, shovel, or hoe — equally likely
        int type = RANDOM.nextInt(3);
        return switch (realmIndex) {
            case 1 -> switch (type) {
                case 0 -> new ItemStack(Items.STONE_PICKAXE);
                case 1 -> new ItemStack(Items.STONE_SHOVEL);
                default -> new ItemStack(Items.STONE_HOE);
            };
            case 2 -> switch (type) {
                case 0 -> new ItemStack(Items.IRON_PICKAXE);
                case 1 -> new ItemStack(Items.IRON_SHOVEL);
                default -> new ItemStack(Items.IRON_HOE);
            };
            case 3 -> switch (type) {
                case 0 -> new ItemStack(Items.GOLDEN_PICKAXE);
                case 1 -> new ItemStack(Items.GOLDEN_SHOVEL);
                default -> new ItemStack(Items.GOLDEN_HOE);
            };
            case 4 -> switch (type) {
                case 0 -> new ItemStack(Items.DIAMOND_PICKAXE);
                case 1 -> new ItemStack(Items.DIAMOND_SHOVEL);
                default -> new ItemStack(Items.DIAMOND_HOE);
            };
            default -> realmIndex >= 5 ? switch (type) {
                case 0 -> new ItemStack(Items.NETHERITE_PICKAXE);
                case 1 -> new ItemStack(Items.NETHERITE_SHOVEL);
                default -> new ItemStack(Items.NETHERITE_HOE);
            } : new ItemStack(Items.WOODEN_PICKAXE);
        };
    }

    // -------------------------------------------------------------------------
    // Enchanting mainhand
    // -------------------------------------------------------------------------

    private static void enchantMainhand(ServerLevel level, ItemStack stack,
                                        int realmIndex, boolean isTool) {
        if (isTool) {
            // Tools get efficiency + unbreaking
            int efficiency = Math.min(5, 1 + realmIndex);
            int unbreaking = Math.min(3, realmIndex);
            enchant(level, stack, Enchantments.EFFICIENCY, efficiency);
            if (unbreaking > 0) enchant(level, stack, Enchantments.UNBREAKING, unbreaking);
            if (realmIndex >= 3) enchant(level, stack, Enchantments.FORTUNE, Math.min(3, realmIndex - 2));
        } else {
            // Weapons get sharpness + looting + fire aspect
            int sharpness = Math.min(5, 1 + realmIndex);
            int looting   = Math.min(3, realmIndex - 1);
            enchant(level, stack, Enchantments.SHARPNESS, sharpness);
            if (looting > 0) enchant(level, stack, Enchantments.LOOTING, looting);
            if (realmIndex >= 2) enchant(level, stack, Enchantments.FIRE_ASPECT, Math.min(2, realmIndex - 1));
            if (realmIndex >= 4) enchant(level, stack, Enchantments.KNOCKBACK, 2);
        }
    }

    // -------------------------------------------------------------------------
    // Armor
    // -------------------------------------------------------------------------

    private static void applyArmorSet(ServerLevel level, LivingEntity entity, int realmIndex) {
        List<ItemStack> armor = buildArmorSet(realmIndex);

        // Slots: FEET=0, LEGS=1, CHEST=2, HEAD=3
        EquipmentSlot[] armorSlots = {
                EquipmentSlot.FEET,
                EquipmentSlot.LEGS,
                EquipmentSlot.CHEST,
                EquipmentSlot.HEAD
        };

        for (int i = 0; i < armor.size(); i++) {
            ItemStack piece = armor.get(i);
            if (piece.isEmpty()) continue;

            enchantArmor(level, piece, realmIndex);
            entity.setItemSlot(armorSlots[i], piece);
            // Don't guarantee armor drop — player must get lucky
        }
    }

    private static List<ItemStack> buildArmorSet(int realmIndex) {
        return switch (realmIndex) {
            // qi_gathering: leather or chainmail, randomly mixed
            case 1 -> List.of(
                    randPick(new ItemStack(Items.LEATHER_BOOTS),    new ItemStack(Items.CHAINMAIL_BOOTS)),
                    randPick(new ItemStack(Items.LEATHER_LEGGINGS), new ItemStack(Items.CHAINMAIL_LEGGINGS)),
                    randPick(new ItemStack(Items.LEATHER_CHESTPLATE),new ItemStack(Items.CHAINMAIL_CHESTPLATE)),
                    randPick(new ItemStack(Items.LEATHER_HELMET),   new ItemStack(Items.CHAINMAIL_HELMET))
            );
            // formation_establishment: chainmail or iron
            case 2 -> List.of(
                    randPick(new ItemStack(Items.CHAINMAIL_BOOTS),    new ItemStack(Items.IRON_BOOTS)),
                    randPick(new ItemStack(Items.CHAINMAIL_LEGGINGS), new ItemStack(Items.IRON_LEGGINGS)),
                    randPick(new ItemStack(Items.CHAINMAIL_CHESTPLATE),new ItemStack(Items.IRON_CHESTPLATE)),
                    randPick(new ItemStack(Items.CHAINMAIL_HELMET),   new ItemStack(Items.IRON_HELMET))
            );
            // golden_core: iron or gold
            case 3 -> List.of(
                    randPick(new ItemStack(Items.IRON_BOOTS),    new ItemStack(Items.GOLDEN_BOOTS)),
                    randPick(new ItemStack(Items.IRON_LEGGINGS), new ItemStack(Items.GOLDEN_LEGGINGS)),
                    randPick(new ItemStack(Items.IRON_CHESTPLATE),new ItemStack(Items.GOLDEN_CHESTPLATE)),
                    randPick(new ItemStack(Items.IRON_HELMET),   new ItemStack(Items.GOLDEN_HELMET))
            );
            // nascent_soul: diamond
            case 4 -> List.of(
                    new ItemStack(Items.DIAMOND_BOOTS),
                    new ItemStack(Items.DIAMOND_LEGGINGS),
                    new ItemStack(Items.DIAMOND_CHESTPLATE),
                    new ItemStack(Items.DIAMOND_HELMET)
            );
            // soul_formation+ (future): netherite
            default -> realmIndex >= 5 ? List.of(
                    new ItemStack(Items.NETHERITE_BOOTS),
                    new ItemStack(Items.NETHERITE_LEGGINGS),
                    new ItemStack(Items.NETHERITE_CHESTPLATE),
                    new ItemStack(Items.NETHERITE_HELMET)
            ) : List.of(); // mortal gets nothing
        };
    }

    private static void enchantArmor(ServerLevel level, ItemStack piece, int realmIndex) {
        int protection = Math.min(4, realmIndex);
        enchant(level, piece, Enchantments.PROTECTION, protection);
        if (realmIndex >= 3) enchant(level, piece, Enchantments.UNBREAKING, Math.min(3, realmIndex - 2));
        if (realmIndex >= 4) enchant(level, piece, Enchantments.MENDING, 1);
    }

    // -------------------------------------------------------------------------
    // Util
    // -------------------------------------------------------------------------

    private static void enchant(ServerLevel level, ItemStack stack,
                                ResourceKey<Enchantment> enchantment,
                                int lvl) {
        if (lvl <= 0) return;
        level.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolder(enchantment)
                .ifPresent(holder -> stack.enchant(holder, lvl));
    }

    private static ItemStack randPick(ItemStack a, ItemStack b) {
        return RANDOM.nextBoolean() ? a : b;
    }
}