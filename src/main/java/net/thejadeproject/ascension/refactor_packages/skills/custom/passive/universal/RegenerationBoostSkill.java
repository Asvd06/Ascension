package net.thejadeproject.ascension.refactor_packages.skills.custom.passive.universal;

import net.minecraft.server.level.ServerPlayer;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.skills.ITickingSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.SimplePassiveSkill;

public class RegenerationBoostSkill extends SimplePassiveSkill implements ITickingSkill {

    private static final int HEAL_INTERVAL_TICKS = 30;

    private static final int BASE_MAJOR_REALM = 1;

    private static final float BASE_FLAT_HEAL_AMOUNT = 2.0F;
    private static final float FLAT_HEAL_PER_PROGRESSION = 2.0F;
    private static final float FLAT_HEAL_PER_PROGRESSION_SQUARED = 3.0F;

    private static final float BASE_MAX_HEALTH_HEAL_FRACTION = 0.0010F;
    private static final float MAX_HEALTH_FRACTION_PER_PROGRESSION = 0.0025F;
    private static final float MAX_HEALTH_FRACTION_PER_PROGRESSION_SQUARED = 0.0015F;

    private static final float MISSING_HEALTH_FRACTION_PER_PROGRESSION = 0.0015F;
    private static final float MISSING_HEALTH_FRACTION_PER_PROGRESSION_SQUARED = 0.0010F;

    private static final float HEALING_POWER_MULTIPLIER = 1.75F;

    @Override
    protected String getTitleKey() {
        return "ascension.skill.regeneration_boost";
    }

    @Override
    protected String getDescriptionKey() {
        return "ascension.skill.regeneration_boost.description";
    }

    @Override
    public void onPlayerTick(ServerPlayer player, IEntityData entityData) {
        if (player.tickCount % HEAL_INTERVAL_TICKS != 0) return;
        if (player.getHealth() >= player.getMaxHealth()) return;

        int highestMajorRealm = getHighestMajorRealm(entityData);
        float healAmount = getHealAmount(player, highestMajorRealm);

        if (healAmount > 0.0F) {
            player.heal(healAmount);
        }
    }

    private int getHighestMajorRealm(IEntityData entityData) {
        if (entityData == null) return 0;

        int highestMajorRealm = 0;

        for (IPathData pathData : entityData.getAllPathData()) {
            if (pathData == null) continue;

            highestMajorRealm = Math.max(highestMajorRealm, pathData.getMajorRealm());
        }

        return highestMajorRealm;
    }

    private float getHealAmount(ServerPlayer player, int majorRealm) {
        int progression = Math.max(0, majorRealm - BASE_MAJOR_REALM);
        int progressionSquared = progression * progression;

        float maxHealth = player.getMaxHealth();
        float missingHealth = Math.max(0.0F, maxHealth - player.getHealth());

        float flatHeal = BASE_FLAT_HEAL_AMOUNT + progression * FLAT_HEAL_PER_PROGRESSION + progressionSquared * FLAT_HEAL_PER_PROGRESSION_SQUARED;
        float maxHealthFraction = BASE_MAX_HEALTH_HEAL_FRACTION + progression * MAX_HEALTH_FRACTION_PER_PROGRESSION + progressionSquared * MAX_HEALTH_FRACTION_PER_PROGRESSION_SQUARED;
        float missingHealthFraction = progression * MISSING_HEALTH_FRACTION_PER_PROGRESSION + progressionSquared * MISSING_HEALTH_FRACTION_PER_PROGRESSION_SQUARED;
        return (flatHeal + maxHealth * maxHealthFraction + missingHealth * missingHealthFraction) * HEALING_POWER_MULTIPLIER;
    }
}