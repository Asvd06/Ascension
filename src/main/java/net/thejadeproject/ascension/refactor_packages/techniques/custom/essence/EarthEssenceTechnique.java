package net.thejadeproject.ascension.refactor_packages.techniques.custom.essence;

import net.minecraft.network.chat.Component;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.skills.custom.ModSkills;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.stat_change_handlers.BasicStatChangeHandler;

public class EarthEssenceTechnique extends ElementalEssenceTechnique {

    public EarthEssenceTechnique(BasicStatChangeHandler statChangeHandler) {
        super(
                Component.translatable("ascension.technique.earth_essence_technique"),
                ModPaths.EARTH.getId(),
                ModSkills.EARTH_ESSENCE_CULTIVATION_SKILL.getId(),
                statChangeHandler
        );
    }

    @Override
    public Component getShortDescription() {
        return Component.literal("Cultivates Essence through earth, stone, and underground exploration.");
    }

    @Override
    public Component getDescription() {
        return Component.literal(
                "An Earth-aligned Essence technique. It cultivates steadily at rest, faster on natural ground, and fastest while deep underground."
        );
    }

}
