package de.crafty.eiv.common.network.payload.transfer;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.network.payload.ICustomEivPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class ClientboundUpdateTransferCachePayload implements ICustomEivPayload {


    public static final ResourceLocation ID = new ResourceLocation(CommonEIV.MODID, "update_transfer_cache");


    @Override
    public void writeTag(CompoundTag tag) {
    }

    @Override
    public void readTag(CompoundTag tag) {
    }

    @Override
    public ResourceLocation getIdentifier() {
        return ID;
    }
}
