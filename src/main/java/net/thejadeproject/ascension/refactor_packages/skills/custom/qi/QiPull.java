package net.thejadeproject.ascension.refactor_packages.skills.custom.qi;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.textures.ITextureData;
import net.lucent.easygui.gui.textures.TextureData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.gui.elements.info_elements.DescriptionDisplayContainer;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.physiques.IPhysiqueData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastEndData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastResult;
import net.thejadeproject.ascension.refactor_packages.skills.IPersistentSkillData;
import net.thejadeproject.ascension.refactor_packages.skills.castable.CastType;
import net.thejadeproject.ascension.refactor_packages.skills.castable.ICastData;
import net.thejadeproject.ascension.refactor_packages.skills.castable.ICastableSkill;
import net.thejadeproject.ascension.refactor_packages.skills.castable.IPreCastData;

import java.util.concurrent.ConcurrentHashMap;

public class QiPull implements ICastableSkill {

    private static final double BASE_TARGET_RANGE        = 12.0;
    private static final double TARGET_RANGE_PER_REALM   = 3.0;
    private static final double BASE_PULL_RADIUS         = 5.0;
    private static final double PULL_RADIUS_PER_REALM    = 1.5;
    private static final double BASE_QI_COST             = 24.0;
    private static final double QI_COST_PER_REALM        = 5.0;
    private static final double BASE_PULL_H              = 1.15;
    private static final double PULL_H_PER_REALM         = 0.45;
    private static final double BASE_PULL_V              = 0.15;
    private static final double PULL_V_PER_REALM         = 0.05;
    private static final double MIN_PULL_DISTANCE        = 0.75;
    private static final double CRASH_MULTIPLIER         = 4.0;
    private static final int    TRACK_TICKS              = 8;
    private static final int    COOLDOWN_TICKS           = 200;

    public static final ConcurrentHashMap<LivingEntity, Float> PULL_CRASH_DAMAGE = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<LivingEntity, Long>  PULL_EXPIRY       = new ConcurrentHashMap<>();

    @Override
    public CastResult canCast(Entity caster, IPreCastData preCastData) {
        if (caster.level().isClientSide()) return new CastResult(CastResult.Type.SUCCESS);
        if (!caster.hasData(ModAttachments.ENTITY_DATA)) return new CastResult(CastResult.Type.FAILURE);

        IEntityData data = caster.getData(ModAttachments.ENTITY_DATA);
        int majorRealm = getMajorRealm(caster);

        double cost = BASE_QI_COST + majorRealm * QI_COST_PER_REALM;

        if (!data.getQiContainer().hasQi(cost)) {
            return new CastResult(CastResult.Type.FAILURE);
        }

        return new CastResult(CastResult.Type.SUCCESS);
    }

    @Override
    public void initialCast(Entity caster, IPreCastData preCastData) {
        if (caster.level().isClientSide()) return;
        if (!caster.hasData(ModAttachments.ENTITY_DATA)) return;

        IEntityData data = caster.getData(ModAttachments.ENTITY_DATA);
        int majorRealm = getMajorRealm(caster);

        double cost = BASE_QI_COST + majorRealm * QI_COST_PER_REALM;
        if (!data.getQiContainer().tryConsumeQi(cost)) return;

        double targetRange = BASE_TARGET_RANGE + majorRealm * TARGET_RANGE_PER_REALM;
        double pullRadius = BASE_PULL_RADIUS + majorRealm * PULL_RADIUS_PER_REALM;
        double pullH = BASE_PULL_H + majorRealm * PULL_H_PER_REALM;
        double pullV = BASE_PULL_V + majorRealm * PULL_V_PER_REALM;

        float crashDmg = (float) (pullH * CRASH_MULTIPLIER);
        long expiry = caster.level().getGameTime() + TRACK_TICKS;

        Vec3 target = getTargetPoint(caster, targetRange);

        pullNearbyEntities(caster, target, pullRadius, pullH, pullV, crashDmg, expiry);
    }

    private Vec3 getTargetPoint(Entity caster, double targetRange) {
        HitResult hit = caster.pick(targetRange, 1.0F, false);

        if (hit.getType() == HitResult.Type.BLOCK) {
            return hit.getLocation();
        }

        Vec3 eye = caster.getEyePosition();
        Vec3 look = caster.getViewVector(1.0F);

        return eye.add(look.scale(targetRange));
    }

