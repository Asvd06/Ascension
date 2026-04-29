package net.thejadeproject.ascension.refactor_packages.alchemy;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.common.effects.ModEffects;
import net.thejadeproject.ascension.refactor_packages.alchemy.effects.AntidotePillEffect;
import net.thejadeproject.ascension.refactor_packages.alchemy.effects.CultivationPillEffect;
import net.thejadeproject.ascension.refactor_packages.alchemy.effects.MarrowCleansePillEffect;
import net.thejadeproject.ascension.refactor_packages.alchemy.effects.MobEffectPillEffect;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;

public class ModPillEffects {
    public static final DeferredRegister<IPillEffect> PILL_EFFECTS =DeferredRegister.create(AscensionRegistries.PillEffects.PILL_EFFECT_REGISTRY, AscensionCraft.MOD_ID);

    //TODO see if i can loop through path registry and create one for each?
    public static final DeferredHolder<IPillEffect, ? extends CultivationPillEffect> BODY_EFFECT = PILL_EFFECTS.register("body_cultivation_pill_effect",()->
            new CultivationPillEffect(100,ModPaths.BODY.getId(),
                    Component.translatable("ascension.pill_effects.body_cultivation_pill_effect.name")
                    ,Component.translatable("ascension.pill_effects.body_cultivation_pill_effect.description"))
    );
    public static final DeferredHolder<IPillEffect, ? extends CultivationPillEffect> ESSENCE_EFFECT = PILL_EFFECTS.register("essence_cultivation_pill_effect",()->
            new CultivationPillEffect(100,ModPaths.ESSENCE.getId(),
                    Component.translatable("ascension.pill_effects.essence_cultivation_pill_effect.name")
                    ,Component.translatable("ascension.pill_effects.essence_cultivation_pill_effect.description"))
    );
    public static final DeferredHolder<IPillEffect, ? extends CultivationPillEffect> SOUL_EFFECT = PILL_EFFECTS.register("soul_cultivation_pill_effect",()->
            new CultivationPillEffect(100,ModPaths.SOUL.getId(),
                    Component.translatable("ascension.pill_effects.soul_cultivation_pill_effect.name")
                    ,Component.translatable("ascension.pill_effects.soul_cultivation_pill_effect.description"))
    );

    public static final DeferredHolder<IPillEffect, ? extends AntidotePillEffect> ANTIDOTE_PILL_EFFECT = PILL_EFFECTS.register("antidote_pill_effect",()->
            new AntidotePillEffect(
                    Component.translatable("ascension.pill_effects.antidote_pill_effect.name")
                    ,Component.translatable("ascension.pill_effects.antidote_pill_effect.description"))
                    .addEffect(new MobEffectInstance(ModEffects.PARASITE,1,0))
    );

    public static final DeferredHolder<IPillEffect, ? extends MobEffectPillEffect> PARASITE_PILL_EFFECT = PILL_EFFECTS.register("parasite_pill_effect",()->
            new MobEffectPillEffect(
                    Component.translatable("ascension.pill_effects.parasite_pill_effect.name")
                    ,Component.translatable("ascension.pill_effects.parasite_pill_effect.description"))
                    .addEffect(new MobEffectInstance(ModEffects.PARASITE,400,1))
    );
    public static final DeferredHolder<IPillEffect, ? extends MobEffectPillEffect> QI_ENHANCED_REGEN_EFFECT = PILL_EFFECTS.register("qi_enhanced_regen_effect",()->
            new MobEffectPillEffect(
                    Component.translatable("ascension.pill_effects.qi_enhanced_regen_effect.name")
                    ,Component.translatable("ascension.pill_effects.qi_enhanced_regen_effect.description"))
                    .addEffect(new MobEffectInstance(ModEffects.QI_ENHANCED_REGEN,400,1))
    );

    public static final DeferredHolder<IPillEffect, ? extends MarrowCleansePillEffect> MARROW_CLEANSE_PILL_EFFECT =
            PILL_EFFECTS.register("marrow_cleanse_pill_effect", () ->
                    new MarrowCleansePillEffect(0.01D, 0.20D,
                            Component.literal("Marrow Cleanse"),
                            Component.literal("Has a small chance to cleanse the marrow of a crippled physique.")
                    )
            );

    public static void register(IEventBus modEventBus){
        PILL_EFFECTS.register(modEventBus);
    }
}
