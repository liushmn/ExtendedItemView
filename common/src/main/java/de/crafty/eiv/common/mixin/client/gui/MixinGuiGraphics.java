package de.crafty.eiv.common.mixin.client.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import de.crafty.eiv.common.access.IEivItemStackRenderState;
import de.crafty.eiv.common.component.EivDataComponents;
import de.crafty.eiv.common.embeddings.EmbeddingData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Random;

@Mixin(GuiGraphics.class)
public abstract class MixinGuiGraphics {


    @Shadow
    public abstract void drawString(Font p_283019_, @Nullable String p_415853_, int p_283379_, int p_283346_, int p_282119_, boolean p_416601_);

    @Shadow
    public abstract void fill(RenderPipeline p_416410_, int p_281437_, int p_283660_, int p_282606_, int p_283413_, int p_283428_);

    @Shadow
    @Final
    private Minecraft minecraft;

    @Redirect(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemModelResolver;updateForTopItem(Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/ItemOwner;I)V"))
    private void injectStackData(ItemModelResolver instance, ItemStackRenderState renderState, ItemStack stack, ItemDisplayContext p_388835_, Level p_388064_, ItemOwner p_434877_, int p_388137_) {
        instance.updateForTopItem(renderState, stack, p_388835_, p_388064_, p_434877_, p_388137_);

        if (stack.has(EivDataComponents.EMBEDDING_DATA)) {
            renderState.setAnimated();
            ((IEivItemStackRenderState) renderState).eiv$setEmbeddingData(stack.get(EivDataComponents.EMBEDDING_DATA));
        }

    }


    @Inject(method = "renderItemCooldown", at = @At("HEAD"), cancellable = true)
    private void renderChatDependantCouldown(ItemStack itemStack, int i, int j, CallbackInfo ci) {

        if (!itemStack.has(EivDataComponents.EMBEDDING_DATA))
            return;


        EmbeddingData data = itemStack.get(EivDataComponents.EMBEDDING_DATA);
        if (data == null)
            return;

        Color c = new Color(Integer.MAX_VALUE);

        LocalPlayer localPlayer = this.minecraft.player;
        float f = localPlayer == null
                ? 0.0F
                : localPlayer.getCooldowns().getCooldownPercent(itemStack, this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true));
        if (f > 0.0F) {
            int k = j + Mth.floor(16.0F * (1.0F - f));
            int l = k + Mth.ceil(16.0F * f);
            this.fill(RenderPipelines.GUI, i, k, i + 16, l, ARGB.color(Math.round(c.getAlpha() * data.alpha()), c.getRed(), c.getGreen(), c.getBlue()));
        }


        ci.cancel();

    }


    @Inject(method = "renderItemBar", at = @At("HEAD"), cancellable = true)
    private void renderChatDependantItemBar(ItemStack itemStack, int i, int j, CallbackInfo ci) {

        if (!itemStack.has(EivDataComponents.EMBEDDING_DATA))
            return;

        EmbeddingData data = itemStack.get(EivDataComponents.EMBEDDING_DATA);

        if (data == null)
            return;

        Color oldBar = new Color(itemStack.getBarColor());
        Color oldBg = new Color(-16777216);

        if (itemStack.isBarVisible()) {
            int k = i + 2;
            int l = j + 13;
            this.fill(RenderPipelines.GUI, k, l, k + 13, l + 2, ARGB.color(Math.round(oldBg.getAlpha() * data.alpha()), oldBg.getRed(), oldBg.getGreen(), oldBg.getBlue()));
            this.fill(RenderPipelines.GUI, k, l, k + itemStack.getBarWidth(), l + 1, ARGB.color(Math.round(oldBar.getAlpha() * data.alpha()), oldBar.getRed(), oldBar.getGreen(), oldBar.getBlue()));
        }

        ci.cancel();
    }

    @Inject(method = "renderItemCount", at = @At("HEAD"), cancellable = true)
    private void renderChatDependantCount(Font font, ItemStack itemStack, int i, int j, String string, CallbackInfo ci) {

        if (!itemStack.has(EivDataComponents.EMBEDDING_DATA))
            return;

        if (itemStack.getCount() == 1 && string == null)
            return;


        EmbeddingData data = itemStack.get(EivDataComponents.EMBEDDING_DATA);

        if (data == null)
            return;

        String string2 = string == null ? String.valueOf(itemStack.getCount()) : string;
        this.drawString(font, string2, i + 19 - 2 - font.width(string2), j + 6 + 3, ARGB.color(Math.round(255 * data.alpha()), 255, 255, 255), true);


        ci.cancel();

    }

}
