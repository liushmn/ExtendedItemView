package de.crafty.eiv.servercompat.network.payload.recipe;

import de.crafty.eiv.servercompat.EivPlugin;
import de.crafty.eiv.servercompat.api.recipe.EivCompatRecipeType;
import de.crafty.eiv.servercompat.network.payload.IEivCompatPacketPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record ClientboundTypeUpdateStartPayload(EivCompatRecipeType<?> recipeType, int amount) implements IEivCompatPacketPayload {


    @Override
    public Identifier getId() {
        return Identifier.fromNamespaceAndPath(EivPlugin.PLUGIN_ID, "type_start");
    }

    @Override
    public void write(CompoundTag tag) {
        tag.putString("recipeType", EivCompatRecipeType.idFromType(recipeType).toString());
        tag.putInt("amount", amount);
    }
}
