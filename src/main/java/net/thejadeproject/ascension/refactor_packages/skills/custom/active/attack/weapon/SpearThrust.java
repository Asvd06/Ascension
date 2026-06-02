package net.thejadeproject.ascension.refactor_packages.skills.custom.active.attack.weapon;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.textures.ITextureData;
import net.lucent.easygui.gui.textures.TextureData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.gui.elements.info_elements.DescriptionDisplayContainer;
import net.thejadeproject.ascension.refactor_packages.handlers.AscensionDamageHandler;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.physiques.IPhysiqueData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastEndData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastResult;
import net.thejadeproject.ascension.refactor_packages.skills.IPersistentSkillData;
import net.thejadeproject.ascension.refactor_packages.skills.castable.CastType;
import net.thejadeproject.ascension.refactor_packages.skills.castable.ICastData;
import net.thejadeproject.ascension.refactor_packages.skills.castable.ICastableSkill;
import net.thejadeproject.ascension.refactor_packages.skills.castable.IPreCastData;
import net.thejadeproject.ascension.refactor_packages.skills.custom.SkillTargetingHelper;
import net.thejadeproject.ascension.util.ModTags;

import java.util.HashSet;
import java.util.List;

public class SpearThrust implements ICastableSkill {

    private static final double START_QI_COST = 10.0D;
    private static final double BASE_THRUST_QI_COST = 2.0D;
    private static final double THRUST_QI_COST_RAMP = 0.45D;

    private static final int HIT_INTERVAL_TICKS = 5;
    private static final int COOLDOWN_TICKS = 120;

    private static final double BASE_REACH = 5.5D;
    private static final double REACH_PER_MAJOR = 0.85D;
    private static final double REACH_PER_MINOR = 0.08D;
    private static final double MAX_REACH = 10.5D;

    private static final double HIT_RADIUS = 0.85D;
    private static final float BASE_DAMAGE = 1.5F;

