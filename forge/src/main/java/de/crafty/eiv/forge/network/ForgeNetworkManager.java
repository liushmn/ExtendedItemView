package de.crafty.eiv.forge.network;

import de.crafty.eiv.common.CommonEIV;
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
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.*;

public class ForgeNetworkManager implements IEivNetworkManager {


    private static final int PROTOCOL_VERSION = 1;
    public static final SimpleChannel INSTANCE = ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, "main"))
            .networkProtocolVersion(PROTOCOL_VERSION)
            .clientAcceptedVersions((status, version) -> status != Channel.VersionTest.Status.PRESENT || version == PROTOCOL_VERSION)
            .serverAcceptedVersions((status, version) -> status != Channel.VersionTest.Status.PRESENT || version == PROTOCOL_VERSION)
            .simpleChannel();


    @Override
    public void sendPacket(ServerPlayer player, CustomPacketPayload payload) {
        INSTANCE.send(payload, PacketDistributor.PLAYER.with(player));
    }

    @Override
    public void sendPacketToServer(CustomPacketPayload payload) {
        INSTANCE.send(payload, PacketDistributor.SERVER.noArg());
    }

    @Override
    public void registerPayloads() {

        //Serverbound

        INSTANCE.play().serverbound().addMain(ServerboundRequestRecipesPayload.class, ServerboundRequestRecipesPayload.STREAM_CODEC, (payload, context) -> {
            ServerRecipeManager.INSTANCE.informAboutAllRecipes(context.getSender());
        });

        INSTANCE.play().serverbound().addMain(ServerboundTransferPayload.class, ServerboundTransferPayload.STREAM_CODEC, (payload, context) -> {
            ServerRecipeManager.INSTANCE.performRecipeTransfer(context.getSender(), payload.transferMap(), payload.usedPlayerSlots());
        });

        //Clientbound

        ForgeNetworkManager.registerVanillaPackets();
        ForgeNetworkManager.registerModPackets();

        //general update start
        INSTANCE.play().clientbound().addMain(ClientboundGeneralUpdateStartedPayload.class, ClientboundGeneralUpdateStartedPayload.STREAM_CODEC, (payload, context) -> {
            if (FMLEnvironment.dist.isClient())
                ClientRecipeManager.INSTANCE.startUpdate();
        });

        //all updates finished
        INSTANCE.play().clientbound().addMain(ClientboundAllUpdatesFinishedPayload.class, ClientboundAllUpdatesFinishedPayload.STREAM_CODEC, (payload, context) -> {
            if (FMLEnvironment.dist.isClient())
                ClientRecipeManager.INSTANCE.processRecipes();
        });

        //update transfer cache
        INSTANCE.play().clientbound().addMain(ClientboundUpdateTransferCachePayload.class, ClientboundUpdateTransferCachePayload.STREAM_CODEC, (payload, context) -> {
            if (FMLEnvironment.dist.isClient()) {
                if (Minecraft.getInstance().screen instanceof RecipeViewScreen viewScreen)
                    viewScreen.getMenu().updateTransferCache();
            }
        });

        INSTANCE.build();
    }


    private static void registerVanillaPackets() {

        //vanilla recipe update
        INSTANCE.play().clientbound().addMain(ClientboundVanillaLikeRecipeUpdatePayload.class, ClientboundVanillaLikeRecipeUpdatePayload.STREAM_CODEC, (payload, context) -> {
            if (FMLEnvironment.dist.isClient())
                VanillaRecipeCache.INSTANCE.vanillaCacheStartReceived(payload.types());
        });

        //vanilla type update start
        INSTANCE.play().clientbound().addMain(ClientboundVanillaLikeTypeUpdateStartPayload.class, ClientboundVanillaLikeTypeUpdateStartPayload.STREAM_CODEC, (payload, context) -> {
            if (FMLEnvironment.dist.isClient())
                VanillaRecipeCache.INSTANCE.startVanillaCaching(payload.recipeType(), payload.recipeAmount());
        });


        //vanilla type update
        INSTANCE.play().clientbound().addMain(ClientboundVanillaLikeTypeUpdatePayload.class, ClientboundVanillaLikeTypeUpdatePayload.STREAM_CODEC, (payload, context) -> {
            if (FMLEnvironment.dist.isClient())
                VanillaRecipeCache.INSTANCE.cacheVanillaLikeRecipe(payload.recipe());
        });

        //vanilla type update end
        INSTANCE.play().clientbound().addMain(ClientboundVanillaLikeTypeUpdateEndPayload.class, ClientboundVanillaLikeTypeUpdateEndPayload.STREAM_CODEC, (payload, context) -> {
            if (FMLEnvironment.dist.isClient())
                VanillaRecipeCache.INSTANCE.endVanillaCaching(payload.recipeType());
        });
    }

    private static void registerModPackets() {

        //mod recipe update
        INSTANCE.play().clientbound().addMain(ClientboundModRecipeUpdatePayload.class, ClientboundModRecipeUpdatePayload.STREAM_CODEC, (payload, context) -> {
            if (FMLEnvironment.dist.isClient())
                ModRecipeCache.INSTANCE.modCacheStartReceived(payload.types());
        });

        //mod type update start
        INSTANCE.play().clientbound().addMain(ClientboundModTypeUpdateStartPayload.class, ClientboundModTypeUpdateStartPayload.STREAM_CODEC, (payload, context) -> {
            if (FMLEnvironment.dist.isClient())
                ModRecipeCache.INSTANCE.startModCaching(payload.recipeType(), payload.amount());
        });

        //mod type update
        INSTANCE.play().clientbound().addMain(ClientboundModTypeUpdatePayload.class, ClientboundModTypeUpdatePayload.STREAM_CODEC, (payload, context) -> {
            if (FMLEnvironment.dist.isClient())
                ModRecipeCache.INSTANCE.cacheModRecipe(payload.entry());
        });

        //mod type update end
        INSTANCE.play().clientbound().addMain(ClientboundModTypeUpdateEndPayload.class, ClientboundModTypeUpdateEndPayload.STREAM_CODEC, (payload, context) -> {
            if (FMLEnvironment.dist.isClient())
                ModRecipeCache.INSTANCE.endModCaching(payload.recipeType());
        });
    }


    @Override
    public void registerServerHandlers() {
        //Forge doesn't need that
    }

    @Override
    public void registerClientHandlers() {
        //Forge doesn't need that
    }
}
