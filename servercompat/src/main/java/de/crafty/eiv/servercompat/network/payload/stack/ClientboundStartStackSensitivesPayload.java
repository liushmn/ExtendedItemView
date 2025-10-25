package de.crafty.eiv.servercompat.network.payload.stack;

import de.crafty.eiv.servercompat.EivPlugin;
import de.crafty.eiv.servercompat.network.payload.IEivCompatPacketPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record ClientboundStartStackSensitivesPayload(int amount) implements IEivCompatPacketPayload {


    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(EivPlugin.PLUGIN_ID, "start_stack_sensitive");
    }

    @Override
    public void write(CompoundTag tag) {
        tag.putInt("amount", amount);
    }
}
