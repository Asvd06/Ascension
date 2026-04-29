package net.thejadeproject.ascension.common.command.cultivation;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
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
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.IEntityFormData;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.SyncEntityForm;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.skills.SyncHeldSkills;
import net.thejadeproject.ascension.refactor_packages.paths.PathData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skills.HeldSkill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ResetAscensionCommand {

    // DOES NOT WORK
    // TODO: IMPLEMENT REMOVE METHODS IN GENERIC ENTITY DATA

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ascensionreset")
                .requires(source -> source.hasPermission(2))

                .then(Commands.literal("all")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ResetAscensionCommand::resetAll)
                        )
                )

                .then(Commands.literal("attributes")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ResetAscensionCommand::resetAttributesOnly)
                        )
                )

                .then(Commands.literal("paths")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ResetAscensionCommand::resetAllPaths)
                        )
                )

                .then(Commands.literal("path")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("path", StringArgumentType.string())
                                        .suggests(ResetAscensionCommand::suggestPaths)
                                        .executes(ResetAscensionCommand::resetSpecificPath)
                                )
                        )
                )

                .then(Commands.literal("skills")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ResetAscensionCommand::resetAllSkills)
                        )
                )

                .then(Commands.literal("techniques")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ResetAscensionCommand::resetAllTechniques)
                        )
                )

                .then(Commands.literal("technique")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("path", StringArgumentType.string())
                                        .suggests(ResetAscensionCommand::suggestPaths)
                                        .executes(ResetAscensionCommand::resetSpecificTechnique)
                                )
                        )
                )

                .then(Commands.literal("physique")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ResetAscensionCommand::resetPhysique)
                        )
                )
        );
    }

    // handlers
    private static int resetAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        int successCount = 0;

        for (ServerPlayer player : players) {
            IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

            resetPlayerSkills(player, entityData);
            resetPlayerTechniques(entityData);
            resetPlayerPaths(entityData);
            resetPlayerPhysique(entityData);
            resetPlayerVanillaAttributes(player);

            syncAllForms(player, entityData);

            player.sendSystemMessage(Component.literal("Your Ascension data was reset."));
            successCount++;
        }

        sendCount(context, "Reset all Ascension data for", successCount);
        return successCount;
    }

    private static int resetAttributesOnly(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        int successCount = 0;

        for (ServerPlayer player : players) {
            resetPlayerVanillaAttributes(player);

            IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

            entityData.addDefaultAttributes(player);

            player.sendSystemMessage(Component.literal("Your vanilla attributes were reset."));
            successCount++;
        }

        sendCount(context, "Reset attributes for", successCount);
        return successCount;
    }

    private static int resetAllPaths(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        int successCount = 0;

        for (ServerPlayer player : players) {
            IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

            resetPlayerPaths(entityData);
            syncAllForms(player, entityData);

            player.sendSystemMessage(Component.literal("Your cultivation paths were reset."));
            successCount++;
        }

        sendCount(context, "Reset paths for", successCount);
        return successCount;
    }

    private static int resetSpecificPath(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        ResourceLocation pathId = normalizeId(StringArgumentType.getString(context, "path"));

        if (!isValidPath(pathId)) {
            context.getSource().sendFailure(Component.literal("Invalid path: " + pathId));
            return 0;
        }

        int successCount = 0;

        for (ServerPlayer player : players) {
            IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

            if (!entityData.hasPath(pathId)) {
                context.getSource().sendFailure(Component.literal(
                        player.getName().getString() + " does not have path " + pathId
                ));
                continue;
            }

            entityData.removePath(pathId);
            syncAllForms(player, entityData);

            player.sendSystemMessage(Component.literal("Your path was reset: " + pathId));
            successCount++;
        }

        sendCount(context, "Reset path " + pathId + " for", successCount);
        return successCount;
    }

    private static int resetAllSkills(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        int successCount = 0;

        for (ServerPlayer player : players) {
            IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

            resetPlayerSkills(player, entityData);
            syncAllForms(player, entityData);

            player.sendSystemMessage(Component.literal("Your skills were reset."));
            successCount++;
        }

        sendCount(context, "Reset skills for", successCount);
        return successCount;
    }

    private static int resetAllTechniques(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        int successCount = 0;

        for (ServerPlayer player : players) {
            IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

            resetPlayerTechniques(entityData);
            syncAllForms(player, entityData);

            player.sendSystemMessage(Component.literal("Your techniques were reset."));
            successCount++;
        }

        sendCount(context, "Reset techniques for", successCount);
        return successCount;
    }

    private static int resetSpecificTechnique(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        ResourceLocation pathId = normalizeId(StringArgumentType.getString(context, "path"));

        if (!isValidPath(pathId)) {
            context.getSource().sendFailure(Component.literal("Invalid path: " + pathId));
            return 0;
        }

        int successCount = 0;

        for (ServerPlayer player : players) {
            IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

            if (entityData.getTechnique(pathId) == null) {
                context.getSource().sendFailure(Component.literal(
                        player.getName().getString() + " has no technique for path " + pathId
                ));
                continue;
            }

            entityData.removeTechnique(pathId);
            syncAllForms(player, entityData);

            player.sendSystemMessage(Component.literal("Your technique was reset for path: " + pathId));
            successCount++;
        }

        sendCount(context, "Reset technique for path " + pathId + " for", successCount);
        return successCount;
    }

    private static int resetPhysique(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        int successCount = 0;

        for (ServerPlayer player : players) {
            IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

            resetPlayerPhysique(entityData);
            syncAllForms(player, entityData);

            player.sendSystemMessage(Component.literal("Your physique was reset."));
            successCount++;
        }

        sendCount(context, "Reset physique for", successCount);
        return successCount;
    }

    // helpers
    private static void resetPlayerSkills(ServerPlayer player, IEntityData entityData) {
        for (IEntityFormData formData : new ArrayList<>(entityData.getFormData())) {
            ResourceLocation formId = formData.getEntityFormId();

            List<ResourceLocation> skillsToRemove = new ArrayList<>();

            for (HeldSkill heldSkill : formData.getHeldSkills().getSkills()) {
                skillsToRemove.add(heldSkill.getKey());
            }

            for (ResourceLocation skillId : skillsToRemove) {
                entityData.removeSkill(skillId, formId);
            }

            syncHeldSkills(player, formId);
        }
    }

    private static void resetPlayerTechniques(IEntityData entityData) {
        List<ResourceLocation> pathsToReset = new ArrayList<>();

        for (PathData pathData : entityData.getAllPathData()) {
            if (pathData != null && entityData.getTechnique(pathData.getPath()) != null) {
                pathsToReset.add(pathData.getPath());
            }
        }

        for (ResourceLocation pathId : pathsToReset) {
            entityData.removeTechnique(pathId);
        }
    }

    private static void resetPlayerPaths(IEntityData entityData) {
        List<ResourceLocation> pathsToRemove = new ArrayList<>();

        for (PathData pathData : entityData.getAllPathData()) {
            if (pathData != null) {
                pathsToRemove.add(pathData.getPath());
            }
        }

        for (ResourceLocation pathId : pathsToRemove) {
            entityData.removePath(pathId);
        }
    }

    private static void resetPlayerPhysique(IEntityData entityData) {
        if (entityData.getPhysique() != null) {
            entityData.removePhysique();
        }
    }

    private static void resetPlayerVanillaAttributes(ServerPlayer player) {
        resetToEntityDefault(player, Attributes.MAX_HEALTH);
        resetToEntityDefault(player, Attributes.JUMP_STRENGTH);
        resetToEntityDefault(player, Attributes.KNOCKBACK_RESISTANCE);
        resetToEntityDefault(player, Attributes.MOVEMENT_SPEED);
        resetToEntityDefault(player, Attributes.FLYING_SPEED);
        resetToEntityDefault(player, Attributes.ATTACK_DAMAGE);
        resetToEntityDefault(player, Attributes.ATTACK_KNOCKBACK);
        resetToEntityDefault(player, Attributes.ATTACK_SPEED);
        resetToEntityDefault(player, Attributes.ARMOR);
        resetToEntityDefault(player, Attributes.ARMOR_TOUGHNESS);
        resetToEntityDefault(player, Attributes.LUCK);
        resetToEntityDefault(player, Attributes.MAX_ABSORPTION);

        player.setHealth(player.getMaxHealth());
        player.setAbsorptionAmount(0.0F);
    }

    private static void resetToEntityDefault(ServerPlayer player, Holder<Attribute> attributeHolder) {
        AttributeInstance instance = player.getAttribute(attributeHolder);
        if (instance == null) return;

        for (AttributeModifier modifier : new ArrayList<>(instance.getModifiers())) {
            instance.removeModifier(modifier);
        }

        double defaultValue = DefaultAttributes.getSupplier(EntityType.PLAYER).getBaseValue(attributeHolder);
        instance.setBaseValue(defaultValue);
    }

    // sync
    private static void syncHeldSkills(ServerPlayer player, ResourceLocation formId) {
        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        IEntityFormData formData = entityData.getEntityFormData(formId);

        if (formData == null) return;

        PacketDistributor.sendToPlayer(
                player,
                new SyncHeldSkills(formId.toString(), formData.getHeldSkills())
        );
    }

    private static void syncAllForms(ServerPlayer player, IEntityData entityData) {
        for (IEntityFormData formData : entityData.getFormData()) {
            PacketDistributor.sendToPlayer(player, new SyncEntityForm(formData));
        }
    }

    private static CompletableFuture<Suggestions> suggestPaths(
            CommandContext<CommandSourceStack> context,
            SuggestionsBuilder builder
    ) {
        List<String> suggestions = new ArrayList<>();

        AscensionRegistries.Paths.PATHS_REGISTRY.keySet()
                .forEach(loc -> suggestions.add(loc.toString()));

        return SharedSuggestionProvider.suggest(suggestions, builder);
    }

    private static ResourceLocation normalizeId(String input) {
        try {
            if (input.contains(":")) {
                return ResourceLocation.parse(input);
            }

            return ResourceLocation.fromNamespaceAndPath("ascension", input);
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isValidPath(ResourceLocation pathId) {
        if (pathId == null) return false;

        return AscensionRegistries.Paths.PATHS_REGISTRY.containsKey(
                ResourceKey.create(AscensionRegistries.Paths.PATHS_REGISTRY_KEY, pathId)
        );
    }

    private static void sendCount(CommandContext<CommandSourceStack> context, String action, int successCount) {
        context.getSource().sendSuccess(() ->
                Component.literal(action + " " + successCount + " player(s)."), true);
    }
}