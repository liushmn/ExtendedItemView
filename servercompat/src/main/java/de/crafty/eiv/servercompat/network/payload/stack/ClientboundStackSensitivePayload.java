package de.crafty.eiv.servercompat.network.payload.stack;

import de.crafty.eiv.servercompat.EivPlugin;
import de.crafty.eiv.servercompat.api.CompatItemView;
import de.crafty.eiv.servercompat.network.payload.IEivCompatPacketPayload;
import de.crafty.eiv.servercompat.util.EivCompatTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record ClientboundStackSensitivePayload(CompatItemView.StackSensitive stackSensitive) implements IEivCompatPacketPayload {


    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(EivPlugin.PLUGIN_ID, "stack_sensitive");
    }

    @Override
    public void write(CompoundTag tag) {
        tag.put("sensitive", EivCompatTagUtil.encodeItemStackOnServer(this.stackSensitive.stack()));
    }
}
