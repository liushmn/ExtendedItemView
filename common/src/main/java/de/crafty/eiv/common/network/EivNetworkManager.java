package de.crafty.eiv.common.network;

import de.crafty.eiv.common.network.payload.ServerboundRequestEivUpdate;
import de.crafty.eiv.common.network.payload.mode.ServerboundPickCheatmodeItemPayload;
import de.crafty.eiv.common.network.payload.recipe.*;
import de.crafty.eiv.common.network.payload.stack.ClientboundFinishStackSensitivesPayload;
import de.crafty.eiv.common.network.payload.stack.ClientboundStackSensitivePayload;
import de.crafty.eiv.common.network.payload.stack.ClientboundStartStackSensitivesPayload;
import de.crafty.eiv.common.network.payload.transfer.ClientboundUpdateTransferCachePayload;
import de.crafty.eiv.common.network.payload.transfer.ServerboundTransferPayload;
import de.crafty.eiv.common.recipe.ClientRecipeManager;
import de.crafty.eiv.common.recipe.ServerRecipeManager;
import de.crafty.eiv.common.recipe.cache.LowEndRecipeCache;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;

/**
 * Network Manager for all EIV packets
 */
public class EivNetworkManager {

    public static final EivNetworkManager INSTANCE = new EivNetworkManager().registerPayloads();

    private final HashMap<ResourceLocation, CustomPacketPayload.TypeAndCodec<?, ?>> clientbound;
    private final HashMap<ResourceLocation, CustomPacketPayload.TypeAndCodec<?, ?>> serverbound;

    /**
     * Payload handlers are used for packet processing on the client and server side
     */
    private final HashMap<ResourceLocation, PayloadHandler<ClientContext, ? extends CustomPacketPayload>> clientPayloadHandlers;
    private final HashMap<ResourceLocation, PayloadHandler<ServerContext, ? extends CustomPacketPayload>> serverPayloadHandlers;

    private EivNetworkManager() {
        this.clientbound = new HashMap<>();
        this.serverbound = new HashMap<>();

        this.clientPayloadHandlers = new HashMap<>();
        this.serverPayloadHandlers = new HashMap<>();
    }

    private <B extends FriendlyByteBuf, T extends CustomPacketPayload> void registerClientbound(CustomPacketPayload.Type<T> type, StreamCodec<B, T> codec, PayloadHandler<ClientContext, T> clientHandler) {
        this.clientbound.put(type.id(), new CustomPacketPayload.TypeAndCodec<>(type, codec));
        this.clientPayloadHandlers.put(type.id(), clientHandler);
    }

    private <B extends FriendlyByteBuf, T extends CustomPacketPayload> void registerServerbound(CustomPacketPayload.Type<T> type, StreamCodec<B, T> codec, PayloadHandler<ServerContext, T> serverHandler) {
        this.serverbound.put(type.id(), new CustomPacketPayload.TypeAndCodec<>(type, codec));
        this.serverPayloadHandlers.put(type.id(), serverHandler);
    }


    public HashMap<ResourceLocation, CustomPacketPayload.TypeAndCodec<?, ?>> getClientbound() {
        return this.clientbound;
    }

    public HashMap<ResourceLocation, CustomPacketPayload.TypeAndCodec<?, ?>> getServerbound() {
        return this.serverbound;
    }

    public HashMap<ResourceLocation, PayloadHandler<ClientContext, ? extends CustomPacketPayload>> clientPayloadHandlers() {
        return this.clientPayloadHandlers;
    }

    public HashMap<ResourceLocation, PayloadHandler<ServerContext, ? extends CustomPacketPayload>> serverPayloadHandlers() {
        return this.serverPayloadHandlers;
    }


    //:D
    public <T extends CustomPacketPayload> T castPayload(CustomPacketPayload payload) {
        return (T) payload;
    }

    /**
     * Send a payload to the server
     *
     * @param payload The payload
     */
    public void sendPacketToServer(CustomPacketPayload payload) {
        if (Minecraft.getInstance().getConnection() != null)
            Minecraft.getInstance().getConnection().send(new ServerboundCustomPayloadPacket(payload));
    }

    /**
     * Send a payload to a player
     *
     * @param player  The player
     * @param payload The payload
     */
    public void sendPacket(ServerPlayer player, CustomPacketPayload payload) {
        player.connection.send(new ClientboundCustomPayloadPacket(payload));
    }


