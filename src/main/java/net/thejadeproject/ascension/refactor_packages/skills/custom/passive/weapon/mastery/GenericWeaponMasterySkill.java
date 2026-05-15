package net.thejadeproject.ascension.refactor_packages.skills.custom.passive.weapon.mastery;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.events.entity.EntitySwingEvent;
import net.thejadeproject.ascension.refactor_packages.paths.PathData;
import net.thejadeproject.ascension.refactor_packages.qi.EntityQiContainer;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skills.ITickingSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.passive.SimplePassiveSkill;
import net.thejadeproject.ascension.refactor_packages.skills.vfx.weaponvfx.WeaponVfxUtils;
import org.joml.Vector3f;

/**
 * Base class for all weapon mastery passives.
 *
 * Passive (ITickingSkill):
 *   Every getSwingIntervalTicks() ticks, if the player holds the right weapon AND has qi:
 *     1. Consume qi scaled by the damage multiplier.
 *     2. Look up the technique the player is currently using on this path.
 *     3. Pass that technique ID to WeaponVfxUtils → VfxColorRegistry resolves the color.
 *     4. Spawn a WeaponSwingVfxEntity with the correct colored texture path.
 *
 * Subclasses register their technique → color mappings in a static block
 * and supply getFallbackColor() for when no technique matches.
 */
public abstract class GenericWeaponMasterySkill extends SimplePassiveSkill implements ITickingSkill {

    public GenericWeaponMasterySkill(){
        NeoForge.EVENT_BUS.addListener(this::onSwing);
    }
    // ── Abstract surface ─────────────────────────────────────────────────────

    protected abstract ResourceLocation getPathId();
    protected abstract TagKey<Item> getWeaponTag();
    /** The VFX logic / folder tag, e.g. WeaponSwingVfxEntity.TYPE_SWORD. */
    protected abstract String getVfxType();

    // ── Overridable config ────────────────────────────────────────────────────

    /** Ticks between passive swings. */
    protected int getSwingIntervalTicks() { return 20; }
    /** Hit-box scale of the spawned VFX entity. */
    protected Vector3f getEffectRadius() { return new Vector3f(2.0f, 2.0f, 2.0f); }
    /** Flat base damage before path multiplier. */
    protected double  getBaseDamage() { return 4.0; }
    /** How many ticks the VFX entity lives. */
    protected int     getEffectDuration() { return 10; }
    /** Z-rotation of the rendered quad (visual tilt). */
    protected float   getRotationZ() { return 0.0f; }
    /** Qi consumed per swing — receives the already-computed damage multiplier. */
    protected double  getQiCostPerSwing(double damageMultiplier) { return 2.0 * damageMultiplier; }
    /**
     * Color folder used when the player's current technique has no registered mapping.
     * Override in each subclass to match the default sprite you want.
     */
    protected String  getFallbackColor()      { return "blue";          }

    // ── Damage multiplier (unchanged from original) ───────────────────────────

    protected double getBaseBonus()           { return 0.10D; }
    protected double getBonusPerMajorRealm()  { return 0.22D; }
    protected double getBonusPerMinorRealm()  { return 0.025D; }
    protected double getMaxBonus()            { return 5.0D;  }

    public  boolean matchesWeapon(ItemStack stack) {
        return !stack.isEmpty() && stack.is(getWeaponTag());
    }

    public boolean matchesDamage(ServerPlayer player, DamageSource source) {
        return matchesWeapon(player.getMainHandItem());
    }

    public double getDamageMultiplier(IEntityData entityData) {
        if (entityData == null) return 1.0D;
        if (!entityData.hasPath(getPathId())) return 1.0D;
        PathData pathData = entityData.getPathData(getPathId());
        if (pathData == null) return 1.0D;

        double bonus = getBaseBonus()
                + pathData.getMajorRealm() * getBonusPerMajorRealm()
                + pathData.getMinorRealm() * getBonusPerMinorRealm();

        return 1.0D + Mth.clamp(bonus, 0.0D, getMaxBonus());
    }

    public void onSwing(EntitySwingEvent event){
        if(event.getEntity().level().isClientSide) return;

        if(!event.getEntity().hasData(ModAttachments.ENTITY_DATA)) return;
        IEntityData entityData = event.getEntity().getData(ModAttachments.ENTITY_DATA);
        ResourceLocation skillId = AscensionRegistries.Skills.SKILL_REGISTRY.getKey(this);

        if(!entityData.hasSkill(skillId)) return;

        if (!matchesWeapon(event.getEntity().getItemInHand(event.getHand()))) return;
        EntityQiContainer qi = entityData.getQiContainer();
        if (qi == null) return;

        double multiplier = getDamageMultiplier(entityData);
        double qiCost     = getQiCostPerSwing(multiplier);
        if (!qi.hasQi(qiCost) || !qi.tryConsumeQi(qiCost)) return;

        // Resolve which technique the player has active on this path.
        // getTechnique() returns the ResourceLocation of the active technique, or null.
        ResourceLocation techniqueId = entityData.getTechnique(getPathId());

        WeaponVfxUtils.spawnSwingVfxAhead(
                event.getEntity().level(),
                event.getEntity(),
                getRotationZ(),
                getEffectRadius(),
                getBaseDamage() * multiplier,
                1.0,
                getEffectDuration(),
                getVfxType(),
                techniqueId,       // ← resolved by VfxColorRegistry
                getFallbackColor()
        );
    }

    // ── ITickingSkill ─────────────────────────────────────────────────────────

    @Override
    public void onPlayerTick(ServerPlayer player, IEntityData entityData) {

    }

}