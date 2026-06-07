package net.thejadeproject.ascension.refactor_packages.events.skills;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.skills.custom.qi.QiPull;
import net.thejadeproject.ascension.refactor_packages.skills.custom.qi.QiRelease;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = AscensionCraft.MOD_ID)
public class QiImpactHandler {

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide()) return;

        long gameTime = event.getLevel().getGameTime();

        handleImpacts(
                QiRelease.PUSH_CRASH_DAMAGE,
                QiRelease.PUSH_EXPIRY,
                gameTime
        );

        handleImpacts(
                QiPull.PULL_CRASH_DAMAGE,
                QiPull.PULL_EXPIRY,
                gameTime
        );
    }

    private static void handleImpacts(
            ConcurrentHashMap<LivingEntity, Float> damageMap,
            ConcurrentHashMap<LivingEntity, Long> expiryMap,
            long gameTime
    ) {
        Iterator<Map.Entry<LivingEntity, Float>> it = damageMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<LivingEntity, Float> entry = it.next();

            LivingEntity entity = entry.getKey();
            float damage = entry.getValue();
            Long expiry = expiryMap.get(entity);

            if (expiry == null || gameTime >= expiry || !entity.isAlive()) {
                it.remove();
                expiryMap.remove(entity);
                continue;
            }

            if (entity.horizontalCollision) {
                entity.hurt(entity.damageSources().generic(), damage);

                it.remove();
                expiryMap.remove(entity);
            }
        }
    }
}