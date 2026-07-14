package net.thejadeproject.ascension.common.items.tools.data.soul_tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.thejadeproject.ascension.common.items.tools.soul_tool.*;

import java.util.UUID;

public record SoulToolComponent(
        UUID owner,
        UUID implementId,
        SoulToolType type,
        SoulToolCore core,
        SoulToolHarvest harvest,
        SoulToolFlow flow,
        SoulToolSpirit spirit,
        int soulMajor,
        int soulMinor
) {
    public static final Codec<SoulToolComponent> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    UUIDUtil.CODEC.fieldOf("owner")
                            .forGetter(SoulToolComponent::owner),

                    UUIDUtil.CODEC.fieldOf("implement_id")
                            .forGetter(SoulToolComponent::implementId),

                    SoulToolType.CODEC
                            .optionalFieldOf("type", SoulToolType.PICKAXE)
                            .forGetter(SoulToolComponent::type),

                    SoulToolCore.CODEC
                            .optionalFieldOf("core", SoulToolCore.NONE)
                            .forGetter(SoulToolComponent::core),

                    SoulToolHarvest.CODEC
                            .optionalFieldOf("harvest", SoulToolHarvest.NONE)
                            .forGetter(SoulToolComponent::harvest),

                    SoulToolFlow.CODEC
                            .optionalFieldOf("flow", SoulToolFlow.NONE)
                            .forGetter(SoulToolComponent::flow),

                    SoulToolSpirit.CODEC
                            .optionalFieldOf("spirit", SoulToolSpirit.NONE)
                            .forGetter(SoulToolComponent::spirit),

                    Codec.INT
                            .optionalFieldOf("soul_major", 0)
                            .forGetter(SoulToolComponent::soulMajor),

                    Codec.INT
                            .optionalFieldOf("soul_minor", 0)
                            .forGetter(SoulToolComponent::soulMinor)
            ).apply(instance, SoulToolComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SoulToolComponent> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public SoulToolComponent decode(RegistryFriendlyByteBuf buf) {
                    return new SoulToolComponent(
                            UUIDUtil.STREAM_CODEC.decode(buf),
                            UUIDUtil.STREAM_CODEC.decode(buf),
                            SoulToolType.fromId(ByteBufCodecs.STRING_UTF8.decode(buf)),
                            SoulToolCore.fromId(ByteBufCodecs.STRING_UTF8.decode(buf)),
                            SoulToolHarvest.fromId(ByteBufCodecs.STRING_UTF8.decode(buf)),
                            SoulToolFlow.fromId(ByteBufCodecs.STRING_UTF8.decode(buf)),
                            SoulToolSpirit.fromId(ByteBufCodecs.STRING_UTF8.decode(buf)),
                            ByteBufCodecs.VAR_INT.decode(buf),
                            ByteBufCodecs.VAR_INT.decode(buf)
                    );
                }

                @Override
                public void encode(
                        RegistryFriendlyByteBuf buf,
                        SoulToolComponent component
                ) {
                    UUIDUtil.STREAM_CODEC.encode(buf, component.owner());
                    UUIDUtil.STREAM_CODEC.encode(buf, component.implementId());

                    ByteBufCodecs.STRING_UTF8.encode(buf, component.type().id());
                    ByteBufCodecs.STRING_UTF8.encode(buf, component.core().id());
                    ByteBufCodecs.STRING_UTF8.encode(buf, component.harvest().id());
                    ByteBufCodecs.STRING_UTF8.encode(buf, component.flow().id());
                    ByteBufCodecs.STRING_UTF8.encode(buf, component.spirit().id());

                    ByteBufCodecs.VAR_INT.encode(buf, component.soulMajor());
                    ByteBufCodecs.VAR_INT.encode(buf, component.soulMinor());
                }
            };

    public double soulToolPower() {
        return soulMajor + soulMinor * 0.10D;
    }

    public boolean isFormed() {
        return core.isFormed();
    }
}