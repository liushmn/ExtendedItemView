package de.crafty.eiv.common.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public interface IEivNetworkManager {


    void sendPacket(ServerPlayer player, CustomPacketPayload payload);

    void sendPacketToServer(CustomPacketPayload payload);

    void registerPayloads();

    void registerServerHandlers();

    void registerClientHandlers();
}
