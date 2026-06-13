package de.crafty.eiv.common.mixin.client.gui.components;

import de.crafty.eiv.common.overlay.itemlist.view.ItemViewOverlay;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;

@Mixin(EditBox.class)
public abstract class MixinEditBox extends AbstractWidget {

    public MixinEditBox(int $$0, int $$1, int $$2, int $$3, Component $$4) {
        super($$0, $$1, $$2, $$3, $$4);
    }

    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V", ordinal = 0))
    private void renderFilterMode(GuiGraphics instance, int xStart, int yStart, int XEnd, int yEnd, int color) {
        if(this.getMessage().contains(Component.literal("eiv:searchbar")) && ItemViewOverlay.INSTANCE.isItemFilterMode())
            instance.fill(xStart, yStart, XEnd, yEnd, new Color(203, 155, 73).getRGB());
        else
            instance.fill(xStart, yStart, XEnd, yEnd, color);
    }

}
