package net.thejadeproject.ascension.refactor_packages.skills.tempskills;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;

public final class TemporarySkillHelper {

    private TemporarySkillHelper() {}

    public static void giveTemporarySkill(ServerPlayer player, ResourceLocation skillId, int durationTicks) {
        giveTemporarySkill(player, skillId, durationTicks, ModForms.MORTAL_VESSEL.getId());
    }

    public static void giveTemporarySkill(ServerPlayer player, ResourceLocation skillId, int durationTicks, ResourceLocation formId) {
        if (!player.hasData(ModAttachments.ENTITY_DATA)) return;

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        long expiresAt = player.serverLevel().getGameTime() + durationTicks;

        boolean alreadyHadSkill = entityData.hasSkill(skillId);

        entityData.getTemporarySkills().addTemporarySkill(
                skillId,
                formId,
                expiresAt,
                !alreadyHadSkill
        );

        if (!alreadyHadSkill) {
            entityData.giveSkill(skillId, formId);
        }
    }

    public static void tickTemporarySkills(ServerPlayer player, IEntityData entityData) {
        long gameTime = player.serverLevel().getGameTime();

        // Re-add active temporary skills after reload/login if they were not saved as normal held skills.
        for (TemporarySkillHolder.TemporarySkillEntry activeSkill : entityData.getTemporarySkills().getActiveSkills(gameTime)) {
            if (!entityData.hasSkill(activeSkill.getSkillId()) && activeSkill.shouldRemoveOnExpire()) {
                entityData.giveSkill(activeSkill.getSkillId(), activeSkill.getFormId());
            }
        }

        for (TemporarySkillHolder.TemporarySkillEntry expiredSkill : entityData.getTemporarySkills().getExpiredSkills(gameTime)) {
            entityData.getTemporarySkills().removeTemporarySkill(expiredSkill.getSkillId());

            if (expiredSkill.shouldRemoveOnExpire()) {
                entityData.removeSkill(expiredSkill.getSkillId(), expiredSkill.getFormId());
            }
        }
    }

    public static boolean hasTemporarySkill(ServerPlayer player, ResourceLocation skillId) {
        if (!player.hasData(ModAttachments.ENTITY_DATA)) return false;

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        long gameTime = player.serverLevel().getGameTime();

        return entityData.getTemporarySkills().hasTemporarySkill(skillId, gameTime);
    }

    public static void removeTemporarySkill(ServerPlayer player, ResourceLocation skillId) {
        if (!player.hasData(ModAttachments.ENTITY_DATA)) return;

        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);

        for (TemporarySkillHolder.TemporarySkillEntry entry : entityData.getTemporarySkills().getActiveSkills(player.serverLevel().getGameTime())) {
            if (!entry.getSkillId().equals(skillId)) continue;

            entityData.getTemporarySkills().removeTemporarySkill(skillId);

            if (entry.shouldRemoveOnExpire()) {
                entityData.removeSkill(skillId, entry.getFormId());
            }

            return;
        }
    }
}