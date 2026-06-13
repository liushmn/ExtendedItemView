package de.crafty.eiv.servercompat.network.payload.recipe;

import com.google.common.io.ByteArrayDataOutput;
import de.crafty.eiv.servercompat.EivPlugin;
import de.crafty.eiv.servercompat.network.payload.IEivCompatPacketPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record ClientboundCacheStartPayload(int types) implements IEivCompatPacketPayload {


    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(EivPlugin.PLUGIN_ID, "cache_start");
    }


    @Override
    public void write(CompoundTag tag) {
        tag.putInt("types", types);
    }
}
