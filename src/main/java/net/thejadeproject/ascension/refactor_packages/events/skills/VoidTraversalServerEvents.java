package net.thejadeproject.ascension.refactor_packages.events.skills;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.skills.custom.active.movement.VoidTraversal;

@EventBusSubscriber(modid = AscensionCraft.MOD_ID)
public final class VoidTraversalServerEvents {

    private VoidTraversalServerEvents() {
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof ServerPlayer target
                && VoidTraversal.isTraversing(target)) {
            event.setCanceled(true);
            return;
        }

        Entity attacker = event.getSource().getEntity();

        if (attacker instanceof ServerPlayer player
                && VoidTraversal.isTraversing(player)) {
            event.setCanceled(true);
        }
    }
}