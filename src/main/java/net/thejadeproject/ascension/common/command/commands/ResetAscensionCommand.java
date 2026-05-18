package net.thejadeproject.ascension.common.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.neoforged.neoforge.network.PacketDistributor;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.attributes.AscensionAttributeHolder;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.attributes.SyncAttributeHolder;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.paths.data.foundation.FoundationPathData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.util.ModAttributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ResetAscensionCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("reset")
                .then(Commands.literal("all")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> forEachTarget(ctx, ResetAscensionCommand::resetAll, "Reset all Ascension data for %s player(s)."))))

                .then(Commands.literal("attributes")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> forEachTarget(ctx, ResetAscensionCommand::resetAttributes, "Reset attributes for %s player(s)."))))

                .then(Commands.literal("paths")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> forEachTarget(ctx, ResetAscensionCommand::resetPaths, "Reset paths for %s player(s)."))))

                .then(Commands.literal("path")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("path", ResourceLocationArgument.id())
                                        .suggests(ResetAscensionCommand::suggestPaths)
                                        .executes(ResetAscensionCommand::resetSpecificPath))))

                .then(Commands.literal("techniques")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> forEachTarget(ctx, ResetAscensionCommand::resetTechniques, "Reset techniques for %s player(s)."))))

                .then(Commands.literal("technique")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("path", ResourceLocationArgument.id())
                                        .suggests(ResetAscensionCommand::suggestPaths)
                                        .executes(ResetAscensionCommand::resetSpecificTechnique))))

                .then(Commands.literal("skills")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> forEachTarget(ctx, ResetAscensionCommand::resetSkills, "Reset skills for %s player(s)."))))

                .then(Commands.literal("physique")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> forEachTarget(ctx, ResetAscensionCommand::resetPhysique, "Reset physique for %s player(s)."))))

                .then(Commands.literal("bloodline")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> forEachTarget(ctx, ResetAscensionCommand::resetBloodline, "Reset bloodline for %s player(s)."))))

                .then(Commands.literal("qi")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> forEachTarget(ctx, ResetAscensionCommand::resetQi, "Reset qi for %s player(s)."))))

                .then(Commands.literal("foundations")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> forEachTarget(ctx, ResetAscensionCommand::resetFoundations, "Reset foundations for %s player(s)."))))

                .then(Commands.literal("foundation")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("path", ResourceLocationArgument.id())
                                        .suggests(ResetAscensionCommand::suggestPaths)
                                        .executes(ResetAscensionCommand::resetSpecificFoundation))));
    }

    private static int forEachTarget(
            CommandContext<CommandSourceStack> context,
            ResetAction action,
            String successMessage
    ) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        int successCount = 0;

        for (ServerPlayer player : players) {
            IEntityData data = player.getData(ModAttachments.ENTITY_DATA);

            if (action.apply(player, data, context.getSource())) {
                successCount++;
            }
        }

        int finalSuccessCount = successCount;
        context.getSource().sendSuccess(
                () -> Component.literal(String.format(successMessage, finalSuccessCount)),
                true
        );

        return successCount;
    }

    private static boolean resetAll(ServerPlayer player, IEntityData data, CommandSourceStack source) {
        resetSkills(player, data, source);
        resetPaths(player, data, source);
        resetPhysique(player, data, source);
        resetBloodline(player, data, source);
        resetAttributes(player, data, source);
        resetQi(player, data, source);

        data.setSuppressed(false);

        player.sendSystemMessage(Component.literal("Your Ascension data has been fully reset."));
        return true;
    }

    private static boolean resetSkills(ServerPlayer player, IEntityData data, CommandSourceStack source) {
        Set<ResourceLocation> skills = new HashSet<>(data.getAllSkills());

        for (ResourceLocation skill : skills) {
            data.removeSkill(skill);
        }

        return true;
    }

    private static boolean resetTechniques(ServerPlayer player, IEntityData data, CommandSourceStack source) {
        for (IPathData pathData : new ArrayList<>(data.getAllPathData())) {
            if (pathData == null) continue;
            if (pathData.getCurrentTechniqueId() == null) continue;

            data.removeTechnique(pathData.getPath());
        }

        return true;
    }

    private static boolean resetPaths(ServerPlayer player, IEntityData data, CommandSourceStack source) {
        List<ResourceLocation> paths = new ArrayList<>();

        for (IPathData pathData : new ArrayList<>(data.getAllPathData())) {
            if (pathData == null || pathData.getPath() == null) continue;
            paths.add(pathData.getPath());
        }

        for (ResourceLocation path : paths) {
            data.removePath(path);
        }

        refreshAttributes(player, data);
        return true;
    }

    private static int resetSpecificPath(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ResourceLocation path = ResourceLocationArgument.getId(context, "path");

        if (!isValidPath(path)) {
            context.getSource().sendFailure(Component.literal("Unknown path: " + path));
            return 0;
        }

        return forEachTarget(context, (player, data, source) -> {
            if (!data.hasPath(path)) {
                source.sendFailure(Component.literal(player.getName().getString() + " does not have path " + path));
                return false;
            }

            data.removePath(path);
            refreshAttributes(player, data);
            return true;
        }, "Reset path " + path + " for %s player(s).");
    }

    private static int resetSpecificTechnique(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ResourceLocation path = ResourceLocationArgument.getId(context, "path");

        if (!isValidPath(path)) {
            context.getSource().sendFailure(Component.literal("Unknown path: " + path));
            return 0;
        }

        return forEachTarget(context, (player, data, source) -> {
            if (data.getTechnique(path) == null) {
                source.sendFailure(Component.literal(player.getName().getString() + " has no technique on path " + path));
                return false;
            }

            data.removeTechnique(path);
            refreshAttributes(player, data);
            return true;
        }, "Reset technique on " + path + " for %s player(s).");
    }

    private static boolean resetPhysique(ServerPlayer player, IEntityData data, CommandSourceStack source) {
        data.removePhysique();
        refreshAttributes(player, data);
        return true;
    }

    private static boolean resetBloodline(ServerPlayer player, IEntityData data, CommandSourceStack source) {
        data.removeBloodline();
        refreshAttributes(player, data);
        return true;
    }

    private static boolean resetQi(ServerPlayer player, IEntityData data, CommandSourceStack source) {
        data.getQiContainer().fullFillQi();
        return true;
    }

    private static boolean resetFoundations(ServerPlayer player, IEntityData data, CommandSourceStack source) {
        boolean changed = false;

        for (IPathData pathData : new ArrayList<>(data.getAllPathData())) {
            if (!(pathData instanceof FoundationPathData foundationData)) continue;

            foundationData.resetAllFoundations(data);
            pathData.sync(player);
            changed = true;
        }

        if (changed) {
            refreshAttributes(player, data);
        }

        return changed;
    }

    private static int resetSpecificFoundation(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ResourceLocation path = ResourceLocationArgument.getId(context, "path");

        if (!isValidPath(path)) {
            context.getSource().sendFailure(Component.literal("Unknown path: " + path));
            return 0;
        }

        return forEachTarget(context, (player, data, source) -> {
            IPathData pathData = data.getPathData(path);

            if (!(pathData instanceof FoundationPathData foundationData)) {
                source.sendFailure(Component.literal(player.getName().getString() + " has no foundation data for path " + path));
                return false;
            }

            foundationData.resetCurrentFoundation(data);
            pathData.sync(player);
            refreshAttributes(player, data);
            return true;
        }, "Reset current foundation on " + path + " for %s player(s).");
    }

    private static boolean resetAttributes(ServerPlayer player, IEntityData data, CommandSourceStack source) {
        resetVanillaAttributeShell(player);

        AscensionAttributeHolder holder = new AscensionAttributeHolder(player);
        data.setAscensionAttributeHolder(player, holder);

        data.addDefaultAttributes(player);
        data.getAscensionAttributeHolder().updateAttributes(data);

        double maxHealth = data.getAttributeValue(Attributes.MAX_HEALTH);
        data.setHealth(maxHealth > 0 ? maxHealth : player.getMaxHealth());
        player.setAbsorptionAmount(0);

        PacketDistributor.sendToPlayer(player, new SyncAttributeHolder(data.getAscensionAttributeHolder()));
        return true;
    }

    private static void resetVanillaAttributeShell(ServerPlayer player) {
        resetAttribute(player, Attributes.MAX_HEALTH);
        resetAttribute(player, Attributes.JUMP_STRENGTH);
        resetAttribute(player, Attributes.KNOCKBACK_RESISTANCE);
        resetAttribute(player, Attributes.MOVEMENT_SPEED);
        resetAttribute(player, Attributes.FLYING_SPEED);
        resetAttribute(player, Attributes.ATTACK_DAMAGE);
        resetAttribute(player, Attributes.ATTACK_KNOCKBACK);
        resetAttribute(player, Attributes.ATTACK_SPEED);
        resetAttribute(player, Attributes.ARMOR);
        resetAttribute(player, Attributes.ARMOR_TOUGHNESS);
        resetAttribute(player, Attributes.LUCK);
        resetAttribute(player, Attributes.MAX_ABSORPTION);
        resetAttribute(player, Attributes.WATER_MOVEMENT_EFFICIENCY);
        resetAttribute(player, Attributes.STEP_HEIGHT);
        resetAttribute(player, Attributes.MINING_EFFICIENCY);
        resetAttribute(player, Attributes.SAFE_FALL_DISTANCE);

        resetAttribute(player, ModAttributes.MAX_QI);
        resetAttribute(player, ModAttributes.QI_REGEN_RATE);
    }

    private static void resetAttribute(ServerPlayer player, Holder<Attribute> attribute) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;

        for (AttributeModifier modifier : new ArrayList<>(instance.getModifiers())) {
            instance.removeModifier(modifier);
        }

        instance.setBaseValue(getPlayerDefault(attribute));
    }

    private static double getPlayerDefault(Holder<Attribute> attribute) {
        try {
            return DefaultAttributes.getSupplier(EntityType.PLAYER).getBaseValue(attribute);
        } catch (Exception ignored) {
            return attribute.value().getDefaultValue();
        }
    }

    private static void refreshAttributes(ServerPlayer player, IEntityData data) {
        if (data.getActiveFormData() == null) return;

        data.getAscensionAttributeHolder().updateAttributes(data);

        double maxHealth = data.getAttributeValue(Attributes.MAX_HEALTH);
        if (maxHealth > 0 && data.getHealth() > maxHealth) {
            data.setHealth(maxHealth);
        }

        PacketDistributor.sendToPlayer(player, new SyncAttributeHolder(data.getAscensionAttributeHolder()));
    }

    private static CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggestPaths(
            CommandContext<CommandSourceStack> context,
            com.mojang.brigadier.suggestion.SuggestionsBuilder builder
    ) {
        return SharedSuggestionProvider.suggestResource(
                AscensionRegistries.Paths.PATHS_REGISTRY.keySet(),
                builder
        );
    }

    private static boolean isValidPath(ResourceLocation path) {
        return AscensionRegistries.Paths.PATHS_REGISTRY.containsKey(
                ResourceKey.create(AscensionRegistries.Paths.PATHS_REGISTRY_KEY, path)
        );
    }

    @FunctionalInterface
    private interface ResetAction {
        boolean apply(ServerPlayer player, IEntityData data, CommandSourceStack source);
    }
}