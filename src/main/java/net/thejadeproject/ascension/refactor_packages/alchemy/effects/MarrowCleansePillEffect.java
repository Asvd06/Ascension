package net.thejadeproject.ascension.refactor_packages.alchemy.effects;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.alchemy.BasicPillEffect;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.events.physiques.MarrowCleanseTreatmentEvents;
import net.thejadeproject.ascension.refactor_packages.physiques.ModPhysiques;
import net.thejadeproject.ascension.refactor_packages.physiques.custom.EvolvingPhysique;
import net.thejadeproject.ascension.refactor_packages.physiques.custom.helpers.PhysiqueEvolutionHelper;

public class MarrowCleansePillEffect extends BasicPillEffect {

    private final double baseChance;
    private final double maxChance;

    public MarrowCleansePillEffect(Component name, Component description) {
        this(0.01D, 0.20D, name, description);
    }

    public MarrowCleansePillEffect(double baseChance, double maxChance, Component name, Component description) {
        super(name, description);
        this.baseChance = baseChance;
        this.maxChance = maxChance;
    }

    @Override
    public boolean tryConsume(LivingEntity entity, ItemStack itemStack, double purityScale, double realmMultiplier) {
        if (!(entity instanceof ServerPlayer player)) return false;
        if (!player.hasData(ModAttachments.ENTITY_DATA)) return false;

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        if (!(entityData.getPhysique() instanceof EvolvingPhysique evolvingPhysique)) {
            return false;
        }

        ResourceLocation targetPhysique = getCleanseTarget(evolvingPhysique);
        if (targetPhysique == null) {
            return false;
        }

        if (ModPhysiques.MORTAL.getId().equals(targetPhysique)) {
            return tryInstantMortalCleanse(player, entityData, purityScale, realmMultiplier);
        }

        return MarrowCleanseTreatmentEvents.addTreatmentFuel(
                player,
                entityData,
                targetPhysique,
                purityScale,
                realmMultiplier
        );
    }

    private boolean tryInstantMortalCleanse(
            ServerPlayer player,
            IEntityData entityData,
            double purityScale,
            double realmMultiplier
    ) {
        double chance = clamp(baseChance * purityScale * realmMultiplier, 0.0D, maxChance);

        if (player.getRandom().nextDouble() >= chance) {
            player.sendSystemMessage(Component.literal("marrow cleanse failed"));
            return true;
        }

        return PhysiqueEvolutionHelper.tryEvolveInto(
                player,
                entityData,
                ModPhysiques.MORTAL.getId()
        );
    }

    private static ResourceLocation getCleanseTarget(EvolvingPhysique evolvingPhysique) {
        if (evolvingPhysique.canEvolveInto(ModPhysiques.MORTAL.getId())) {
            return ModPhysiques.MORTAL.getId();
        }

        if (evolvingPhysique.canEvolveInto(ModPhysiques.POISON_SLIGHTED_MERIDIANS.getId())) {
            return ModPhysiques.POISON_SLIGHTED_MERIDIANS.getId();
        }

        if (evolvingPhysique.canEvolveInto(ModPhysiques.STRENGTHENED_VESSELS.getId())) {
            return ModPhysiques.STRENGTHENED_VESSELS.getId();
        }

        return null;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
