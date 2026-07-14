package net.thejadeproject.ascension.data_attachments.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.thejadeproject.ascension.common.items.tools.soul_tool.*;

import java.util.UUID;

public class SoulToolData {

    public static final UUID NIL_UUID = new UUID(0L, 0L);

    public static final Codec<SoulToolData> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.BOOL
                            .optionalFieldOf("bound", false)
                            .forGetter(data -> data.bound),

                    Codec.BOOL
                            .optionalFieldOf("summoned", false)
                            .forGetter(data -> data.summoned),

                    UUIDUtil.CODEC
                            .optionalFieldOf("implement_id", NIL_UUID)
                            .forGetter(data -> data.implementId),

                    SoulToolType.CODEC
                            .optionalFieldOf("active_type", SoulToolType.PICKAXE)
                            .forGetter(data -> data.activeType),

                    SoulToolCore.CODEC
                            .optionalFieldOf("core", SoulToolCore.NONE)
                            .forGetter(data -> data.core),

                    SoulToolHarvest.CODEC
                            .optionalFieldOf("harvest", SoulToolHarvest.NONE)
                            .forGetter(data -> data.harvest),

                    SoulToolFlow.CODEC
                            .optionalFieldOf("flow", SoulToolFlow.NONE)
                            .forGetter(data -> data.flow),

                    SoulToolSpirit.CODEC
                            .optionalFieldOf("spirit", SoulToolSpirit.NONE)
                            .forGetter(data -> data.spirit),

                    Codec.INT
                            .optionalFieldOf("last_soul_major", 0)
                            .forGetter(data -> data.lastSoulMajor),

                    Codec.INT
                            .optionalFieldOf("last_soul_minor", 0)
                            .forGetter(data -> data.lastSoulMinor)
            ).apply(instance, SoulToolData::new));

    public boolean bound;
    public boolean summoned;

    public UUID implementId = NIL_UUID;

    public SoulToolType activeType = SoulToolType.PICKAXE;
    public SoulToolCore core = SoulToolCore.NONE;
    public SoulToolHarvest harvest = SoulToolHarvest.NONE;
    public SoulToolFlow flow = SoulToolFlow.NONE;
    public SoulToolSpirit spirit = SoulToolSpirit.NONE;

    public int lastSoulMajor;
    public int lastSoulMinor;

    public transient long dissolveConfirmUntilTick;

    public SoulToolData() {
    }

    private SoulToolData(
            boolean bound,
            boolean summoned,
            UUID implementId,
            SoulToolType activeType,
            SoulToolCore core,
            SoulToolHarvest harvest,
            SoulToolFlow flow,
            SoulToolSpirit spirit,
            int lastSoulMajor,
            int lastSoulMinor
    ) {
        this.bound = bound;
        this.summoned = summoned;
        this.implementId = implementId == null ? NIL_UUID : implementId;
        this.activeType = activeType == null
                ? SoulToolType.PICKAXE
                : activeType;
        this.core = core == null ? SoulToolCore.NONE : core;
        this.harvest = harvest == null
                ? SoulToolHarvest.NONE
                : harvest;
        this.flow = flow == null ? SoulToolFlow.NONE : flow;
        this.spirit = spirit == null
                ? SoulToolSpirit.NONE
                : spirit;
        this.lastSoulMajor = Math.max(0, lastSoulMajor);
        this.lastSoulMinor = Math.max(0, lastSoulMinor);
    }

    public void bind(int soulMajor, int soulMinor) {
        bound = true;
        summoned = false;

        implementId = UUID.randomUUID();

        activeType = SoulToolType.PICKAXE;
        core = SoulToolCore.NONE;
        harvest = SoulToolHarvest.NONE;
        flow = SoulToolFlow.NONE;
        spirit = SoulToolSpirit.NONE;

        lastSoulMajor = Math.max(0, soulMajor);
        lastSoulMinor = Math.max(0, soulMinor);

        dissolveConfirmUntilTick = 0L;
    }

    public void clear() {
        bound = false;
        summoned = false;

        implementId = NIL_UUID;

        activeType = SoulToolType.PICKAXE;
        core = SoulToolCore.NONE;
        harvest = SoulToolHarvest.NONE;
        flow = SoulToolFlow.NONE;
        spirit = SoulToolSpirit.NONE;

        lastSoulMajor = 0;
        lastSoulMinor = 0;

        dissolveConfirmUntilTick = 0L;
    }

    public boolean hasImplementId() {
        return implementId != null && !NIL_UUID.equals(implementId);
    }

    public double soulToolPower() {
        return lastSoulMajor + lastSoulMinor * 0.10D;
    }

    public boolean isFormed() {
        return core.isFormed();
    }
}