package de.crafty.eiv.common.mixin.client.gui.screens.inventory;

import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.overlay.AbstractEivOverlay;
import de.crafty.eiv.common.overlay.itemlist.bookmark.ItemBookmarkOverlay;
import de.crafty.eiv.common.overlay.OverlayManager;
import de.crafty.eiv.common.overlay.itemlist.view.ItemViewOverlay;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinAbstractContainerScreen<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {


    @Shadow
    protected int leftPos;

    @Shadow
    protected int topPos;

    @Shadow
    protected int imageWidth;

    @Shadow
    protected int imageHeight;


    @Shadow
    @Nullable
    protected Slot hoveredSlot;

    @Shadow
    public abstract T getMenu();

    protected MixinAbstractContainerScreen(Component component) {
        super(component);
    }


    @Inject(method = "init", at = @At("TAIL"))
    private void injectOverlay$0(CallbackInfo ci) {

        AbstractEivOverlay.InventoryPositionInfo info = new AbstractEivOverlay.InventoryPositionInfo((AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this, this.width, this.height, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);

        OverlayManager.INSTANCE.screenContextMap().forEach((abstractEivOverlay, screenContext) -> {
            screenContext.renderables().forEach(this::removeWidget);
            screenContext.nonRenderables().forEach(this::removeWidget);
        });

        OverlayManager.INSTANCE.checkForScreenChange(info);

        OverlayManager.INSTANCE.screenContextMap().forEach((abstractEivOverlay, screenContext) -> {
            screenContext.renderables().forEach(guiEventListener -> this.addRenderableWidget((GuiEventListener & Renderable & NarratableEntry) guiEventListener));
            screenContext.nonRenderables().forEach(guiEventListener -> this.addWidget((GuiEventListener & NarratableEntry) guiEventListener));
        });


    }


    @Inject(method = "renderContents", at = @At("TAIL"))
    private void injectOverlay$1(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (minecraft == null) return;

        AbstractEivOverlay.InventoryPositionInfo info = new AbstractEivOverlay.InventoryPositionInfo((AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this, this.width, this.height, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);


        HashMap<AbstractEivOverlay, AbstractEivOverlay.ScreenContext> old = new HashMap<>(OverlayManager.INSTANCE.screenContextMap());

        if (OverlayManager.INSTANCE.checkForScreenChange(info)) {
            old.forEach((abstractEivOverlay, screenContext) -> {
                screenContext.renderables().forEach(this::removeWidget);
                screenContext.nonRenderables().forEach(this::removeWidget);
            });

            OverlayManager.INSTANCE.screenContextMap().forEach((abstractEivOverlay, screenContext) -> {
                screenContext.renderables().forEach(guiEventListener -> this.addRenderableWidget((GuiEventListener & Renderable & NarratableEntry) guiEventListener));
                screenContext.nonRenderables().forEach(guiEventListener -> this.addWidget((GuiEventListener & NarratableEntry) guiEventListener));
            });
        }


        OverlayManager.INSTANCE.renderAll(guiGraphics, mouseX, mouseY, partialTicks);

    }


    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void injectOverlay$2(double mouseX, double mouseY, double scrolledX, double scrolledY, CallbackInfoReturnable<Boolean> cir) {
        if (OverlayManager.INSTANCE.scrollMouse(mouseX, mouseY, scrolledX, scrolledY))
            cir.setReturnValue(true);
    }


    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void injectOverlay$3(int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {

        if (OverlayManager.INSTANCE.isOverlayWidgetFocused())
            return;

        if (!((AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this instanceof CreativeModeInventoryScreen) && OverlayManager.INSTANCE.keyPressed(i, j, k))
            cir.setReturnValue(true);

        if (this.hoveredSlot == null)
            return;

        if (CommonEIVClient.USAGE_KEYBIND.matches(i, j) && this.hoveredSlot.hasItem())
            ItemViewOverlay.INSTANCE.openRecipeView(this.hoveredSlot.getItem(), ItemViewOverlay.ItemViewOpenType.INPUT);

        if (CommonEIVClient.RECIPE_KEYBIND.matches(i, j) && this.hoveredSlot.hasItem())
            ItemViewOverlay.INSTANCE.openRecipeView(this.hoveredSlot.getItem(), ItemViewOverlay.ItemViewOpenType.RESULT);

        if (CommonEIVClient.ADD_BOOKMARK_KEYBIND.matches(i, j) && this.hoveredSlot.hasItem()) {
            ItemBookmarkOverlay.INSTANCE.bookmarkItem(this.hoveredSlot.getItem());

        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void injectOverlay$3(double mouseX, double mouseY, int mouseButton, CallbackInfoReturnable<Boolean> cir) {
        if (OverlayManager.INSTANCE.mouseClicked(mouseX, mouseY, mouseButton))
            cir.setReturnValue(true);
    }


    //Optional Slots

    @Inject(method = "renderSlotHighlightBack", at = @At("HEAD"), cancellable = true)
    private void preventFromRender$0(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (this.hoveredSlot != null && !this.hoveredSlot.hasItem() && ((AbstractContainerScreen) (Object) this) instanceof RecipeViewScreen viewScreen && viewScreen.getMenu().isOptionalSlot(this.hoveredSlot.index))
            ci.cancel();
    }

    @Inject(method = "renderSlotHighlightFront", at = @At("HEAD"), cancellable = true)
    private void preventFromRender$1(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (this.hoveredSlot != null && !this.hoveredSlot.hasItem() && ((AbstractContainerScreen) (Object) this) instanceof RecipeViewScreen viewScreen && viewScreen.getMenu().isOptionalSlot(this.hoveredSlot.index))
            ci.cancel();
    }
}
