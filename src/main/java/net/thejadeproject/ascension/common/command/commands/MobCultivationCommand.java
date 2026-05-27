package net.thejadeproject.ascension.common.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.mob_cultivation.*;
import net.thejadeproject.ascension.mob_cultivation.events.MobCultivationEvents;
import net.thejadeproject.ascension.mob_cultivation.util.EliteGearApplier;
import net.thejadeproject.ascension.mob_cultivation.util.MobCultivationCommandHelper;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public final class MobCultivationCommand {
    private MobCultivationCommand() {}

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("mob")

                // ------------------------------------------------------------------
                // /ascension mob summon <entity> <realm> <stage> [pos]
                // Summons a mob at a specific cultivation rank.
                // ------------------------------------------------------------------
                .then(Commands.literal("summon")
                        .then(Commands.argument("entity", ResourceLocationArgument.id())
                                .suggests((context, builder) -> {
                                    BuiltInRegistries.ENTITY_TYPE.keySet()
                                            .forEach(id -> builder.suggest(id.toString()));
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("realm", word())
                                        .suggests((context, builder) -> {
                                            MobCultivationList.getRealmIds()
                                                    .forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("stage", integer(1, 3))
                                                .executes(ctx -> summon(
                                                        ctx.getSource(),
                                                        ResourceLocationArgument.getId(ctx, "entity"),
                                                        getString(ctx, "realm"),
                                                        getInteger(ctx, "stage"),
                                                        ctx.getSource().getPosition()
                                                ))
                                                .then(Commands.argument("pos", Vec3Argument.vec3())
                                                        .executes(ctx -> summon(
                                                                ctx.getSource(),
                                                                ResourceLocationArgument.getId(ctx, "entity"),
                                                                getString(ctx, "realm"),
                                                                getInteger(ctx, "stage"),
                                                                Vec3Argument.getVec3(ctx, "pos")
                                                        ))
                                                )
                                        )
                                )
                        )
                )

                // ------------------------------------------------------------------
                // /ascension mob set <target> <realm> <stage>
                // Sets the cultivation rank of an existing entity.
                // ------------------------------------------------------------------
                .then(Commands.literal("set")
                        .then(Commands.argument("target", EntityArgument.entity())
                                .then(Commands.argument("realm", word())
                                        .suggests((context, builder) -> {
                                            MobCultivationList.getRealmIds()
                                                    .forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("stage", integer(1, 3))
                                                .executes(ctx -> set(
                                                        ctx.getSource(),
                                                        EntityArgument.getEntity(ctx, "target"),
                                                        getString(ctx, "realm"),
                                                        getInteger(ctx, "stage")
                                                ))
                                        )
                                )
                        )
                )

                // ------------------------------------------------------------------
                // /ascension mob stats <target>
                // Prints full cultivation and attribute stats for an entity.
                // ------------------------------------------------------------------
                .then(Commands.literal("stats")
                        .then(Commands.argument("target", EntityArgument.entity())
                                .executes(ctx -> getStats(
                                        ctx.getSource(),
                                        EntityArgument.getEntity(ctx, "target")
                                ))
                        )
                )

                // ------------------------------------------------------------------
                // /ascension mob forcespawn <entity> [realm] [stage] [pos]
                // Forces an elite mob to spawn. If realm/stage omitted, resolves
                // relative to nearest player (old behavior). If provided, spawns
                // at exactly that rank with full elite treatment (gear, glow, notify).
                // ------------------------------------------------------------------
                .then(Commands.literal("forcespawn")
                        .then(Commands.argument("entity", ResourceLocationArgument.id())
                                .suggests((context, builder) -> {
                                    BuiltInRegistries.ENTITY_TYPE.keySet()
                                            .forEach(id -> builder.suggest(id.toString()));
                                    return builder.buildFuture();
                                })
                                // forcespawn <entity> — auto-resolve from nearest player
                                .executes(ctx -> forceSpawn(
                                        ctx.getSource(),
                                        ResourceLocationArgument.getId(ctx, "entity"),
                                        null, 0,
                                        ctx.getSource().getPosition()
                                ))
                                // forcespawn <entity> <realm> <stage> — specific rank
                                .then(Commands.argument("realm", word())
                                        .suggests((context, builder) -> {
                                            MobCultivationList.getRealmIds()
                                                    .forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("stage", integer(1, 3))
                                                .executes(ctx -> forceSpawn(
                                                        ctx.getSource(),
                                                        ResourceLocationArgument.getId(ctx, "entity"),
                                                        getString(ctx, "realm"),
                                                        getInteger(ctx, "stage"),
                                                        ctx.getSource().getPosition()
                                                ))
                                                .then(Commands.argument("pos", Vec3Argument.vec3())
                                                        .executes(ctx -> forceSpawn(
                                                                ctx.getSource(),
                                                                ResourceLocationArgument.getId(ctx, "entity"),
                                                                getString(ctx, "realm"),
                                                                getInteger(ctx, "stage"),
                                                                Vec3Argument.getVec3(ctx, "pos")
                                                        ))
                                                )
                                        )
                                )
                                // forcespawn <entity> [pos] — auto-resolve with custom pos
                                .then(Commands.argument("pos", Vec3Argument.vec3())
                                        .executes(ctx -> forceSpawn(
                                                ctx.getSource(),
                                                ResourceLocationArgument.getId(ctx, "entity"),
                                                null, 0,
                                                Vec3Argument.getVec3(ctx, "pos")
                                        ))
                                )
                        )
                )

                // ------------------------------------------------------------------
                // /ascension mob elite <entity> <realm> <stage> [pos]
                // Guaranteed elite spawn at specific rank with full gear + notification.
                // Alias for forcespawn with required rank — more explicit naming.
                // ------------------------------------------------------------------
                .then(Commands.literal("elite")
                        .then(Commands.argument("entity", ResourceLocationArgument.id())
                                .suggests((context, builder) -> {
                                    BuiltInRegistries.ENTITY_TYPE.keySet()
                                            .forEach(id -> builder.suggest(id.toString()));
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("realm", word())
                                        .suggests((context, builder) -> {
                                            MobCultivationList.getRealmIds()
                                                    .forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("stage", integer(1, 3))
                                                .executes(ctx -> spawnElite(
                                                        ctx.getSource(),
                                                        ResourceLocationArgument.getId(ctx, "entity"),
                                                        getString(ctx, "realm"),
                                                        getInteger(ctx, "stage"),
                                                        ctx.getSource().getPosition()
                                                ))
                                                .then(Commands.argument("pos", Vec3Argument.vec3())
                                                        .executes(ctx -> spawnElite(
                                                                ctx.getSource(),
                                                                ResourceLocationArgument.getId(ctx, "entity"),
                                                                getString(ctx, "realm"),
                                                                getInteger(ctx, "stage"),
                                                                Vec3Argument.getVec3(ctx, "pos")
                                                        ))
                                                )
                                        )
                                )
                        )
                );
    }

    // -------------------------------------------------------------------------
    // Command implementations
    // -------------------------------------------------------------------------

    private static int summon(
            CommandSourceStack source,
            ResourceLocation entityId,
            String realmId,
            int stage,
            Vec3 pos
    ) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityId);
        if (type == null) {
            source.sendFailure(Component.literal("Unknown entity type: " + entityId));
            return 0;
        }

        Entity entity = type.create(source.getLevel());
        if (!(entity instanceof LivingEntity living)) {
            source.sendFailure(Component.literal("Entity is not a LivingEntity: " + entityId));
            return 0;
        }

        living.moveTo(pos.x, pos.y, pos.z, source.getRotation().y, 0.0F);

        if (!MobCultivationCommandHelper.applyCultivation(living, realmId, stage)) {
            source.sendFailure(Component.literal("Failed to apply mob cultivation."));
            return 0;
        }

        source.getLevel().addFreshEntity(living);
        source.sendSuccess(
                () -> Component.literal(
                        "Summoned " + entityId + " with " + realmId + " stage " + stage),
                true
        );
        return 1;
    }

    private static int set(CommandSourceStack source, Entity entity, String realmId, int stage) {
        if (!(entity instanceof LivingEntity living)) {
            source.sendFailure(Component.literal("Target is not a LivingEntity."));
            return 0;
        }

        if (!MobCultivationCommandHelper.applyCultivation(living, realmId, stage)) {
            source.sendFailure(Component.literal("Failed to apply mob cultivation."));
            return 0;
        }

        source.sendSuccess(
                () -> Component.literal(
                        "Set " + living.getName().getString() + " to " + realmId + " stage " + stage),
                true
        );
        return 1;
    }

    private static int getStats(CommandSourceStack source, Entity entity) {
        if (!(entity instanceof LivingEntity living)) {
            source.sendFailure(Component.literal("Target is not a LivingEntity."));
            return 0;
        }

        source.sendSuccess(() -> MobCultivationCommandHelper.getStatsMessage(living), false);
        return 1;
    }

    /**
     * Spawns an entity and resolves its rank.
     * If realmId is null, resolves relative to nearest player (old behavior).
     * If realmId is provided, forces that exact rank with full elite treatment.
     */
    private static int forceSpawn(
            CommandSourceStack source,
            ResourceLocation entityId,
            String realmId,
            int stage,
            Vec3 pos
    ) {
        ServerLevel level = source.getLevel();

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityId);
        if (type == null) {
            source.sendFailure(Component.literal("Unknown entity type: " + entityId));
            return 0;
        }

        Entity entity = type.create(level);
        if (!(entity instanceof LivingEntity living)) {
            source.sendFailure(Component.literal("Entity is not a LivingEntity: " + entityId));
            return 0;
        }

        if (!MobCultivationRoller.canHaveRank(living)) {
            source.sendFailure(Component.literal(entityId + " cannot have a cultivation rank."));
            return 0;
        }

        living.moveTo(pos.x, pos.y, pos.z, 0.0F, 0.0F);

        MobCultivationDefinition definition;

        if (realmId != null && MobCultivationList.isValidRealm(realmId)) {
            // Explicit rank provided — use it directly
            definition = MobCultivationList.get(realmId, stage);
        } else {
            // Auto-resolve from nearest player
            definition = MobCultivationResolver.resolveAroundNearbyPlayer(living);

            if (definition == null) {
                Player nearest = level.getNearestPlayer(pos.x, pos.y, pos.z, -1, false);
                if (nearest != null) {
                    definition = MobCultivationResolver.resolveFromPlayer(nearest);
                } else {
                    definition = MobCultivationList.getFirst();
                }
            }
        }

        MobCultivationData data = living.getData(ModAttachments.MOB_RANK);
        if (data == null) {
            source.sendFailure(Component.literal("Entity has no cultivation data attachment."));
            return 0;
        }

        MobCultivationEvents.assignRank(living, data, definition);
        level.addFreshEntity(living);

        // Apply elite gear and notification for above-mortal ranks
        int power = MobCultivationResolver.getRankPower(
                definition.realmId(), definition.stage());
        if (power > 0) {
            EliteGearApplier.applyGear(level, living, definition);
            MobCultivationEvents.sendEliteSpawnNotification(level, living, definition);
        }

        final MobCultivationDefinition finalDef = definition;
        final boolean wasExplicit = realmId != null;
        source.sendSuccess(
                () -> Component.literal(
                        (wasExplicit ? "Force-spawned elite " : "Force-spawned ")
                                + entityId
                                + " at [" + (int)pos.x + " " + (int)pos.y + " " + (int)pos.z + "]"
                                + " as " + finalDef.realmId() + " stage " + finalDef.stage()),
                true
        );
        return 1;
    }

    /**
     * Explicit elite spawn — always applies gear, glow, and notification.
     * Works even for mortal rank (useful for testing gear on low-tier mobs).
     */
    private static int spawnElite(
            CommandSourceStack source,
            ResourceLocation entityId,
            String realmId,
            int stage,
            Vec3 pos
    ) {
        ServerLevel level = source.getLevel();

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityId);
        if (type == null) {
            source.sendFailure(Component.literal("Unknown entity type: " + entityId));
            return 0;
        }

        Entity entity = type.create(level);
        if (!(entity instanceof LivingEntity living)) {
            source.sendFailure(Component.literal("Entity is not a LivingEntity: " + entityId));
            return 0;
        }

        if (!MobCultivationRoller.canHaveRank(living)) {
            source.sendFailure(Component.literal(entityId + " cannot have a cultivation rank."));
            return 0;
        }

        if (!MobCultivationList.isValidRealm(realmId)) {
            source.sendFailure(Component.literal("Invalid realm: " + realmId));
            return 0;
        }

        living.moveTo(pos.x, pos.y, pos.z, 0.0F, 0.0F);

        MobCultivationDefinition definition = MobCultivationList.get(realmId, stage);

        MobCultivationData data = living.getData(ModAttachments.MOB_RANK);
        if (data == null) {
            source.sendFailure(Component.literal("Entity has no cultivation data attachment."));
            return 0;
        }

        MobCultivationEvents.assignRank(living, data, definition);
        level.addFreshEntity(living);

        // Always apply elite treatment regardless of rank
        EliteGearApplier.applyGear(level, living, definition);
        MobCultivationEvents.sendEliteSpawnNotification(level, living, definition);

        source.sendSuccess(
                () -> Component.literal(
                        "Spawned elite " + entityId
                                + " at [" + (int)pos.x + " " + (int)pos.y + " " + (int)pos.z + "]"
                                + " as " + definition.realmId() + " stage " + definition.stage()),
                true
        );
        return 1;
    }
}