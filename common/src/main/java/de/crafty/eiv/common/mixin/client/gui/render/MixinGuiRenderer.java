package de.crafty.eiv.common.mixin.client.gui.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import de.crafty.eiv.common.access.IEivItemStackRenderState;
import de.crafty.eiv.common.embeddings.EmbeddingData;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(GuiRenderer.class)
public abstract class MixinGuiRenderer implements AutoCloseable {


    @Shadow
    @Final
    private GuiRenderState renderState;

    @Shadow
    private @Nullable GpuTextureView itemsAtlasView;


    @Inject(method = "submitBlitFromItemAtlas", at = @At("HEAD"), cancellable = true)
    private void injectEmbeddingData(GuiItemRenderState guiItemRenderState, float f, float g, int i, int j, CallbackInfo ci) {
        float h = f + (float) i / j;
        float k = g + (float) (-i) / j;

        EmbeddingData data = ((IEivItemStackRenderState) guiItemRenderState.itemStackRenderState()).eiv$getEmbeddingData();

        if (data == null)
            return;


        this.renderState.submitBlitToCurrentLayer(
                new BlitRenderState(
                        RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA,
                        TextureSetup.singleTexture(this.itemsAtlasView, RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST)),
                        guiItemRenderState.pose(),
                        guiItemRenderState.x(),
                        guiItemRenderState.y(),
                        guiItemRenderState.x() + 16,
                        guiItemRenderState.y() + 16,
                        f,
                        h,
                        g,
                        k,
                        ARGB.color(Math.round(255 * data.alpha()), Math.round(255 * data.alpha()), Math.round(255 * data.alpha()), Math.round(255 * data.alpha())),
                        guiItemRenderState.scissorArea(),
                        null
                )
        );

        ci.cancel();

    }
}
