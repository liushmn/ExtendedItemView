package de.crafty.eiv.fabric.network;

import de.crafty.eiv.common.network.IEivNetworkManager;
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
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class FabricNetworkManager implements IEivNetworkManager {

    @Override
    public void sendPacket(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public void sendPacketToServer(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    @Override
    public void registerPayloads() {
        PayloadTypeRegistry.playC2S().register(ServerboundRequestRecipesPayload.TYPE, ServerboundRequestRecipesPayload.STREAM_CODEC);


        //Vanilla-like
        PayloadTypeRegistry.playS2C().register(ClientboundVanillaLikeRecipeUpdatePayload.TYPE, ClientboundVanillaLikeRecipeUpdatePayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundVanillaLikeTypeUpdateStartPayload.TYPE, ClientboundVanillaLikeTypeUpdateStartPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundVanillaLikeTypeUpdatePayload.TYPE, ClientboundVanillaLikeTypeUpdatePayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundVanillaLikeTypeUpdateEndPayload.TYPE, ClientboundVanillaLikeTypeUpdateEndPayload.STREAM_CODEC);


        //Mod
        PayloadTypeRegistry.playS2C().register(ClientboundModRecipeUpdatePayload.TYPE, ClientboundModRecipeUpdatePayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundModTypeUpdateStartPayload.TYPE, ClientboundModTypeUpdateStartPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundModTypeUpdatePayload.TYPE, ClientboundModTypeUpdatePayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundModTypeUpdateEndPayload.TYPE, ClientboundModTypeUpdateEndPayload.STREAM_CODEC);

        PayloadTypeRegistry.playS2C().register(ClientboundGeneralUpdateStartedPayload.TYPE, ClientboundGeneralUpdateStartedPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundAllUpdatesFinishedPayload.TYPE, ClientboundAllUpdatesFinishedPayload.STREAM_CODEC);

        //Transfer
        PayloadTypeRegistry.playC2S().register(ServerboundTransferPayload.TYPE, ServerboundTransferPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundUpdateTransferCachePayload.TYPE, ClientboundUpdateTransferCachePayload.STREAM_CODEC);
    }

    @Override
    public void registerServerHandlers() {
        ServerPlayNetworking.registerGlobalReceiver(ServerboundRequestRecipesPayload.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                ServerRecipeManager.INSTANCE.informAboutAllRecipes(context.player());
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(ServerboundTransferPayload.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                ServerRecipeManager.INSTANCE.performRecipeTransfer(context.player(), payload.transferMap(), payload.usedPlayerSlots());
            });
        });
    }

    @Override
    public void registerClientHandlers() {

        registerVanillaHandlers();
        registerModHandlers();

        ClientPlayNetworking.registerGlobalReceiver(ClientboundGeneralUpdateStartedPayload.TYPE, (payload, context) -> {
            context.client().execute(ClientRecipeManager.INSTANCE::startUpdate);
        });

        ClientPlayNetworking.registerGlobalReceiver(ClientboundAllUpdatesFinishedPayload.TYPE, (payload, context) -> {
            context.client().execute(ClientRecipeManager.INSTANCE::processRecipes);
        });

        ClientPlayNetworking.registerGlobalReceiver(ClientboundUpdateTransferCachePayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                if(context.client().screen instanceof RecipeViewScreen viewScreen)
                    viewScreen.getMenu().updateTransferCache();
            });
        });

    }


    private static void registerVanillaHandlers() {

        ClientPlayNetworking.registerGlobalReceiver(ClientboundVanillaLikeRecipeUpdatePayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                VanillaRecipeCache.INSTANCE.vanillaCacheStartReceived(payload.types());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ClientboundVanillaLikeTypeUpdateStartPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                VanillaRecipeCache.INSTANCE.startVanillaCaching(payload.recipeType(), payload.recipeAmount());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ClientboundVanillaLikeTypeUpdatePayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                VanillaRecipeCache.INSTANCE.cacheVanillaLikeRecipe(payload.recipe());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ClientboundVanillaLikeTypeUpdateEndPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                VanillaRecipeCache.INSTANCE.endVanillaCaching(payload.recipeType());
            });
        });

    }

    private static void registerModHandlers() {

        ClientPlayNetworking.registerGlobalReceiver(ClientboundModRecipeUpdatePayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                ModRecipeCache.INSTANCE.modCacheStartReceived(payload.types());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ClientboundModTypeUpdateStartPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                ModRecipeCache.INSTANCE.startModCaching(payload.recipeType(), payload.amount());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ClientboundModTypeUpdatePayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                ModRecipeCache.INSTANCE.cacheModRecipe(payload.entry());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ClientboundModTypeUpdateEndPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                ModRecipeCache.INSTANCE.endModCaching(payload.recipeType());
            });
        });

    }
}
