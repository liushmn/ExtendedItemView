package de.crafty.eiv.common.mixin.world.entity;

import de.crafty.eiv.common.access.IEivEntity;
import de.crafty.eiv.common.component.EivDataComponents;
import de.crafty.eiv.common.embeddings.EmbeddingData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEivEntity {


    @Unique
    private EmbeddingData embeddingData;

    @Inject(method = "setComponent", at = @At("HEAD"))
    private <T> void addEmbeddingData(DataComponentType<T> dataComponentType, T object, CallbackInfo ci) {
        if (dataComponentType == EivDataComponents.EMBEDDING_DATA)
            this.embeddingData = (EmbeddingData) object;
    }

    @Override
    public EmbeddingData eiv$getEmbeddingData() {
        return this.embeddingData;
    }
}
