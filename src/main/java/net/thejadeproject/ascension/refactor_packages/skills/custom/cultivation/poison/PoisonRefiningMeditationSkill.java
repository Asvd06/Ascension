package net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.poison;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.thejadeproject.ascension.common.items.ModItems;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.common.items.pills.PillItem;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.breakthroughs.IBreakthroughInstance;
import net.thejadeproject.ascension.refactor_packages.breakthroughs.NineHeavenlyTribulations;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.PathData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skills.custom.ModSkills;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.SimplePassiveSkill;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.handlers.MyriadVenomTechniqueData;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.poison.MyriadVenomRefinementTechnique;

import java.util.HashMap;
import java.util.Map;

public class PoisonRefiningMeditationSkill extends SimplePassiveSkill {

    private static final double BASE_GAIN         = 20.0D;
    private static final double BASE_QI_COST      = 15.0D;
    private static final double QI_COST_PER_REALM = 10.0D;
    private static final double REALM_STEP        = 0.5D;
    private static final double PURITY_DIVISOR    = 100.0D;

    private static final Map<Item, Double> POISON_ITEM_VALUES = new HashMap<>();

    static {
        POISON_ITEM_VALUES.put(ModItems.QI_DEVOURING_PARASITE_PILL.get(), 2.0D);
        POISON_ITEM_VALUES.put(Items.SPIDER_EYE,           1.0D);
        POISON_ITEM_VALUES.put(Items.FERMENTED_SPIDER_EYE, 1.5D);
        POISON_ITEM_VALUES.put(Items.POISONOUS_POTATO,     0.8D);
        POISON_ITEM_VALUES.put(Items.PUFFERFISH,           1.2D);
    }

    public PoisonRefiningMeditationSkill() {
        NeoForge.EVENT_BUS.addListener(this::onItemFinishedUsing);
    }

    private void onItemFinishedUsing(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        if (!entityData.hasSkill(ModSkills.POISON_REFINING_MEDITATION_SKILL.getId())) return;

        PathData poisonPath = entityData.getPathData(ModPaths.POISON.getId());
        if (poisonPath == null || poisonPath.isBreakingThrough()) return;
        if (poisonPath.getLastUsedTechnique() == null) return;

        ITechnique rawTechnique = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(
                poisonPath.getLastUsedTechnique()
        );
        if (!(rawTechnique instanceof MyriadVenomRefinementTechnique technique)) return;

        ItemStack stack = event.getItem();
        Item item = stack.getItem();

        Double itemBaseValue = POISON_ITEM_VALUES.get(item);
        if (itemBaseValue == null) return;

        ITechniqueData rawData = poisonPath.getTechniqueData(poisonPath.getLastUsedTechnique());

        double realmMultiplier;
        double purityScale;
        double qiCost;

        if (item instanceof PillItem) {
            Integer pillMajorRealm = stack.get(ModDataComponents.PILL_MAJOR_REALM.get());
            Integer pillPurity     = stack.get(ModDataComponents.PILL_PURITY.get());

            int resolvedRealm   = pillMajorRealm != null ? pillMajorRealm : 1;
            int resolvedPurity  = pillPurity     != null ? pillPurity     : 1;

            realmMultiplier = 1.0D + (resolvedRealm - 1) * REALM_STEP;
            purityScale     = resolvedPurity / PURITY_DIVISOR;
            qiCost          = BASE_QI_COST + (resolvedRealm * QI_COST_PER_REALM);
        } else {
            realmMultiplier = 1.0D;
            purityScale     = 1.0D;
            qiCost          = BASE_QI_COST + (poisonPath.getMajorRealm() * QI_COST_PER_REALM);
        }

        if (!entityData.getQiContainer().hasQi(qiCost)) return;
        if (!entityData.getQiContainer().tryConsumeQi(qiCost)) return;

        if (rawData instanceof MyriadVenomTechniqueData venomData) {
            venomData.setEntityData(entityData);
            venomData.incrementConsumption();
        }

        double pathBonus = Math.max(1.0D, entityData.getPathBonusHandler().getPathBonus(ModPaths.POISON.getId()));
        double gain      = BASE_GAIN * itemBaseValue * realmMultiplier * purityScale * pathBonus;

        advanceCultivation(entityData, poisonPath, technique, rawData, gain);
        poisonPath.sync(player);
    }

    private void advanceCultivation(
            IEntityData entityData,
            PathData pathData,
            MyriadVenomRefinementTechnique technique,
            ITechniqueData rawData,
            double gain
    ) {
        double maxQi = technique.getMaxQiForRealm(pathData.getMajorRealm(), pathData.getMinorRealm());

        if (pathData.getCurrentRealmProgress() + gain >= maxQi) {
            pathData.setCurrentRealmProgress(maxQi);

            boolean canMinorBreakthrough = pathData.getMinorRealm() < technique.getMaxMinorRealm(pathData.getMajorRealm())
                    && technique.canBreakthroughMinorRealm(
                    entityData,
                    pathData.getMajorRealm(),
                    pathData.getMinorRealm(),
                    pathData.getCurrentRealmProgress()
            );

            if (canMinorBreakthrough) {
                pathData.handleRealmChange(
                        pathData.getMajorRealm(),
                        pathData.getMinorRealm() + 1,
                        entityData
                );
                return;
            }

            boolean canMajorBreakthrough = pathData.getMajorRealm() < technique.getMaxMajorRealm()
                    && technique.canBreakthrough(
                    entityData,
                    pathData.getMajorRealm(),
                    pathData.getMinorRealm(),
                    pathData.getCurrentRealmProgress()
            );

            if (canMajorBreakthrough) {
                IBreakthroughInstance instance = new NineHeavenlyTribulations(1);
                pathData.setBreakthroughInstance(instance);
                pathData.setBreakingThrough(true);
            }
        } else {
            pathData.setCurrentRealmProgress(pathData.getCurrentRealmProgress() + gain);
        }
    }

    @Override
    protected String getTitleKey()       { return "ascension.skill.poison_refining_meditation"; }

    @Override
    protected String getDescriptionKey() { return "ascension.skill.poison_refining_meditation.desc"; }

    @Override
    protected String getIconPath()       { return "textures/spells/icon/placeholder.png"; }
}
