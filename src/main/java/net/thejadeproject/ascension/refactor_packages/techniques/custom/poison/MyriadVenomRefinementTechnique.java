package net.thejadeproject.ascension.refactor_packages.techniques.custom.poison;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.breakthroughs.IBreakthroughInstance;
import net.thejadeproject.ascension.refactor_packages.breakthroughs.NineHeavenlyTribulations;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.PathData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skills.custom.ModSkills;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.GenericTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.handlers.MyriadVenomTechniqueData;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.stat_change_handlers.BasicStatChangeHandler;

import java.util.Set;

public class MyriadVenomRefinementTechnique extends GenericTechnique {

    public MyriadVenomRefinementTechnique(BasicStatChangeHandler statChangeHandler) {
        super(
                ModPaths.POISON.getId(),
                Component.translatable("ascension.technique.myriad_venom_refinement_scripture"),
                3.0D,
                Set.of()
        );
        setStatChangeHandler(statChangeHandler);
    }

    @Override
    public Component getShortDescription() {
        return Component.translatable("ascension.technique.myriad_venom_refinement_scripture.desc.short");
    }

    @Override
    public Component getDescription() {
        return Component.translatable("ascension.technique.myriad_venom_refinement_scripture.desc");
    }

    @Override
    public void onTechniqueAdded(IEntityData heldEntity) {
        ResourceLocation techniqueId = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.getKey(this);
        PathData pathData = heldEntity.getPathData(getPath());

        if (pathData != null && techniqueId != null && pathData.getTechniqueData(techniqueId) == null) {
            pathData.addTechniqueData(techniqueId, new MyriadVenomTechniqueData(heldEntity));
        }

        heldEntity.giveSkill(
                ModSkills.POISON_REFINING_MEDITATION_SKILL.getId(),
                ModForms.MORTAL_VESSEL.getId()
        );

        refreshUniversalTechniqueSkills(heldEntity);
    }

    @Override
    public void onTechniqueRemoved(IEntityData heldEntity, ITechniqueData techniqueData) {
        PathData pathData = heldEntity.getPathData(getPath());

        if (pathData != null) {
            pathData.handleRealmChange(pathData.getMajorRealm(), 0, heldEntity);
        }

        heldEntity.removeSkill(
                ModSkills.POISON_REFINING_MEDITATION_SKILL.getId(),
                ModForms.MORTAL_VESSEL.getId()
        );

        refreshUniversalTechniqueSkills(heldEntity);
    }

    @Override
    public void onRealmChange(
            IEntityData entityData,
            int oldMajorRealm,
            int oldMinorRealm,
            int newMajorRealm,
            int newMinorRealm
    ) {
        super.onRealmChange(entityData, oldMajorRealm, oldMinorRealm, newMajorRealm, newMinorRealm);
    }

    public boolean canBreakthroughWithData(ITechniqueData techniqueData) {
        if (!(techniqueData instanceof MyriadVenomTechniqueData venomData)) return false;
        return venomData.hasMetGateForNextRealm();
    }

    @Override
    public boolean canBreakthrough(IEntityData entityData, int majorRealm, int minorRealm, double currentProgress) {
        PathData pathData = entityData.getPathData(getPath());
        if (pathData == null) return false;
        ITechniqueData raw = pathData.getTechniqueData(pathData.getLastUsedTechnique());
        if (!(raw instanceof MyriadVenomTechniqueData venomData)) return false;
        return venomData.hasMetGateForNextRealm();
    }

    @Override
    public boolean isCompatibleWith(ResourceLocation technique) {
        return AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(technique)
                instanceof MyriadVenomRefinementTechnique;
    }

    @Override
    public ITechniqueData freshTechniqueData(IEntityData heldEntity) {
        return new MyriadVenomTechniqueData(heldEntity);
    }

    @Override
    public ITechniqueData fromCompound(CompoundTag tag) {
        return new MyriadVenomTechniqueData(null, tag);
    }

    @Override
    public ITechniqueData fromNetwork(RegistryFriendlyByteBuf buf) {
        return new MyriadVenomTechniqueData(null, buf);
    }

    @Override
    public IBreakthroughInstance freshBreakthroughData(IEntityData heldEntity) {
        return new NineHeavenlyTribulations(1);
    }

    @Override
    public IBreakthroughInstance breakthroughInstanceFromCompound(
            CompoundTag tag, int majorRealm, int minorRealm, ITechniqueData data) {
        return new NineHeavenlyTribulations(1);
    }

    @Override
    public IBreakthroughInstance breakthroughInstanceFromNetwork(
            RegistryFriendlyByteBuf buf, int majorRealm, int minorRealm, ITechniqueData data) {
        return new NineHeavenlyTribulations(1);
    }
}
