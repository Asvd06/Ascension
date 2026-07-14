package net.thejadeproject.ascension.refactor_packages.alchemy;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.common.effects.ModEffects;
import net.thejadeproject.ascension.refactor_packages.alchemy.effects.*;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skills.custom.ModSkills;

public class ModPillEffects {
    public static final DeferredRegister<IPillEffect> PILL_EFFECTS =DeferredRegister.create(AscensionRegistries.PillEffects.PILL_EFFECT_REGISTRY, AscensionCraft.MOD_ID);


    //TODO see if i can loop through path registry and create one for each?
    public static final DeferredHolder<IPillEffect, ? extends CultivationPillEffect> BODY_EFFECT = PILL_EFFECTS.register("body_cultivation_pill_effect",()->
            new CultivationPillEffect(500,ModPaths.BODY.getId(),
                    Component.translatable("ascension.pill_effects.body_cultivation_pill_effect.name")
                    ,Component.translatable("ascension.pill_effects.body_cultivation_pill_effect.description"))
    );
    public static final DeferredHolder<IPillEffect, ? extends CultivationPillEffect> ESSENCE_EFFECT = PILL_EFFECTS.register("essence_cultivation_pill_effect",()->
            new CultivationPillEffect(500,ModPaths.ESSENCE.getId(),
                    Component.translatable("ascension.pill_effects.essence_cultivation_pill_effect.name")
                    ,Component.translatable("ascension.pill_effects.essence_cultivation_pill_effect.description"))
    );
    public static final DeferredHolder<IPillEffect, ? extends CultivationPillEffect> SOUL_EFFECT = PILL_EFFECTS.register("soul_cultivation_pill_effect",()->
            new CultivationPillEffect(500,ModPaths.SOUL.getId(),
                    Component.translatable("ascension.pill_effects.soul_cultivation_pill_effect.name")
                    ,Component.translatable("ascension.pill_effects.soul_cultivation_pill_effect.description"))
    );

    public static final DeferredHolder<IPillEffect, ? extends QiRestorePillEffect> QI_REPLENISHING_EFFECT = PILL_EFFECTS.register("qi_replenishing_effect", ()->
            new QiRestorePillEffect(80,
                    Component.translatable("ascension.pill_effects.qi_replenishing_effect.name"),
                    Component.translatable("ascension.pill_effects.qi_replenishing_effect.description"))
    );

    public static final DeferredHolder<IPillEffect, ? extends AntidotePillEffect> ANTIDOTE_PILL_EFFECT = PILL_EFFECTS.register("antidote_pill_effect",()->
            new AntidotePillEffect(
                    Component.translatable("ascension.pill_effects.antidote_pill_effect.name")
                    ,Component.translatable("ascension.pill_effects.antidote_pill_effect.description"))
                    .addEffect(new MobEffectInstance(ModEffects.PARASITE,1,0))
    );




    // ── Poison Pill Effects ──────────────────────────────────────────────

    public static final DeferredHolder<IPillEffect, ? extends DebuffSkillPillEffect> CRACKED_MERIDIANS_PILL_EFFECT =
            PILL_EFFECTS.register("cracked_meridians_pill_effect", () ->
                    new DebuffSkillPillEffect(
                            "pill_cracked_meridians",
                            ModSkills.CRACKED_MERIDIANS.getId(),
                            20 * 20,
                            Component.translatable("ascension.pill_effects.cracked_meridian_pill_effect.name"),
                            Component.translatable("ascension.pill_effects.cracked_meridian_pill_effect.description")
                    )
            );
    public static final DeferredHolder<IPillEffect, ? extends DebuffSkillPillEffect> BLINDED_SENSES_PILL_EFFECT =
            PILL_EFFECTS.register("blinded_senses_pill_effect", () ->
                    new DebuffSkillPillEffect(
                            "pill_blinded_senses",
                            ModSkills.BLINDED_SENSES.getId(),
                            20 * 20,
                            Component.translatable("ascension.pill_effects.blinded_senses_pill_effect.name"),
                            Component.translatable("ascension.pill_effects.blinded_senses_pill_effect.description")
                    )
            );

    public static final DeferredHolder<IPillEffect, ? extends DebuffSkillPillEffect> PARALYZED_BODY_PILL_EFFECT =
            PILL_EFFECTS.register("paralyzed_body_pill_effect", () ->
                    new DebuffSkillPillEffect(
                            "pill_paralyzed_body",
                            ModSkills.PARALYZED_BODY.getId(),
                            20 * 20,
                            Component.translatable("ascension.pill_effects.paralyzed_body_pill_effect.name"),
                            Component.translatable("ascension.pill_effects.paralyzed_body_pill_effect.description")
                    )
            );

