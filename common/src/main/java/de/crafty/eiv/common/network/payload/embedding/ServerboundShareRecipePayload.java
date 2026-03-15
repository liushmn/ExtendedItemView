package de.crafty.eiv.common.network.payload.embedding;

import de.crafty.eiv.common.CommonEIV;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public record ServerboundShareRecipePayload(Identifier recipeId, CompoundTag extraData) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundShareRecipePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            serverboundShareRecipePayload -> serverboundShareRecipePayload.recipeId().toString(),
            ByteBufCodecs.COMPOUND_TAG,
            ServerboundShareRecipePayload::extraData,
            (id, extraData) -> new ServerboundShareRecipePayload(Identifier.tryParse(id), extraData)
    );

    public static final Type<ServerboundShareRecipePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CommonEIV.MODID, "share_recipe_to_server"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }



}
