package net.thejadeproject.ascension.mob_cultivation.events;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.MobSplitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.mob_cultivation.*;
import net.thejadeproject.ascension.mob_cultivation.util.MobCultivationInheritance;
import net.thejadeproject.ascension.mob_cultivation.util.EliteGearApplier;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.mob_culti.SyncMobCultivation;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = AscensionCraft.MOD_ID)
public class MobCultivationEvents {

    /**
     * Chance (0–1) that a naturally spawned mob will roll from the elite pool
     * instead of the mortal pool. 5% by default.
     */
    private static final float ELITE_RANK_CHANCE = 0.25f;

    /**
     * Cooldown in ticks between elite-rank spawns per dimension.
     * 6000 ticks = 5 minutes.
     */
    private static final int ELITE_RANK_COOLDOWN = 6000; //6000

    /**
     * Announce range in blocks. Players within this radius receive the action
     * bar message and thunder sound when an elite-ranked mob spawns.
     */
    private static final double ANNOUNCE_RANGE = 64.0;

    /**
     * Per-dimension cooldown tracker. Not persisted across restarts — intentional.
     */
    private static final Map<ResourceLocation, Integer> RANK_COOLDOWNS = new HashMap<>();

    // -------------------------------------------------------------------------
    // Spawn initialization
    // -------------------------------------------------------------------------

    /**
     * Primary spawn handler. Mirrors Apotheosis's FinalizeSpawnEvent pattern.
     *
     * All naturally spawned mobs roll from the mortal pool (mortal 1–3).
     * A 5% chance upgrades the mob to the elite pool (qi_gathering through
     * nascent_soul and beyond as they are added), gated by spawn type,
     * player proximity, and a per-dimension cooldown.
     *
     * Non-natural spawns (spawners, eggs, commands) always land on mortal 1–3.
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        if (event.isCanceled() || event.isSpawnCancelled()) return;
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof LivingEntity living)) return;
        if (!MobCultivationRoller.canInitializeRank(living)) return;

        MobCultivationData data = living.getData(ModAttachments.MOB_RANK);
        if (data == null || data.isInitialized()) return;

        // Step 1: all mobs get a mortal 1–3 rank by default.
        MobCultivationDefinition defaultDef = MobCultivationRoller.rollMortalRank(living);
        assignRank(living, data, defaultDef);

        // Step 2: elite promotion — natural/chunk-gen spawns only.
        if (event.getSpawnType() != MobSpawnType.NATURAL
                && event.getSpawnType() != MobSpawnType.CHUNK_GENERATION) {
            return;
        }

        if (isOnCooldown(event.getLevel().getLevel())) return;

        Player player = event.getLevel().getNearestPlayer(
                event.getX(), event.getY(), event.getZ(), -1, false);
        if (player == null) return;

        if (event.getLevel().getRandom().nextFloat() > ELITE_RANK_CHANCE) return;

        // Step 3: roll from the elite pool — always above mortal by construction.
        MobCultivationDefinition eliteDef = MobCultivationRoller.rollEliteRank(living);

        assignRank(living, data, eliteDef);
        startCooldown(event.getLevel().getLevel(), ELITE_RANK_COOLDOWN);
        EliteGearApplier.applyGear((ServerLevel) event.getLevel().getLevel(), living, eliteDef);
        sendEliteSpawnNotification((ServerLevel) event.getLevel().getLevel(), living, eliteDef);
    }

    // -------------------------------------------------------------------------
    // Elite spawn notification
    // -------------------------------------------------------------------------

    /**
     * Sends an action bar message and thunder sound to all players within
     * {@link #ANNOUNCE_RANGE} blocks of the spawning elite mob.
     *
     * Translation key: "message.ascension.elite_mob_spawn"
     * Suggested en_us.json entry:
     *   "message.ascension.elite_mob_spawn": "A powerful %s [%s Stage %s] stirs nearby..."
     */
    public static void sendEliteSpawnNotification(ServerLevel level,
                                                  LivingEntity entity,
                                                  MobCultivationDefinition definition) {
        String realmDisplay = formatRealm(definition.realmId());
        int stage = definition.stage();
        int color = realmColor(definition.realmId());

        Component message = Component.translatable(
                "message.ascension.elite_mob_spawn",
                entity.getName(),
                realmDisplay,
                stage
        ).copy().withStyle(style -> style.withColor(color));

        applyEliteGlow(level, entity, definition.realmId());

        double rangeSq = ANNOUNCE_RANGE * ANNOUNCE_RANGE;

        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(entity) > rangeSq) continue;

