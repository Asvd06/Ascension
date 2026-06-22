package net.thejadeproject.ascension.refactor_packages.skills.custom.passive.universal;

import net.minecraft.server.level.ServerPlayer;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.skills.ITickingSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.SimplePassiveSkill;

public class RegenerationBoostSkill extends SimplePassiveSkill implements ITickingSkill {

    private static final int HEAL_INTERVAL_TICKS = 30;

    private static final int UNLOCK_MAJOR_REALM = 3;

    private static final float BASE_FLAT_HEAL_AMOUNT = 5.0F;
    private static final float FLAT_HEAL_AMOUNT_PER_EFFECTIVE_REALM = 5.0F;

    private static final float BASE_MAX_HEALTH_HEAL_FRACTION = 0.0025F;
    private static final float MAX_HEALTH_HEAL_FRACTION_PER_EFFECTIVE_REALM = 0.0035F;

    private static final float MISSING_HEALTH_HEAL_FRACTION_PER_EFFECTIVE_REALM = 0.0025F;

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
        if (highestMajorRealm < UNLOCK_MAJOR_REALM) return;

        float healAmount = getHealAmount(player, highestMajorRealm);
        if (healAmount <= 0.0F) return;

        player.heal(healAmount);
    }

    private int getHighestMajorRealm(IEntityData entityData) {
        if (entityData == null) return 0;

        int highestMajorRealm = 0;

        for (IPathData pathData : entityData.getAllPathData()) {
            if (pathData == null) continue;

            highestMajorRealm = Math.max(
                    highestMajorRealm,
                    pathData.getMajorRealm()
            );
        }

        return highestMajorRealm;
    }

    private float getHealAmount(ServerPlayer player, int majorRealm) {
        int effectiveRealm = Math.max(1, majorRealm - UNLOCK_MAJOR_REALM + 1);

        float maxHealth = player.getMaxHealth();
        float missingHealth = maxHealth - player.getHealth();

        float flatHeal =
                BASE_FLAT_HEAL_AMOUNT
                        + effectiveRealm * FLAT_HEAL_AMOUNT_PER_EFFECTIVE_REALM;

        float maxHealthHeal =
                maxHealth
                        * (
                        BASE_MAX_HEALTH_HEAL_FRACTION
                                + effectiveRealm * MAX_HEALTH_HEAL_FRACTION_PER_EFFECTIVE_REALM
                );

        float missingHealthHeal =
                missingHealth
                        * effectiveRealm
                        * MISSING_HEALTH_HEAL_FRACTION_PER_EFFECTIVE_REALM;

        return flatHeal + maxHealthHeal + missingHealthHeal;
    }
}