package de.crafty.eiv.common.mixin.client.gui.screens.inventory;

import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.overlay.ItemBookmarkOverlay;
import de.crafty.eiv.common.overlay.ItemViewOverlay;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

        ItemViewOverlay.InventoryPositionInfo info = new ItemViewOverlay.InventoryPositionInfo(this.width, this.height, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);

        //Init screen dimensions before adding searchbar for the first time
        if (ItemViewOverlay.SEARCHBAR == null)
            ItemViewOverlay.INSTANCE.checkForScreenChange((AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this, info);

        this.addSearchbar(info);

    }

    @Inject(method = "renderContents", at = @At("TAIL"))
    private void injectOverlay$1(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (minecraft == null) return;

        ItemViewOverlay.InventoryPositionInfo info = new ItemViewOverlay.InventoryPositionInfo(this.width, this.height, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
        if (ItemViewOverlay.INSTANCE.checkForScreenChange((AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this, info)) {
            if (ItemViewOverlay.SEARCHBAR != null)
                this.removeWidget(ItemViewOverlay.SEARCHBAR);

            this.addSearchbar(info);
        }

        ItemViewOverlay.INSTANCE.render((AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this, info, this.minecraft, guiGraphics, mouseX, mouseY, partialTicks);

        ItemViewOverlay.INSTANCE.renderItemHighlighting((AbstractContainerScreen<?>) (Object) this, guiGraphics, mouseX, mouseY, partialTicks);

    }


    @Unique
    private void addSearchbar(ItemViewOverlay.InventoryPositionInfo info) {
        ItemViewOverlay.INSTANCE.createSearchbarElement(info);
        this.addRenderableWidget(ItemViewOverlay.SEARCHBAR);
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void injectOverlay$2(double mouseX, double mouseY, double scrolledX, double scrolledY, CallbackInfoReturnable<Boolean> cir) {
        if (ItemViewOverlay.INSTANCE.scrollMouse(mouseX, mouseY, scrolledX, scrolledY))
            cir.setReturnValue(true);
    }


    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void injectOverlay$3(int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {
        if (ItemViewOverlay.SEARCHBAR.isFocused())
            cir.setReturnValue(super.keyPressed(i, j, k));

        ItemViewOverlay.INSTANCE.keyPressed(i, j, k);

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
        if (ItemViewOverlay.SEARCHBAR.isHovered() && mouseButton == 1) {
            ItemViewOverlay.SEARCHBAR.setValue("");
            ItemViewOverlay.SEARCHBAR.setFocused(true);
            cir.setReturnValue(true);
        }

        if (mouseButton == 0 && !ItemViewOverlay.SEARCHBAR.isHovered() && ItemViewOverlay.SEARCHBAR.isFocused())
            ItemViewOverlay.SEARCHBAR.setFocused(false);

        ItemViewOverlay.INSTANCE.clickMouse((int) mouseX, (int) mouseY, mouseButton);
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
