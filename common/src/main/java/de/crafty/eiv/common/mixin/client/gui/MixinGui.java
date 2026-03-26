package de.crafty.eiv.common.mixin.client.gui;

import de.crafty.eiv.common.recipe.ClientRecipeManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class MixinGui {


    @Shadow(remap = false) public abstract Font getFont();

    @Inject(method = "extractRenderState", at = @At("RETURN"), remap = false)
    private void renderRecipeProgress(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker, CallbackInfo ci) {
        Font font = this.getFont();
        String statusMsg = ClientRecipeManager.INSTANCE.status().get();

        if(!ClientRecipeManager.INSTANCE.status().isIdle())
            guiGraphicsExtractor.text(font, statusMsg, guiGraphicsExtractor.guiWidth() - font.width(statusMsg) - 2, 2, -1);
    }
}
