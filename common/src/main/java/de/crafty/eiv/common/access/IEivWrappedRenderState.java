package de.crafty.eiv.common.access;

import de.crafty.eiv.common.embeddings.EmbeddingData;

/**
 * Interface used to allow the rendering of multiple entities in one frame
 * <br>
 * Wrapped to make sure EIV won't affect the rendering of other mods
 */
public interface IEivWrappedRenderState {


    void eiv$enableMultiRendering();

    boolean eiv$isMultiRenderingEnabled();

    void eiv$setEmbeddingData(EmbeddingData embeddingData);

    EmbeddingData eiv$getEmbeddingData();
}
