package net.thejadeproject.ascension.refactor_packages.skills.custom.passive.elemental;

import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.SimplePassiveSkill;

public class AdamantSpiritSkill extends SimplePassiveSkill {

    private static final float BASE_DAMAGE_REDUCTION = 0.08F;
    private static final float REDUCTION_PER_MAJOR = 0.025F;
    private static final float REDUCTION_PER_MINOR = 0.0025F;
    private static final float MAX_DAMAGE_REDUCTION = 0.30F;

    public static float getDamageReduction(
            IEntityData entityData
    ) {
        IPathData metal = entityData.getPathData(ModPaths.METAL.getId());

        int major = metal != null ? metal.getMajorRealm() : 0;
        int minor = metal != null ? metal.getMinorRealm() : 0;

        float reduction = BASE_DAMAGE_REDUCTION + major * REDUCTION_PER_MAJOR + minor * REDUCTION_PER_MINOR;

        return Math.min(reduction, MAX_DAMAGE_REDUCTION);
    }

    @Override
    protected String getTitleKey() {
        return "ascension.skill.adamant_spirit";
    }

    @Override
    protected String getDescriptionKey() {
        return "ascension.skill.adamant_spirit.description";
    }
}