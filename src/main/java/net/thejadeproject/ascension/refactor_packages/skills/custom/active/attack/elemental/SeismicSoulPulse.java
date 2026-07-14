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
import net.thejadeproject.ascension.refactor_packages.skills.custom.active.SimpleInstantCastSkill;

import java.util.HashSet;
import java.util.List;

public class SeismicSoulPulse extends SimpleInstantCastSkill {

    private static final double QI_COST = 38.0D;

    private static final double BASE_RADIUS = 4.5D;
    private static final double RADIUS_PER_MAJOR = 0.65D;
    private static final double RADIUS_PER_MINOR = 0.06D;
    private static final double MAX_RADIUS = 8.5D;

    private static final float BASE_DAMAGE = 7.0F;
    private static final int COOLDOWN_TICKS = 130;

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

        return getTargets(player).isEmpty() ? new CastResult(CastResult.Type.FAILURE) : new CastResult(CastResult.Type.SUCCESS);
    }

    @Override
    public void initialCast(Entity caster, IPreCastData preCastData) {
        if (!(caster instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;
        if (!player.hasData(ModAttachments.ENTITY_DATA)) return;

        List<LivingEntity> targets = getTargets(player);
        if (targets.isEmpty()) return;

        IEntityData entityData =
                player.getData(ModAttachments.ENTITY_DATA);

        if (!entityData.getQiContainer().tryConsumeQi(QI_COST)) {
            return;
        }

        float damage = calculateDamage(player, entityData);

        HashSet<ResourceLocation> paths = new HashSet<>();
        paths.add(ModPaths.EARTH.getId());

        AscensionDamageHandler.AscensionDamageSource source = new AscensionDamageHandler.AscensionDamageSource(paths, player.damageSources().magic());

        for (LivingEntity target : targets) {
            target.hurt(source, damage);

            Vec3 away = target.position().subtract(player.position());

            Vec3 horizontal = new Vec3(away.x, 0.0D, away.z);

            if (horizontal.lengthSqr() < 0.001D) {
                horizontal = new Vec3(0.0D, 0.0D, 1.0D);
            }

            horizontal = horizontal.normalize();
            target.push(horizontal.x * 0.95D, 0.35D, horizontal.z * 0.95D);
            target.hurtMarked = true;
        }

        spawnPulseParticles(player);
    }

    private List<LivingEntity> getTargets(ServerPlayer player) {
        double radius = calculateRadius(player.getData(ModAttachments.ENTITY_DATA));

        return player.serverLevel().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(radius),
                target -> target != player
                        && target.isAlive()
                        && !target.isSpectator()
                        && target.isPickable()
        );
    }

    private double calculateRadius(IEntityData entityData) {
        IPathData earth = entityData.getPathData(ModPaths.EARTH.getId());

        int major = earth != null ? earth.getMajorRealm() : 0;
        int minor = earth != null ? earth.getMinorRealm() : 0;

        return Math.min(BASE_RADIUS + major * RADIUS_PER_MAJOR + minor * RADIUS_PER_MINOR, MAX_RADIUS);
    }

    private float calculateDamage(
            ServerPlayer player,
            IEntityData entityData
    ) {
        IPathData earth = entityData.getPathData(ModPaths.EARTH.getId());

        int major = earth != null ? earth.getMajorRealm() : 0;
        int minor = earth != null ? earth.getMinorRealm() : 0;

        float attackDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float multiplier = 0.60F + major * 0.10F + minor * 0.012F;
        return BASE_DAMAGE + attackDamage * multiplier;
    }

    private void spawnPulseParticles(ServerPlayer player) {
        ServerLevel level = player.serverLevel();

        double radius = calculateRadius(player.getData(ModAttachments.ENTITY_DATA));

        int rings = 3;
        int points = 36;

        for (int ring = 1; ring <= rings; ring++) {
            double ringRadius = radius * ring / (double) rings;

            for (int i = 0; i < points; i++) {
                double angle = Math.PI * 2.0D * i / points;

                double x = player.getX() + Math.cos(angle) * ringRadius;
                double z = player.getZ() + Math.sin(angle) * ringRadius;

                level.sendParticles(
                        ParticleTypes.ASH,
                        x,
                        player.getY() + 0.15D,
                        z,
                        1,
                        0.02D,
                        0.02D,
                        0.02D,
                        0.0D
                );

                if (i % 3 == 0) {
                    level.sendParticles(
                            ParticleTypes.POOF,
                            x,
                            player.getY() + 0.1D,
                            z,
                            1,
                            0.04D,
                            0.02D,
                            0.04D,
                            0.01D
                    );
                }
            }
        }
    }

    @Override
    public int getCooldown(CastEndData castEndData) {
        return COOLDOWN_TICKS;
    }

    @Override
    protected String getTitleKey() {
        return "ascension.skill.seismic_soul_pulse";
    }

    @Override
    protected String getDescriptionKey() {
        return "ascension.skill.seismic_soul_pulse.description";
    }
}