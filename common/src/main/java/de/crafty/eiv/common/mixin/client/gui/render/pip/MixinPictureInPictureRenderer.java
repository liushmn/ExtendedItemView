package de.crafty.eiv.common.mixin.client.gui.render.pip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import de.crafty.eiv.common.embeddings.EmbeddingData;
import de.crafty.eiv.common.access.IEivWrappedRenderState;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.GuiEntityRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PictureInPictureRenderer.class)
public abstract class MixinPictureInPictureRenderer<T extends PictureInPictureRenderState> implements AutoCloseable {


    @Shadow
    private @Nullable GpuTextureView textureView;

    @Inject(method = "blitTexture", at = @At("HEAD"), cancellable = true)
    private void injectEmbeddingData(T pictureInPictureRenderState, GuiRenderState guiRenderState, CallbackInfo ci) {

        if (!(pictureInPictureRenderState instanceof GuiEntityRenderState state))
            return;

        EmbeddingData data = ((IEivWrappedRenderState) state.renderState()).eiv$getEmbeddingData();
        if (data == null)
            return;


        guiRenderState.submitBlitToCurrentLayer(
                new BlitRenderState(
                        RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA,
                        TextureSetup.singleTexture(this.textureView, RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST)),
                        pictureInPictureRenderState.pose(),
                        pictureInPictureRenderState.x0(),
                        pictureInPictureRenderState.y0(),
                        pictureInPictureRenderState.x1(),
                        pictureInPictureRenderState.y1(),
                        0.0F,
                        1.0F,
                        1.0F,
                        0.0F,
                        ARGB.color(Math.round(255 * data.alpha()), Math.round(255 * data.alpha()), Math.round(255 * data.alpha()), Math.round(255 * data.alpha())),
                        pictureInPictureRenderState.scissorArea(),
                        null
                )
        );

        ci.cancel();
    }
}
