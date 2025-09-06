package de.crafty.eiv.common.mixin.client.gui.components;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.overlay.ItemViewOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

@Mixin(EditBox.class)
public abstract class MixinEditBox extends AbstractWidget {

    @Unique
    private static final ResourceLocation FILTERMODE_LOCATION = ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, "widget/searchbar_filtermode");

    public MixinEditBox(int $$0, int $$1, int $$2, int $$3, Component $$4) {
        super($$0, $$1, $$2, $$3, $$4);
    }

    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private void renderFilterMode(GuiGraphics instance, RenderPipeline renderPipeline, ResourceLocation resourceLocation, int i, int j, int k, int l){


        if(ItemViewOverlay.SEARCHBAR != null && ((EditBox) (Object) this).equals(ItemViewOverlay.SEARCHBAR) && ItemViewOverlay.INSTANCE.isItemFilterMode())
            instance.blitSprite(renderPipeline, FILTERMODE_LOCATION, i, j, k, l);
        else
            instance.blitSprite(renderPipeline, resourceLocation, i, j, k, l);
    }
}
