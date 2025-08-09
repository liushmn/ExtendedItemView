package de.crafty.eiv.common.mixin.client.multiplayer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.network.EivNetworkManager;
import de.crafty.eiv.common.recipe.ClientRecipeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.Connection;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener extends ClientCommonPacketListenerImpl implements ClientGamePacketListener, TickablePacketListener {

//    @Shadow protected abstract ParseResults<SharedSuggestionProvider> parseCommand(String p_249982_);

    @Shadow
    private CommandDispatcher<SharedSuggestionProvider> commands;

    protected MixinClientPacketListener(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void requestRecipes(ClientboundLoginPacket clientboundLoginPacket, CallbackInfo ci) {
        ClientRecipeManager.INSTANCE.requestServerEivData();
    }


    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onEivPayloadReceived(CustomPacketPayload payload, CallbackInfo ci) {

        ResourceLocation payloadId = payload.type().id();

        EivNetworkManager.INSTANCE.getClientbound().forEach((resourceLocation, typeAndCodec) -> {

            if (!payloadId.equals(resourceLocation))
                return;

            if (EivNetworkManager.INSTANCE.clientPayloadHandlers().containsKey(payloadId)) {
                EivNetworkManager.INSTANCE.clientPayloadHandlers().get(payloadId).handle(new EivNetworkManager.ClientContext(this.minecraft), EivNetworkManager.INSTANCE.castPayload(payload));
            } else
                CommonEIV.LOGGER.error("Cannot resolve payload handler for id: {}", payloadId);

            ci.cancel();
        });

    }
}
