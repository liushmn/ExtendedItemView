package de.crafty.eiv.fabric.mixin.client.gui.screens.inventory;

import de.crafty.eiv.common.overlay.OverlayManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinAbstractContainerScreen<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {


    protected MixinAbstractContainerScreen(Component component) {
        super(component);
    }

    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;matches(II)Z", ordinal = 0))
    private boolean injectOverlay$4(KeyMapping instance, int i, int j){
        return OverlayManager.INSTANCE.isOverlayWidgetFocused() && this.getFocused() instanceof EditBox ? false : instance.matches(i, j);
    }


}
