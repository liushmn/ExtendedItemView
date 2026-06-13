package de.crafty.eiv.common.mixin.client.gui.screens;

import de.crafty.eiv.common.embeddings.ChatEmbedding;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class MixinChatScreen extends Screen {

    protected MixinChatScreen(Component component) {
        super(component);
    }


    @Inject(method = "mouseClicked", at = @At("RETURN"))
    private void clickEmbeddings(double mouseX, double mouseY, int mouseButton, CallbackInfoReturnable<Boolean> cir){
        ChatEmbedding.onMouseClicked((int) mouseX, (int) mouseY, mouseButton);
    }

    @Inject(method = "mouseScrolled", at = @At("RETURN"))
    private void scrollEmbeddings(double mouseX, double mouseY, double scrolledY, CallbackInfoReturnable<Boolean> cir){
        ChatEmbedding.onMouseScrolled(mouseX, mouseY, scrolledY);
    }

    @Inject(method = "keyPressed", at = @At("RETURN"))
    private void keyPressEmbeddings(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir){
        ChatEmbedding.onKeyPressed(keyCode, scanCode, modifiers);
    }



}
