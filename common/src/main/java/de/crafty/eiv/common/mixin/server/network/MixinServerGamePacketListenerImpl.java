package de.crafty.eiv.common.mixin.server.network;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.network.EivNetworkManager;
import de.crafty.eiv.common.network.payload.ICustomEivPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerGamePacketListenerImpl {


    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onEivPayloadReceived(net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
        ResourceLocation payloadId = packet.getIdentifier();

        EivNetworkManager.INSTANCE.getServerPayloadHandlers().forEach((id, payloadHandler) -> {

            if (!id.equals(payloadId))
                return;

            if (EivNetworkManager.INSTANCE.getServerPayloadFactories().containsKey(id)) {

                ICustomEivPayload payload = EivNetworkManager.INSTANCE.getServerPayloadFactories().get(payloadId).createEmpty();

                this.player.level().getServer().execute(() -> {
                    payloadHandler.handle(new EivNetworkManager.ServerContext(this.player.level().getServer(), this.player), EivNetworkManager.INSTANCE.castPayload(payload));
                });
            } else
                CommonEIV.LOGGER.error("Cannot resolve payload factory for id: {}", payloadId);

            ci.cancel();
        });

    }

}
