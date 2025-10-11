package de.crafty.eiv.common.mixin.client.gui.screens.inventory;

import de.crafty.eiv.common.overlay.OverlayManager;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class MixinCreativeModeInventoryScreen extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> {


    public MixinCreativeModeInventoryScreen(CreativeModeInventoryScreen.ItemPickerMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void injectSearchBar$0(char c, int i, CallbackInfoReturnable<Boolean> cir) {
        if (OverlayManager.INSTANCE.isTextWidgetFocused() && this.getFocused() instanceof EditBox box && box.charTyped(c, i))
            cir.setReturnValue(true);

    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void injectSearchBar$1(int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {
        if (this.getFocused() != null && this.getFocused().isFocused() && this.getFocused() instanceof EditBox box) {

            //We don't want to affect other mods compat
            if(OverlayManager.INSTANCE.isTextWidgetFocused()) {
                box.keyPressed(i, j, k);

                if ((i != 256 && i != 258))
                    cir.setReturnValue(true);
            }

        }
        else if (OverlayManager.INSTANCE.keyPressed(i, j, k))
            cir.setReturnValue(true);
    }
}