            // Action bar text
            player.connection.send(new ClientboundSetActionBarTextPacket(message));

            // Thunder sound at the mob's position, heard by all nearby players
            level.playSound(
                    null,
                    entity.blockPosition(),
                    SoundEvents.LIGHTNING_BOLT_THUNDER,
                    SoundSource.HOSTILE,
                    1.5f,
                    0.8f + level.getRandom().nextFloat() * 0.2f
            );
        }
    }

    // -------------------------------------------------------------------------
    // Tracking sync
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (!(event.getTarget() instanceof LivingEntity living)) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        MobCultivationData data = living.getData(ModAttachments.MOB_RANK);
        if (data == null) return;

        PacketDistributor.sendToPlayer(
                player,
                new SyncMobCultivation(
                        living.getId(),
                        data.getRealmId(),
                        data.getStage(),
                        data.isInitialized()
                )
        );
    }

    // -------------------------------------------------------------------------
    // Reapply
    // -------------------------------------------------------------------------

    public static void reapplyRank(LivingEntity entity) {
        if (!MobCultivationRoller.canInitializeRank(entity)) return;

        MobCultivationData data = entity.getData(ModAttachments.MOB_RANK);
        if (data == null || !data.isInitialized()) return;

        MobCultivationApplier.applyFromData(entity, data);
        syncMobRanks(entity);
    }

    // -------------------------------------------------------------------------
    // Inheritance — babies and slime/magma splits
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onBabyEntitySpawn(BabyEntitySpawnEvent event) {
        if (!(event.getChild() instanceof LivingEntity child)) return;
        if (!(event.getParentA() instanceof LivingEntity parentA)) return;
        if (!(event.getParentB() instanceof LivingEntity parentB)) return;

        if (!MobCultivationRoller.canHaveRank(child)) return;

        MobCultivationData inherited =
                MobCultivationInheritance.inheritFromParents(parentA, parentB);
        if (inherited == null) return;

        applyInheritedRank(child, inherited);
    }

    @SubscribeEvent
    public static void onMobSplit(MobSplitEvent event) {
        LivingEntity origin = event.getParent();

        MobCultivationData inherited =
                MobCultivationInheritance.inheritFromOrigin(origin);
        if (inherited == null) return;

        for (Mob childMob : event.getChildren()) {
            if (!MobCultivationRoller.canHaveRank(childMob)) continue;
            applyInheritedRank(childMob, MobCultivationInheritance.copyOf(inherited));
        }
    }

    // -------------------------------------------------------------------------
    // Cooldown tick
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        ResourceLocation dim = event.getLevel().dimension().location();
        RANK_COOLDOWNS.computeIfPresent(dim, (k, v) -> v <= 1 ? null : v - 1);
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    /**
     * Writes the given definition into the mob's data, applies stat modifiers,
     * and syncs to tracking clients. Public so the force-spawn command can use it.
     */
    public static void assignRank(LivingEntity entity,
                                  MobCultivationData data,
                                  MobCultivationDefinition definition) {
        data.setRealmId(definition.realmId());
        data.setStage(definition.stage());
        data.setInitialized(true);
        MobCultivationApplier.applyRank(entity, definition);
        syncMobRanks(entity);
    }

    private static void applyInheritedRank(LivingEntity entity,
                                           MobCultivationData inherited) {
        if (inherited == null) return;

        MobCultivationData data = entity.getData(ModAttachments.MOB_RANK);
        if (data == null) return;

        data.setRealmId(inherited.getRealmId());
        data.setStage(inherited.getStage());
        data.setInitialized(true);

        MobCultivationApplier.applyFromData(entity, data);
        syncMobRanks(entity);
    }

    private static void syncMobRanks(LivingEntity entity) {
        if (entity.level().isClientSide()) return;

        MobCultivationData data = entity.getData(ModAttachments.MOB_RANK);
        if (data == null) return;

        PacketDistributor.sendToPlayersTrackingEntity(
                entity,
                new SyncMobCultivation(
                        entity.getId(),
                        data.getRealmId(),
                        data.getStage(),
                        data.isInitialized()
                )
        );
    }

    public static boolean isOnCooldown(Level level) {
        return RANK_COOLDOWNS.getOrDefault(level.dimension().location(), 0) > 0;
    }

    public static void startCooldown(Level level, int ticks) {
        RANK_COOLDOWNS.put(level.dimension().location(), ticks);
    }

    /**
     * Converts a snake_case realm ID to Title Case for display.
     * e.g. "qi_gathering" -> "Qi Gathering"
     */
    public static String formatRealm(String realmId) {
        String[] parts = realmId.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty()) continue;
            sb.append(Character.toUpperCase(parts[i].charAt(0)))
                    .append(parts[i].substring(1));
            if (i < parts.length - 1) sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Returns an RGB int color for each realm used in the elite notification message.
     * Add entries here when new realms are added to {@link MobCultivationRoller#ELITE_WEIGHTS}.
     */
    public static int realmColor(String realmId) {
        return switch (realmId) {
            case "mortal"                    -> 0xAAAAAA; // grey
            case "qi_gathering"              -> 0x55FF55; // green
            case "formation_establishment"   -> 0x00AAFF; // blue
            case "golden_core"               -> 0xFFD700; // gold
            case "nascent_soul"              -> 0xFF6600; // orange
            case "soul_formation"            -> 0xFF2222; // red
            case "void_refinement"           -> 0xCC44FF; // purple
            case "body_integration"          -> 0xFF44CC; // pink
            case "tribulation_transcendence" -> 0xFF0000; // bright red
            case "mahayana"                  -> 0xFFFFFF; // white
            case "earth_immortal"            -> 0xFFD700; // gold
            default                          -> 0xFFFFFF;
        };
    }

    /**
     * Gives the entity a permanent Glowing effect and assigns it to a scoreboard
     * team whose color matches the realm. The glow outline will render in that color.
     *
     * Team names follow the pattern "ascension_elite_<realmId>" so each realm has
     * exactly one team, created once and reused for all mobs of that realm.
     */
    public static void applyEliteGlow(ServerLevel level, LivingEntity entity, String realmId) {
        // Infinite glowing effect (duration Integer.MAX_VALUE), no particles.
        entity.addEffect(new MobEffectInstance(
                MobEffects.GLOWING,
                Integer.MAX_VALUE,
                0,
                false,
                false
        ));

        // Scoreboard team controls the glow outline color.
        String teamName = "ascension_elite_" + realmId;
        PlayerTeam team = level.getScoreboard().getPlayerTeam(teamName);

        if (team == null) {
            team = level.getScoreboard().addPlayerTeam(teamName);
            team.setColor(realmGlowColor(realmId));
            // Hide the team nametag so it doesn't interfere with the mob's own name.
            team.setNameTagVisibility(Team.Visibility.NEVER);
        }

        level.getScoreboard().addPlayerToTeam(entity.getScoreboardName(), team);
    }

    /**
     * Maps each realm to the closest vanilla {@link ChatFormatting} color for the
     * glow outline. Minecraft's glow effect only supports the 16 named colors.
     */
    public static ChatFormatting realmGlowColor(String realmId) {
        return switch (realmId) {
            case "mortal"                    -> ChatFormatting.GRAY;
            case "qi_gathering"              -> ChatFormatting.GREEN;
            case "formation_establishment"   -> ChatFormatting.AQUA;
            case "golden_core"               -> ChatFormatting.YELLOW;
            case "nascent_soul"              -> ChatFormatting.GOLD;
            case "soul_formation"            -> ChatFormatting.RED;
            case "void_refinement"           -> ChatFormatting.LIGHT_PURPLE;
            case "body_integration"          -> ChatFormatting.DARK_PURPLE;
            case "tribulation_transcendence" -> ChatFormatting.DARK_RED;
            case "mahayana"                  -> ChatFormatting.DARK_GREEN;
            case "earth_immortal"            -> ChatFormatting.DARK_BLUE;
            default                          -> ChatFormatting.WHITE;
        };
    }
}