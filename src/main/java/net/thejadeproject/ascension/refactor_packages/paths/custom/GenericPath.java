package net.thejadeproject.ascension.refactor_packages.paths.custom;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Panda;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
import net.thejadeproject.ascension.refactor_packages.gui.elements.info_elements.PathDataDisplayElement;
import net.thejadeproject.ascension.refactor_packages.paths.IPath;
import net.thejadeproject.ascension.refactor_packages.paths.PathData;
import net.thejadeproject.ascension.refactor_packages.paths.PathInteraction;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GenericPath implements IPath {
    private final Component title;
    private Component pathDescription;
    private HashMap<ResourceLocation, Double> destructive = new HashMap<>();
    private HashMap<ResourceLocation, Double> generative = new HashMap<>();
    private HashMap<ResourceLocation, Double> related = new HashMap<>();
    private ArrayList<Component> realmNames = new ArrayList<>();
    public GenericPath(Component title){
        this.title = title;
    }
    public GenericPath addDestructiveInteraction(ResourceLocation path, double val){
        destructive.put(path,val);
        return this;
    }
    public GenericPath addGenerativeInteraction(ResourceLocation path, double val){
        generative.put(path,val);
        return this;
    }
    public GenericPath addRelatedInteraction(ResourceLocation path, double val){
        related.put(path,val);
        return this;
    }
    public GenericPath setDescription(Component description){
        this.pathDescription = description;
        return this;
    }
    public GenericPath addMajorRealmName(Component realmName){
        realmNames.add(realmName);
        return this;
    }
    public GenericPath addMajorRealmName(String realmName){
        realmNames.add(Component.translatable(realmName));
        return this;
    }

    @Override
    public Component getDisplayTitle() {
        return title;
    }

    @Override
    public Component getDescription() {
        return pathDescription;
    }

    @Override
    public Component getDisplayPathInteractions() {
        return Component.empty();//TODO
    }

    @Override
    public Component getMajorRealmName(int majorRealm) {
        return majorRealm >= realmNames.size() ? Component.literal(String.valueOf(majorRealm)) : realmNames.get(majorRealm);
    }

    @Override
    public Component getMinorRealmName(int majorRealm, int minorRealm) {
        return Component.literal(String.valueOf(minorRealm));
    }

    @Override
    public RenderableElement getInformationContainer(UIFrame frame, PathData pathData) {
        return new PathDataDisplayElement(frame,
                getMajorRealmName(pathData.getMajorRealm()),
                getMinorRealmName(pathData.getMajorRealm(),pathData.getMinorRealm()),
                getDescription());
    }


    @Override
    public int getMaxMajorRealm() {
        return 5;
    }

    @Override
    public int getMaxMinorRealm(int majorRealm) {
        return 9;
    }

    @Override
    public double getMaxQiForRealm(int majorRealm, int minorRealm) {
        return 100;
    }

    @Override
    public double getQiConversionRatio() {
        return 1;
    }

    @Override
    public double tryRegenQi(double amount, IEntityData heldEntity) {
        return amount;
    }

    @Override
    public void qiConsumed(double amount, IEntityData heldEntity) {

    }


    @Override
    public Double getInteractionValue(ResourceLocation path) {
        return 0.0;
    }

    @Override
    public PathInteraction getInteractionType(ResourceLocation path) {
        return null;
    }

    @Override
    public ResourceLocation defaultForm() {
        return ModForms.MORTAL_VESSEL.getId();
    }

    @Override
    public PathData freshPathData(IEntityData heldEntity) {
        return new PathData(AscensionRegistries.Paths.PATHS_REGISTRY.getKey(this));
    }

    @Override
    public PathData fromCompound(CompoundTag tag, IEntityData heldEntity) {
        //todo handle cultivation data simulations
        PathData pathData = freshPathData(heldEntity);
        heldEntity.addPathData(AscensionRegistries.Paths.PATHS_REGISTRY.getKey(this),pathData);
        pathData.read(tag,heldEntity);
        return pathData;
    }

    @Override
    public PathData fromNetwork(RegistryFriendlyByteBuf buf) {
        PathData pathData = new PathData(AscensionRegistries.Paths.PATHS_REGISTRY.getKey(this));
        pathData.decode(buf);
        return pathData;
    }
}
