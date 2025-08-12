package de.crafty.eiv.common.mixin.client.gui.screens.inventory;

import de.crafty.eiv.common.overlay.ItemViewOverlay;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractRecipeBookScreen.class)
public abstract class MixinAbstractRecipeBookScreen <T extends RecipeBookMenu>
        extends AbstractContainerScreen<T>
        implements RecipeUpdateListener {
    public MixinAbstractRecipeBookScreen(T abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (minecraft == null) return;

        ItemViewOverlay.InventoryPositionInfo info = new ItemViewOverlay.InventoryPositionInfo(this.width, this.height, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
        if (ItemViewOverlay.INSTANCE.checkForScreenChange((AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this, info)) {
            if (ItemViewOverlay.SEARCHBAR != null)
                this.removeWidget(ItemViewOverlay.SEARCHBAR);

            this.addSearchbar(info);
        }

        ItemViewOverlay.INSTANCE.render((AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this, info, this.minecraft, guiGraphics, mouseX, mouseY, partialTicks);

    }

    @Inject(method = "render", at =@At("TAIL"))
    public void renderEnd(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        ItemViewOverlay.INSTANCE.renderItemHighlighting((AbstractContainerScreen<?>) (Object) this, guiGraphics, mouseX, mouseY, partialTicks);

    }

    @Unique
    private void addSearchbar(ItemViewOverlay.InventoryPositionInfo info) {
        int boxWidth = Math.min(100, info.screenWidth() - ItemViewOverlay.INSTANCE.getOverlayStartX() - 4);

        ItemViewOverlay.SEARCHBAR = new EditBox(font, this.width - ItemViewOverlay.INSTANCE.getWidth() / 2 - boxWidth / 2, this.height - 22, boxWidth, 20, Component.literal("moin"));
        ItemViewOverlay.SEARCHBAR.setMaxLength(32);
        ItemViewOverlay.SEARCHBAR.setValue(ItemViewOverlay.INSTANCE.getCurrentQuery());
        ItemViewOverlay.SEARCHBAR.setResponder(ItemViewOverlay.INSTANCE::updateQuery);

        ItemViewOverlay.SEARCHBAR.visible = ItemViewOverlay.INSTANCE.isEnabled();
        this.addRenderableWidget(ItemViewOverlay.SEARCHBAR);
    }
}