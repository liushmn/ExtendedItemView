package de.crafty.eiv.common.embeddings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;

public record EmbeddingData(float alpha) {


    public static final HashMap<BakedQuad, EmbeddingData> DATA_CACHE =  new HashMap<>();


    public static void cache(BakedQuad quad, EmbeddingData data) {
        DATA_CACHE.put(quad, data);
    }

    public static EmbeddingData getCached(BakedQuad quad) {
        return DATA_CACHE.get(quad);
    }


    public static final Codec<EmbeddingData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.FLOAT.fieldOf("alpha").forGetter(o -> ((EmbeddingData) o).alpha())

            ).apply(instance, EmbeddingData::new)
    );

}
