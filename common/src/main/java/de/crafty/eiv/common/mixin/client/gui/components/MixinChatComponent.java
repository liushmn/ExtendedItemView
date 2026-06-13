package de.crafty.eiv.common.mixin.client.gui.components;

import de.crafty.eiv.common.embeddings.ChatEmbedding;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
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
    protected abstract void addMessage(Component p_240562_, MessageSignature p_241566_, int p_240583_, GuiMessageTag p_240624_, boolean p_240558_);

    @Shadow
    protected abstract boolean isChatHidden();

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

    @Inject(method = "render", at = @At("TAIL"))
    private void injectEmbeddings(GuiGraphics guiGraphics, int i, int j, int k, CallbackInfo ci) {

        ChatEmbedding.setSyncedChatScrollbarPos(this.chatScrollbarPos);
        ChatEmbedding.setSyncedMessageCount(this.trimmedMessages.size());

    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderEmbeddings(GuiGraphics guiGraphics, int mouseX, int mouseY, int currentTime, CallbackInfo ci) {
        ChatEmbedding.renderEmbeddings(guiGraphics, mouseX, mouseY, currentTime, !this.isChatHidden());
    }


    @Inject(method = "tick", at = @At("HEAD"))
    private void tickEmbeddings(CallbackInfo ci) {
        ChatEmbedding.tickEmbeddings();
    }


    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 0, shift = At.Shift.AFTER))
    private void updateEmbeddingPositions(CallbackInfo ci) {
        ChatEmbedding.updateChatPositions();
    }


    @Redirect(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(I)Ljava/lang/Object;", ordinal = 1))
    private <E> E removeEmbedding(List<E> instance, int i) {

        GuiMessage removed = (GuiMessage) instance.remove(i);
        ChatEmbedding.stored().remove(removed);

        return (E) removed;
    }

    @Inject(method = "refreshTrimmedMessage", at = @At("HEAD"))
    private void refreshEmbeddings$0(CallbackInfo ci) {
        ChatEmbedding.clearCache();
    }

    @Redirect(method = "refreshTrimmedMessage", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
    private <E> E refreshEmbeddings$1(List<E> instance, int i) {
        GuiMessage guiMessage = (GuiMessage) instance.get(i);

        ChatEmbedding embedding = ChatEmbedding.stored().getOrDefault(guiMessage, null);
        if (embedding != null)
            ChatEmbedding.cache(embedding);


        return (E) guiMessage;
    }
}
