package de.crafty.eiv.common.network.payload;

import de.crafty.eiv.common.CommonEIV;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ClientboundAllUpdatesFinishedPayload() implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundAllUpdatesFinishedPayload> STREAM_CODEC = CustomPacketPayload.codec(ClientboundAllUpdatesFinishedPayload::write, ClientboundAllUpdatesFinishedPayload::new);
    public static final Type<ClientboundAllUpdatesFinishedPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, "all_upates_finished"));

    public ClientboundAllUpdatesFinishedPayload(FriendlyByteBuf friendlyByteBuf){
        this();
    }

    private void write(FriendlyByteBuf friendlyByteBuf){

    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
