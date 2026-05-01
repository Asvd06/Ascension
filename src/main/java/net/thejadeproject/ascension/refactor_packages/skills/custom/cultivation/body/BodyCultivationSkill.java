package net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.body;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.PathData;
import net.thejadeproject.ascension.refactor_packages.physiques.IPhysiqueData;
import net.thejadeproject.ascension.refactor_packages.physiques.custom.ElementalPhysiqueData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skills.castable.ICastData;
import net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.skill_data.BodyCultivationCastData;
import net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.GenericCultivationSkill;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.body.BodyElementTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.body.CombinedBodyElementTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.body.FiveElementBodyTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.technique_data.BodyTechniqueData;

import java.util.Set;

public class BodyCultivationSkill extends GenericCultivationSkill {
    public BodyCultivationSkill() {
        super(5.0, ModPaths.BODY.getId());
    }

    @Override
    public ICastData freshCastData() {
        return new BodyCultivationCastData();
    }

    @Override
    public ICastData castDataFromCompound(CompoundTag tag) {
        return new BodyCultivationCastData();
    }

    @Override
    public ICastData castDataFromNetwork(RegistryFriendlyByteBuf buf) {
        return new BodyCultivationCastData();
    }

    @Override
    protected double getEffectiveRate(Entity caster) {
        return super.getEffectiveRate(caster) * computePhysiqueMultiplier(caster);
    }

    private static double computePhysiqueMultiplier(Entity caster) {
        IEntityData entityData = caster.getData(ModAttachments.ENTITY_DATA);
        PathData bodyPath = entityData.getPathData(ModPaths.BODY.getId());
        if (bodyPath == null || bodyPath.getLastUsedTechnique() == null) return 1.0;

        Set<ResourceLocation> required = getTechniqueElements(bodyPath.getLastUsedTechnique());
        if (required.isEmpty()) return 1.0;

        IPhysiqueData physiqueData = entityData.getActiveFormData().getPhysiqueData();
        Set<ResourceLocation> active = (physiqueData instanceof ElementalPhysiqueData ep)
            ? ep.getActiveElements() : Set.of();

        long matched = required.stream().filter(active::contains).count();
        long missing = required.size() - matched;

        if (missing > 0) return Math.pow(0.3, missing);

        long extra = active.size() - matched;
        return 1.0 + extra * 0.2;
    }

    private static Set<ResourceLocation> getTechniqueElements(ResourceLocation techniqueId) {
        ITechnique technique = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(techniqueId);
        if (technique instanceof BodyElementTechnique single)
            return Set.of(single.getElement());
        if (technique instanceof CombinedBodyElementTechnique combined)
            return combined.getElements();
        if (technique instanceof FiveElementBodyTechnique)
            return Set.of(ModPaths.FIRE.getId(), ModPaths.WATER.getId(),
                          ModPaths.WOOD.getId(), ModPaths.EARTH.getId(), ModPaths.METAL.getId());
        return Set.of();
    }

    @Override
    public boolean continueCasting(int ticksElapsed, Entity caster, ICastData castData) {
        boolean result = super.continueCasting(ticksElapsed, caster, castData);

        if (!caster.level().isClientSide() && castData instanceof BodyCultivationCastData) {
            IEntityData entityData = caster.getData(ModAttachments.ENTITY_DATA);
            PathData bodyPathData = entityData.getPathData(ModPaths.BODY.getId());

            if (bodyPathData != null && bodyPathData.getLastUsedTechnique() != null) {
                ITechniqueData techniqueData = bodyPathData.getTechniqueData(bodyPathData.getLastUsedTechnique());
                if (techniqueData instanceof BodyTechniqueData bodyTechData) {
                    bodyTechData.incrementCultivationTicks();
                }
            }
        }

        return result;
    }
}
