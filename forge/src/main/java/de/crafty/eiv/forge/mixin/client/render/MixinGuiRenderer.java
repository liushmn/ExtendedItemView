package de.crafty.eiv.forge.mixin.client.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import de.crafty.eiv.common.access.IEivWrappedRenderState;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.GuiEntityRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.pip.GuiEntityRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(GuiRenderer.class)
public abstract class MixinGuiRenderer<T extends PictureInPictureRenderState> implements AutoCloseable {


    @Shadow(remap = false)
    @Final
    private Map<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> pictureInPictureRenderers;

    @Shadow(remap = false)
    @Final
    private GuiRenderState renderState;


    @Unique
    private Object2ObjectMap<T, PictureInPictureRenderer<T>> eiv$renderersLastFrame = new Object2ObjectOpenHashMap<>();

    @Unique
    private Object2ObjectMap<T, PictureInPictureRenderer<T>> eiv$renderersThisFrame = new Object2ObjectOpenHashMap<>();

    @Inject(method = "preparePictureInPictureState", at = @At("HEAD"), cancellable = true, remap = false)
    private void useFreshRenderers(T renderState, int guiScale, CallbackInfo ci) {

        PictureInPictureRenderer<T> renderer = (PictureInPictureRenderer<T>) this.pictureInPictureRenderers.get(renderState.getClass());

        if (!(renderer instanceof GuiEntityRenderer) || !(renderState instanceof GuiEntityRenderState guiEntityRenderState))
            return;

        if (!(guiEntityRenderState.renderState() instanceof IEivWrappedRenderState wrappedRenderState) || !wrappedRenderState.eiv$isMultiRenderingEnabled())
            return;

        if (this.eiv$renderersLastFrame.containsKey(renderState))
            this.eiv$renderersThisFrame.put(renderState, this.eiv$renderersLastFrame.remove(renderState));
        else
            this.eiv$renderersThisFrame.put(renderState, (PictureInPictureRenderer<T>) new GuiEntityRenderer(Minecraft.getInstance().renderBuffers().bufferSource(), Minecraft.getInstance().getEntityRenderDispatcher()));

        this.eiv$renderersThisFrame.get(renderState).prepare(renderState, this.renderState, guiScale);
        ci.cancel();
    }


    @Inject(method = "render", at = @At("RETURN"), remap = false)
    private void clearUnused(GpuBufferSlice p_406940_, CallbackInfo ci) {
        this.eiv$renderersLastFrame.values().forEach(PictureInPictureRenderer::close);
        this.eiv$renderersLastFrame.clear();

        Object2ObjectMap<T, PictureInPictureRenderer<T>> lastFrameCache = this.eiv$renderersLastFrame;
        this.eiv$renderersLastFrame = this.eiv$renderersThisFrame;
        this.eiv$renderersThisFrame = lastFrameCache;

    }

    @Inject(method = "close", at = @At("RETURN"), remap = false)
    private void closeRenderers(CallbackInfo ci) {
        this.eiv$renderersLastFrame.values().forEach(PictureInPictureRenderer::close);
        this.eiv$renderersThisFrame.values().forEach(PictureInPictureRenderer::close);
    }
}
