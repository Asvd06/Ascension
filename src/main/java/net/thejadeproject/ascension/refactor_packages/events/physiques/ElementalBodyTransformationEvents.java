package net.thejadeproject.ascension.refactor_packages.events.physiques;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.physiques.ModPhysiques;
import net.thejadeproject.ascension.refactor_packages.physiques.custom.EvolvingPhysique;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = AscensionCraft.MOD_ID)
public final class ElementalBodyTransformationEvents {

    private ElementalBodyTransformationEvents() {}

    private static final Map<UUID, TransformationState> ACTIVE = new HashMap<>();

    public static final int DURATION_TICKS = 1200; // 60 seconds

    // At purity 10 → 3.5%/s, purity 100 → 1.65%/s
    private static float damagePerSecond(int purity) {
        float percentPerSecond = 5.0f - (purity - 10) * (1.85f / 90f);
        return percentPerSecond / 100f;
    }

    public static void beginTransformation(ServerPlayer player, int purity) {
        TransformationState state = new TransformationState(DURATION_TICKS, purity);
        ACTIVE.put(player.getUUID(), state);
        persistState(player, state);
        player.sendSystemMessage(Component.translatable("ascension.transformation.started").withStyle(ChatFormatting.GOLD));
    }

    public static boolean isTransforming(ServerPlayer player) {
        return ACTIVE.containsKey(player.getUUID());
    }

    // Restore in-memory state from attachment when player logs in
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        int ticks = player.getData(ModAttachments.TRANSFORMATION_TICKS.get());
        int purity = player.getData(ModAttachments.TRANSFORMATION_PURITY.get());
        if (ticks > 0) {
            ACTIVE.put(player.getUUID(), new TransformationState(ticks, purity));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        UUID id = player.getUUID();
        TransformationState state = ACTIVE.get(id);
        if (state == null) return;

        state.ticksRemaining--;
        player.removeEffect(MobEffects.REGENERATION);

        if (state.ticksRemaining % 20 == 0) {
            float maxHp = player.getMaxHealth();
            float dmg = maxHp * damagePerSecond(state.purity);
            DamageSource src = player.level().damageSources().magic();
            player.hurt(src, dmg);
            persistState(player, state);
        }

        if (!player.isAlive()) {
            cancelTransformation(player);
            return;
        }

        if (state.ticksRemaining <= 0) {
            completeTransformation(player);
        }
    }

    private static void persistState(ServerPlayer player, TransformationState state) {
        player.setData(ModAttachments.TRANSFORMATION_TICKS.get(), state.ticksRemaining);
        player.setData(ModAttachments.TRANSFORMATION_PURITY.get(), state.purity);
    }

    private static void cancelTransformation(ServerPlayer player) {
        ACTIVE.remove(player.getUUID());
        player.setData(ModAttachments.TRANSFORMATION_TICKS.get(), -1);
    }

    private static void completeTransformation(ServerPlayer player) {
        ACTIVE.remove(player.getUUID());
        player.setData(ModAttachments.TRANSFORMATION_TICKS.get(), -1);

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        if (!(entityData.getPhysique() instanceof EvolvingPhysique evolvingPhysique)) return;

        ResourceLocation fivePalaceId = ModPhysiques.FIVE_PALACE_IMMORTAL.getId();
        if (!evolvingPhysique.canEvolveInto(fivePalaceId)) return;

        evolvingPhysique.tryEvolveInto(player, entityData, fivePalaceId);
    }

    private static final class TransformationState {
        int ticksRemaining;
        final int purity;

        TransformationState(int ticks, int purity) {
            this.ticksRemaining = ticks;
            this.purity = purity;
        }
    }
}
