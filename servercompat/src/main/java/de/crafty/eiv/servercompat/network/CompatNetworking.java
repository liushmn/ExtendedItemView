package de.crafty.eiv.servercompat.network;

import de.crafty.eiv.servercompat.EivPlugin;
import de.crafty.eiv.servercompat.network.payload.IEivCompatPacketPayload;
import net.minecraft.nbt.CompoundTag;
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

        player.sendPluginMessage(EivPlugin.getInstance(), EivPlugin.MESSAGE_CHANNEL, tag.toString().getBytes(StandardCharsets.UTF_8));

    }


}