    private void pullNearbyEntities(
            Entity caster,
            Vec3 target,
            double pullRadius,
            double pullH,
            double pullV,
            float crashDmg,
            long expiry
    ) {
        AABB area = AABB.ofSize(target, pullRadius * 2, pullRadius * 2, pullRadius * 2);

        caster.level().getEntitiesOfClass(
                LivingEntity.class,
                area,
                e -> e != caster && e.isAlive() && !e.isSpectator()
        ).forEach(entity -> {
            Vec3 offset = target.subtract(entity.position());
            double distance = offset.length();

            if (distance < MIN_PULL_DISTANCE) return;

            Vec3 dir = offset.normalize();

            double scaledPull = Math.min(pullH, distance * 0.35);

            entity.push(
                    dir.x * scaledPull,
                    Math.max(dir.y * scaledPull, pullV),
                    dir.z * scaledPull
            );

            entity.hurtMarked = true;

            PULL_CRASH_DAMAGE.put(entity, crashDmg);
            PULL_EXPIRY.put(entity, expiry);
        });
    }

    private int getMajorRealm(Entity caster) {
        if (!caster.hasData(ModAttachments.ENTITY_DATA)) return 0;

        IEntityData data = caster.getData(ModAttachments.ENTITY_DATA);
        int highest = 0;

        for (IPathData pathData : data.getAllPathData()) {
            if (pathData == null) continue;
            highest = Math.max(highest, pathData.getMajorRealm());
        }

        return highest;
    }

    @Override public boolean continueCasting(int ticksElapsed, Entity caster, ICastData castData) { return false; }
    @Override public void finalCast(CastEndData reason, Entity caster, ICastData castData) {}
    @Override public void onEquip(IEntityData entityData) {}
    @Override public void onUnEquip(IEntityData entityData, IPreCastData preCastData) {}
    @Override public int getCooldown(CastEndData castEndData) { return COOLDOWN_TICKS; }
    @Override public void selected(IEntityData entityData) {}
    @Override public void unselected(IEntityData entityData) {}
    @Override public void onAdded(IEntityData attachedEntityData) {}
    @Override public void onRemoved(IEntityData attachedEntityData, IPersistentSkillData persistentData) {}
    @Override public void onFormAdded(IEntityData heldEntity, ResourceLocation form, IPhysiqueData physiqueData) {}
    @Override public void onFormRemoved(IEntityData heldEntity, ResourceLocation form, IPhysiqueData physiqueData) {}
    @Override public void finishedCooldown(IEntityData attachedEntityData, String identifier) {}

    @Override public IPreCastData freshPreCastData() { return null; }
    @Override public IPreCastData preCastDataFromCompound(CompoundTag tag) { return null; }
    @Override public IPreCastData preCastDataFromNetwork(RegistryFriendlyByteBuf buf) { return null; }
    @Override public ICastData freshCastData() { return null; }
    @Override public ICastData castDataFromCompound(CompoundTag tag) { return null; }
    @Override public ICastData castDataFromNetwork(RegistryFriendlyByteBuf buf) { return null; }
    @Override public IPersistentSkillData freshPersistentInstance() { return null; }
    @Override public IPersistentSkillData persistentInstanceFromCompound(CompoundTag tag) { return null; }
    @Override public IPersistentSkillData persistentInstanceFromNetwork(RegistryFriendlyByteBuf buf) { return null; }
    @Override public IPersistentSkillData freshPersistentData(IEntityData heldEntity) { return null; }
    @Override public IPersistentSkillData fromCompound(CompoundTag tag, IEntityData heldEntity) { return null; }
    @Override public IPersistentSkillData fromNetwork(RegistryFriendlyByteBuf buf) { return null; }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public RenderableElement getCastElement(UIFrame frame) {
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ITextureData getIcon(IEntityData entityData) {
        return new TextureData(
                ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "textures/spells/icon/placeholder.png"),
                16, 16
        );
    }

    @Override
    public Component getTitle(IEntityData entityData) {
        return Component.translatable("ascension.skills.qi_pull");
    }

    @Override
    public Component getDescription(IEntityData entityData) {
        return Component.translatable("ascension.skills.qi_pull.desc");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public RenderableElement getInformationContainer(UIFrame frame, IEntityData entityData) {
        return new DescriptionDisplayContainer(frame, getTitle(entityData), getDescription(entityData));
    }
}