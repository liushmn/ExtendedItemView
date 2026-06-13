package de.crafty.eiv.servercompat.network.payload.recipe;

import de.crafty.eiv.servercompat.EivPlugin;
import de.crafty.eiv.servercompat.network.payload.IEivCompatPacketPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record ClientboundStartUpdatesPayload() implements IEivCompatPacketPayload {


    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(EivPlugin.PLUGIN_ID, "start_updates");
    }

    @Override
    public void write(CompoundTag tag) {

    }
}
