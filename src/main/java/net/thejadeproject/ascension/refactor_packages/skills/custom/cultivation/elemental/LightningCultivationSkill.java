package net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.elemental;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.thejadeproject.ascension.refactor_packages.events.skills.ElementalEssenceSkillEvents;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;

public class LightningCultivationSkill extends ElementalCultivationSkill {
    @Override
    protected ResourceLocation getElementPath() {
        return ModPaths.LIGHTNING.getId();
    }

    @Override
    protected double getEnvironmentMultiplier(Entity caster) {
        boolean stormingUnderOpenSky = caster.level().isThundering()
                && caster.level().canSeeSky(caster.blockPosition());

        if (stormingUnderOpenSky) {
            return 1.50D;
        }

        return 1.00D;
    }
}