package de.crafty.eiv.common.mixin.client.gui.screens.inventory;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import de.crafty.eiv.common.overlay.BlockingGuiComponent;
import de.crafty.eiv.common.overlay.OverlayManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
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

    @Shadow
    protected abstract Component getEffectName(MobEffectInstance p_376252_);

    @Inject(method = "renderEffects", at = @At("HEAD"))
    private void injectBlocking$0(GuiGraphics guiGraphics, Collection<MobEffectInstance> collection, int i, int j, int k, int l, int m, CallbackInfo ci) {

        List<Identifier> effectsToRemove = new ArrayList<>();
        for (BlockingGuiComponent guiBlock : OverlayManager.INSTANCE.allGuiBlockings()) {

            if (!guiBlock.id().getPath().startsWith("mobeffect_"))
                continue;

            String descriptionId = guiBlock.id().getPath().split("_")[1];

            if (this.minecraft.player.getActiveEffects().stream().noneMatch(mobEffectInstance -> mobEffectInstance.getDescriptionId().equals(descriptionId)))
                effectsToRemove.add(guiBlock.id());

        }

        OverlayManager.INSTANCE.removeGuiBlocking(effectsToRemove::contains, !effectsToRemove.isEmpty());
    }

    @Inject(method = "renderEffects", at = @At("RETURN"))
    private void injectBlocking$1(GuiGraphics guiGraphics, Collection<MobEffectInstance> collection, int i, int j, int k, int l, int m, CallbackInfo ci) {

        if (this.minecraft.player == null)
            return;


        int topPos = OverlayManager.INSTANCE.currentInfo().topPos();

        int n = 0;
        for (MobEffectInstance mobEffectInstance : this.minecraft.player.getActiveEffects()) {

            Component effectName = this.getEffectName(mobEffectInstance);
            Component durationText = MobEffectUtil.formatDuration(mobEffectInstance, 1.0F, this.minecraft.level.tickRateManager().tickrate());

            int nameWidth = 32 + this.minecraft.font.width(effectName) + 7;
            int durationWidth = 32 + this.minecraft.font.width(durationText) + 7;
            int effectWidth = Math.min(m, Math.max(nameWidth, durationWidth));

            OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                    Identifier.withDefaultNamespace("mobeffect_" + mobEffectInstance.getDescriptionId()), i, topPos + n, effectWidth, 32
            ));
            n += j;
        }


    }

}
