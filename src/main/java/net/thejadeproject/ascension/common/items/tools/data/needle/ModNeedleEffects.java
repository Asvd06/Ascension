package net.thejadeproject.ascension.common.items.tools.data.needle;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.common.effects.ModEffects;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModNeedleEffects {

    private static final Map<ResourceLocation, INeedleEffect> REGISTRY = new HashMap<>();


    public static final INeedleEffect POISON = register(new INeedleEffect() {
        private final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "needle_poison");

        @Override
        public ResourceLocation getId() {return id;}

        @Override
        public void onHit(LivingEntity target, LivingEntity shooter, Projectile projectile) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
        }
    });
    public static final INeedleEffect BLINDNESS = register(new INeedleEffect() {
        private final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "needle_blindness");

        @Override
        public ResourceLocation getId() {return id;}

        @Override
        public void onHit(LivingEntity target, LivingEntity shooter, Projectile projectile) {
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
        }
    });
    public static final INeedleEffect SLOWNESS = register(new INeedleEffect() {
        private final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "needle_slowness");

        @Override
        public ResourceLocation getId() {return id;}

        @Override
        public void onHit(LivingEntity target, LivingEntity shooter, Projectile projectile) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 0));
        }
    });
    public static final INeedleEffect WEAKNESS = register(new INeedleEffect() {
        private final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "needle_weakness");

        @Override
        public ResourceLocation getId() {return id;}

        @Override
        public void onHit(LivingEntity target, LivingEntity shooter, Projectile projectile) {
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
        }
    });
    public static final INeedleEffect QI_DEVOURING = register(new INeedleEffect() {
        private final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "needle_qi_devouring");

        @Override
        public ResourceLocation getId() {return id;}

        @Override
        public void onHit(LivingEntity target, LivingEntity shooter, Projectile projectile) {
            target.addEffect(new MobEffectInstance(ModEffects.PARASITE, 100, 0));
        }
    });








    private static INeedleEffect register(INeedleEffect effect) {
        REGISTRY.put(effect.getId(), effect);
        return effect;
    }

    public static Optional<INeedleEffect> get(ResourceLocation id) {
        return Optional.ofNullable(REGISTRY.get(id));
    }

    public static Optional<INeedleEffect> get(String id) {
        return get(ResourceLocation.parse(id));
    }
}
