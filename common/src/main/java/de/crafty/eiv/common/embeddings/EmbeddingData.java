package de.crafty.eiv.common.embeddings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record EmbeddingData(float alpha) {



    public static final Codec<EmbeddingData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.FLOAT.fieldOf("alpha").forGetter(o -> ((EmbeddingData) o).alpha())

            ).apply(instance, EmbeddingData::new)
    );

}
