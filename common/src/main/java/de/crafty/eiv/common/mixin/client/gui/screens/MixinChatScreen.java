package de.crafty.eiv.common.mixin.client.gui.screens;

import de.crafty.eiv.common.embeddings.ChatEmbedding;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class MixinChatScreen extends Screen {

    protected MixinChatScreen(Component component) {
        super(component);
    }


    @Inject(method = "mouseClicked", at = @At("RETURN"))
    private void clickEmbeddings(MouseButtonEvent mouseButtonEvent, boolean doubleClick, CallbackInfoReturnable<Boolean> cir){
        ChatEmbedding.onMouseClicked(mouseButtonEvent, doubleClick);
    }

    @Inject(method = "mouseScrolled", at = @At("RETURN"))
    private void scrollEmbeddings(double mouseX, double mouseY, double horizontal, double vertical, CallbackInfoReturnable<Boolean> cir){
        ChatEmbedding.onMouseScrolled(mouseX, mouseY, horizontal, vertical);
    }

    @Inject(method = "keyPressed", at = @At("RETURN"))
    private void keyPressEmbeddings(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir){
        ChatEmbedding.onKeyPressed(keyEvent);
    }



}
