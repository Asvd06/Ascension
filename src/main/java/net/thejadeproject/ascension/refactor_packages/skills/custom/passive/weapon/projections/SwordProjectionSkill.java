package net.thejadeproject.ascension.refactor_packages.skills.custom.passive.weapon.projections;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastEndData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastResult;
import net.thejadeproject.ascension.refactor_packages.skills.castable.ICastData;
import net.thejadeproject.ascension.refactor_packages.skills.castable.ICastableSkill;
import net.thejadeproject.ascension.refactor_packages.skills.castable.IPreCastData;
import net.thejadeproject.ascension.refactor_packages.skills.custom.ModSkills;
import net.thejadeproject.ascension.refactor_packages.skills.custom.active.SimpleInstantCastSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.weapon.projections.data.ProjectionTogglePreCastData;
import net.thejadeproject.ascension.refactor_packages.skills.vfx.weaponvfx.WeaponSwingVfxEntity;
import net.thejadeproject.ascension.refactor_packages.skills.vfx.weaponvfx.WeaponVfxUtils;
import org.joml.Vector3f;

public class SwordProjectionSkill extends SimpleInstantCastSkill {
    @Override
    protected String getTitleKey() {
        return "";
    }

    @Override
    protected String getDescriptionKey() {
        return "";
    }

    @Override
    public void initialCast(Entity caster, IPreCastData preCastData) {
        //TODO update with configurable variables
        if(((LivingEntity) caster).getWeaponItem().isEmpty() || !((LivingEntity) caster).getWeaponItem().is(ItemTags.SWORDS)) return;
        if(caster.level().isClientSide) return;

        WeaponVfxUtils.spawnSwingVfxAhead(
                caster.level(),
                (LivingEntity) caster,
                0,
                new Vector3f(2.0f, 2.0f, 2.0f),
                10,
                1.0,
                60,
                WeaponSwingVfxEntity.TYPE_SWORD,
                ModSkills.SWORD_MASTERY_SKILL.getId(),       // ← resolved by VfxColorRegistry
                "blue",
                new Vec3(0,0,1)
        );
    }

    @Override
    public CastResult canCast(Entity caster, IPreCastData preCastData) {
        IEntityData entityData = caster.getData(ModAttachments.ENTITY_DATA);

        return entityData.getQiContainer().tryConsumeQi(80) ? CastResult.success() : CastResult.fail();
    }

    @Override
    public int getCooldown(CastEndData castEndData) {
        return 200;
    }



}
