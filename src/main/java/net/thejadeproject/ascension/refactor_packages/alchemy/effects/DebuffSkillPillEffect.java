package net.thejadeproject.ascension.refactor_packages.alchemy.effects;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.alchemy.BasicPillEffect;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.debuff.skill_data.DebuffSkillHelper;
import net.thejadeproject.ascension.refactor_packages.util.PillEffectUtil;

public class DebuffSkillPillEffect extends BasicPillEffect {
    private static final int MAX_DURATION_TICKS = 20 * 60 * 10;

    private final ResourceLocation sourceId;
    private final ResourceLocation skillId;
    private final int durationTicks;

    public DebuffSkillPillEffect(
            String sourcePath,
            ResourceLocation skillId,
            int durationTicks,
            Component name,
            Component description
    ) {
        super(name, description);
        this.sourceId = ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, sourcePath);
        this.skillId = skillId;
        this.durationTicks = durationTicks;
    }

    @Override
    public boolean tryConsume(LivingEntity livingEntity, ItemStack itemStack, double purityScale, double realmMultiplier) {
        if (!(livingEntity instanceof ServerPlayer player)) return false;

        int scaledDuration = Mth.clamp(
                (int) Math.round(durationTicks * PillEffectUtil.getDurationScale(itemStack)),
                1,
                MAX_DURATION_TICKS
        );

        DebuffSkillHelper.giveTempDebuffSource(
                player,
                sourceId,
                skillId,
                scaledDuration,
                ModForms.MORTAL_VESSEL.getId()
        );

        return true;
    }
}
