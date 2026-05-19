package net.thejadeproject.ascension.events;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.common.items.tools.SpearItem;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.data_attachments.attachments.PhysiqueAcquisitionCounters;
import net.thejadeproject.ascension.common.items.physiques.PhysiqueTransferItem;

@EventBusSubscriber(modid = AscensionCraft.MOD_ID)
public class PhysiqueAcquisitionHandler {

    private static final float CHANCE_FIRE_ATTUNED         = 0.50f;
    private static final float CHANCE_WATER_ATTUNED        = 0.52f;
    private static final float CHANCE_EARTH_ATTUNED        = 0.48f;
    private static final float CHANCE_WOOD_ATTUNED         = 0.55f;
    private static final float CHANCE_METAL_ATTUNED        = 0.47f;
    private static final float CHANCE_SWORD_APPRENTICE     = 0.53f;
    private static final float CHANCE_THUGGISH_FORM        = 0.49f;
    private static final float CHANCE_HARDENED_GENERAL     = 0.51f;
    private static final float CHANCE_ARROW_BLESSED        = 0.54f;
    private static final float CHANCE_FLOW_SEVERING_EYES   = 0.45f;

    private static final float CHANCE_WEAK_SOUL            = 0.50f;
    private static final float CHANCE_TWISTED_VESSELS      = 0.26f;

    private static final float CHANCE_LIGHTNING_ATTUNED    = 0.30f;
    private static final float CHANCE_WIND_ATTUNED         = 0.25f;
    private static final float CHANCE_ASHEN_SOUL_FLAME     = 0.22f;
    private static final float CHANCE_FLAME_TOUCHED        = 0.20f;
    private static final float CHANCE_DECAYING_MERIDIANS   = 0.28f;
    private static final float CHANCE_CORRUPTED_ENTITY     = 0.24f;
    private static final float CHANCE_CLEAR_SPIRIT         = 0.23f;
    private static final float CHANCE_SOUL_GAZE            = 0.32f;
    private static final float CHANCE_DREAMING_SOUL        = 0.20f;
    private static final float CHANCE_ACADEMIC_SPIRIT      = 0.26f;
    private static final float CHANCE_THUNDERING_SOUL_CORE = 0.35f;
    private static final float CHANCE_SOUL_SWORD_HEART     = 0.21f;
    private static final float CHANCE_DUAL_SOUL            = 0.30f;
    private static final float CHANCE_IRON_BULWARK_SPINE   = 0.24f;
    private static final float CHANCE_BRUISED_KNUCKLE_BODY = 0.22f;
    private static final float CHANCE_POINTED_EYES         = 0.23f;
    private static final float CHANCE_SWORD_BONE           = 0.20f;
    private static final float CHANCE_THIN_SWORD_PULSE     = 0.21f;
    private static final float CHANCE_SPEAR_SOUL_MARK      = 0.22f;
    private static final float CHANCE_STONE_MONKEY         = 0.25f;
    private static final float CHANCE_TYRANT_BODY          = 0.23f;
    private static final float CHANCE_WILD_CLEAVER_VETERAN = 0.26f;

    private static final float CHANCE_MYRIAD_POISON_VESSEL   = 0.18f;
    private static final float CHANCE_TITAN_BORN             = 0.15f;
    private static final float CHANCE_BLOOD_WRAITH           = 0.14f;
    private static final float CHANCE_CRYSTAL_SOUL           = 0.11f;
    private static final float CHANCE_SWORD_MONSTER          = 0.13f;
    private static final float CHANCE_MOLTEN_CASTED          = 0.16f;
    //private static final float CHANCE_WORLD_DOMINATOR        = 0.12f;

    private static final int FIRE_HITS_FOR_ATTUNED          = 80;
    private static final int WATER_HITS_FOR_ATTUNED         = 60;
    private static final int EARTH_BLOCKS_FOR_ATTUNED       = 300;
    private static final int WOOD_LOGS_FOR_ATTUNED          = 200;
    private static final int METAL_ORES_FOR_ATTUNED         = 150;
    private static final int SWORD_KILLS_APPRENTICE         = 100;
    private static final int FIST_KILLS_THUGGISH            = 80;
    private static final int SPEAR_KILLS_GENERAL            = 80;
    private static final int BOW_KILLS_ARROW_BLESSED        = 100;

