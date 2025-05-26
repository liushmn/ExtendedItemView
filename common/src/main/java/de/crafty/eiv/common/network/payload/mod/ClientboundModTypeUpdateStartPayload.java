package de.crafty.eiv.common.network.payload.mod;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.ModRecipeType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ClientboundModTypeUpdateStartPayload(ModRecipeType<?> recipeType, int amount) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundModTypeUpdateStartPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            payload -> payload.recipeType().getId().toString(),
            ByteBufCodecs.INT,
            ClientboundModTypeUpdateStartPayload::amount,
            (s, integer) -> new ClientboundModTypeUpdateStartPayload(ModRecipeType.byId(ResourceLocation.tryParse(s)), integer)
    );

    public static final Type<ClientboundModTypeUpdateStartPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, "mt_update_start"));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
