package de.crafty.eiv.common.mixin.client.gui.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import de.crafty.eiv.common.access.IEivItemStackRenderState;
import de.crafty.eiv.common.embeddings.EmbeddingData;
import net.minecraft.client.gui.render.GuiItemAtlas;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(GuiRenderer.class)
public abstract class MixinGuiRenderer implements AutoCloseable {


    @Shadow(remap = false)
    @Final
    private GuiRenderState renderState;



    @Inject(method = "submitBlitFromItemAtlas", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectEmbeddingData(GuiItemRenderState itemState, GuiItemAtlas.SlotView slotView, CallbackInfo ci) {


        EmbeddingData data = ((IEivItemStackRenderState) itemState.itemStackRenderState()).eiv$getEmbeddingData();

        if (data == null)
            return;


        this.renderState.addBlitToCurrentLayer(
                new BlitRenderState(
                        RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA,
                        TextureSetup.singleTexture(slotView.textureView(), RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST)),
                        itemState.pose(),
                        itemState.x(),
                        itemState.y(),
                        itemState.x() + 16,
                        itemState.y() + 16,
                        slotView.u0(),
                        slotView.u1(),
                        slotView.v0(),
                        slotView.v1(),
                        ARGB.color(Math.round(255 * data.alpha()), Math.round(255 * data.alpha()), Math.round(255 * data.alpha()), Math.round(255 * data.alpha())),
                        itemState.scissorArea(),
                        null
                )
        );

        ci.cancel();

    }
}
