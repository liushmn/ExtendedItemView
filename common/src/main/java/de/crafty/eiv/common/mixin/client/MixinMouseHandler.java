package de.crafty.eiv.common.mixin.client;

import de.crafty.eiv.common.overlay.OverlayManager;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MouseHandler.class)
public abstract class MixinMouseHandler {

    @Redirect(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseScrolled(DDD)Z"))
    private boolean injectOverlayScroll(Screen instance, double mouseX, double mouseY, double scrolledY){
        boolean b = instance.mouseScrolled(mouseX, mouseY, scrolledY);
        OverlayManager.INSTANCE.scrollMouse(mouseX, mouseY, scrolledY);
        return b;
    }

}
