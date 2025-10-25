package de.crafty.eiv.servercompat.network;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.crafty.eiv.servercompat.EivPlugin;
import de.crafty.eiv.servercompat.network.payload.IEivCompatPacketPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;

public class CompatNetworking {

    public static final CompatNetworking INSTANCE = new CompatNetworking();


    public void sendPayload(Player player, IEivCompatPacketPayload payload) {

        CompoundTag tag = new CompoundTag();
        tag.putString("payloadType", payload.getId().toString());

        CompoundTag payloadData = new CompoundTag();
        payload.write(payloadData);
        tag.put("payloadData", payloadData);

        ((CraftPlayer) player).getHandle().connection.send(new ClientboundCustomPayloadPacket(new DiscardedPayload(ResourceLocation.parse(EivPlugin.MESSAGE_CHANNEL), tag.toString().getBytes(StandardCharsets.UTF_8))));

    }


}
