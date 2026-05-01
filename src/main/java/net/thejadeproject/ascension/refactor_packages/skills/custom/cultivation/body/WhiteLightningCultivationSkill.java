package net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.body;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.textures.ITextureData;
import net.lucent.easygui.gui.textures.TextureData;
import net.lucent.easygui.gui.textures.TextureDataSubsection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.breakthroughs.IBreakthroughInstance;
import net.thejadeproject.ascension.refactor_packages.breakthroughs.NineHeavenlyTribulations;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.events.CultivateEvent;
import net.thejadeproject.ascension.refactor_packages.gui.elements.skills.cultivation.CultivationProgressBar;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.PathData;
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
import net.thejadeproject.ascension.refactor_packages.techniques.custom.body.WhiteLightningTenStageTechnique;

import java.util.List;

public class WhiteLightningCultivationSkill implements ICastableSkill {


    private static final double BASE_RATE = 2.0D;

    @Override
    public CastResult canCast(Entity caster, IPreCastData preCastData) {
        return new CastResult(CastResult.Type.SUCCESS);
    }

    @Override
    public boolean continueCasting(int ticksElapsed, Entity caster, ICastData castData) {
        if (!caster.hasData(ModAttachments.INPUT_STATES)) return false;

        if (!caster.level().isClientSide()) {
            IEntityData entityData = caster.getData(ModAttachments.ENTITY_DATA);
            PathData pathData = entityData.getPathData(ModPaths.BODY.getId());

            if (pathData == null) return false;
            if (pathData.isBreakingThrough()) return false;
            if (pathData.getLastUsedTechnique() == null) return false;

            ITechnique rawTechnique = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(
                    pathData.getLastUsedTechnique()
            );

            if (!(rawTechnique instanceof WhiteLightningTenStageTechnique technique)) {
                return false;
            }

            double bodyBonus = Math.max(
                    1.0D,
                    entityData.getPathBonusHandler().getPathBonus(ModPaths.BODY.getId())
            );

            double fistBonus = Math.max(
                    1.0D,
                    entityData.getPathBonusHandler().getPathBonus(ModPaths.FIST.getId())
            );

            double base = BASE_RATE
                    * bodyBonus
                    * fistBonus
                    * getCultivationMultiplier(caster);

            CultivateEvent event = new CultivateEvent(
                    caster,
                    base,
                    ModPaths.BODY.getId(),
                    List.of(ModPaths.FIST.getId())
            );

            NeoForge.EVENT_BUS.post(event);

            double maxQi = technique.getMaxQiForRealm(
                    pathData.getMajorRealm(),
                    pathData.getMinorRealm()
            );

            if (pathData.getCurrentRealmProgress() + event.getRate() >= maxQi) {
                pathData.setCurrentRealmProgress(maxQi);

                if (
                        pathData.getMinorRealm() < technique.getMaxMinorRealm(pathData.getMajorRealm())
                                && technique.canBreakthroughMinorRealm(
                                entityData,
                                pathData.getMajorRealm(),
                                pathData.getMinorRealm(),
                                pathData.getCurrentRealmProgress()
                        )
                ) {
                    pathData.handleRealmChange(
                            pathData.getMajorRealm(),
                            pathData.getMinorRealm() + 1,
                            entityData
                    );
                } else if (
                        pathData.getMajorRealm() < technique.getMaxMajorRealm()
                                && technique.getStabilityHandler() != null
                                && pathData.getCurrentRealmStability() < technique.getStabilityHandler().getMaxCultivationTicks()
                ) {
                    IBreakthroughInstance instance = new NineHeavenlyTribulations(1);

                    pathData.setBreakthroughInstance(instance);
                    pathData.setBreakingThrough(true);
                }
            } else {
                pathData.setCurrentRealmProgress(
                        pathData.getCurrentRealmProgress() + event.getRate()
                );
            }

            if (caster instanceof Player player) {
                pathData.sync(player);
            }
        }

        return caster.getData(ModAttachments.INPUT_STATES).isHeld("skill_cast");
    }

    private double getCultivationMultiplier(Entity caster) {
        double multiplier = 1.0D;

        if (isUnarmed(caster)) {
            multiplier += 0.25D;
        }

        if (hasTurbidEnergy(caster)) {
            multiplier += 0.35D;
        }

        return multiplier;
    }

    private boolean isUnarmed(Entity caster) {
        if (!(caster instanceof LivingEntity living)) return false;

        return living.getMainHandItem().isEmpty()
                && living.getOffhandItem().isEmpty();
    }

    private boolean hasTurbidEnergy(Entity caster) {
        if (!(caster instanceof LivingEntity living)) return false;

        for (MobEffectInstance effect : living.getActiveEffects()) {
            if (!effect.getEffect().value().isBeneficial()) {
                return true;
            }
        }

        return false;
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
                ModPaths.BODY.getId()
        );
    }

    @Override
    public ITextureData getIcon() {
        return new TextureData(
                ResourceLocation.fromNamespaceAndPath(
                        AscensionCraft.MOD_ID,
                        "textures/spells/icon/placeholder_white.png"
                ),
                16,
                16
        );
    }

    @Override
    public Component getTitle() {
        return Component.translatable("ascension.skill.white_lightning_cultivation_skill");
    }

    @Override
    public Component getDescription() {
        return Component.translatable(
                "ascension.skill.white_lightning_cultivation_skill.description"
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