    @Override
    public CastResult canCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) {
            return new CastResult(CastResult.Type.FAILURE);
        }

        if (!player.hasData(ModAttachments.ENTITY_DATA)) {
            return new CastResult(CastResult.Type.FAILURE);
        }

        if (!player.getMainHandItem().is(ModTags.Items.SPEAR)) {
            return new CastResult(CastResult.Type.FAILURE);
        }

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        return entityData.getQiContainer().hasQi(START_QI_COST)
                ? new CastResult(CastResult.Type.SUCCESS)
                : new CastResult(CastResult.Type.FAILURE);
    }

    @Override
    public void initialCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;
        if (!player.hasData(ModAttachments.ENTITY_DATA)) return;
        if (!player.getMainHandItem().is(ModTags.Items.SPEAR)) return;

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        if (!entityData.getQiContainer().tryConsumeQi(START_QI_COST)) {
            return;
        }

        player.swing(InteractionHand.MAIN_HAND, true);
        spawnSpearThrustParticles(player);
    }

    @Override
    public boolean continueCasting(int ticksElapsed, Entity caster, ICastData castData) {
        if (!(caster instanceof ServerPlayer player)) {
            return false;
        }

        if (player.level().isClientSide()) {
            return false;
        }

        if (!player.hasData(ModAttachments.ENTITY_DATA)) {
            return false;
        }

        if (!player.getData(ModAttachments.INPUT_STATES).isHeld("skill_cast")) {
            return false;
        }

        if (!player.getMainHandItem().is(ModTags.Items.SPEAR)) {
            return false;
        }

        if (ticksElapsed % HIT_INTERVAL_TICKS != 0) {
            return true;
        }

        int thrustIndex = ticksElapsed / HIT_INTERVAL_TICKS;
        double qiCost = calculateThrustQiCost(thrustIndex);

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        if (!entityData.getQiContainer().tryConsumeQi(qiCost)) {
            return false;
        }

        hitTargetsInLine(player);
        spawnSpearThrustParticles(player);
        player.swing(InteractionHand.MAIN_HAND, true);

        return true;
    }

    private double calculateThrustQiCost(int thrustIndex) {
        return BASE_THRUST_QI_COST + thrustIndex * THRUST_QI_COST_RAMP;
    }

    private void hitTargetsInLine(ServerPlayer player) {
        Vec3 direction = player.getLookAngle().normalize();

        List<LivingEntity> targets = SkillTargetingHelper.findLivingTargetsInLine(
                player,
                calculateReach(player),
                HIT_RADIUS,
                true
        );

        float damage = calculateDamage(player);

        HashSet<ResourceLocation> paths = new HashSet<>();
        paths.add(ModPaths.SPEAR.getId());

        AscensionDamageHandler.AscensionDamageSource source =
                new AscensionDamageHandler.AscensionDamageSource(
                        paths,
                        player.damageSources().playerAttack(player)
                );

        for (LivingEntity target : targets) {
            target.invulnerableTime = 0;
            target.hurt(source, damage);

            Vec3 push = direction.scale(0.18D);
            target.push(push.x, 0.04D, push.z);
            target.hurtMarked = true;
        }
    }

    private double calculateReach(ServerPlayer player) {
        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        IPathData spearData = entityData.getPathData(ModPaths.SPEAR.getId());
        int major = spearData != null ? spearData.getMajorRealm() : 0;
        int minor = spearData != null ? spearData.getMinorRealm() : 0;

        double reach = BASE_REACH
                + major * REACH_PER_MAJOR
                + minor * REACH_PER_MINOR;

        return Math.min(reach, MAX_REACH);
    }

    private float calculateDamage(ServerPlayer player) {
        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        IPathData spearData = entityData.getPathData(ModPaths.SPEAR.getId());
        int major = spearData != null ? spearData.getMajorRealm() : 0;
        int minor = spearData != null ? spearData.getMinorRealm() : 0;

        float attackDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);

        float multiplier = 0.38F + major * 0.055F + minor * 0.008F;

        return BASE_DAMAGE + attackDamage * multiplier;
    }

    private void spawnSpearThrustParticles(ServerPlayer player) {
        ServerLevel level = player.serverLevel();

        Vec3 direction = player.getLookAngle().normalize();
        Vec3 start = player.getEyePosition().add(direction.scale(0.6D));
        Vec3 end = start.add(direction.scale(calculateReach(player)));

        Vec3 diff = end.subtract(start);
        int steps = 8;

        for (int i = 0; i <= steps; i++) {
            double progress = i / (double) steps;
            Vec3 pos = start.add(diff.scale(progress));

            level.sendParticles(
                    ParticleTypes.CRIT,
                    pos.x, pos.y, pos.z,
                    1,
                    0.025D, 0.025D, 0.025D,
                    0.0D
            );
        }

        level.sendParticles(
                ParticleTypes.END_ROD,
                end.x, end.y, end.z,
                3,
                0.08D, 0.08D, 0.08D,
                0.01D
        );
    }

    @Override
    public void finalCast(CastEndData reason, Entity caster, ICastData castData) {

    }

    @Override
    public int getCooldown(CastEndData castEndData) {
        return COOLDOWN_TICKS;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
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
        return Component.translatable("ascension.skill.spear_thrust");
    }

    @Override
    public Component getDescription(IEntityData entityData) {
        return Component.translatable("ascension.skill.spear_thrust.description");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public RenderableElement getInformationContainer(UIFrame frame, IEntityData entityData) {
        return new DescriptionDisplayContainer(frame, getTitle(entityData), getDescription(entityData));
    }

    @Override public void onEquip(IEntityData entityData) {}
    @Override public void onUnEquip(IEntityData entityData, IPreCastData preCastData) {}
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

    @OnlyIn(Dist.CLIENT)
    @Override public RenderableElement getCastElement(UIFrame frame) { return null; }

    @Override public void onAdded(IEntityData attachedEntityData) {}
    @Override public void onRemoved(IEntityData attachedEntityData, IPersistentSkillData persistentData) {}
    @Override public void onFormAdded(IEntityData heldEntity, ResourceLocation form, IPhysiqueData physiqueData) {}
    @Override public void onFormRemoved(IEntityData heldEntity, ResourceLocation form, IPhysiqueData physiqueData) {}
    @Override public void finishedCooldown(IEntityData attachedEntityData, String identifier) {}

    @Override public IPersistentSkillData freshPersistentData(IEntityData heldEntity) { return null; }
    @Override public IPersistentSkillData fromCompound(CompoundTag tag, IEntityData heldEntity) { return null; }
    @Override public IPersistentSkillData fromNetwork(RegistryFriendlyByteBuf buf) { return null; }
}