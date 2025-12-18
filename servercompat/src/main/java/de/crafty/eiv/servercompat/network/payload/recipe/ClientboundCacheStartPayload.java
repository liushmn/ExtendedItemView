package de.crafty.eiv.servercompat.network.payload.recipe;

import com.google.common.io.ByteArrayDataOutput;
import de.crafty.eiv.servercompat.EivPlugin;
import de.crafty.eiv.servercompat.network.payload.IEivCompatPacketPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

public record ClientboundCacheStartPayload(int types) implements IEivCompatPacketPayload {


    @Override
    public Identifier getId() {
        return Identifier.fromNamespaceAndPath(EivPlugin.PLUGIN_ID, "cache_start");
    }


    @Override
    public void write(CompoundTag tag) {
        tag.putInt("types", types);
    }
}
