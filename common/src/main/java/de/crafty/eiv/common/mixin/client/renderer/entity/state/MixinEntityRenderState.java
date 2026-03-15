package de.crafty.eiv.common.mixin.client.renderer.entity.state;

import de.crafty.eiv.common.embeddings.EmbeddingData;
import de.crafty.eiv.common.access.IEivWrappedRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public abstract class MixinEntityRenderState implements IEivWrappedRenderState {


    @Unique
    private boolean eiv$multiRendering = false;

    @Unique
    private EmbeddingData eiv$embeddingData;

    @Override
    public void eiv$enableMultiRendering() {
        this.eiv$multiRendering = true;
    }

    @Override
    public boolean eiv$isMultiRenderingEnabled() {
        return this.eiv$multiRendering;
    }

    @Override
    public void eiv$setEmbeddingData(EmbeddingData embeddingData) {
        this.eiv$embeddingData = embeddingData;
    }

    @Override
    public EmbeddingData eiv$getEmbeddingData() {
        return this.eiv$embeddingData;
    }
}
