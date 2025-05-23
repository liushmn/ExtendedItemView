package de.crafty.eiv.common.network.payload.mod;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.ModRecipeType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ClientboundModTypeUpdateEndPayload(ModRecipeType<?> recipeType) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundModTypeUpdateEndPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            payload -> payload.recipeType().getId().toString(),
            s -> new ClientboundModTypeUpdateEndPayload(ModRecipeType.byId(ResourceLocation.tryParse(s)))
    );

    public static final Type<ClientboundModTypeUpdateEndPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, "mt_update_end"));


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
