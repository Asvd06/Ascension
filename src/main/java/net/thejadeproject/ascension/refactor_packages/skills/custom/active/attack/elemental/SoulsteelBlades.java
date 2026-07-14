package net.thejadeproject.ascension.refactor_packages.skills.custom.active.attack.elemental;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.handlers.AscensionDamageHandler;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastEndData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastResult;
import net.thejadeproject.ascension.refactor_packages.skills.castable.IPreCastData;
import net.thejadeproject.ascension.refactor_packages.skills.custom.SkillTargetingHelper;
import net.thejadeproject.ascension.refactor_packages.skills.custom.active.SimpleInstantCastSkill;

import java.util.HashSet;

public class SoulsteelBlades extends SimpleInstantCastSkill {

    private static final double QI_COST = 44.0D;
    private static final double RANGE = 20.0D;
    private static final double TARGET_INFLATION = 1.5D;

    private static final int BASE_BLADE_COUNT = 3;
    private static final int MAX_BLADE_COUNT = 8;

    private static final float BASE_DAMAGE_PER_BLADE = 3.0F;
    private static final int COOLDOWN_TICKS = 150;

    @Override
    public CastResult canCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) {
            return new CastResult(CastResult.Type.FAILURE);
        }

        if (!player.hasData(ModAttachments.ENTITY_DATA)) {
            return new CastResult(CastResult.Type.FAILURE);
        }

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        if (!entityData.getQiContainer().hasQi(QI_COST)) {
            return new CastResult(CastResult.Type.FAILURE);
        }

        LivingEntity target = findTarget(player);

        return target != null ? new CastResult(CastResult.Type.SUCCESS) : new CastResult(CastResult.Type.FAILURE);
    }

    @Override
    public void initialCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;
        if (!player.hasData(ModAttachments.ENTITY_DATA)) return;


        LivingEntity target = findTarget(player);
        if (target == null) return;

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        if (!entityData.getQiContainer().tryConsumeQi(QI_COST)) {
            return;
        }

        int bladeCount = calculateBladeCount(entityData);
        float damagePerBlade = calculateDamagePerBlade(player, entityData);

        HashSet<ResourceLocation> paths = new HashSet<>();
        paths.add(ModPaths.METAL.getId());

        AscensionDamageHandler.AscensionDamageSource source = new AscensionDamageHandler.AscensionDamageSource(paths, player.damageSources().magic());


        float totalDamage = damagePerBlade * bladeCount;
        target.invulnerableTime = 0;
        boolean damaged = target.hurt(source, totalDamage);

        if (!damaged) {
            return;
        }

        spawnBladeVolley(player, target, bladeCount);
    }

    private LivingEntity findTarget(ServerPlayer player) {
        return SkillTargetingHelper.findLookTarget(player, RANGE, TARGET_INFLATION, true);
    }

    private int calculateBladeCount(IEntityData entityData) {
        IPathData metal = entityData.getPathData(ModPaths.METAL.getId());

        int major = metal != null ? metal.getMajorRealm() : 0;
        return Math.min(BASE_BLADE_COUNT + major, MAX_BLADE_COUNT);
    }

    private float calculateDamagePerBlade(ServerPlayer player, IEntityData entityData) {
        IPathData metal = entityData.getPathData(ModPaths.METAL.getId());

        int major = metal != null ? metal.getMajorRealm() : 0;
        int minor = metal != null ? metal.getMinorRealm() : 0;
        float attackDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);

        float multiplier = 0.20F + major * 0.035F + minor * 0.004F;
        return BASE_DAMAGE_PER_BLADE + attackDamage * multiplier;
    }

    private void spawnBladeVolley(ServerPlayer player, LivingEntity target, int bladeCount) {
        ServerLevel level = player.serverLevel();
        Vec3 targetPosition = target.getBoundingBox().getCenter();

        for (int blade = 0; blade < bladeCount; blade++) {
            double angle = Math.PI * 2.0D * blade / bladeCount;

            Vec3 start = player.position().add(Math.cos(angle) * 1.35D, 1.05D + Math.sin(angle * 2.0D) * 0.3D, Math.sin(angle) * 1.35D);

            level.sendParticles(
                    ParticleTypes.END_ROD,
                    start.x,
                    start.y,
                    start.z,
                    3,
                    0.025D,
                    0.08D,
                    0.025D,
                    0.0D
            );

            spawnBladeTrail(level, start, targetPosition);
        }

        level.sendParticles(
                ParticleTypes.CRIT,
                targetPosition.x,
                targetPosition.y,
                targetPosition.z,
                18,
                0.28D,
                0.35D,
                0.28D,
                0.08D
        );
    }

    private void spawnBladeTrail(ServerLevel level, Vec3 start, Vec3 end) {
        Vec3 difference = end.subtract(start);
        int steps = Math.max(10, (int) (difference.length() * 2.0D));

        for (int i = 0; i <= steps; i++) {
            double progress = i / (double) steps;
            Vec3 position = start.add(difference.scale(progress));

            level.sendParticles(
                    ParticleTypes.ENCHANTED_HIT,
                    position.x,
                    position.y,
                    position.z,
                    1,
                    0.012D,
                    0.012D,
                    0.012D,
                    0.0D
            );

            if (i % 3 == 0) {
                level.sendParticles(
                        ParticleTypes.END_ROD,
                        position.x,
                        position.y,
                        position.z,
                        1,
                        0.01D,
                        0.01D,
                        0.01D,
                        0.0D
                );
            }
        }
    }

    @Override
    public int getCooldown(CastEndData castEndData) {
        return COOLDOWN_TICKS;
    }

    @Override
    protected String getTitleKey() {
        return "ascension.skill.soulsteel_blades";
    }

    @Override
    protected String getDescriptionKey() {
        return "ascension.skill.soulsteel_blades.description";
    }
}