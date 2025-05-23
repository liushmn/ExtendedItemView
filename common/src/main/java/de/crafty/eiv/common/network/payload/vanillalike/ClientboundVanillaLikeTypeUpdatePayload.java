package de.crafty.eiv.common.network.payload.vanillalike;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.recipe.ServerRecipeManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ClientboundVanillaLikeTypeUpdatePayload(ServerRecipeManager.VanillaRecipeEntry recipe) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundVanillaLikeTypeUpdatePayload> STREAM_CODEC = StreamCodec.composite(
            ServerRecipeManager.VanillaRecipeEntry.STREAM_CODEC, ClientboundVanillaLikeTypeUpdatePayload::recipe,
            ClientboundVanillaLikeTypeUpdatePayload::new);

    public static final Type<ClientboundVanillaLikeTypeUpdatePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, "vlt_update"));



    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
