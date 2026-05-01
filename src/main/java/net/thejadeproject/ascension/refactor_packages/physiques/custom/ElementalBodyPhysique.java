package net.thejadeproject.ascension.refactor_packages.physiques.custom;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.gui.elements.info_elements.DescriptionDisplayContainer;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.physiques.IPhysique;
import net.thejadeproject.ascension.refactor_packages.physiques.IPhysiqueData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ElementalBodyPhysique implements IPhysique {

    private final ResourceLocation element;
    private final Component title;

    public ElementalBodyPhysique(ResourceLocation element, Component title) {
        this.element = element;
        this.title   = title;
    }

    public ResourceLocation getElement() { return element; }

    @Override
    public void onPhysiqueAdded(IEntityData heldEntity, ResourceLocation oldPhysique, IPhysiqueData oldPhysiqueData) {
        // Tier 1 bonuses on first equip
        heldEntity.getPathBonusHandler().addPathBonus(ModPaths.BODY.getId(), 1.0);
        heldEntity.getPathBonusHandler().addPathBonus(element, 1.0);
    }

    @Override
    public void onPhysiqueRemoved(IEntityData heldEntity, IPhysiqueData physiqueData, ResourceLocation newPhysique) {
        if (!(physiqueData instanceof ElementalPhysiqueData data)) return;
        double tier = data.getActiveCount();
        heldEntity.getPathBonusHandler().removePathBonus(ModPaths.BODY.getId(), tier);
        for (ResourceLocation el : data.getActiveElements()) {
            heldEntity.getPathBonusHandler().removePathBonus(el, tier);
        }
    }

    @Override
    public void onFormAdded(IEntityData heldEntity, ResourceLocation form, IPhysiqueData physiqueData) {}

    @Override
    public void onFormRemoved(IEntityData heldEntity, ResourceLocation form, IPhysiqueData physiqueData) {}

    @Override
    public Component getDisplayTitle() { return title; }

    public Component getDisplayTitle(ElementalPhysiqueData data) {
        Set<ResourceLocation> active = data.getActiveElements();
        return switch (active.size()) {
            case 2 -> {
                if (active.containsAll(Set.of(ModPaths.WOOD.getId(), ModPaths.FIRE.getId())))
                    yield Component.literal("Liver-Heart Awakening Physique");
                if (active.containsAll(Set.of(ModPaths.FIRE.getId(), ModPaths.EARTH.getId())))
                    yield Component.literal("Heart-Spleen Awakening Physique");
                if (active.containsAll(Set.of(ModPaths.EARTH.getId(), ModPaths.METAL.getId())))
                    yield Component.literal("Spleen-Lung Awakening Physique");
                if (active.containsAll(Set.of(ModPaths.METAL.getId(), ModPaths.WATER.getId())))
                    yield Component.literal("Lung-Kidney Awakening Physique");
                if (active.containsAll(Set.of(ModPaths.WATER.getId(), ModPaths.WOOD.getId())))
                    yield Component.literal("Kidney-Liver Awakening Physique");
                yield title;
            }
            case 3 -> {
                if (active.containsAll(Set.of(ModPaths.WOOD.getId(), ModPaths.FIRE.getId(), ModPaths.EARTH.getId())))
                    yield Component.literal("Vital Blood Physique");
                if (active.containsAll(Set.of(ModPaths.FIRE.getId(), ModPaths.EARTH.getId(), ModPaths.METAL.getId())))
                    yield Component.literal("Refined Qi Physique");
                if (active.containsAll(Set.of(ModPaths.EARTH.getId(), ModPaths.METAL.getId(), ModPaths.WATER.getId())))
                    yield Component.literal("Flowing Tide Physique");
                if (active.containsAll(Set.of(ModPaths.METAL.getId(), ModPaths.WATER.getId(), ModPaths.WOOD.getId())))
                    yield Component.literal("Deep Root Physique");
                if (active.containsAll(Set.of(ModPaths.WATER.getId(), ModPaths.WOOD.getId(), ModPaths.FIRE.getId())))
                    yield Component.literal("Rising Yang Physique");
                yield title;
            }
            case 4 -> {
                if (!active.contains(ModPaths.WATER.getId())) yield Component.literal("Unquenched Palace Physique");
                if (!active.contains(ModPaths.WOOD.getId()))  yield Component.literal("Unrooted Palace Physique");
                if (!active.contains(ModPaths.FIRE.getId()))  yield Component.literal("Unlit Palace Physique");
                if (!active.contains(ModPaths.EARTH.getId())) yield Component.literal("Ungrounded Palace Physique");
                if (!active.contains(ModPaths.METAL.getId())) yield Component.literal("Unrefined Palace Physique");
                yield title;
            }
            case 5  -> Component.literal("Five Palace Immortal Physique");
            default -> title;
        };
    }

    @Override
    public Component getShortDescription() { return null; }

    @Override
    public Component getDescription() { return null; }

    @OnlyIn(Dist.CLIENT)
    @Override
    public RenderableElement getInformationContainer(UIFrame frame) {
         return new DescriptionDisplayContainer(frame,getDisplayTitle(),getDescription());
    }

    @Override
    public Collection<ResourceLocation> paths() { return Set.of(ModPaths.BODY.getId()); }

    @Override
    public Map<ResourceLocation, Double> pathBonuses() { return Map.of(); }

    @Override
    public IPhysiqueData freshPhysiqueData(IEntityData heldEntity) {
        ResourceLocation key = AscensionRegistries.Physiques.PHSIQUES_REGISTRY.getKey(this);
        return ElementalPhysiqueData.forElement(key, element);
    }

    @Override
    public IPhysiqueData fromCompound(CompoundTag tag, IEntityData heldEntity) {
        return ElementalPhysiqueData.read(tag);
    }

    @Override
    public IPhysiqueData fromNetwork(RegistryFriendlyByteBuf buf) {
        return ElementalPhysiqueData.decode(buf);
    }
}
