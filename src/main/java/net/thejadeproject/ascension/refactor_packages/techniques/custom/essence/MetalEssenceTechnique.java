package net.thejadeproject.ascension.refactor_packages.techniques.custom.essence;

import net.minecraft.network.chat.Component;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.skills.custom.ModSkills;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.stat_change_handlers.BasicStatChangeHandler;

public class MetalEssenceTechnique extends ElementalEssenceTechnique {

    public MetalEssenceTechnique(BasicStatChangeHandler statChangeHandler) {
        super(
                Component.translatable("ascension.technique.metal_essence_technique"),
                ModPaths.METAL.getId(),
                ModSkills.METAL_ESSENCE_CULTIVATION_SKILL.getId(),
                statChangeHandler
        );
    }

    @Override
    public Component getShortDescription() {
        return Component.literal("Cultivates Essence through metal, ores, and natural veins.");
    }

    @Override
    public Component getDescription() {
        return Component.literal(
                "A Metal-aligned Essence technique. It cultivates slowly at rest, faster near ores, and fastest within rich mineral deposits."
        );
    }

}
