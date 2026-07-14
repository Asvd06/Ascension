package net.thejadeproject.ascension.refactor_packages.alchemy.effects;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.thejadeproject.ascension.refactor_packages.alchemy.BasicPillEffect;
import net.thejadeproject.ascension.refactor_packages.util.PillEffectUtil;

import java.util.ArrayList;

public class MobEffectPillEffect extends BasicPillEffect {

    private static final int MAX_DURATION_TICKS = 20 * 60 * 30;

    private final ArrayList<MobEffectInstance> effects = new ArrayList<>();

    private int realmsPerAmplifier = 0;

    public MobEffectPillEffect(Component name, Component description) {
        super(name, description);
    }

    public MobEffectPillEffect addEffect(MobEffectInstance instance) {
        effects.add(instance);
        return this;
    }

    public MobEffectPillEffect scaleAmplifierEvery(int realms) {
        this.realmsPerAmplifier = Math.max(1, realms);
        return this;
    }

    @Override
    public boolean tryConsume(LivingEntity entity, ItemStack itemStack, double purityScale, double realmMultiplier) {
        int pillRealm = PillEffectUtil.getMajorRealm(itemStack);
        double durationScale = PillEffectUtil.getDurationScale(itemStack);

        int amplifierBonus = realmsPerAmplifier > 0 ? Math.max(0, pillRealm - 1) / realmsPerAmplifier : 0;

        for (MobEffectInstance effect : effects) {
            int scaledDuration = Mth.clamp((int) Math.round(effect.getDuration() * durationScale), 1, MAX_DURATION_TICKS);
            int scaledAmplifier = effect.getAmplifier() + amplifierBonus;

            entity.addEffect(new MobEffectInstance(effect.getEffect(), scaledDuration, scaledAmplifier));
        }

        return true;
    }
}