package de.crafty.eiv.common.mixin.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import de.crafty.eiv.common.access.IEivItemStack;
import de.crafty.eiv.common.embeddings.EmbeddingData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(GuiGraphics.class)
public abstract class MixinGuiGraphics {


    @Shadow
    @Final
    private Minecraft minecraft;


    @Shadow
    @Final
    private PoseStack pose;

    @Shadow
    public abstract int drawString(Font p_283343_, @Nullable String p_281896_, int p_283569_, int p_283418_, int p_281560_, boolean p_282130_);

    @Shadow
    public abstract void fill(RenderType p_286602_, int p_286738_, int p_286614_, int p_286741_, int p_286610_, int p_286560_);

    @Redirect(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I"))
    private int injectEmbeddingData$0(GuiGraphics instance, Font font, String string, int x, int y, int color, boolean b) {
        return 0;
    }

    @Redirect(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(Lnet/minecraft/client/renderer/RenderType;IIIII)V"))
    private void injectEmbeddingData$1(GuiGraphics instance, RenderType renderType, int xStart, int yStart, int xEnd, int yEnd, int color) {
    }


    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I"))
    private void injectEmbeddingData$2(Font font, ItemStack itemStack, int x, int y, String string, CallbackInfo ci) {
        IEivItemStack eivStack = (IEivItemStack) (Object) itemStack;
        EmbeddingData embeddingData = eivStack.eiv$getEmbeddingData();

        if(itemStack.getCount() != 1 || string != null){
            String s = string == null ? String.valueOf(itemStack.getCount()) : string;
            this.pose.translate(0.0F, 0.0F, 200.0F);
            Color c = new Color(16777215);
            this.drawString(font, s, x + 19 - 2 - font.width(s), y + 6 + 3, new Color(c.getRed(), c.getGreen(), c.getBlue(), embeddingData == null ? c.getAlpha() : c.getAlpha() * embeddingData.alpha()).getRGB(), true);
        }
    }


    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(Lnet/minecraft/client/renderer/RenderType;IIIII)V", ordinal = 0))
    private void injectEmbeddingData$3(Font font, ItemStack itemStack, int x, int y, String string, CallbackInfo ci) {
        IEivItemStack eivStack = (IEivItemStack) (Object) itemStack;
        EmbeddingData embeddingData = eivStack.eiv$getEmbeddingData();

        if(itemStack.isBarVisible()){
            int width = itemStack.getBarWidth();
            int color = itemStack.getBarColor();
            int j = x + 2;
            int k = y + 13;

            Color c1 = new Color(-16777216);
            Color c2 = new Color(color | -16777216);
            this.fill(RenderType.guiOverlay(), j, k, j + 13, k + 2, new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), embeddingData == null ? c1.getAlpha() : c1.getAlpha() * embeddingData.alpha()).getRGB());
            this.fill(RenderType.guiOverlay(), j, k, j + width, k + 1, new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), embeddingData == null ? c2.getAlpha() : c2.getAlpha() * embeddingData.alpha()).getRGB());
        }
    }

    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(Lnet/minecraft/client/renderer/RenderType;IIIII)V", ordinal = 2))
    private void injectEmbeddingData$4(Font font, ItemStack itemStack, int x, int y, String string, CallbackInfo ci) {
        IEivItemStack eivStack = (IEivItemStack) (Object) itemStack;
        EmbeddingData embeddingData = eivStack.eiv$getEmbeddingData();

        LocalPlayer localPlayer = this.minecraft.player;
        float f = localPlayer == null ? 0.0F : localPlayer.getCooldowns().getCooldownPercent(itemStack.getItem(), this.minecraft.getFrameTime());

        if(f > 0.0F){
            int m = y + Mth.floor(16.0F * (1.0F - f));
            int n = m + Mth.ceil(16.0F * f);
            Color c = new Color(Integer.MAX_VALUE);
            this.fill(RenderType.guiOverlay(), x, m, x + 16, n, new Color(c.getRed(), c.getGreen(), c.getBlue(), embeddingData == null ? c.getAlpha() : c.getAlpha() * embeddingData.alpha()).getRGB());
        }

    }




}
