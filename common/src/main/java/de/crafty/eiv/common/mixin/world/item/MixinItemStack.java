package de.crafty.eiv.common.mixin.world.item;

import de.crafty.eiv.common.access.IEivItemStack;
import de.crafty.eiv.common.embeddings.EmbeddingData;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements IEivItemStack {


    @Unique
    private EmbeddingData embeddingData;

    @Override
    public void eiv$setEmbeddingData(EmbeddingData data) {
        this.embeddingData = data;
    }

    @Override
    public EmbeddingData eiv$getEmbeddingData() {
        return this.embeddingData;
    }
}
