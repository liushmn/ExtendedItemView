package de.crafty.eiv.common.mixin.client.gui.screens.inventory;

import de.crafty.eiv.common.overlay.BlockingGuiComponent;
import de.crafty.eiv.common.overlay.OverlayManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(EffectRenderingInventoryScreen.class)
public abstract class MixinEffectsInInventory<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {


    public MixinEffectsInInventory(T abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Inject(method = "renderEffects", at = @At("HEAD"))
    private void injectBlocking$0(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci) {

        List<ResourceLocation> effectsToRemove = new ArrayList<>();
        for (BlockingGuiComponent guiBlock : OverlayManager.INSTANCE.allGuiBlockings()) {

            if (!guiBlock.id().getPath().startsWith("mobeffect_"))
                continue;

            String descriptionId = guiBlock.id().getPath().split("_")[1];

            if (Minecraft.getInstance().player.getActiveEffects().stream().noneMatch(mobEffectInstance -> mobEffectInstance.getDescriptionId().equals(descriptionId)))
                effectsToRemove.add(guiBlock.id());

        }

        OverlayManager.INSTANCE.removeGuiBlocking(effectsToRemove::contains, !effectsToRemove.isEmpty());
    }


    @Inject(method = "renderBackgrounds", at = @At("HEAD"))
    private void injectBlocking$1(GuiGraphics guiGraphics, int i, int j, Iterable<MobEffectInstance> iterable, boolean bl, CallbackInfo ci){
        int k = this.topPos;

        for(MobEffectInstance mobEffectInstance : iterable){
            OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                    new ResourceLocation("mobeffect_" + mobEffectInstance.getDescriptionId()), i, k, bl ? 120 : 32, 32
            ));

            k += j;
        }
    }


}