    private static final int SWORD_KILLS_SWORD_BONE         = 300;
    private static final int SWORD_KILLS_THIN_SWORD         = 250;
    private static final int SWORD_KILLS_SOUL_SWORD         = 200;
    private static final int FIST_KILLS_TYRANT              = 200;
    private static final int FIST_KILLS_BRUISED             = 180;
    private static final int AXE_KILLS_WILD_CLEAVER         = 150;
    private static final int SPEAR_KILLS_POINTED            = 200;
    private static final int SPEAR_KILLS_SPEAR_SOUL         = 180;
    private static final int SHIELD_BLOCKS_IRON_BULWARK     = 200;
    private static final int POISON_HITS_DECAYING           = 50;
    private static final int WEAK_SOUL_HITS                 = 30;
    private static final int TWISTED_VESSEL_HITS            = 30;
    private static final int WITHER_MAGIC_CORRUPTED         = 100;
    private static final int BOOKS_ACADEMIC                 = 30;
    private static final int NEAR_DEATH_FIRE_ASHEN          = 15;
    private static final int NEAR_DEATH_FIRE_FLAME_TOUCHED  = 20;
    private static final int UNDERGROUND_BLOCKS_MONKEY      = 500;
    private static final float HIGH_FALL_DISTANCE_T2        = 15.0f;
    private static final long DUAL_SOUL_WINDOW_MS           = 600_000L;

    private static final float TITAN_BORN_FALL_DISTANCE     = 50.0f;
    private static final int LAVA_SOAK_HITS                 = 40;
    private static final long MOLTEN_QUENCH_WINDOW_MS       = 10_000L;
    private static final int POISON_CYCLES_FOR_MYRIAD       = 3;

    private static final int BOSS_KILLS_SOUL_GAZE           = 100;
    private static final int BOSS_KILLS_BLOOD_WRAITH        = 100;
    private static final int BOSS_KILLS_SWORD_MONSTER       = 100;
    //private static final int WARDEN_KILLS_WORLD_DOMINATOR   = 100;

    private static final Map<UUID, ItemStack> PENDING_DUAL_SOUL = new HashMap<>();

    private static PhysiqueAcquisitionCounters getCounters(Player player) {
        return player.getData(ModAttachments.PHYSIQUE_COUNTERS);
    }

    private static boolean tryGiveEssence(ServerPlayer player, String physiqueId,
                                          float chance, int minPurity, int maxPurity) {
        if (player.level().getRandom().nextFloat() >= chance) return false;
        int purity = minPurity + player.level().getRandom().nextInt(maxPurity - minPurity + 1);
        ItemStack essence = PhysiqueTransferItem.createWithPhysique(physiqueId, purity);
        if (!player.getInventory().add(essence)) {
            player.drop(essence, false);
        }
        return true;
    }

    private static boolean isSword(ItemStack stack) {
        return stack.getItem() instanceof SwordItem;
    }

    private static boolean isFist(ItemStack stack) {
        return stack.isEmpty() || stack.getItem() == Items.AIR;
    }

    private static boolean isAxe(ItemStack stack) {
        return stack.getItem() == Items.IRON_AXE || stack.getItem() == Items.DIAMOND_AXE
                || stack.getItem() == Items.NETHERITE_AXE || stack.getItem() == Items.GOLDEN_AXE
                || stack.getItem() == Items.STONE_AXE || stack.getItem() == Items.WOODEN_AXE;
    }

    private static boolean isSpear(ItemStack stack) {
        return stack.getItem() instanceof SpearItem;
    }

    private static boolean hasFeatherFalling(ServerPlayer player) {
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        ItemEnchantments enchantments = boots.get(DataComponents.ENCHANTMENTS);
        if (enchantments == null) return false;
        var registry = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        return enchantments.getLevel(registry.getOrThrow(Enchantments.FEATHER_FALLING)) > 0;
    }

