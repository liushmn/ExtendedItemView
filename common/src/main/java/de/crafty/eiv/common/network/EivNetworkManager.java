package de.crafty.eiv.common.network;

import de.crafty.eiv.common.network.payload.ClientboundAllUpdatesFinishedPayload;
import de.crafty.eiv.common.network.payload.ClientboundGeneralUpdateStartedPayload;
import de.crafty.eiv.common.network.payload.ServerboundRequestRecipesPayload;
import de.crafty.eiv.common.network.payload.mod.ClientboundModRecipeUpdatePayload;
import de.crafty.eiv.common.network.payload.mod.ClientboundModTypeUpdateEndPayload;
import de.crafty.eiv.common.network.payload.mod.ClientboundModTypeUpdatePayload;
import de.crafty.eiv.common.network.payload.mod.ClientboundModTypeUpdateStartPayload;
import de.crafty.eiv.common.network.payload.transfer.ClientboundUpdateTransferCachePayload;
import de.crafty.eiv.common.network.payload.transfer.ServerboundTransferPayload;
import de.crafty.eiv.common.network.payload.vanillalike.ClientboundVanillaLikeRecipeUpdatePayload;
import de.crafty.eiv.common.network.payload.vanillalike.ClientboundVanillaLikeTypeUpdateEndPayload;
import de.crafty.eiv.common.network.payload.vanillalike.ClientboundVanillaLikeTypeUpdatePayload;
import de.crafty.eiv.common.network.payload.vanillalike.ClientboundVanillaLikeTypeUpdateStartPayload;
import de.crafty.eiv.common.recipe.ClientRecipeManager;
import de.crafty.eiv.common.recipe.ServerRecipeManager;
import de.crafty.eiv.common.recipe.cache.ModRecipeCache;
import de.crafty.eiv.common.recipe.cache.VanillaRecipeCache;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EivNetworkManager {

    public static final EivNetworkManager INSTANCE = new EivNetworkManager().registerPayloads();

    private final HashMap<ResourceLocation, CustomPacketPayload.TypeAndCodec<?, ?>> clientbound;
    private final HashMap<ResourceLocation, CustomPacketPayload.TypeAndCodec<?, ?>> serverbound;

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

    public void sendPacketToServer(CustomPacketPayload payload) {
        if (Minecraft.getInstance().getConnection() != null)
            Minecraft.getInstance().getConnection().send(new ServerboundCustomPayloadPacket(payload));
    }

    public void sendPacket(ServerPlayer player, CustomPacketPayload payload) {
        player.connection.send(new ClientboundCustomPayloadPacket(payload));
    }


    public EivNetworkManager registerPayloads() {

        this.registerServerbound(ServerboundRequestRecipesPayload.TYPE, ServerboundRequestRecipesPayload.STREAM_CODEC, (context, payload) -> {
            ServerRecipeManager.INSTANCE.informAboutAllRecipes(context.sender());
        });


        //Vanilla-like
        this.registerClientbound(ClientboundVanillaLikeRecipeUpdatePayload.TYPE, ClientboundVanillaLikeRecipeUpdatePayload.STREAM_CODEC, (context, payload) -> {
            VanillaRecipeCache.INSTANCE.vanillaCacheStartReceived(payload.types());
        });

        this.registerClientbound(ClientboundVanillaLikeTypeUpdateStartPayload.TYPE, ClientboundVanillaLikeTypeUpdateStartPayload.STREAM_CODEC, (context, payload) -> {
            VanillaRecipeCache.INSTANCE.startVanillaCaching(payload.recipeType(), payload.recipeAmount());
        });
        this.registerClientbound(ClientboundVanillaLikeTypeUpdatePayload.TYPE, ClientboundVanillaLikeTypeUpdatePayload.STREAM_CODEC, (context, payload) -> {
            VanillaRecipeCache.INSTANCE.cacheVanillaLikeRecipe(payload.recipe());
        });
        this.registerClientbound(ClientboundVanillaLikeTypeUpdateEndPayload.TYPE, ClientboundVanillaLikeTypeUpdateEndPayload.STREAM_CODEC, (context, payload) -> {
            VanillaRecipeCache.INSTANCE.endVanillaCaching(payload.recipeType());
        });


        //Mod
        this.registerClientbound(ClientboundModRecipeUpdatePayload.TYPE, ClientboundModRecipeUpdatePayload.STREAM_CODEC, (context, payload) -> {
            ModRecipeCache.INSTANCE.modCacheStartReceived(payload.types());
        });
        this.registerClientbound(ClientboundModTypeUpdateStartPayload.TYPE, ClientboundModTypeUpdateStartPayload.STREAM_CODEC, (context, payload) -> {
            ModRecipeCache.INSTANCE.startModCaching(payload.recipeType(), payload.amount());
        });
        this.registerClientbound(ClientboundModTypeUpdatePayload.TYPE, ClientboundModTypeUpdatePayload.STREAM_CODEC, (context, payload) -> {
            ModRecipeCache.INSTANCE.cacheModRecipe(payload.entry());
        });
        this.registerClientbound(ClientboundModTypeUpdateEndPayload.TYPE, ClientboundModTypeUpdateEndPayload.STREAM_CODEC, (context, payload) -> {
            ModRecipeCache.INSTANCE.endModCaching(payload.recipeType());
        });

        this.registerClientbound(ClientboundGeneralUpdateStartedPayload.TYPE, ClientboundGeneralUpdateStartedPayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.startUpdate();
        });
        this.registerClientbound(ClientboundAllUpdatesFinishedPayload.TYPE, ClientboundAllUpdatesFinishedPayload.STREAM_CODEC, (context, payload) -> {
            ClientRecipeManager.INSTANCE.processRecipes();
        });

        //Transfer
        this.registerServerbound(ServerboundTransferPayload.TYPE, ServerboundTransferPayload.STREAM_CODEC, (context, payload) -> {
            ServerRecipeManager.INSTANCE.performRecipeTransfer(context.sender(), payload.transferMap(), payload.usedPlayerSlots());
        });
        this.registerClientbound(ClientboundUpdateTransferCachePayload.TYPE, ClientboundUpdateTransferCachePayload.STREAM_CODEC, (context, payload) -> {
            if (context.client.screen instanceof RecipeViewScreen viewScreen)
                viewScreen.getMenu().updateTransferCache();
        });

        return this;
    }


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
