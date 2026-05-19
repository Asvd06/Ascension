package net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.weapon;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.gui.elements.info_elements.DescriptionDisplayContainer;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.qi.EntityQiContainer;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.SimplePassiveSkill;
import net.thejadeproject.ascension.refactor_packages.util.CultivationUtil;

import java.util.List;

public class SwordCultivationSkill extends SimplePassiveSkill {

    private static final float  MIN_DAMAGE         = 2.0f;
    private static final double BASE_MULTIPLIER    = 2.5D;
    private static final double QI_COST_MULTIPLIER = 1.0D;

    private final String titleKey;
    private final String descriptionKey;
    private final ResourceLocation skillId;

    public SwordCultivationSkill(String titleKey, String descriptionKey, ResourceLocation skillId) {
        this.titleKey = titleKey;
        this.descriptionKey = descriptionKey;
        this.skillId = skillId;
        NeoForge.EVENT_BUS.addListener(this::onLivingDamage);
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        float damage = event.getNewDamage();
        if (damage < MIN_DAMAGE) return;

        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.isEmpty() || !mainHand.is(ItemTags.SWORDS)) return;

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        if (entityData == null) return;
        if (!entityData.hasSkill(skillId)) return;

        IPathData swordPath = entityData.getPathData(ModPaths.SWORD.getId());
        if (swordPath == null || swordPath.isBreakingThrough()) return;

        EntityQiContainer qiContainer = entityData.getQiContainer();
        if (qiContainer == null) return;

        double qiCost = damage * QI_COST_MULTIPLIER;
        if (!qiContainer.hasQi(qiCost)) return;
        if (!qiContainer.tryConsumeQi(qiCost)) return;

        double gain = damage * BASE_MULTIPLIER;

        CultivationUtil.tryCultivate(player, ModPaths.SWORD.getId(), List.of(), gain);
        swordPath.sync(player);
    }

    @Override
    protected String getTitleKey()       { return titleKey; }

    @Override
    protected String getDescriptionKey() { return descriptionKey; }

    @Override
    protected String getIconPath()       { return "textures/spells/icon/placeholder.png"; }

    @OnlyIn(Dist.CLIENT)
    @Override
    public RenderableElement getInformationContainer(UIFrame frame, IEntityData entityData) {
        return new DescriptionDisplayContainer(frame, getTitle(entityData), getDescription(entityData));
    }
}