package net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.elemental;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.textures.ITextureData;
import net.lucent.easygui.gui.textures.TextureData;
import net.lucent.easygui.gui.textures.TextureDataSubsection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.gui.elements.info_elements.DescriptionDisplayContainer;
import net.thejadeproject.ascension.refactor_packages.gui.elements.skills.cultivation.CultivationProgressBar;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.physiques.IPhysiqueData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastEndData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastResult;
import net.thejadeproject.ascension.refactor_packages.skills.IPersistentSkillData;
import net.thejadeproject.ascension.refactor_packages.skills.castable.CastType;
import net.thejadeproject.ascension.refactor_packages.skills.castable.ICastData;
import net.thejadeproject.ascension.refactor_packages.skills.castable.ICastableSkill;
import net.thejadeproject.ascension.refactor_packages.skills.castable.IPreCastData;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.GenericTechnique;
import net.thejadeproject.ascension.refactor_packages.util.CultivationUtil;

import java.util.List;


public abstract class ElementalCultivationSkill implements ICastableSkill {

    private static final double FALLBACK_RATE = 1.0D;

    protected abstract ResourceLocation getElementPath();

    protected double getEnvironmentMultiplier(Entity caster) {
        return 1.0D;
    }

    protected Component getElementTitle() {
        var pathObj = AscensionRegistries.Paths.PATHS_REGISTRY.get(getElementPath());
        return pathObj != null ? pathObj.getDisplayTitle() : Component.literal(getElementPath().toString());
    }

    protected String getTranslationName() {
        return getElementPath().getPath() + "_cultivation";
    }

    @Override
    public CastResult canCast(Entity caster, IPreCastData preCastData) {
        return new CastResult(CastResult.Type.SUCCESS);
    }

    @Override
    public boolean continueCasting(int ticksElapsed, Entity caster, ICastData castData) {
        if (!caster.hasData(ModAttachments.INPUT_STATES)) return false;

        if (!caster.level().isClientSide()) {
            IEntityData entityData = caster.getData(ModAttachments.ENTITY_DATA);
            IPathData pathData = entityData.getPathData(getElementPath());

            double rate = FALLBACK_RATE;

            if (pathData != null) {
                ITechnique technique = pathData.getCurrentTechnique();

                if (technique instanceof GenericTechnique genericTechnique) {
                    rate = genericTechnique.getBaseRate();
                }
            }

            CultivationUtil.tryCultivate(
                    caster,
                    getElementPath(),
                    List.of(),
                    rate * getEnvironmentMultiplier(caster)
            );
        }

        return caster.getData(ModAttachments.INPUT_STATES).isHeld("skill_cast");
    }

    protected int countNearbyBlocks(Entity caster, int radius, java.util.function.Predicate<net.minecraft.world.level.block.state.BlockState> predicate) {
        net.minecraft.world.level.Level level = caster.level();
        net.minecraft.core.BlockPos center = caster.blockPosition();

        int count = 0;

        for (net.minecraft.core.BlockPos pos : net.minecraft.core.BlockPos.betweenClosed(
                center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius)
        )) {
            if (predicate.test(level.getBlockState(pos))) {
                count++;
            }
        }

        return count;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public RenderableElement getCastElement(UIFrame frame) {
        return new CultivationProgressBar(
                frame,
                new TextureDataSubsection(
                        ResourceLocation.fromNamespaceAndPath(
                                AscensionCraft.MOD_ID,
                                "textures/gui/overlay/overlays_all.png"
                        ),
                        256,
                        256,
                        0,
                        0,
                        65,
                        7
                ),
                getElementPath()
        );
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ITextureData getIcon(IEntityData entityData) {
        return new TextureData(
                ResourceLocation.fromNamespaceAndPath(
                        AscensionCraft.MOD_ID,
                        "textures/spells/icon/placeholder.png"
                ),
                16,
                16
        );
    }

    @Override
    public Component getTitle(IEntityData entityData) {
        return Component.translatable(
                "ascension.skill.elemental_cultivation",
                getElementTitle()
        );
    }

    @Override
    public Component getDescription(IEntityData entityData) {
        return Component.translatable(
                "ascension.skill." + getTranslationName() + ".description"
        );
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

    @Override public void onEquip(IEntityData entityData) {}
    @Override public void onUnEquip(IEntityData entityData, IPreCastData preCastData) {}
    @Override public void finalCast(CastEndData reason, Entity caster, ICastData castData) {}
    @Override public void initialCast(Entity caster, IPreCastData preCastData) {}
    @Override public int getCooldown(CastEndData castEndData) { return 0; }
    @Override public void selected(IEntityData entityData) {}
    @Override public void unselected(IEntityData entityData) {}
    @Override public IPreCastData freshPreCastData() { return null; }
    @Override public IPreCastData preCastDataFromCompound(CompoundTag tag) { return null; }
    @Override public IPreCastData preCastDataFromNetwork(RegistryFriendlyByteBuf buf) { return null; }
    @Override public ICastData freshCastData() { return null; }
    @Override public ICastData castDataFromCompound(CompoundTag tag) { return null; }
    @Override public ICastData castDataFromNetwork(RegistryFriendlyByteBuf buf) { return null; }
    @Override public IPersistentSkillData freshPersistentInstance() { return null; }
    @Override public IPersistentSkillData persistentInstanceFromCompound(CompoundTag tag) { return null; }
    @Override public IPersistentSkillData persistentInstanceFromNetwork(RegistryFriendlyByteBuf buf) { return null; }
    @Override public void onAdded(IEntityData attachedEntityData) {}
    @Override public void onRemoved(IEntityData attachedEntityData, IPersistentSkillData persistentData) {}
    @Override public void onFormAdded(IEntityData heldEntity, ResourceLocation form, IPhysiqueData physiqueData) {}
    @Override public void onFormRemoved(IEntityData heldEntity, ResourceLocation form, IPhysiqueData physiqueData) {}
    @Override public void finishedCooldown(IEntityData attachedEntityData, String identifier) {}
    @Override public IPersistentSkillData freshPersistentData(IEntityData heldEntity) { return null; }
    @Override public IPersistentSkillData fromCompound(CompoundTag tag, IEntityData heldEntity) { return null; }
    @Override public IPersistentSkillData fromNetwork(RegistryFriendlyByteBuf buf) { return null; }
}