package de.crafty.eiv.common.mixin.client.multiplayer;

import com.mojang.brigadier.CommandDispatcher;
import de.crafty.eiv.common.api.recipe.ItemView;
import de.crafty.eiv.common.network.EivNetworkManager;
import de.crafty.eiv.common.network.payload.ICustomEivPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener implements TickablePacketListener, ClientGamePacketListener {


    @Shadow
    public CommandDispatcher<SharedSuggestionProvider> commands;

    @Shadow
    @Final
    private static Logger LOGGER;


    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void requestRecipes(ClientboundLoginPacket clientboundLoginPacket, CallbackInfo ci) {
        ItemView.getClientReloadCallbacks().forEach(ItemView.ReloadCallback::onReload);
    }


    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onEivPayloadReceived(ClientboundCustomPayloadPacket packet, CallbackInfo ci) {
        ResourceLocation payloadId = packet.getIdentifier();

        EivNetworkManager.INSTANCE.getClientPayloadHandlers().forEach((id, payloadHandler) -> {

            if (!id.equals(payloadId))
                return;

            CompoundTag data = packet.getData().readNbt();
            if (EivNetworkManager.INSTANCE.getClientPayloadFactories().containsKey(payloadId)) {

                ICustomEivPayload payload = EivNetworkManager.INSTANCE.getClientPayloadFactories().get(payloadId).createEmpty();
                payload.readTag(data);
                payloadHandler.handle(new EivNetworkManager.ClientContext(this.minecraft), EivNetworkManager.INSTANCE.castPayload(payload));

            } else
                LOGGER.error("Cannot resolve payload factory for id: {}", payloadId);


            ci.cancel();
        });

    }
}
