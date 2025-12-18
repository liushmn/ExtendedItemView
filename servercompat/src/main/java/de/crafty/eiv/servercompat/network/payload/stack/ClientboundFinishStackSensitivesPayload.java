package de.crafty.eiv.servercompat.network.payload.stack;

import de.crafty.eiv.servercompat.EivPlugin;
import de.crafty.eiv.servercompat.network.payload.IEivCompatPacketPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

public record ClientboundFinishStackSensitivesPayload() implements IEivCompatPacketPayload {

    @Override
    public Identifier getId() {
        return Identifier.fromNamespaceAndPath(EivPlugin.PLUGIN_ID, "finish_stack_sensitive");
    }

    @Override
    public void write(CompoundTag tag) {

    }
}
