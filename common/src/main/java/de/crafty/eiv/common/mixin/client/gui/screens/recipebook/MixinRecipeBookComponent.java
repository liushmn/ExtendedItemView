package de.crafty.eiv.common.mixin.client.gui.screens.recipebook;

import de.crafty.eiv.common.overlay.BlockingGuiComponent;
import de.crafty.eiv.common.overlay.OverlayManager;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RecipeBookComponent.class)
public abstract class MixinRecipeBookComponent {


    @Shadow
    private boolean visible;

    @Shadow
    @Final
    private List<RecipeBookTabButton> tabButtons;

    @Shadow
    private int width;

    @Shadow
    private int xOffset;

    @Shadow
    private int height;

    @Inject(method = "init", at = @At("TAIL"))
    private void injectBlocking$0(CallbackInfo ci) {
        this.setGuiBlocking();
    }

    @Inject(method = "toggleVisibility", at = @At("TAIL"))
    private void injectBlocking$1(CallbackInfo ci) {
        this.setGuiBlocking();
    }


    //We do not need to update the slots because the inventory image position also changes (this causes an update)

    @Unique
    private void setGuiBlocking() {


        if (!this.visible) {
            OverlayManager.INSTANCE.removeGuiBlocking(ResourceLocation -> ResourceLocation.getPath().startsWith("recipetabbutton_"), false);
            OverlayManager.INSTANCE.removeGuiBlocking(new ResourceLocation("recipebook"), false);
            return;
        }

        //Width and height hardcoded
        OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                new ResourceLocation("recipebook"),
                (this.width - 147) / 2 - this.xOffset - 30,
                (this.height - 166) / 2 + 3,
                147,
                166
        ));


    }

    @Inject(method = "updateTabs", at = @At("TAIL"))
    private void injectBlocking$2(CallbackInfo ci) {

        OverlayManager.INSTANCE.removeGuiBlocking(ResourceLocation -> ResourceLocation.getPath().startsWith("recipetabbutton_"), false);


        for (int i = 0; i < this.tabButtons.size(); i++) {
            RecipeBookTabButton tabButton = this.tabButtons.get(i);

            if (tabButton.visible)
                OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                        new ResourceLocation("recipetabbutton_" + i),
                        tabButton.getX(),
                        tabButton.getY(),
                        tabButton.getWidth(),
                        tabButton.getHeight()
                ));
        }
    }
}
