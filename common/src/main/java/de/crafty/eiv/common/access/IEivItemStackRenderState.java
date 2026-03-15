package de.crafty.eiv.common.access;

import de.crafty.eiv.common.embeddings.EmbeddingData;
import net.minecraft.world.item.ItemStack;

public interface IEivItemStackRenderState {


    void eiv$setEmbeddingData(EmbeddingData embeddingData);

    EmbeddingData eiv$getEmbeddingData();

}
