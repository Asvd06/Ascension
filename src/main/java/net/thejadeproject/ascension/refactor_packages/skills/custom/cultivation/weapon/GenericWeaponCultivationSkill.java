package net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.weapon;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.gui.elements.info_elements.DescriptionDisplayContainer;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.qi.EntityQiContainer;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.SimplePassiveSkill;
import net.thejadeproject.ascension.refactor_packages.util.CultivationUtil;

import java.util.List;

public class GenericWeaponCultivationSkill extends SimplePassiveSkill {

    private static final float DEFAULT_MIN_DAMAGE = 2.0f;
    private static final double DEFAULT_BASE_MULTIPLIER = 2.5D;
    private static final double DEFAULT_QI_COST_MULTIPLIER = 1.0D;

    private final String titleKey;
    private final String descriptionKey;
    private final String iconPath;

    private final ResourceLocation skillId;
    private final ResourceLocation pathId;
    private final List<ResourceLocation> secondaryPaths;

    private final TagKey<Item> weaponTag;
    private final boolean allowEmptyHand;
    private final boolean requireDirectPlayerHit;

    private final float minDamage;
    private final double baseMultiplier;
    private final double qiCostMultiplier;

    public GenericWeaponCultivationSkill(
            String titleKey,
            String descriptionKey,
            String iconPath,
            ResourceLocation skillId,
            ResourceLocation pathId,
            TagKey<Item> weaponTag,
            boolean allowEmptyHand,
            boolean requireDirectPlayerHit
    ) {
        this(
                titleKey,
                descriptionKey,
                iconPath,
                skillId,
                pathId,
                List.of(),
                weaponTag,
                allowEmptyHand,
                requireDirectPlayerHit,
                DEFAULT_MIN_DAMAGE,
                DEFAULT_BASE_MULTIPLIER,
                DEFAULT_QI_COST_MULTIPLIER
        );
    }

    public GenericWeaponCultivationSkill(
            String titleKey,
            String descriptionKey,
            String iconPath,
            ResourceLocation skillId,
            ResourceLocation pathId,
            List<ResourceLocation> secondaryPaths,
            TagKey<Item> weaponTag,
            boolean allowEmptyHand,
            boolean requireDirectPlayerHit,
            float minDamage,
            double baseMultiplier,
            double qiCostMultiplier
    ) {
        this.titleKey = titleKey;
        this.descriptionKey = descriptionKey;
        this.iconPath = iconPath;

        this.skillId = skillId;
        this.pathId = pathId;
        this.secondaryPaths = List.copyOf(secondaryPaths);

        this.weaponTag = weaponTag;
        this.allowEmptyHand = allowEmptyHand;
        this.requireDirectPlayerHit = requireDirectPlayerHit;

        this.minDamage = minDamage;
        this.baseMultiplier = baseMultiplier;
        this.qiCostMultiplier = qiCostMultiplier;

        NeoForge.EVENT_BUS.addListener(this::onLivingDamage);
    }

    public void onLivingDamage(LivingDamageEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        if (requireDirectPlayerHit && event.getSource().getDirectEntity() != player) return;

        float damage = event.getNewDamage();
        if (damage < minDamage) return;

        ItemStack mainHand = player.getMainHandItem();
        if (!isValidWeapon(mainHand)) return;

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        if (entityData == null) return;
        if (!entityData.hasSkill(skillId)) return;

        IPathData pathData = entityData.getPathData(pathId);
        if (pathData == null || pathData.isBreakingThrough()) return;

        EntityQiContainer qiContainer = entityData.getQiContainer();
        if (qiContainer == null) return;

        double qiCost = damage * qiCostMultiplier;
        if (!qiContainer.hasQi(qiCost)) return;
        if (!qiContainer.tryConsumeQi(qiCost)) return;

        double gain = damage * baseMultiplier;

        CultivationUtil.tryCultivate(
                player,
                pathId,
                secondaryPaths,
                gain
        );

        pathData.sync(player);
    }

    private boolean isValidWeapon(ItemStack stack) {
        if (stack.isEmpty()) {
            return allowEmptyHand;
        }

        return stack.is(weaponTag);
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
        return iconPath;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public RenderableElement getInformationContainer(UIFrame frame, IEntityData entityData) {
        return new DescriptionDisplayContainer(
                frame,
                getTitle(entityData),
                getDescription(entityData)
        );
    }
}