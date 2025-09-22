package de.crafty.eiv.forge.mixin.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.InputConstants;
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

    protected MixinAbstractContainerScreen(Component p_96550_) {
        super(p_96550_);
    }

    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isActiveAndMatches(Lcom/mojang/blaze3d/platform/InputConstants$Key;)Z"))
    private boolean injectOverlay$4(KeyMapping instance, InputConstants.Key key){

        return OverlayManager.INSTANCE.isOverlayWidgetFocused() && this.getFocused() instanceof EditBox ? false : instance.isActiveAndMatches(key);
    }

}
