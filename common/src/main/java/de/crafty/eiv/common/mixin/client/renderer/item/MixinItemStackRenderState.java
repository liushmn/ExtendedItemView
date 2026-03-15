package de.crafty.eiv.common.mixin.client.renderer.item;

import de.crafty.eiv.common.access.IEivItemStackRenderState;
import de.crafty.eiv.common.embeddings.EmbeddingData;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStackRenderState.class)
public abstract class MixinItemStackRenderState implements IEivItemStackRenderState {


    @Unique
    private EmbeddingData embeddingData;


    @Override
    public void eiv$setEmbeddingData(EmbeddingData embeddingData) {
        this.embeddingData = embeddingData;
    }

    @Override
    public EmbeddingData eiv$getEmbeddingData() {
        return this.embeddingData;
    }
}
