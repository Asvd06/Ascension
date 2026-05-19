package net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.soul;

import net.lucent.easygui.gui.textures.ITextureData;
import net.lucent.easygui.gui.textures.TextureData;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastResult;
import net.thejadeproject.ascension.refactor_packages.skills.castable.ICastData;
import net.thejadeproject.ascension.refactor_packages.skills.castable.IPreCastData;
import net.thejadeproject.ascension.refactor_packages.techniques.custom.soul.PaleMoonTechnique;

public class PaleMoonCultivationSkill extends SimpleSoulCultivationSkill {

    private static final double MOON_MULTIPLIER = 1.5D;

    public PaleMoonCultivationSkill() {
        super(PaleMoonTechnique.BASE_RATE);
    }

    @Override
    public CastResult canCast(Entity caster, IPreCastData preCastData) {
        if (!caster.level().canSeeSky(caster.blockPosition())) {
            return new CastResult(
                    CastResult.Type.FAILURE,
                    Component.translatable("ascension.skill.pale_moon_cultivation_skill.blocked_indoors")
            );
        }

        return super.canCast(caster, preCastData);
    }

    @Override
    protected double getEffectiveRate(Entity caster) {
        double rate = super.getEffectiveRate(caster);

        if (isLookingAtMoon(caster)) {
            rate *= MOON_MULTIPLIER;
        }

        return rate;
    }

    @Override
    public boolean continueCasting(int ticksElapsed, Entity caster, ICastData castData) {
        boolean continuing = super.continueCasting(ticksElapsed, caster, castData);

        if (continuing && !caster.level().isClientSide()) {
            damageIfSunExposed(caster);

            if (ticksElapsed % 20 == 0 && isLookingAtMoon(caster) && caster instanceof ServerPlayer player) {
                player.sendSystemMessage(Component.literal("[Debug] Pale Moon bonus active (1.5x)"));
            }
        }

        return continuing;
    }

    protected static void damageIfSunExposed(Entity caster) {
        if (!isSunExposed(caster)) return;
        if (!(caster instanceof LivingEntity living)) return;

        DamageSource source = new DamageSource(
                caster.level().registryAccess()
                        .registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(DamageTypes.IN_FIRE)
        );

        living.hurt(source, living.getMaxHealth() * 0.01F);
    }

    public static boolean isLookingAtMoon(Entity entity) {
        Level level = entity.level();

        if (!level.isNight()) return false;
        if (!level.canSeeSky(entity.blockPosition())) return false;

        float sunAngle = level.getSunAngle(1.0F);
        float moonAngle = sunAngle + (float) Math.PI;

        Vec3 moonDir = new Vec3(0, Math.sin(moonAngle), -Math.cos(moonAngle)).normalize();
        Vec3 lookDir = entity.getLookAngle();

        return moonDir.dot(lookDir) > 0.98D;
    }

    public static boolean isSunExposed(Entity entity) {
        Level level = entity.level();
        return !level.isNight() && level.canSeeSky(entity.blockPosition());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ITextureData getIcon(IEntityData entityData) {
        return new TextureData(
                ResourceLocation.fromNamespaceAndPath(
                        AscensionCraft.MOD_ID,
                        "textures/spells/icon/placeholder.png"
                ),
                16, 16
        );
    }

    @Override
    public Component getTitle(IEntityData entityData) {
        return Component.translatable("ascension.skill.pale_moon_cultivation_skill");
    }

    @Override
    public Component getDescription(IEntityData entityData) {
        return Component.translatable("ascension.skill.pale_moon_cultivation_skill.description");
    }
}