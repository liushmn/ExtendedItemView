package de.crafty.eiv.common.network.payload;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public interface ICustomEivPayload {


    void writeTag(CompoundTag tag);

    void readTag(CompoundTag tag);


    ResourceLocation getIdentifier();


}
