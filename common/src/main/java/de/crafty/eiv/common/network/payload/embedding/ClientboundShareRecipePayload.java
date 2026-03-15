package de.crafty.eiv.common.network.payload.embedding;

import de.crafty.eiv.common.CommonEIV;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public record ClientboundShareRecipePayload(Identifier recipeId, CompoundTag extraData, UUID sender) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf,  ClientboundShareRecipePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            clientboundShareRecipePayload -> clientboundShareRecipePayload.recipeId().toString(),
            ByteBufCodecs.COMPOUND_TAG,
            ClientboundShareRecipePayload::extraData,
            ByteBufCodecs.STRING_UTF8,
            clientboundShareRecipePayload -> clientboundShareRecipePayload.sender().toString(),
            (id, extraData, sender) -> new ClientboundShareRecipePayload(Identifier.tryParse(id), extraData, UUID.fromString(sender))
    );

    public static final Type<ClientboundShareRecipePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CommonEIV.MODID, "share_recipe_to_client"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
