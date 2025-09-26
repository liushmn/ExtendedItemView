package de.crafty.eiv.common.mixin.client.gui.screens.inventory;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import de.crafty.eiv.common.overlay.BlockingGuiComponent;
import de.crafty.eiv.common.overlay.OverlayManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;

@Mixin(EffectsInInventory.class)
public abstract class MixinEffectsInInventory {


    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private AbstractContainerScreen<?> screen;

    @Inject(method = "renderEffects", at = @At("HEAD"))
    private void injectBlocking$0(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci){
        OverlayManager.INSTANCE.removeGuiBlocking(resourceLocation -> {

            if(!resourceLocation.getPath().startsWith("mobeffect_"))
                return false;

            String descriptionId = resourceLocation.getPath().split("_")[1];
            return this.minecraft.player.getActiveEffects().stream().noneMatch(mobEffectInstance -> mobEffectInstance.getDescriptionId().equals(descriptionId));
        }, true);
    }

    @Inject(method = "renderBackgrounds", at = @At("HEAD"))
    private void injectBlocking$1(GuiGraphics guiGraphics, int i, int j, Iterable<MobEffectInstance> iterable, boolean bl, CallbackInfo ci){

        int k = OverlayManager.INSTANCE.currentInfo().topPos();

        for (MobEffectInstance mobEffectInstance : iterable) {
            if (bl) {
                OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                        ResourceLocation.withDefaultNamespace("mobeffect_" + mobEffectInstance.getDescriptionId()), i, k, 120, 32
                ));

            } else {
                OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                        ResourceLocation.withDefaultNamespace("mobeffect_" + mobEffectInstance.getDescriptionId()), i, k, 32, 32
                ));

            }
            k += j;
        }


    }

}
