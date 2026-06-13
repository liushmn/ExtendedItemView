package de.crafty.eiv.servercompat.network.payload.recipe;

import de.crafty.eiv.servercompat.EivPlugin;
import de.crafty.eiv.servercompat.api.recipe.EivCompatRecipeType;
import de.crafty.eiv.servercompat.network.payload.IEivCompatPacketPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record ClientboundTypeUpdateEndPayload(EivCompatRecipeType<?> recipeType) implements IEivCompatPacketPayload {


    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(EivPlugin.PLUGIN_ID, "type_update_end");
    }

    @Override
    public void write(CompoundTag tag) {
        tag.putString("recipeType", EivCompatRecipeType.idFromType(recipeType).toString());
    }
}