    private static boolean isWearingAnyArmor(Player player) {
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            if (!player.getItemBySlot(slot).isEmpty()) return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        PhysiqueAcquisitionCounters c = getCounters(player);
        DamageSource src = event.getSource();

        if (src.is(DamageTypeTags.IS_FIRE)) {
            c.t1.fireDamageHits++;

            if (player.getHealth() <= 4.0f) {
                c.t2.nearDeathFireHits++;
                c.t2.flameTouchedNearDeathHits++;
            }

            if (player.isInLava()) {
                if (c.t3.enteredLavaTime == 0L) {
                    c.t3.enteredLavaTime = System.currentTimeMillis();
                }
                if (!c.t3.lavaSoakComplete && c.t1.fireDamageHits >= LAVA_SOAK_HITS) {
                    c.t3.lavaSoakComplete = true;
                }
            } else {
                c.t3.enteredLavaTime = 0L;
                c.t3.lavaSoakComplete = false;
            }

            if (c.t1.fireDamageHits >= FIRE_HITS_FOR_ATTUNED) {
                c.t1.fireDamageHits -= FIRE_HITS_FOR_ATTUNED;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":fire_attuned",
                        CHANCE_FIRE_ATTUNED, 20, 40);
            }

            if (c.t2.nearDeathFireHits >= NEAR_DEATH_FIRE_ASHEN) {
                c.t2.nearDeathFireHits -= NEAR_DEATH_FIRE_ASHEN;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":ashen_soul_flame",
                        CHANCE_ASHEN_SOUL_FLAME, 20, 30);
            }

