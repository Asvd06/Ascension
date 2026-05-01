package net.thejadeproject.ascension.refactor_packages.skills.tempskills;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class TemporarySkillHolder {

    private final Map<ResourceLocation, TemporarySkillEntry> temporarySkills = new HashMap<>();

    public void addTemporarySkill(ResourceLocation skillId, ResourceLocation formId, long expiresAtGameTime, boolean removeOnExpire) {
        TemporarySkillEntry existing = temporarySkills.get(skillId);

        if (existing == null) {
            temporarySkills.put(skillId, new TemporarySkillEntry(skillId, formId, expiresAtGameTime, removeOnExpire));
            return;
        }

        existing.setExpiresAtGameTime(Math.max(existing.getExpiresAtGameTime(), expiresAtGameTime));
        existing.setRemoveOnExpire(existing.shouldRemoveOnExpire() || removeOnExpire);
    }

    public boolean isTemporary(ResourceLocation skillId) {
        return temporarySkills.containsKey(skillId);
    }

    public boolean hasTemporarySkill(ResourceLocation skillId, long currentGameTime) {
        TemporarySkillEntry entry = temporarySkills.get(skillId);
        return entry != null && entry.getExpiresAtGameTime() > currentGameTime;
    }

    public void removeTemporarySkill(ResourceLocation skillId) {
        temporarySkills.remove(skillId);
    }

    public List<TemporarySkillEntry> getActiveSkills(long currentGameTime) {
        List<TemporarySkillEntry> active = new ArrayList<>();

        for (TemporarySkillEntry entry : temporarySkills.values()) {
            if (entry.getExpiresAtGameTime() > currentGameTime) {
                active.add(entry);
            }
        }

        return active;
    }

    public List<TemporarySkillEntry> getExpiredSkills(long currentGameTime) {
        List<TemporarySkillEntry> expired = new ArrayList<>();

        for (TemporarySkillEntry entry : temporarySkills.values()) {
            if (entry.getExpiresAtGameTime() <= currentGameTime) {
                expired.add(entry);
            }
        }

        return expired;
    }

    public void clear() {
        temporarySkills.clear();
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();

        for (TemporarySkillEntry entry : temporarySkills.values()) {
            CompoundTag skillTag = new CompoundTag();
            skillTag.putString("Skill", entry.getSkillId().toString());
            skillTag.putString("Form", entry.getFormId().toString());
            skillTag.putLong("ExpiresAt", entry.getExpiresAtGameTime());
            skillTag.putBoolean("RemoveOnExpire", entry.shouldRemoveOnExpire());
            list.add(skillTag);
        }

        tag.put("TemporarySkills", list);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        temporarySkills.clear();

        ListTag list = tag.getList("TemporarySkills", Tag.TAG_COMPOUND);

        for (Tag rawTag : list) {
            if (!(rawTag instanceof CompoundTag skillTag)) continue;

            ResourceLocation skillId = ResourceLocation.tryParse(skillTag.getString("Skill"));
            ResourceLocation formId = ResourceLocation.tryParse(skillTag.getString("Form"));

            if (skillId == null || formId == null) continue;

            long expiresAt = skillTag.getLong("ExpiresAt");
            boolean removeOnExpire = !skillTag.contains("RemoveOnExpire") || skillTag.getBoolean("RemoveOnExpire");

            temporarySkills.put(skillId, new TemporarySkillEntry(skillId, formId, expiresAt, removeOnExpire));
        }
    }

    public static class TemporarySkillEntry {
        private final ResourceLocation skillId;
        private final ResourceLocation formId;
        private long expiresAtGameTime;
        private boolean removeOnExpire;

        public TemporarySkillEntry(ResourceLocation skillId, ResourceLocation formId, long expiresAtGameTime, boolean removeOnExpire) {
            this.skillId = skillId;
            this.formId = formId;
            this.expiresAtGameTime = expiresAtGameTime;
            this.removeOnExpire = removeOnExpire;
        }

        public ResourceLocation getSkillId() {
            return skillId;
        }

        public ResourceLocation getFormId() {
            return formId;
        }

        public long getExpiresAtGameTime() {
            return expiresAtGameTime;
        }

        public void setExpiresAtGameTime(long expiresAtGameTime) {
            this.expiresAtGameTime = expiresAtGameTime;
        }

        public boolean shouldRemoveOnExpire() {
            return removeOnExpire;
        }

        public void setRemoveOnExpire(boolean removeOnExpire) {
            this.removeOnExpire = removeOnExpire;
        }
    }
}