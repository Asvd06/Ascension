package net.thejadeproject.ascension.refactor_packages.paths.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
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

    public void onFoundationBreakthrough(IEntityData entityData, int majorRealm, int foundationStage){

        if(foundationStage <= 0) return;

        entityData.getEntityFormData(ModForms.MORTAL_VESSEL.getId()).getStatSheet().addStatModifier(ModStats.VITALITY.get(),
                new ValueContainerModifier(
                        0.1*foundationStage,
                        ModifierOperation.MULTIPLY_FINAL,
                        ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"vitality_"+foundationStage+"_"+majorRealm),
                        ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"foundation_stats")
                ));
    }
    public void onFoundationDown(IEntityData entityData,int majorRealm,int newStage){


        int oldStage = newStage + 1;
        if(oldStage <= 0) return;
        StatSheet statSheet = entityData.getEntityFormData(ModForms.MORTAL_VESSEL.getId()).getStatSheet();

        statSheet.removeStatModifier(ModStats.VITALITY.get(),ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"vitality_"+oldStage+"_"+majorRealm));

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
}