    /**
     * Registers all EIV payloads
     *
     * @return The instance of the NetworkManager
     */
    public EivNetworkManager registerPayloads() {

        this.registerServerbound(ServerboundRequestEivUpdate.TYPE, ServerboundRequestEivUpdate.STREAM_CODEC, (context, payload) -> {
            ServerRecipeManager.INSTANCE.informAboutStackSensitives();
            ServerRecipeManager.INSTANCE.informAboutRecipes(context.sender());
        });


        //Stack-Sensitives
        this.registerClientbound(ClientboundStartStackSensitivesPayload.TYPE, ClientboundStartStackSensitivesPayload.STREAM_CODEC, (context, payload) -> {
            LowEndRecipeCache.INSTANCE.stackSensitiveStartReceived(payload.amount());
        });

        this.registerClientbound(ClientboundStackSensitivePayload.TYPE, ClientboundStackSensitivePayload.STREAM_CODEC, (context, payload) -> {
            LowEndRecipeCache.INSTANCE.stackSensitiveReceived(payload.stackSensitive());
        });

        this.registerClientbound(ClientboundFinishStackSensitivesPayload.TYPE, ClientboundFinishStackSensitivesPayload.STREAM_CODEC, (context, payload) -> {
            LowEndRecipeCache.INSTANCE.stackSensitiveEndReceived();
        });

        /*
         * Enclosing payloads (for update start and end)
         */
        this.registerClientbound(ClientboundStartUpdatesPayload.TYPE, ClientboundStartUpdatesPayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.queueTask(ClientRecipeManager.INSTANCE::startUpdate);
        });

        this.registerClientbound(ClientboundFinishUpdatesPayload.TYPE, ClientboundFinishUpdatesPayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.queueTask(ClientRecipeManager.INSTANCE::processRecipes);
            ClientRecipeManager.INSTANCE.runTasks();
        });

        //Recipes
        this.registerClientbound(ClientboundCacheStartPayload.TYPE, ClientboundCacheStartPayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.queueTask(() -> LowEndRecipeCache.INSTANCE.cacheStartReceived(payload.types()));
        });
        this.registerClientbound(ClientboundTypeUpdateStartPayload.TYPE, ClientboundTypeUpdateStartPayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.queueTask(() -> LowEndRecipeCache.INSTANCE.startCaching(payload.recipeType(), payload.amount()));
        });
        this.registerClientbound(ClientboundTypeUpdatePayload.TYPE, ClientboundTypeUpdatePayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.queueTask(() -> LowEndRecipeCache.INSTANCE.cacheModRecipe(payload.entry()));
        });
        this.registerClientbound(ClientboundTypeUpdateEndPayload.TYPE, ClientboundTypeUpdateEndPayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.queueTask(() -> LowEndRecipeCache.INSTANCE.endCaching(payload.recipeType()));
        });


        //Item-Transfer payloads
        this.registerServerbound(ServerboundTransferPayload.TYPE, ServerboundTransferPayload.STREAM_CODEC, (context, payload) -> {
            ServerRecipeManager.INSTANCE.performRecipeTransfer(context.sender(), payload.transferMap(), payload.usedPlayerSlots());
        });
        this.registerClientbound(ClientboundUpdateTransferCachePayload.TYPE, ClientboundUpdateTransferCachePayload.STREAM_CODEC, (context, payload) -> {
            if (context.client.screen instanceof RecipeViewScreen viewScreen)
                viewScreen.getMenu().updateTransferCache();
        });


        //Cheatmode
        this.registerServerbound(ServerboundPickCheatmodeItemPayload.TYPE, ServerboundPickCheatmodeItemPayload.STREAM_CODEC, (context, payload) -> {

            if (context.server().getPlayerList().isOp(context.sender().getGameProfile())) {
                context.sender().sendSystemMessage(
                        Component.literal("Took x").withStyle(ChatFormatting.GRAY)
                                .append(
                                        Component.literal(String.valueOf(payload.amount())).withStyle(ChatFormatting.GOLD)
                                )
                                .append(" ")
                                .append(payload.stack().getDisplayName().copy())
                );

                context.sender().addItem(payload.stack().copyWithCount(payload.amount()));
            }else
                context.sender().sendSystemMessage(
                        Component.translatable("cheatmode.eiv.denied").withStyle(ChatFormatting.RED)
                );

        });

        return this;
    }


    /**
     * The context where the packet is handled in (either client or server)
     */
    public interface Context {
    }

    public record ClientContext(Minecraft client) implements Context {
    }

    public record ServerContext(MinecraftServer server, ServerPlayer sender) implements Context {
    }

    public interface PayloadHandler<S extends Context, T extends CustomPacketPayload> {

        void handle(S context, T payload);
    }

}
