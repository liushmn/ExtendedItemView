package de.crafty.eiv.common.mixin.client.gui.components;

import de.crafty.eiv.common.embeddings.ChatEmbedding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatComponent.class)
public abstract class MixinChatComponent {


    @Shadow(remap = false)
    private int chatScrollbarPos;

    @Shadow(remap = false)
    @Final
    private List<GuiMessage.Line> trimmedMessages;


    @Shadow(remap = false)
    protected abstract void addMessageToDisplayQueue(GuiMessage p_338816_);

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void syncMessages(Minecraft minecraft, CallbackInfo ci) {
        ChatEmbedding.setSyncedMessageLines(this.trimmedMessages);
    }


    @Inject(method = "clearMessages", at = @At("HEAD"), remap = false)
    private void clearEmbedCacheAndQueue(boolean bl, CallbackInfo ci) {
        ChatEmbedding.queue().clear();
        ChatEmbedding.clearCache();
        ChatEmbedding.stored().clear();
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V", at = @At("TAIL"), remap = false)
    private void injectEmbeddings(ChatComponent.ChatGraphicsAccess graphics, int screenHeight, int ticks, ChatComponent.DisplayMode displayMode, CallbackInfo ci) {

        ChatEmbedding.setSyncedChatScrollbarPos(this.chatScrollbarPos);
        ChatEmbedding.setSyncedMessageCount(this.trimmedMessages.size());

    }

    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;pose()Lorg/joml/Matrix3x2fStack;", ordinal = 1), remap = false)
    private void renderEmbeddings(GuiGraphicsExtractor graphics, Font font, int ticks, int mouseX, int mouseY, ChatComponent.DisplayMode displayMode, boolean changeCursorOnInsertions, CallbackInfo ci) {
        ChatEmbedding.renderEmbeddings(graphics, mouseX, mouseY, ticks, changeCursorOnInsertions);
    }


    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void tickEmbeddings(CallbackInfo ci) {
        ChatEmbedding.tickEmbeddings();
    }


    @Inject(method = "addMessageToDisplayQueue", at = @At(value = "INVOKE", target = "Ljava/util/List;addFirst(Ljava/lang/Object;)V", shift = At.Shift.AFTER), remap = false)
    private void updateEmbeddingPositions(CallbackInfo ci) {
        ChatEmbedding.updateChatPositions();
    }


    @Redirect(method = "addMessageToQueue", at = @At(value = "INVOKE", target = "Ljava/util/List;removeLast()Ljava/lang/Object;"), remap = false)
    private <E> E removeEmbedding(List<E> instance) {

        GuiMessage removed = (GuiMessage) instance.removeLast();
        ChatEmbedding.stored().remove(removed);

        return (E) removed;
    }

    @Inject(method = "refreshTrimmedMessages", at = @At("HEAD"), remap = false)
    private void refreshEmbeddings$0(CallbackInfo ci) {
        ChatEmbedding.clearCache();
    }

    @Redirect(method = "refreshTrimmedMessages", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessageToDisplayQueue(Lnet/minecraft/client/multiplayer/chat/GuiMessage;)V"), remap = false)
    private void refreshEmbeddings$1(ChatComponent instance, GuiMessage guiMessage) {
        this.addMessageToDisplayQueue(guiMessage);

        ChatEmbedding embedding = ChatEmbedding.stored().getOrDefault(guiMessage, null);
        if (embedding != null)
            ChatEmbedding.cache(embedding);


    }
}
