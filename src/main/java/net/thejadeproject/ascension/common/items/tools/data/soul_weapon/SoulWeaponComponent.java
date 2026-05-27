package net.thejadeproject.ascension.common.items.tools.data.soul_weapon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record SoulWeaponComponent(
        UUID owner,
        String type,
        int grade,
        int tempering,
        int forgedMarks,
        int soulMajor,
        int soulMinor
) {
    public static final Codec<SoulWeaponComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            UUIDUtil.CODEC.fieldOf("owner").forGetter(SoulWeaponComponent::owner),
            Codec.STRING.fieldOf("type").forGetter(SoulWeaponComponent::type),
            Codec.INT.optionalFieldOf("currentGrade", 0).forGetter(SoulWeaponComponent::grade),
            Codec.INT.optionalFieldOf("tempering", 0).forGetter(SoulWeaponComponent::tempering),
            Codec.INT.optionalFieldOf("forged_marks", 0).forGetter(SoulWeaponComponent::forgedMarks),
            Codec.INT.optionalFieldOf("soul_major", 0).forGetter(SoulWeaponComponent::soulMajor),
            Codec.INT.optionalFieldOf("soul_minor", 0).forGetter(SoulWeaponComponent::soulMinor)
    ).apply(inst, SoulWeaponComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SoulWeaponComponent> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public SoulWeaponComponent decode(RegistryFriendlyByteBuf buf) {
                    return new SoulWeaponComponent(
                            UUIDUtil.STREAM_CODEC.decode(buf),
                            ByteBufCodecs.STRING_UTF8.decode(buf),
                            ByteBufCodecs.VAR_INT.decode(buf),
                            ByteBufCodecs.VAR_INT.decode(buf),
                            ByteBufCodecs.VAR_INT.decode(buf),
                            ByteBufCodecs.VAR_INT.decode(buf),
                            ByteBufCodecs.VAR_INT.decode(buf)
                    );
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, SoulWeaponComponent component) {
                    UUIDUtil.STREAM_CODEC.encode(buf, component.owner());
                    ByteBufCodecs.STRING_UTF8.encode(buf, component.type());
                    ByteBufCodecs.VAR_INT.encode(buf, component.grade());
                    ByteBufCodecs.VAR_INT.encode(buf, component.tempering());
                    ByteBufCodecs.VAR_INT.encode(buf, component.forgedMarks());
                    ByteBufCodecs.VAR_INT.encode(buf, component.soulMajor());
                    ByteBufCodecs.VAR_INT.encode(buf, component.soulMinor());
                }
            };

}