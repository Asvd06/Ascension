package net.thejadeproject.ascension.refactor_packages.skills.custom.passive.body;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.qi.EntityQiContainer;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.SimplePassiveSkill;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.body.BodyElementTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.body.CombinedBodyElementTechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.body.FiveElementBodyTechnique;
import net.thejadeproject.ascension.refactor_packages.util.CultivationUtil;

import java.util.List;
import java.util.Set;

public class BodyCultivationSkill extends SimplePassiveSkill {

    private static final float MIN_DAMAGE = 10.0f;
    private static final double BASE_MULTIPLIER = 3.3;

    private static final double ELEMENTAL_SPLASH_RATE = 0.25D;

    private static final Set<ResourceLocation> FIVE_ELEMENTS = Set.of(
            ModPaths.FIRE.getId(),
            ModPaths.WATER.getId(),
            ModPaths.WOOD.getId(),
            ModPaths.EARTH.getId(),
            ModPaths.METAL.getId()
    );

    private final String titleKey;
    private final String descriptionKey;
    private final ResourceLocation skillId;

    public BodyCultivationSkill(String titleKey, String descriptionKey, ResourceLocation skillId) {
        this.titleKey = titleKey;
        this.descriptionKey = descriptionKey;
        this.skillId = skillId;
        NeoForge.EVENT_BUS.addListener(this::onLivingDamage);
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        float damage = event.getNewDamage();
        if (damage < MIN_DAMAGE) return;

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        if (!entityData.hasSkill(skillId)) return;

        IPathData bodyPath = entityData.getPathData(ModPaths.BODY.getId());
        if (bodyPath == null || bodyPath.isBreakingThrough()) return;

        EntityQiContainer qiContainer = entityData.getQiContainer();
        if (qiContainer == null) return;
        if (!qiContainer.hasQi(damage)) return;
        if (!qiContainer.tryConsumeQi(damage)) return;

        ITechnique currentTechnique = bodyPath.getCurrentTechnique();
        Set<ResourceLocation> elementalSubPaths = getElementalSubPaths(currentTechnique);

        double gain = damage * BASE_MULTIPLIER;

        boolean cultivatedBody = CultivationUtil.tryCultivate(
                player,
                ModPaths.BODY.getId(),
                List.copyOf(elementalSubPaths),
                gain
        );

        if (cultivatedBody && !elementalSubPaths.isEmpty()) {
            double totalElementalGain = gain * ELEMENTAL_SPLASH_RATE;
            double perElementGain = totalElementalGain / elementalSubPaths.size();

            for (ResourceLocation elementPath : elementalSubPaths) {
                CultivationUtil.tryCultivate(
                        player,
                        elementPath,
                        List.of(ModPaths.BODY.getId()),
                        perElementGain
                );
            }
        }
    }

    private Set<ResourceLocation> getElementalSubPaths(ITechnique technique) {
        if (technique instanceof BodyElementTechnique bodyElementTechnique) {
            return Set.of(bodyElementTechnique.getElement());
        }

        if (technique instanceof CombinedBodyElementTechnique combinedBodyElementTechnique) {
            return combinedBodyElementTechnique.getElements();
        }

        if (technique instanceof FiveElementBodyTechnique) {
            return FIVE_ELEMENTS;
        }

        return Set.of();
    }

    @Override
    protected String getTitleKey() {
        return titleKey;
    }

    @Override
    protected String getDescriptionKey() {
        return descriptionKey;
    }

    @Override
    protected String getIconPath() {
        return "textures/spells/icon/placeholder.png";
    }
}