package de.crafty.eiv.servercompat.network.payload.recipe;

import de.crafty.eiv.servercompat.EivPlugin;
import de.crafty.eiv.servercompat.network.payload.IEivCompatPacketPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

public record ClientboundFinishUpdatesPayload() implements IEivCompatPacketPayload {


    @Override
    public Identifier getId() {
        return Identifier.fromNamespaceAndPath(EivPlugin.PLUGIN_ID, "finish_updates");
    }

    @Override
    public void write(CompoundTag tag) {

    }
}