package net.thejadeproject.ascension.refactor_packages.breakthroughs;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.IEntityFormData;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.attributes.SyncAttributeHolder;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.stats.Stat;
import net.thejadeproject.ascension.refactor_packages.stats.StatSheet;
import net.thejadeproject.ascension.refactor_packages.stats.custom.ModStats;
import net.thejadeproject.ascension.refactor_packages.util.value_modifiers.ModifierOperation;
import net.thejadeproject.ascension.refactor_packages.util.value_modifiers.ValueContainerModifier;

import java.util.List;

public final class TribulationRewardHelper {

    public static final ResourceLocation THREE_NINES_REWARD =
            ResourceLocation.fromNamespaceAndPath(
                    AscensionCraft.MOD_ID,
                    "three_nines"
            );

    private static final ResourceLocation TRIBULATION_REWARD_GROUP =
            ResourceLocation.fromNamespaceAndPath(
                    AscensionCraft.MOD_ID,
                    "tribulation_rewards"
            );

    private static final double THREE_NINES_STAT_BONUS = 0.05D;

    private TribulationRewardHelper() {
    }

    public static boolean grantThreeNinesFoundationReward(IEntityData entityData, ResourceLocation path, int reachedRealm) {
        List<StatMultiplierReward> rewards;

        if (ModPaths.BODY.getId().equals(path)) {
            rewards = List.of(
                    new StatMultiplierReward(
                            "vitality",
                            ModStats.VITALITY.get(),
                            THREE_NINES_STAT_BONUS
                    ),
                    new StatMultiplierReward(
                            "strength",
                            ModStats.STRENGTH.get(),
                            THREE_NINES_STAT_BONUS
                    )
            );

        } else if (ModPaths.ESSENCE.getId().equals(path)) {
            rewards = List.of(
                    new StatMultiplierReward(
                            "intelligence",
                            ModStats.INTELLIGENCE.get(),
                            THREE_NINES_STAT_BONUS
                    ),
                    new StatMultiplierReward(
                            "vitality",
                            ModStats.VITALITY.get(),
                            THREE_NINES_STAT_BONUS
                    )
            );

        } else if (ModPaths.SOUL.getId().equals(path)) {
            rewards = List.of(
                    new StatMultiplierReward(
                            "intelligence",
                            ModStats.INTELLIGENCE.get(),
                            THREE_NINES_STAT_BONUS
                    ),
                    new StatMultiplierReward(
                            "agility",
                            ModStats.AGILITY.get(),
                            THREE_NINES_STAT_BONUS
                    )
            );

        } else {
            return false;
        }

        return grantStatMultiplierReward(
                entityData,
                THREE_NINES_REWARD,
                path,
                reachedRealm,
                rewards
        );
    }

    public static boolean grantStatMultiplierReward(IEntityData entityData, ResourceLocation rewardType, ResourceLocation path, int reachedRealm, List<StatMultiplierReward> rewards) {
        if (entityData == null
                || entityData.getAttachedEntity() == null
                || entityData.getAttachedEntity().level().isClientSide()) {
            return false;
        }

        if (rewardType == null
                || path == null
                || reachedRealm < 0
                || rewards == null
                || rewards.isEmpty()) {
            return false;
        }

        IEntityFormData mortalVesselData = entityData.getEntityFormData(ModForms.MORTAL_VESSEL.getId()
        );

        if (mortalVesselData == null) {
            return false;
        }

        StatSheet statSheet = mortalVesselData.getStatSheet();

        for (StatMultiplierReward reward : rewards) {
            if (reward == null
                    || reward.stat() == null
                    || reward.key() == null
                    || reward.key().isBlank()
                    || reward.amount() == 0.0D) {
                continue;
            }

            ResourceLocation modifierId = rewardModifierId(
                    rewardType,
                    path,
                    reachedRealm,
                    reward.key()
            );

            ValueContainerModifier modifier =
                    new ValueContainerModifier(
                            reward.amount(),
                            ModifierOperation.MULTIPLY_FINAL,
                            modifierId,
                            TRIBULATION_REWARD_GROUP
                    );

            statSheet.addStatModifier(
                    reward.stat(),
                    modifier
            );
        }


        entityData.getAscensionAttributeHolder()
                .updateAttributes(entityData);

        syncRewardChanges(entityData);

        return true;
    }

    private static ResourceLocation rewardModifierId(ResourceLocation rewardType, ResourceLocation path, int reachedRealm, String statKey) {
        return ResourceLocation.fromNamespaceAndPath(
                AscensionCraft.MOD_ID,
                "tribulation_reward/"
                        + rewardType.getPath()
                        + "/"
                        + path.getPath()
                        + "/realm_"
                        + reachedRealm
                        + "/"
                        + statKey
        );
    }

    private static void syncRewardChanges(IEntityData entityData) {
        if (!(entityData.getAttachedEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (player.connection == null) {
            return;
        }

        PacketDistributor.sendToPlayer(player, new SyncAttributeHolder(entityData.getAscensionAttributeHolder())
        );

        for (IEntityFormData formData : entityData.getFormData()) {
            formData.getStatSheet().sync(player, formData.getEntityFormId());
        }
    }

    public record StatMultiplierReward(String key, Stat stat, double amount) {
    }
}