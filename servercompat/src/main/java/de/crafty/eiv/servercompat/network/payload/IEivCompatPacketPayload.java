package de.crafty.eiv.servercompat.network.payload;

import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public interface IEivCompatPacketPayload {


    ResourceLocation getId();

    void write(CompoundTag tag);

}