            if (c.t2.flameTouchedNearDeathHits >= NEAR_DEATH_FIRE_FLAME_TOUCHED) {
                c.t2.flameTouchedNearDeathHits -= NEAR_DEATH_FIRE_FLAME_TOUCHED;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":flame_touched",
                        CHANCE_FLAME_TOUCHED, 22, 30);
            }
        }

        if (src.is(DamageTypes.DROWN)) {
            c.t1.waterDrowningHits++;

            if (c.t3.lavaSoakComplete) {
                long timeSinceLava = System.currentTimeMillis() - c.t3.enteredLavaTime;
                if (timeSinceLava <= MOLTEN_QUENCH_WINDOW_MS) {
                    c.t3.lavaSoakComplete = false;
                    c.t3.enteredLavaTime = 0L;
                    c.t1.fireDamageHits = 0;
                    tryGiveEssence(player, AscensionCraft.MOD_ID + ":molten_casted",
                            CHANCE_MOLTEN_CASTED, 10, 20);
                }
            }

            if (c.t1.waterDrowningHits >= WATER_HITS_FOR_ATTUNED) {
                c.t1.waterDrowningHits -= WATER_HITS_FOR_ATTUNED;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":water_attuned",
                        CHANCE_WATER_ATTUNED, 22, 42);
            }
        }

        if (src.is(DamageTypes.WITHER) || src.is(DamageTypes.MAGIC)) {
            c.t2.witherMagicDamageTotal += (int) event.getNewDamage();
            if (c.t2.witherMagicDamageTotal >= WITHER_MAGIC_CORRUPTED) {
                c.t2.witherMagicDamageTotal -= WITHER_MAGIC_CORRUPTED;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":corrupted_entity",
                        CHANCE_CORRUPTED_ENTITY, 16, 28);
            }
        }

        if (src.is(DamageTypes.MAGIC) || src.is(DamageTypes.WITHER) || src.is(DamageTypes.SONIC_BOOM)) {
            c.t1.weakSoulHits++;
            if (c.t1.weakSoulHits >= WEAK_SOUL_HITS) {
                c.t1.weakSoulHits -= WEAK_SOUL_HITS;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":weak_soul",
                        CHANCE_WEAK_SOUL, 10, 20);
            }
        }

        if (src.is(DamageTypes.MAGIC) && player.hasEffect(net.minecraft.world.effect.MobEffects.POISON)) {
            c.t2.poisonHitsReceived++;

            if (c.t2.poisonHitsReceived >= POISON_HITS_DECAYING) {
                c.t2.poisonHitsReceived -= POISON_HITS_DECAYING;
                boolean dropped = tryGiveEssence(player, AscensionCraft.MOD_ID + ":decaying_meridians",
                        CHANCE_DECAYING_MERIDIANS, 15, 25);

                if (dropped) {
                    c.t3.poisonCyclesCompleted++;

                    if (c.t3.poisonCyclesCompleted >= POISON_CYCLES_FOR_MYRIAD) {
                        if (tryGiveEssence(player, AscensionCraft.MOD_ID + ":myriad_poison_vessel",
                                CHANCE_MYRIAD_POISON_VESSEL, 12, 22)) {
                            c.t3.poisonCyclesCompleted -= POISON_CYCLES_FOR_MYRIAD;
                        }
                    }
                }
            }
        }

        if (player.hasEffect(net.minecraft.world.effect.MobEffects.POISON)
                && player.hasEffect(net.minecraft.world.effect.MobEffects.WEAKNESS)) {
            c.t2.twistedVesselHits++;
            if (c.t2.twistedVesselHits >= TWISTED_VESSEL_HITS) {
                c.t2.twistedVesselHits -= TWISTED_VESSEL_HITS;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":twisted_vessels",
                        CHANCE_TWISTED_VESSELS, 15, 25);
            }
        }

        if (event.getNewDamage() == 0 && player.isBlocking()) {
            c.t2.shieldBlocks++;
            if (c.t2.shieldBlocks >= SHIELD_BLOCKS_IRON_BULWARK) {
                c.t2.shieldBlocks -= SHIELD_BLOCKS_IRON_BULWARK;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":iron_bulwark_spine",
                        CHANCE_IRON_BULWARK_SPINE, 16, 27);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        PhysiqueAcquisitionCounters c = getCounters(player);
        ItemStack weapon = player.getMainHandItem();
        boolean veryLowHp = player.getHealth() <= 2.0f;
        boolean isBossKill = event.getEntity().getMaxHealth() >= 100f;
        var entityType = event.getEntity().getType();

        if (isSword(weapon)) {
            c.t1.swordKills++;
            c.t1.distinctWeaponKillTypes |= 1;

            if (c.t1.swordKills >= SWORD_KILLS_SWORD_BONE) {
                c.t1.swordKills -= SWORD_KILLS_SWORD_BONE;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":sword_bone",
                        CHANCE_SWORD_BONE, 21, 30);
            }
            if (c.t1.swordKills >= SWORD_KILLS_THIN_SWORD) {
                c.t1.swordKills -= SWORD_KILLS_THIN_SWORD;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":thin_sword_pulse",
                        CHANCE_THIN_SWORD_PULSE, 18, 30);
            }
            if (c.t1.swordKills >= SWORD_KILLS_SOUL_SWORD) {
                c.t1.swordKills -= SWORD_KILLS_SOUL_SWORD;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":soul_sword_heart",
                        CHANCE_SOUL_SWORD_HEART, 19, 30);
            }
            if (c.t1.swordKills >= SWORD_KILLS_APPRENTICE) {
                c.t1.swordKills -= SWORD_KILLS_APPRENTICE;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":sword_apprentice",
                        CHANCE_SWORD_APPRENTICE, 21, 40);
            }

            if (isBossKill) {
                c.t2.bossKillsSword++;
                if (c.t2.bossKillsSword >= BOSS_KILLS_SWORD_MONSTER) {
                    c.t2.bossKillsSword -= BOSS_KILLS_SWORD_MONSTER;
                    tryGiveEssence(player, AscensionCraft.MOD_ID + ":sword_monster",
                            CHANCE_SWORD_MONSTER, 14, 25);
                }
            }
        }

        if (isFist(weapon)) {
            c.t1.fistKills++;
            c.t1.distinctWeaponKillTypes |= 2;

            if (c.t1.fistKills >= FIST_KILLS_TYRANT) {
                c.t1.fistKills -= FIST_KILLS_TYRANT;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":tyrant_body",
                        CHANCE_TYRANT_BODY, 18, 30);
            }
            if (c.t1.fistKills >= FIST_KILLS_BRUISED) {
                c.t1.fistKills -= FIST_KILLS_BRUISED;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":bruised_knuckle_body",
                        CHANCE_BRUISED_KNUCKLE_BODY, 20, 30);
            }
            if (c.t1.fistKills >= FIST_KILLS_THUGGISH) {
                c.t1.fistKills -= FIST_KILLS_THUGGISH;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":thuggish_form",
                        CHANCE_THUGGISH_FORM, 24, 44);
            }
        }

        if (isAxe(weapon)) {
            c.t1.distinctWeaponKillTypes |= 16;
            c.t1.axeKills++;
            if (c.t1.axeKills >= AXE_KILLS_WILD_CLEAVER) {
                c.t1.axeKills -= AXE_KILLS_WILD_CLEAVER;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":wild_cleaver_veteran",
                        CHANCE_WILD_CLEAVER_VETERAN, 16, 28);
            }
        }

        if (isSpear(weapon)) {
            c.t1.spearKills++;
            c.t1.distinctWeaponKillTypes |= 4;

            if (c.t1.spearKills >= SPEAR_KILLS_POINTED) {
                c.t1.spearKills -= SPEAR_KILLS_POINTED;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":pointed_eyes",
                        CHANCE_POINTED_EYES, 17, 29);
            }
            if (c.t1.spearKills >= SPEAR_KILLS_SPEAR_SOUL) {
                c.t1.spearKills -= SPEAR_KILLS_SPEAR_SOUL;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":spear_soul_mark",
                        CHANCE_SPEAR_SOUL_MARK, 19, 30);
            }
            if (c.t1.spearKills >= SPEAR_KILLS_GENERAL) {
                c.t1.spearKills -= SPEAR_KILLS_GENERAL;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":hardened_general",
                        CHANCE_HARDENED_GENERAL, 20, 45);
            }
        }

        if (event.getSource().is(DamageTypeTags.IS_PROJECTILE)
                && event.getSource().getDirectEntity() != null
                && event.getSource().getDirectEntity().getType().toString().contains("arrow")) {
            c.t1.bowKills++;
            c.t1.distinctWeaponKillTypes |= 8;

            if (c.t1.bowKills >= BOW_KILLS_ARROW_BLESSED) {
                c.t1.bowKills -= BOW_KILLS_ARROW_BLESSED;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":arrow_blessed",
                        CHANCE_ARROW_BLESSED, 22, 41);
            }
        }

        if ((c.t1.distinctWeaponKillTypes & 0b1111) == 0b1111) {
            c.t1.distinctWeaponKillTypes = 0;
            tryGiveEssence(player, AscensionCraft.MOD_ID + ":flow_severing_eyes",
                    CHANCE_FLOW_SEVERING_EYES, 25, 45);
        }