    public static final DeferredHolder<IPillEffect, ? extends DebuffSkillPillEffect> VENOMOUS_MERIDIANS_PILL_EFFECT =
            PILL_EFFECTS.register("venomous_meridians_pill_effect", () ->
                    new DebuffSkillPillEffect(
                            "pill_venomous_meridians",
                            ModSkills.VENOMOUS_MERIDIANS.getId(),
                            20 * 20,
                            Component.translatable("ascension.pill_effects.venomous_meridians_pill_effect.name"),
                            Component.translatable("ascension.pill_effects.venomous_meridians_pill_effect.description")
                    )
            );

    public static final DeferredHolder<IPillEffect, ? extends DebuffSkillPillEffect> SCORCHING_YANG_POISON_PILL_EFFECT =
            PILL_EFFECTS.register("scorching_yang_poison_pill_effect", () ->
                    new DebuffSkillPillEffect(
                            "pill_scorching_yang_poison",
                            ModSkills.SCORCHING_YANG_POISON.getId(),
                            20 * 20,
                            Component.translatable("ascension.pill_effects.scorching_yang_poison_pill_effect.name"),
                            Component.translatable("ascension.pill_effects.scorching_yang_poison_pill_effect.description")
                    )
            );

    public static final DeferredHolder<IPillEffect, ? extends DebuffSkillPillEffect> PARASITE_PILL_EFFECT =
            PILL_EFFECTS.register("parasite_pill_effect", () ->
                    new DebuffSkillPillEffect(
                            "pill_parasite",
                            ModSkills.QI_DEVOURING_POISON.getId(),
                            20 * 20,
                            Component.translatable("ascension.pill_effects.parasite_pill_effect.name"),
                            Component.translatable("ascension.pill_effects.parasite_pill_effect.description")
                    )
            );

    public static final DeferredHolder<IPillEffect, ? extends DebuffSkillPillEffect> CORROSIVE_POISON_PILL_EFFECT =
            PILL_EFFECTS.register("corrosive_poison_pill_effect", () ->
                    new DebuffSkillPillEffect(
                            "pill_corrosive_poison",
                            ModSkills.CORROSIVE_POISON_DEBUFF.getId(),
                            20 * 20,
                            Component.translatable("ascension.pill_effects.corrosive_poison_pill_effect.name"),
                            Component.translatable("ascension.pill_effects.corrosive_poison_pill_effect.description")
                    )
            );

    public static final DeferredHolder<IPillEffect, ? extends DebuffSkillPillEffect> FROST_SILKWORM_POISON_PILL_EFFECT =
            PILL_EFFECTS.register("frost_silkworm_poison_temp_pill_effect", () ->
                    new DebuffSkillPillEffect(
                            "pill_frost_silkworm_poison",
                            ModSkills.FROST_SILKWORM_POISON_TEMP.getId(),
                            20 * 20,
                            Component.translatable("ascension.pill_effects.frost_silkworm_poison_pill_effect.name"),
                            Component.translatable("ascension.pill_effects.frost_silkworm_poison_pill_effect.description")
                    )
            );












    public static final DeferredHolder<IPillEffect, ? extends MobEffectPillEffect> QI_ENHANCED_REGEN_EFFECT =
            PILL_EFFECTS.register("qi_enhanced_regen_effect", () ->
                    new MobEffectPillEffect(
                            Component.translatable("ascension.pill_effects.qi_enhanced_regen_effect.name"),
                            Component.translatable("ascension.pill_effects.qi_enhanced_regen_effect.description")
                    ).addEffect(new MobEffectInstance(ModEffects.QI_ENHANCED_REGEN, 400, 1)).scaleAmplifierEvery(2)
            );

    public static final DeferredHolder<IPillEffect, ? extends MarrowCleansePillEffect> MARROW_CLEANSE_PILL_EFFECT =
            PILL_EFFECTS.register("marrow_cleanse_pill_effect", () ->
                    new MarrowCleansePillEffect(0.01D, 0.20D,
                            Component.literal("Marrow Cleanse"),
                            Component.literal("Has a small chance to cleanse the marrow of a crippled physique.")
                    )
            );

    public static final DeferredHolder<IPillEffect, ? extends BasicPillEffect> CRIMSON_LOTUS_BONE_EFFECT =
            PILL_EFFECTS.register("crimson_lotus_bone_effect", () ->
                    new BasicPillEffect(
                            Component.translatable("ascension.pill_effects.crimson_lotus_bone_effect.name"),
                            Component.translatable("ascension.pill_effects.crimson_lotus_bone_effect.description")
                    ) {
                        @Override
                        public boolean tryConsume(LivingEntity livingEntity, ItemStack itemStack, double purityScale, double realmMultiplier) {
                            return livingEntity instanceof ServerPlayer;
                        }

                        @Override
                        public boolean shouldGoOnCooldown() {
                            return true;
                        }
                    }
            );

    public static void register(IEventBus modEventBus){
        PILL_EFFECTS.register(modEventBus);
    }
}
