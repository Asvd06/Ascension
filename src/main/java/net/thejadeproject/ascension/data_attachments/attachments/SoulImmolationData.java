package net.thejadeproject.ascension.data_attachments.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SoulImmolationData {

    public static final Codec<SoulImmolationData> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.optionalFieldOf(
                            "highest_sacrificed_score",
                            0
                    ).forGetter(SoulImmolationData::getHighestSacrificedScore)
            ).apply(instance, SoulImmolationData::new));

    private int highestSacrificedScore;

    public SoulImmolationData(int highestSacrificedScore) {
        this.highestSacrificedScore =
                Math.max(0, highestSacrificedScore);
    }

    public SoulImmolationData() {
        this(0);
    }

    public int getHighestSacrificedScore() {
        return highestSacrificedScore;
    }

    public boolean tryUpgrade(int newScore) {
        if (newScore <= highestSacrificedScore) {
            return false;
        }

        highestSacrificedScore = newScore;
        return true;
    }
}