//        if (entityType == EntityType.WARDEN) {
//            c.t1.wardenKills++;
//            if (c.t1.wardenKills >= WARDEN_KILLS_WORLD_DOMINATOR) {
//                c.t1.wardenKills -= WARDEN_KILLS_WORLD_DOMINATOR;
//                tryGiveEssence(player, AscensionCraft.MOD_ID + ":world_dominator",
//                        CHANCE_WORLD_DOMINATOR, 15, 25);
//            }
//        }

        if (veryLowHp && isBossKill) {
            c.t2.soulKillsLowHp++;
            if (c.t2.soulKillsLowHp >= 3) {
                c.t2.soulKillsLowHp = 0;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":soul_gaze",
                        CHANCE_SOUL_GAZE, 20, 30);
            }

            c.t2.bossKillsLowHp++;
            if (c.t2.bossKillsLowHp >= BOSS_KILLS_BLOOD_WRAITH) {
                c.t2.bossKillsLowHp -= BOSS_KILLS_BLOOD_WRAITH;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":blood_wraith",
                        CHANCE_BLOOD_WRAITH, 16, 25);
            }
        }
    }

    @SubscribeEvent
    public static void onLightningStrike(EntityStruckByLightningEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        PhysiqueAcquisitionCounters c = getCounters(player);

        c.t2.lightningStrikesReceived++;

        if (player.isInWater()) {
            tryGiveEssence(player, AscensionCraft.MOD_ID + ":lightning_attuned",
                    CHANCE_LIGHTNING_ATTUNED, 15, 28);
        }

        if (c.t2.lightningStrikesReceived >= 2) {
            if (tryGiveEssence(player, AscensionCraft.MOD_ID + ":thundering_soul_core",
                    CHANCE_THUNDERING_SOUL_CORE, 20, 30)) {
                c.t2.lightningStrikesReceived -= 2;
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        PhysiqueAcquisitionCounters c = getCounters(player);
        Block block = event.getState().getBlock();
        BlockPos pos = event.getPos();

        if (event.getState().is(BlockTags.STONE_ORE_REPLACEABLES)
                || block == Blocks.DEEPSLATE || block == Blocks.STONE || block == Blocks.COBBLESTONE) {
            c.t1.earthBlocksMined++;
            if (c.t1.earthBlocksMined >= EARTH_BLOCKS_FOR_ATTUNED) {
                c.t1.earthBlocksMined -= EARTH_BLOCKS_FOR_ATTUNED;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":earth_attuned",
                        CHANCE_EARTH_ATTUNED, 25, 45);
            }
        }

        if (event.getState().is(BlockTags.LOGS)) {
            c.t1.woodLogsChopped++;
            if (c.t1.woodLogsChopped >= WOOD_LOGS_FOR_ATTUNED) {
                c.t1.woodLogsChopped -= WOOD_LOGS_FOR_ATTUNED;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":wood_attuned",
                        CHANCE_WOOD_ATTUNED, 20, 38);
            }
        }

        if (event.getState().is(BlockTags.IRON_ORES) || event.getState().is(BlockTags.GOLD_ORES)
                || event.getState().is(BlockTags.COPPER_ORES) || event.getState().is(BlockTags.COAL_ORES)) {
            c.t1.metalOresMined++;
            if (c.t1.metalOresMined >= METAL_ORES_FOR_ATTUNED) {
                c.t1.metalOresMined -= METAL_ORES_FOR_ATTUNED;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":metal_attuned",
                        CHANCE_METAL_ATTUNED, 23, 45);
            }
        }

        if (pos.getY() < 0) {
            c.t2.undergroundBlocksMined++;
            if (c.t2.undergroundBlocksMined >= UNDERGROUND_BLOCKS_MONKEY) {
                c.t2.undergroundBlocksMined -= UNDERGROUND_BLOCKS_MONKEY;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":stone_monkey",
                        CHANCE_STONE_MONKEY, 20, 30);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.getHealth() <= 0) return;
        PhysiqueAcquisitionCounters c = getCounters(player);

        float distance = event.getDistance();

        if (distance >= HIGH_FALL_DISTANCE_T2) {
            tryGiveEssence(player, AscensionCraft.MOD_ID + ":wind_attuned",
                    CHANCE_WIND_ATTUNED, 18, 30);
        }

        if (distance >= TITAN_BORN_FALL_DISTANCE
                && !hasFeatherFalling(player)
                && !player.hasEffect(net.minecraft.world.effect.MobEffects.SLOW_FALLING)) {
            tryGiveEssence(player, AscensionCraft.MOD_ID + ":titan_born",
                    CHANCE_TITAN_BORN, 15, 25);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        PhysiqueAcquisitionCounters c = getCounters(player);

        long now = System.currentTimeMillis();
        if (c.t2.firstDeathTimestamp == 0L) {
            c.t2.firstDeathTimestamp = now;
            c.t2.deathsThisWindow = 1;
        } else if (now - c.t2.firstDeathTimestamp <= DUAL_SOUL_WINDOW_MS) {
            c.t2.deathsThisWindow++;
            if (c.t2.deathsThisWindow >= 2) {
                c.t2.deathsThisWindow = 0;
                c.t2.firstDeathTimestamp = 0L;
                if (player.level().getRandom().nextFloat() < CHANCE_DUAL_SOUL) {
                    int purity = 18 + player.level().getRandom().nextInt(13);
                    ItemStack essence = PhysiqueTransferItem.createWithPhysique(AscensionCraft.MOD_ID + ":dual_soul", purity);
                    PENDING_DUAL_SOUL.put(player.getUUID(), essence);
                }
            }
        } else {
            c.t2.firstDeathTimestamp = now;
            c.t2.deathsThisWindow = 1;
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ItemStack pending = PENDING_DUAL_SOUL.remove(player.getUUID());
        if (pending != null && !pending.isEmpty()) {
            if (!player.getInventory().add(pending)) {
                player.drop(pending, false);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (event.updateLevel()) return;

        tryGiveEssence(player, AscensionCraft.MOD_ID + ":dreaming_soul",
                CHANCE_DREAMING_SOUL, 15, 25);

        if (!isWearingAnyArmor(player)) {
            tryGiveEssence(player, AscensionCraft.MOD_ID + ":clear_spirit",
                    CHANCE_CLEAR_SPIRIT, 18, 30);

            tryGiveEssence(player, AscensionCraft.MOD_ID + ":crystal_soul",
                    CHANCE_CRYSTAL_SOUL, 18, 25);
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        Block block = event.getLevel().getBlockState(event.getPos()).getBlock();
        if (block == Blocks.ENCHANTING_TABLE || block == Blocks.BOOKSHELF
                || block == Blocks.LECTERN || block == Blocks.CHISELED_BOOKSHELF) {
            PhysiqueAcquisitionCounters c = getCounters(player);
            c.t2.booksUsed++;
            if (c.t2.booksUsed >= BOOKS_ACADEMIC) {
                c.t2.booksUsed -= BOOKS_ACADEMIC;
                tryGiveEssence(player, AscensionCraft.MOD_ID + ":academic_spirit",
                        CHANCE_ACADEMIC_SPIRIT, 17, 28);
            }
        }
    }
}