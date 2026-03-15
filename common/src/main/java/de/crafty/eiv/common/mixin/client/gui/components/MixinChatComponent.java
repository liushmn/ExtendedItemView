package de.crafty.eiv.common.mixin.client.gui.components;

import de.crafty.eiv.common.embeddings.ChatEmbedding;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
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


    @Shadow
    private int chatScrollbarPos;

    @Shadow
    @Final
    private List<GuiMessage.Line> trimmedMessages;


    @Shadow
    public abstract void addMessage(Component p_93786_);

    @Shadow
    protected abstract void addMessageToDisplayQueue(GuiMessage p_338816_);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void syncMessages(Minecraft minecraft, CallbackInfo ci) {
        ChatEmbedding.setSyncedMessageLines(this.trimmedMessages);
    }


    @Inject(method = "clearMessages", at = @At("HEAD"))
    private void clearEmbedCacheAndQueue(boolean bl, CallbackInfo ci) {
        ChatEmbedding.queue().clear();
        ChatEmbedding.clearCache();
        ChatEmbedding.stored().clear();
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IIZ)V", at = @At("TAIL"))
    private void injectEmbeddings(ChatComponent.ChatGraphicsAccess chatGraphicsAccess, int mouseX, int mouseY, boolean chatOpen, CallbackInfo ci) {

        ChatEmbedding.setSyncedChatScrollbarPos(this.chatScrollbarPos);
        ChatEmbedding.setSyncedMessageCount(this.trimmedMessages.size());

    }

    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIZZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;pose()Lorg/joml/Matrix3x2fStack;", ordinal = 1))
    private void renderEmbeddings(GuiGraphics guiGraphics, Font font, int currentTime, int mouseX, int mouseY, boolean chatOpen, boolean bl2, CallbackInfo ci) {
        ChatEmbedding.renderEmbeddings(guiGraphics, mouseX, mouseY, currentTime, chatOpen);
    }


    @Inject(method = "tick", at = @At("HEAD"))
    private void tickEmbeddings(CallbackInfo ci) {
        ChatEmbedding.tickEmbeddings();
    }


    @Inject(method = "addMessageToDisplayQueue", at = @At(value = "INVOKE", target = "Ljava/util/List;addFirst(Ljava/lang/Object;)V", shift = At.Shift.AFTER))
    private void updateEmbeddingPositions(CallbackInfo ci) {
        ChatEmbedding.updateChatPositions();
    }


    @Redirect(method = "addMessageToQueue", at = @At(value = "INVOKE", target = "Ljava/util/List;removeLast()Ljava/lang/Object;"))
    private <E> E removeEmbedding(List<E> instance) {

        GuiMessage removed = (GuiMessage) instance.removeLast();
        ChatEmbedding.stored().remove(removed);

        return (E) removed;
    }

    @Inject(method = "refreshTrimmedMessages", at = @At("HEAD"))
    private void refreshEmbeddings$0(CallbackInfo ci) {
        ChatEmbedding.clearCache();
    }

    @Redirect(method = "refreshTrimmedMessages", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessageToDisplayQueue(Lnet/minecraft/client/GuiMessage;)V"))
    private void refreshEmbeddings$1(ChatComponent instance, GuiMessage guiMessage) {
        this.addMessageToDisplayQueue(guiMessage);

        ChatEmbedding embedding = ChatEmbedding.stored().getOrDefault(guiMessage, null);
        if (embedding != null)
            ChatEmbedding.cache(embedding);


    }
}
