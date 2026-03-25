package de.crafty.eiv.servercompat.network.payload.recipe;

import de.crafty.eiv.servercompat.EivPlugin;
import de.crafty.eiv.servercompat.network.payload.IEivCompatPacketPayload;
import de.crafty.eiv.servercompat.recipe.CompatRecipeManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record ClientboundTypeUpdatePayload(CompatRecipeManager.CompatRecipeEntry entry) implements IEivCompatPacketPayload {


    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(EivPlugin.PLUGIN_ID, "recipe_update");
    }

    @Override
    public void write(CompoundTag tag) {
        tag.put("entry", this.entry.createFullTagWithId());
    }
}