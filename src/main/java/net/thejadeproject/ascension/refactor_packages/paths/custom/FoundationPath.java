package net.thejadeproject.ascension.refactor_packages.paths.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.IEntityFormData;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.attributes.SyncAttributeHolder;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.paths.data.SimplePathData;
import net.thejadeproject.ascension.refactor_packages.paths.data.foundation.FoundationPathData;
import net.thejadeproject.ascension.refactor_packages.paths.data.foundation.stability.IStabilityHandler;
import net.thejadeproject.ascension.refactor_packages.paths.data.foundation.stability.LnStabilityHandler;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.stats.StatSheet;
import net.thejadeproject.ascension.refactor_packages.stats.custom.ModStats;
import net.thejadeproject.ascension.refactor_packages.util.value_modifiers.ModifierOperation;
import net.thejadeproject.ascension.refactor_packages.util.value_modifiers.ValueContainerModifier;

import java.util.HashMap;

public class FoundationPath extends GenericPath{
    private IStabilityHandler stabilityHandler = new LnStabilityHandler(100000);
    private HashMap<Integer,IStabilityHandler> realmHandlers = new HashMap<>();
    public FoundationPath(Component title) {
        super(title);

    }

    public FoundationPath addFoundationRequirement(int realm,int maxProgress){
        realmHandlers.put(realm,new LnStabilityHandler(maxProgress));
        return this;
    }
    public IStabilityHandler getStabilityHandler(int realm){

        return realmHandlers.containsKey(realm)?realmHandlers.get(realm):stabilityHandler;
    }

    public void onFoundationBreakthrough(
            IEntityData entityData,
            int majorRealm,
            int foundationStage
    ) {
        if (foundationStage <= 0) return;

        StatSheet statSheet = entityData.getEntityFormData(ModForms.MORTAL_VESSEL.getId()).getStatSheet();

        ResourceLocation modifierGroup = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "foundation_stats");

        double amount = 0.1 * foundationStage;

        // removing the old modifier ids, so they don't get counted again...
        statSheet.removeStatModifier(ModStats.VITALITY.get(), ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "vit_" + foundationStage + "_" + majorRealm));
        statSheet.removeStatModifier(ModStats.INTELLIGENCE.get(), ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "int_" + foundationStage + "_" + majorRealm));
        statSheet.removeStatModifier(ModStats.AGILITY.get(), ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "agi_" + foundationStage + "_" + majorRealm));
        statSheet.removeStatModifier(ModStats.STRENGTH.get(), ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "str_" + foundationStage + "_" + majorRealm));

        statSheet.addStatModifier(ModStats.VITALITY.get(), new ValueContainerModifier(
                amount,
                ModifierOperation.MULTIPLY_FINAL,
                foundationModifierId("vit", majorRealm, foundationStage),
                modifierGroup)
        );
        statSheet.addStatModifier(ModStats.INTELLIGENCE.get(), new ValueContainerModifier(
                amount,
                ModifierOperation.MULTIPLY_FINAL,
                foundationModifierId("int", majorRealm, foundationStage),
                modifierGroup)
        );
        statSheet.addStatModifier(ModStats.AGILITY.get(), new ValueContainerModifier(
                amount,
                ModifierOperation.MULTIPLY_FINAL,
                foundationModifierId("agi", majorRealm, foundationStage),
                modifierGroup)
        );
        statSheet.addStatModifier(ModStats.STRENGTH.get(), new ValueContainerModifier(
                amount,
                ModifierOperation.MULTIPLY_FINAL,
                foundationModifierId("str", majorRealm, foundationStage),
                modifierGroup)
        );

        if(entityData.getAttachedEntity() instanceof ServerPlayer player && player.connection != null){

            PacketDistributor.sendToPlayer(player,
                    new SyncAttributeHolder(entityData.getAscensionAttributeHolder()));
            for (IEntityFormData formData : entityData.getFormData()) {
                formData.getStatSheet().sync(player, formData.getEntityFormId());
            }
        }
    }

    public void onFoundationDown(
            IEntityData entityData,
            int majorRealm,
            int newStage
    ) {
        int oldStage = newStage + 1;
        if (oldStage <= 0) return;

        StatSheet statSheet = entityData.getEntityFormData(ModForms.MORTAL_VESSEL.getId()).getStatSheet();

        statSheet.removeStatModifier(ModStats.VITALITY.get(), foundationModifierId("vit", majorRealm, oldStage));
        statSheet.removeStatModifier(ModStats.INTELLIGENCE.get(), foundationModifierId("int", majorRealm, oldStage));
        statSheet.removeStatModifier(ModStats.AGILITY.get(), foundationModifierId("agi", majorRealm, oldStage));
        statSheet.removeStatModifier(ModStats.STRENGTH.get(), foundationModifierId("str", majorRealm, oldStage));

        // removing old ones again, if they were missed on the foundation breakthrough
        statSheet.removeStatModifier(ModStats.VITALITY.get(), ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "vit_" + oldStage + "_" + majorRealm));
        statSheet.removeStatModifier(ModStats.INTELLIGENCE.get(), ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "int_" + oldStage + "_" + majorRealm));
        statSheet.removeStatModifier(ModStats.AGILITY.get(), ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "agi_" + oldStage + "_" + majorRealm));
        statSheet.removeStatModifier(ModStats.STRENGTH.get(), ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "str_" + oldStage + "_" + majorRealm));

        if(entityData.getAttachedEntity() instanceof ServerPlayer player && player.connection != null){

            PacketDistributor.sendToPlayer(player,
                    new SyncAttributeHolder(entityData.getAscensionAttributeHolder()));
            for (IEntityFormData formData : entityData.getFormData()) {
                formData.getStatSheet().sync(player, formData.getEntityFormId());
            }
        }

    }

    @Override
    public IPathData freshPathData(IEntityData heldEntity) {
        return new FoundationPathData(AscensionRegistries.Paths.PATHS_REGISTRY.getKey(this));
    }
    @Override
    public IPathData fromCompound(CompoundTag tag, IEntityData heldEntity) {
        //todo handle cultivation data simulations
        IPathData pathData = freshPathData(heldEntity);
        heldEntity.addPathData(AscensionRegistries.Paths.PATHS_REGISTRY.getKey(this),pathData);
        pathData = heldEntity.getPathData(AscensionRegistries.Paths.PATHS_REGISTRY.getKey(this)); //makes sure we are modifying the saved instance
        pathData.load(tag,heldEntity);
        return pathData;
    }
    @Override
    public IPathData fromNetwork(RegistryFriendlyByteBuf buf) {
        IPathData pathData = new FoundationPathData(AscensionRegistries.Paths.PATHS_REGISTRY.getKey(this));
        pathData.load(buf);
        return pathData;
    }
    public double foundationBuildingSpeed(){
        return 1;
    }

    private ResourceLocation foundationModifierId(
            String stat,
            int majorRealm,
            int foundationStage
    ) {
        ResourceLocation pathId =
                AscensionRegistries.Paths.PATHS_REGISTRY.getKey(this);

        return ResourceLocation.fromNamespaceAndPath(
                AscensionCraft.MOD_ID,
                pathId.getPath()
                        + "_foundation_"
                        + stat
                        + "_"
                        + majorRealm
                        + "_"
                        + foundationStage
        );
    }

    @Override
    public boolean requiresTribulation() {
        return true;
    }
}
