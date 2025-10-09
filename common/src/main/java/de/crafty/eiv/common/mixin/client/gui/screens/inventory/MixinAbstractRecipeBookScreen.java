package de.crafty.eiv.common.mixin.client.gui.screens.inventory;

import de.crafty.eiv.common.overlay.AbstractEivOverlay;
import de.crafty.eiv.common.overlay.BlockingGuiComponent;
import de.crafty.eiv.common.overlay.OverlayManager;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractRecipeBookScreen.class)
public abstract class MixinAbstractRecipeBookScreen<T extends RecipeBookMenu> extends AbstractContainerScreen<T> implements RecipeUpdateListener {

    public MixinAbstractRecipeBookScreen(T abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void injectOverlay$0(CallbackInfo ci) {

        AbstractEivOverlay.InventoryPositionInfo info = new AbstractEivOverlay.InventoryPositionInfo((AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this, this.width, this.height, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);

        OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                ResourceLocation.withDefaultNamespace("container"),
                info.leftPos(),
                info.topPos(),
                info.imageWidth(),
                info.imageHeight()
        ));

        OverlayManager.INSTANCE.checkForScreenChange(info);
        OverlayManager.INSTANCE.updateOverlaysAndWidgets();
        this.updateWidgets();

    }


    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void injectOverlay$1(int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {
        if(OverlayManager.INSTANCE.isWidgetFocused())
            cir.setReturnValue(super.keyPressed(i, j, k));
    }


    @Unique
    private void updateWidgets() {
        OverlayManager.INSTANCE.oldWidgets().forEach((this::removeWidget));
        OverlayManager.INSTANCE.oldWidgets().clear();

        OverlayManager.INSTANCE.screenContextMap().forEach((abstractEivOverlay, screenContext) -> {
            screenContext.renderables().forEach(eventListener -> this.addRenderableWidget((GuiEventListener & Renderable & NarratableEntry) eventListener));
            screenContext.nonRenderables().forEach(eventListener -> this.addWidget((GuiEventListener & NarratableEntry) eventListener));
        });

        OverlayManager.INSTANCE.setQueuedWidgetUpdate(false);

    }

}
