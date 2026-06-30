package net.thejadeproject.ascension.refactor_packages.skills.custom.passive.universal;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.skills.ITickingSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.SimplePassiveSkill;

public class QiSustainedBodySkill extends SimplePassiveSkill implements ITickingSkill {

    private static final int CHECK_INTERVAL_TICKS = 20;
    private static final int TARGET_FOOD_LEVEL = 20;
    private static final float TARGET_SATURATION = 20.0F;

    @Override
    protected String getTitleKey() {
        return "ascension.skill.qi_sustained_body";
    }

    @Override
    protected String getDescriptionKey() {
        return "ascension.skill.qi_sustained_body.description";
    }

    @Override
    public void onPlayerTick(ServerPlayer player, IEntityData entityData) {
        if (player.tickCount % CHECK_INTERVAL_TICKS != 0) return;

        FoodData foodData = player.getFoodData();

        if (foodData.getFoodLevel() < TARGET_FOOD_LEVEL) {
            foodData.setFoodLevel(TARGET_FOOD_LEVEL);
        }

        if (foodData.getSaturationLevel() < TARGET_SATURATION) {
            foodData.setSaturation(TARGET_SATURATION);
        }
    }
}