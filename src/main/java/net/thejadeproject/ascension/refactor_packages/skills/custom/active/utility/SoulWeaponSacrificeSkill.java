package net.thejadeproject.ascension.refactor_packages.skills.custom.active.utility;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.textures.ITextureData;
import net.lucent.easygui.gui.textures.TextureData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.common.items.tools.soul_weapon.ISoulboundItem;
import net.thejadeproject.ascension.common.items.tools.soul_weapon.SoulImmolationHelper;
import net.thejadeproject.ascension.common.items.tools.soul_weapon.SoulWeaponHelper;
import net.thejadeproject.ascension.common.items.tools.soul_weapon.SoulWeaponType;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.data_attachments.attachments.SoulImmolationData;
import net.thejadeproject.ascension.data_attachments.attachments.SoulWeaponData;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.gui.elements.info_elements.DescriptionDisplayContainer;
import net.thejadeproject.ascension.refactor_packages.physiques.IPhysiqueData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastEndData;
import net.thejadeproject.ascension.refactor_packages.skill_casting.casting.CastResult;
import net.thejadeproject.ascension.refactor_packages.skills.IPersistentSkillData;
import net.thejadeproject.ascension.refactor_packages.skills.castable.CastType;
import net.thejadeproject.ascension.refactor_packages.skills.castable.ICastData;
import net.thejadeproject.ascension.refactor_packages.skills.castable.ICastableSkill;
import net.thejadeproject.ascension.refactor_packages.skills.castable.IPreCastData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SoulWeaponSacrificeSkill implements ICastableSkill {

    private static final int CHANNEL_TICKS = 100;
    private static final int COOLDOWN_TICKS = 100;

    private static final double MAX_MOVEMENT_DISTANCE_SQR =
            0.10D * 0.10D;

    private static final double HEALTH_EPSILON = 0.001D;
    private static final float MIN_ATTACK_STRENGTH = 0.90F;

    private static final Map<UUID, SacrificeState> ACTIVE_RITUALS =
            new HashMap<>();

    @Override
    public CastResult canCast(
            Entity caster,
            IPreCastData preCastData
    ) {
        if (caster.level().isClientSide()) {
            return new CastResult(CastResult.Type.SUCCESS);
        }

        if (!(caster instanceof ServerPlayer player)) {
            return failure();
        }

        if (!player.hasData(ModAttachments.ENTITY_DATA)) {
            return failure();
        }

        SoulWeaponData weaponData =
                player.getData(ModAttachments.SOUL_WEAPON);

        if (!weaponData.bound || !weaponData.summoned) {
            player.displayClientMessage(
                    Component.translatable(
                            "ascension.skill.soul_weapon_immolation.no_weapon"
                    ),
                    true
            );

            return failure();
        }

        if (!player.isShiftKeyDown()
                || !player.getOffhandItem().isEmpty()
                || !isHeldSoulWeaponValid(player, weaponData)) {

            player.displayClientMessage(
                    Component.translatable(
                            "ascension.skill.soul_weapon_immolation.position"
                    ),
                    true
            );

            return failure();
        }

        int score =
                SoulImmolationHelper.calculateImmolationScore(weaponData);

        if (score
                < SoulImmolationHelper.MINIMUM_SACRIFICE_SCORE) {

            player.displayClientMessage(
                    Component.translatable(
                            "ascension.skill.soul_weapon_immolation.too_weak",
                            score,
                            SoulImmolationHelper.MINIMUM_SACRIFICE_SCORE
                    ),
                    true
            );

            return failure();
        }

        SoulImmolationData immolationData =
                player.getData(ModAttachments.SOUL_IMMOLATION);

        if (score
                <= immolationData.getHighestSacrificedScore()) {

            player.displayClientMessage(
                    Component.translatable(
                            "ascension.skill.soul_weapon_immolation.not_improvement",
                            score,
                            immolationData.getHighestSacrificedScore()
                    ),
                    true
            );

            return failure();
        }

        if (player.getAttackStrengthScale(0.0F)
                < MIN_ATTACK_STRENGTH) {

            player.displayClientMessage(
                    Component.translatable(
                            "ascension.skill.soul_weapon_immolation.wait"
                    ),
                    true
            );

            return failure();
        }

        return new CastResult(CastResult.Type.SUCCESS);
    }

    @Override
    public void initialCast(
            Entity caster,
            IPreCastData preCastData
    ) {
        if (!(caster instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;

        SoulWeaponData weaponData =
                player.getData(ModAttachments.SOUL_WEAPON);

        SoulWeaponType type =
                SoulWeaponType.fromId(weaponData.weaponType);

        if (type == null) return;

        IEntityData entityData =
                player.getData(ModAttachments.ENTITY_DATA);

        int score =
                SoulImmolationHelper.calculateImmolationScore(weaponData);

        int boostPercent =
                SoulImmolationHelper.getDisplayedBoostPercent(score);

        ACTIVE_RITUALS.put(
                player.getUUID(),
                new SacrificeState(
                        player.position(),
                        player.getHealth(),
                        entityData.getHealth(),
                        type
                )
        );

        player.displayClientMessage(
                Component.translatable(
                        "ascension.skill.soul_weapon_immolation.started",
                        score,
                        boostPercent
                ),
                true
        );

        player.serverLevel().sendParticles(
                ParticleTypes.SOUL,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                16,
                0.30D,
                0.55D,
                0.30D,
                0.02D
        );

        player.level().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.BEACON_POWER_SELECT,
                SoundSource.PLAYERS,
                0.65F,
                0.65F
        );
    }

    @Override
    public boolean continueCasting(
            int ticksElapsed,
            Entity caster,
            ICastData castData
    ) {
        if (caster.level().isClientSide()) {
            if (!caster.hasData(ModAttachments.INPUT_STATES)) {
                return false;
            }

            return ticksElapsed < CHANNEL_TICKS
                    && caster.getData(ModAttachments.INPUT_STATES)
                    .isHeld("skill_cast");
        }

        if (!(caster instanceof ServerPlayer player)) {
            return false;
        }

        SacrificeState state =
                ACTIVE_RITUALS.get(player.getUUID());

        if (state == null) {
            return false;
        }

        if (!player.hasData(ModAttachments.INPUT_STATES)
                || !player.getData(ModAttachments.INPUT_STATES)
                .isHeld("skill_cast")) {

            cancelRitual(player);
            return false;
        }

        if (!isRitualStillValid(player, state)) {
            cancelRitual(player);
            return false;
        }

        if (ticksElapsed >= CHANNEL_TICKS) {
            completeSacrifice(player, state);
            return false;
        }

        if (ticksElapsed % 10 == 0) {
            player.serverLevel().sendParticles(
                    ParticleTypes.SOUL,
                    player.getX(),
                    player.getY() + 1.0D,
                    player.getZ(),
                    5,
                    0.22D,
                    0.45D,
                    0.22D,
                    0.01D
            );
        }

        return true;
    }

    private boolean isRitualStillValid(
            ServerPlayer player,
            SacrificeState state
    ) {
        if (!player.isAlive() || player.isRemoved()) {
            return false;
        }

        if (!player.isShiftKeyDown()) {
            return false;
        }

        if (!player.getOffhandItem().isEmpty()) {
            return false;
        }

        if (player.position().distanceToSqr(state.startPosition)
                > MAX_MOVEMENT_DISTANCE_SQR) {

            return false;
        }

        if (player.getHealth()
                < state.startVanillaHealth - HEALTH_EPSILON) {

            return false;
        }

        IEntityData entityData =
                player.getData(ModAttachments.ENTITY_DATA);

        if (entityData.getHealth()
                < state.startAscensionHealth - HEALTH_EPSILON) {

            return false;
        }

        if (player.getAttackStrengthScale(0.0F)
                < MIN_ATTACK_STRENGTH) {

            return false;
        }

        SoulWeaponData weaponData =
                player.getData(ModAttachments.SOUL_WEAPON);

        if (!weaponData.bound || !weaponData.summoned) {
            return false;
        }

        if (!state.weaponType.id().equals(
                weaponData.weaponType
        )) {
            return false;
        }

        return isHeldSoulWeaponValid(player, weaponData);
    }

    private static boolean isHeldSoulWeaponValid(
            ServerPlayer player,
            SoulWeaponData weaponData
    ) {
        ItemStack held = player.getMainHandItem();

        if (!SoulWeaponHelper.isSoulWeapon(held)) {
            return false;
        }

        if (!SoulWeaponHelper.isOwner(held, player)) {
            return false;
        }

        if (!(held.getItem()
                instanceof ISoulboundItem soulboundItem)) {

            return false;
        }

        SoulWeaponType expectedType =
                SoulWeaponType.fromId(weaponData.weaponType);

        return expectedType != null
                && soulboundItem.getSoulWeaponType()
                == expectedType;
    }

    private void completeSacrifice(
            ServerPlayer player,
            SacrificeState state
    ) {
        SoulWeaponData weaponData =
                player.getData(ModAttachments.SOUL_WEAPON);

        int score =
                SoulImmolationHelper.calculateImmolationScore(weaponData);

        SoulImmolationData immolationData =
                player.getData(ModAttachments.SOUL_IMMOLATION);

        if (score
                < SoulImmolationHelper.MINIMUM_SACRIFICE_SCORE
                || score
                <= immolationData.getHighestSacrificedScore()) {

            cancelRitual(player);
            return;
        }

        if (!immolationData.tryUpgrade(score)) {
            cancelRitual(player);
            return;
        }

        int boostPercent =
                SoulImmolationHelper.getDisplayedBoostPercent(score);

        IEntityData entityData =
                player.getData(ModAttachments.ENTITY_DATA);

        entityData.removeEntitySource(
                SoulWeaponHelper.getWeaponPathSourceId(
                        state.weaponType
                )
        );

        SoulWeaponHelper.removeOwnedSoulWeapons(player);
        weaponData.clear();

        ACTIVE_RITUALS.remove(player.getUUID());

        player.serverLevel().sendParticles(
                ParticleTypes.SOUL,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                70,
                0.65D,
                0.85D,
                0.65D,
                0.08D
        );

        player.serverLevel().sendParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                32,
                0.45D,
                0.65D,
                0.45D,
                0.04D
        );

        player.level().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.BEACON_DEACTIVATE,
                SoundSource.PLAYERS,
                1.0F,
                0.55F
        );

        player.displayClientMessage(
                Component.translatable(
                        "ascension.skill.soul_weapon_immolation.completed",
                        score,
                        boostPercent
                ),
                false
        );
    }

    private void cancelRitual(ServerPlayer player) {
        SacrificeState removed =
                ACTIVE_RITUALS.remove(player.getUUID());

        if (removed == null) return;

        player.displayClientMessage(
                Component.translatable(
                        "ascension.skill.soul_weapon_immolation.cancelled"
                ),
                true
        );
    }

    private static CastResult failure() {
        return new CastResult(CastResult.Type.FAILURE);
    }

    @Override
    public void finalCast(
            CastEndData reason,
            Entity caster,
            ICastData castData
    ) {
        if (!(caster instanceof ServerPlayer player)) return;
        cancelRitual(player);
    }

    @Override
    public void onUnEquip(
            IEntityData entityData,
            IPreCastData preCastData
    ) {
        if (entityData.getAttachedEntity()
                instanceof ServerPlayer player) {

            cancelRitual(player);
        }
    }

    @Override
    public void onRemoved(
            IEntityData entityData,
            IPersistentSkillData persistentData
    ) {
        if (entityData.getAttachedEntity()
                instanceof ServerPlayer player) {

            cancelRitual(player);
        }
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public int getCooldown(CastEndData castEndData) {
        return COOLDOWN_TICKS;
    }

    @Override
    public Component getTitle(IEntityData entityData) {
        return Component.translatable(
                "ascension.skill.soul_weapon_immolation"
        );
    }

    @Override
    public Component getDescription(IEntityData entityData) {
        return Component.translatable(
                "ascension.skill.soul_weapon_immolation.description"
        );
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ITextureData getIcon(IEntityData entityData) {
        return new TextureData(
                ResourceLocation.fromNamespaceAndPath(
                        AscensionCraft.MOD_ID,
                        "textures/spells/icon/placeholder.png"
                ),
                16,
                16
        );
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public RenderableElement getInformationContainer(
            UIFrame frame,
            IEntityData entityData
    ) {
        return new DescriptionDisplayContainer(
                frame,
                getTitle(entityData),
                getDescription(entityData)
        );
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public RenderableElement getCastElement(UIFrame frame) {
        return null;
    }

    @Override public void onEquip(IEntityData entityData) {}
    @Override public void selected(IEntityData entityData) {}
    @Override public void unselected(IEntityData entityData) {}

    @Override
    public IPreCastData freshPreCastData() {
        return null;
    }

    @Override
    public IPreCastData preCastDataFromCompound(CompoundTag tag) {
        return null;
    }

    @Override
    public IPreCastData preCastDataFromNetwork(
            RegistryFriendlyByteBuf buf
    ) {
        return null;
    }

    @Override
    public ICastData freshCastData() {
        return null;
    }

    @Override
    public ICastData castDataFromCompound(CompoundTag tag) {
        return null;
    }

    @Override
    public ICastData castDataFromNetwork(
            RegistryFriendlyByteBuf buf
    ) {
        return null;
    }

    @Override
    public IPersistentSkillData freshPersistentInstance() {
        return null;
    }

    @Override
    public IPersistentSkillData persistentInstanceFromCompound(
            CompoundTag tag
    ) {
        return null;
    }

    @Override
    public IPersistentSkillData persistentInstanceFromNetwork(
            RegistryFriendlyByteBuf buf
    ) {
        return null;
    }

    @Override
    public IPersistentSkillData freshPersistentData(
            IEntityData entityData
    ) {
        return null;
    }

    @Override
    public IPersistentSkillData fromCompound(
            CompoundTag tag,
            IEntityData entityData
    ) {
        return null;
    }

    @Override
    public IPersistentSkillData fromNetwork(
            RegistryFriendlyByteBuf buf
    ) {
        return null;
    }

    @Override
    public void onAdded(IEntityData entityData) {}

    @Override
    public void onFormAdded(
            IEntityData entityData,
            ResourceLocation form,
            IPhysiqueData physiqueData
    ) {}

    @Override
    public void onFormRemoved(
            IEntityData entityData,
            ResourceLocation form,
            IPhysiqueData physiqueData
    ) {}

    @Override
    public void finishedCooldown(
            IEntityData entityData,
            String identifier
    ) {}

    private static final class SacrificeState {
        private final Vec3 startPosition;
        private final float startVanillaHealth;
        private final double startAscensionHealth;
        private final SoulWeaponType weaponType;

        private SacrificeState(
                Vec3 startPosition,
                float startVanillaHealth,
                double startAscensionHealth,
                SoulWeaponType weaponType
        ) {
            this.startPosition = startPosition;
            this.startVanillaHealth = startVanillaHealth;
            this.startAscensionHealth = startAscensionHealth;
            this.weaponType = weaponType;
        }
    }
}