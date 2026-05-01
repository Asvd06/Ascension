package net.thejadeproject.ascension.refactor_packages.techniques.custom.soul;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.breakthroughs.IBreakthroughInstance;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.PathData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skills.custom.ModSkills;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.GenericTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.stat_change_handlers.BasicStatChangeHandler;

import java.util.Set;

public class ZenithSunTechnique extends GenericTechnique {

    public static final double BASE_RATE = 1.5D;

    public ZenithSunTechnique(BasicStatChangeHandler statChangeHandler) {
        super(
                ModPaths.SOUL.getId(),
                Component.translatable("ascension.technique.zenith_sun_scripture"),
                BASE_RATE,
                Set.of()
        );
        setStatChangeHandler(statChangeHandler);
    }

    @Override
    public Component getShortDescription() {
        return Component.literal("A stronger solar soul scripture attuned to the sun at its zenith.");
    }

    @Override
    public Component getDescription() {
        return Component.literal(
                "Cultivates the Soul by drawing upon the blazing power of the sun at its peak. Must be practised under open sky. Greatly empowered when gazing directly at the sun. Harmful beneath moonlit night skies."
        );
    }

    @Override
    public void onTechniqueAdded(IEntityData heldEntity) {
        heldEntity.giveSkill(ModSkills.ZENITH_SUN_CULTIVATION_SKILL.getId(), ModForms.MORTAL_VESSEL.getId());
        refreshUniversalTechniqueSkills(heldEntity);
    }

    @Override
    public void onTechniqueRemoved(IEntityData heldEntity, ITechniqueData techniqueData) {
        PathData pathData = heldEntity.getPathData(getPath());
        if (pathData != null) {
            pathData.handleRealmChange(pathData.getMajorRealm(), 0, heldEntity);
        }
        heldEntity.removeSkill(ModSkills.ZENITH_SUN_CULTIVATION_SKILL.getId(), ModForms.MORTAL_VESSEL.getId());
        refreshUniversalTechniqueSkills(heldEntity);
    }

    @Override
    public boolean isCompatibleWith(ResourceLocation technique) {
        var tech = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(technique);
        return !(tech instanceof ZenithSunTechnique)
                && !(tech instanceof DawningSunTechnique)
                && !(tech instanceof PaleMoonTechnique)
                && !(tech instanceof GibbousMoonTechnique)
                && !(tech instanceof ScholarlySoulTechnique);
    }

    @Override
    public ITechniqueData freshTechniqueData(IEntityData heldEntity) { return null; }

    @Override
    public ITechniqueData fromCompound(CompoundTag tag) { return null; }

    @Override
    public ITechniqueData fromNetwork(RegistryFriendlyByteBuf buf) { return null; }

    @Override
    public IBreakthroughInstance freshBreakthroughData(IEntityData heldEntity) { return null; }

    @Override
    public IBreakthroughInstance breakthroughInstanceFromCompound(CompoundTag tag, int majorRealm, int minorRealm, ITechniqueData techniqueData) { return null; }

    @Override
    public IBreakthroughInstance breakthroughInstanceFromNetwork(RegistryFriendlyByteBuf buf, int majorRealm, int minorRealm, ITechniqueData techniqueData) { return null; }
}
