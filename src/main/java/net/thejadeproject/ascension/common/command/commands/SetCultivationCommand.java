package net.thejadeproject.ascension.common.command.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.network.PacketDistributor;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.bloodlines.IBloodline;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.attributes.SyncAttributeHolder;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.paths.data.foundation.FoundationPathData;
import net.thejadeproject.ascension.refactor_packages.paths.data.foundation.RealmFoundation;
import net.thejadeproject.ascension.refactor_packages.qi.EntityQiContainer;
import net.thejadeproject.ascension.refactor_packages.physiques.IPhysique;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;


public class SetCultivationCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("cultivation")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("target", EntityArgument.players())
                                .then(Commands.argument("path", ResourceLocationArgument.id())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(
                                                AscensionRegistries.Paths.PATHS_REGISTRY.keySet(), builder))
                                        .then(Commands.argument("majorRealm", IntegerArgumentType.integer(0, 11))
                                                .then(Commands.argument("minorRealm", IntegerArgumentType.integer(0, 9))
                                                        .executes(SetCultivationCommand::setCultivationRealm)
                                                        .then(Commands.argument("progress", IntegerArgumentType.integer(0, 100))
                                                                .executes(SetCultivationCommand::setCultivationRealm)
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("get")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(SetCultivationCommand::getCultivationInfo)
                                .then(Commands.literal("physique")
                                        .executes(SetCultivationCommand::getPhysiqueInfo)
                                )
                        )
                )
                .then(Commands.literal("foundation")
                        .then(Commands.literal("set")
                                .then(Commands.argument("target", EntityArgument.players())
                                        .then(Commands.argument("path", ResourceLocationArgument.id())
                                                .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(
                                                        AscensionRegistries.Paths.PATHS_REGISTRY.keySet(), builder))
                                                .then(Commands.argument("foundationStage", IntegerArgumentType.integer(-4, 5))
                                                        .executes(SetCultivationCommand::setFoundation)
                                                        .then(Commands.argument("progressPercent", IntegerArgumentType.integer(0, 100))
                                                                .executes(SetCultivationCommand::setFoundation)
                                                        )
                                                )
                                        )
                                )
                        )
                );

    }

    private static int setCultivationRealm(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var players = EntityArgument.getPlayers(context, "target");
        ResourceLocation pathId = ResourceLocationArgument.getId(context, "path");
        int majorRealm = IntegerArgumentType.getInteger(context, "majorRealm");
        int minorRealm = IntegerArgumentType.getInteger(context, "minorRealm");

        int progressPercent = -1;
        try {
            progressPercent = IntegerArgumentType.getInteger(context, "progress");
        } catch (IllegalArgumentException e) {
            // Not provided — fine
        }

        if (!isValidPath(pathId)) {
            context.getSource().sendFailure(
                    Component.literal("Unknown path '" + pathId + "'. Use a registered path ID (e.g. ascension:body).")
            );
            return 0;
        }

        int successCount = 0;
        for (ServerPlayer player : players) {
            if (setPlayerCultivationRealm(player, pathId, majorRealm, minorRealm, progressPercent, context.getSource())) {
                successCount++;
            }
        }
        return successCount;
    }

    private static int getCultivationInfo(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "target");
        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        context.getSource().sendSuccess(() ->
                Component.translatable(
                        "command.ascension.cultivation.info.header",
                        player.getName().getString()
                ), false);

        IPhysique physique = entityData.getPhysique();
        if (physique != null) {
            ResourceLocation physiqueId = AscensionRegistries.Physiques.PHSIQUES_REGISTRY.getKey(physique);

            if (physiqueId != null && !physiqueId.toString().equals("minecraft:none")) {
                context.getSource().sendSuccess(() ->
                        Component.translatable(
                                "command.ascension.cultivation.info.physique",
                                physique.getDisplayTitle()
                        ), false);
            }
        }

        IBloodline bloodline = entityData.getBloodline();
        if (bloodline != null) {
            ResourceLocation bloodlineId = AscensionRegistries.Bloodlines.BLOODLINE_REGISTRY.getKey(bloodline);

            context.getSource().sendSuccess(() ->
                    Component.translatable(
                            "command.ascension.cultivation.info.bloodline",
                            bloodline.getDisplayTitle()
                    ), false);

            context.getSource().sendSuccess(() ->
                    Component.translatable(
                            "command.ascension.cultivation.info.bloodline_id",
                            bloodlineId != null ? bloodlineId.toString() : "unknown"
                    ), false);
        }

        EntityQiContainer qi = entityData.getQiContainer();
        double qiRegen = entityData.getAscensionAttributeHolder()
                .getAttribute(net.thejadeproject.ascension.util.ModAttributes.QI_REGEN_RATE)
                .getValue();

        context.getSource().sendSuccess(() ->
                Component.translatable(
                        "command.ascension.cultivation.info.qi",
                        String.format("%.1f", qi.getCurrentQi()),
                        String.format("%.1f", qi.getMaxQi()),
                        String.format("%.2f", qiRegen)
                ), false);

        boolean hasAnyPath = false;

        for (var entry : AscensionRegistries.Paths.PATHS_REGISTRY.entrySet()) {
            ResourceLocation pathId = entry.getKey().location();
            IPathData data = entityData.getPathData(pathId);

            if (data == null) {
                continue;
            }

            hasAnyPath = true;

            var path = entry.getValue();

            Component pathName = Component.translatable(
                    "ascension.path." + pathId.getPath()
            );

            Component realmName = path.getMajorRealmName(data.getMajorRealm());

            context.getSource().sendSuccess(() ->
                    Component.translatable(
                            "command.ascension.cultivation.info.path_header",
                            pathName,
                            realmName,
                            data.getMajorRealm(),
                            data.getMinorRealm()
                    ), false);

            // Technique
            ResourceLocation techniqueId = data.getCurrentTechniqueId();

            if (techniqueId != null) {
                Component techniqueName = Component.translatable(
                        "ascension.technique." + techniqueId.getPath()
                );

                context.getSource().sendSuccess(() ->
                        Component.translatable(
                                "command.ascension.cultivation.info.technique",
                                techniqueName
                        ), false);
            } else {
                context.getSource().sendSuccess(() ->
                        Component.translatable(
                                "command.ascension.cultivation.info.technique.none"
                        ), false);
            }

            // Realm progress
            context.getSource().sendSuccess(() ->
                    Component.translatable(
                            "command.ascension.cultivation.info.progress",
                            String.format("%.2f", data.getCurrentRealmProgress())
                    ), false);

            // Cultivating state
            context.getSource().sendSuccess(() ->
                    Component.translatable(
                            data.isCultivating()
                                    ? "command.ascension.cultivation.info.cultivating.yes"
                                    : "command.ascension.cultivation.info.cultivating.no"
                    ), false);

            // Foundation
            if (data instanceof FoundationPathData foundationPathData) {
                RealmFoundation foundation = foundationPathData.getCurrentFoundation();

                if (foundation != null) {
                    context.getSource().sendSuccess(() ->
                            Component.translatable(
                                    "command.ascension.cultivation.info.foundation",
                                    foundation.getFoundationRealm(),
                                    foundation.getFoundationPercentage()
                            ), false);
                }
            }
        }

        if (!hasAnyPath) {
            context.getSource().sendSuccess(() ->
                    Component.translatable(
                            "command.ascension.cultivation.info.no_paths"
                    ), false);
        }

        return 1;
    }


    private static int getPhysiqueInfo(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "target");
        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        IPhysique physique = entityData.getPhysique();
        if (physique == null) {
            context.getSource().sendFailure(Component.literal(player.getName().getString() + " has no physique."));
            return 0;
        }

        ResourceLocation physiqueId = AscensionRegistries.Physiques.PHSIQUES_REGISTRY.getKey(physique);
        var physiqueData = entityData.getActiveFormData().getPhysiqueData();

        String name = physique.getDisplayTitle().getString();

        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(player.getName().getString()).append(" — Physique ===\n");
        sb.append("  Name: ").append(name).append("\n");
        sb.append("  ID:   ").append(physiqueId != null ? physiqueId : "unknown").append("\n");

        context.getSource().sendSuccess(() -> Component.literal(sb.toString()), false);
        return 1;
    }

    /** Validates against the live registry so any registered path is accepted. */
    private static boolean isValidPath(ResourceLocation path) {
        return AscensionRegistries.Paths.PATHS_REGISTRY.containsKey(
                ResourceKey.create(AscensionRegistries.Paths.PATHS_REGISTRY_KEY, path)
        );
    }

    private static boolean setPlayerCultivationRealm(ServerPlayer player, ResourceLocation pathId,
                                                     int newMajorRealm, int newMinorRealm,
                                                     int progressPercent,
                                                     CommandSourceStack source) {
        try {
            IPathData data = player.getData(ModAttachments.ENTITY_DATA).getPathData(pathId);

            if (data == null) {
                source.sendFailure(Component.literal(
                        player.getName().getString() + " has no data for path " + pathId));
                return false;
            }

            // handleRealmChange is a no-op when lastUsedTechnique is null —
            // warn the operator rather than silently doing nothing.
            if (data.getCurrentTechniqueId() == null) {
                source.sendFailure(Component.literal(
                        player.getName().getString() + " has no technique set on path " + pathId
                                + ". Assign a technique first before setting realm."));
                return false;
            }

            // Capture old values before mutation for feedback message
            int oldMajor = data.getMajorRealm();
            int oldMinor = data.getMinorRealm();

            data.handleRealmChange(newMajorRealm, newMinorRealm,
                    player.getData(ModAttachments.ENTITY_DATA));

            // Set progress if provided — scaled against the technique's max Qi for the
            // realm the player actually ended up at (may be bounded by technique max)
            if (progressPercent >= 0) {
                var technique = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY
                        .get(data.getCurrentTechniqueId());
                double maxQi = technique.getMaxQiForRealm(data.getMajorRealm(), data.getMinorRealm());
                data.setCurrentRealmProgress(maxQi * (progressPercent / 100.0));
            }
            data.sync(player);
            String progressStr = (progressPercent >= 0)
                    ? String.format(" with %d%% progress", progressPercent) : "";

            String feedbackToSource = String.format(
                    "Set %s's %s cultivation to realm %d.%d (was %d.%d)%s",
                    player.getName().getString(),
                    pathId,
                    data.getMajorRealm(), data.getMinorRealm(),
                    oldMajor, oldMinor,
                    progressStr
            );
            source.sendSuccess(() -> Component.literal(feedbackToSource), true);

            if (source.getPlayer() != player) {
                String feedbackToPlayer = String.format(
                        "Your %s cultivation has been set to realm %d.%d%s",
                        pathId, data.getMajorRealm(), data.getMinorRealm(), progressStr
                );
                player.sendSystemMessage(Component.literal(feedbackToPlayer));
            }

            return true;

        } catch (Exception e) {
            source.sendFailure(Component.literal(
                    "Failed to set cultivation for " + player.getName().getString()
                            + ": " + e.getMessage()));
            return false;
        }
    }

    private static int setFoundation(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var players = EntityArgument.getPlayers(context, "target");
        ResourceLocation pathId = ResourceLocationArgument.getId(context, "path");
        int foundationStage = IntegerArgumentType.getInteger(context, "foundationStage");

        int progressPercent = 0;
        try {
            progressPercent = IntegerArgumentType.getInteger(context, "progressPercent");
        } catch (IllegalArgumentException ignored) {
        }

        if (!isValidPath(pathId)) {
            context.getSource().sendFailure(Component.literal("Unknown path: " + pathId));
            return 0;
        }

        int successCount = 0;

        for (ServerPlayer player : players) {
            if (setPlayerFoundation(player, pathId, foundationStage, progressPercent, context.getSource())) {
                successCount++;
            }
        }

        int finalSuccessCount = successCount;
        context.getSource().sendSuccess(
                () -> Component.literal("Set foundation for " + finalSuccessCount + " player(s)."),
                true
        );

        return successCount;
    }

    private static boolean setPlayerFoundation(
            ServerPlayer player,
            ResourceLocation pathId,
            int foundationStage,
            int progressPercent,
            CommandSourceStack source
    ) {
        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        IPathData pathData = entityData.getPathData(pathId);

        if (!(pathData instanceof FoundationPathData foundationPathData)) {
            source.sendFailure(Component.literal(
                    player.getName().getString() + " has no foundation data for path " + pathId
            ));
            return false;
        }

        RealmFoundation foundation = foundationPathData.getCurrentFoundation();

        double targetStability = getTargetFoundationStability(foundationStage, progressPercent);
        double targetProgress = findProgressForStability(foundation, targetStability);

        foundation.setPrimordial(foundationStage == 5);
        foundation.setFoundationProgress(targetProgress, entityData);

        pathData.sync(player);
        refreshAttributes(player, entityData);

        source.sendSuccess(() -> Component.literal(
                "Set " + player.getName().getString() + "'s " + pathId
                        + " foundation to stage " + foundation.getFoundationRealm()
                        + " (" + foundation.getFoundationPercentage() + "%)"
        ), true);

        return true;
    }

    private static double getTargetFoundationStability(int stage, int progressPercent) {
        if (stage == 5) {
            return 1.0;
        }

        double stageStart = stage * 0.25;
        double stageProgress = (progressPercent / 100.0) * 0.25;

        return Math.clamp(stageStart + stageProgress, -1.0, 1.0);
    }

    private static double findProgressForStability(RealmFoundation foundation, double targetStability) {
        double max = foundation.getHandler().getMaxCultivationTicks();

        if (targetStability >= 1.0) return max;
        if (targetStability <= -1.0) return -max;
        if (targetStability == 0.0) return 0;

        double low = 0;
        double high = max;
        double target = Math.abs(targetStability);

        for (int i = 0; i < 64; i++) {
            double mid = (low + high) / 2.0;
            double stability = foundation.getHandler().getStability(mid);

            if (stability < target) {
                low = mid;
            } else {
                high = mid;
            }
        }

        double result = (low + high) / 2.0;
        return targetStability < 0 ? -result : result;
    }

    private static void refreshAttributes(ServerPlayer player, IEntityData entityData) {
        entityData.getAscensionAttributeHolder().updateAttributes(entityData);

        double maxHealth = entityData.getAttributeValue(Attributes.MAX_HEALTH);
        if (maxHealth > 0 && entityData.getHealth() > maxHealth) {
            entityData.setHealth(maxHealth);
        }

        PacketDistributor.sendToPlayer(
                player,
                new SyncAttributeHolder(entityData.getAscensionAttributeHolder())
        );
    }

}