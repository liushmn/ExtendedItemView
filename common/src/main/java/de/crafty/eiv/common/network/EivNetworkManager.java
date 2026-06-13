package de.crafty.eiv.common.network;

import com.mojang.authlib.GameProfile;
import de.crafty.eiv.common.config.Configs;
import de.crafty.eiv.common.embeddings.ChatEmbedding;
import de.crafty.eiv.common.embeddings.container.RecipeChatEmbedding;
import de.crafty.eiv.common.network.payload.ICustomEivPayload;
import de.crafty.eiv.common.network.payload.embedding.ClientboundShareRecipePayload;
import de.crafty.eiv.common.network.payload.embedding.ServerboundShareRecipePayload;
import de.crafty.eiv.common.network.payload.mode.ServerboundPickCheatmodeItemPayload;
import de.crafty.eiv.common.network.payload.transfer.ClientboundUpdateTransferCachePayload;
import de.crafty.eiv.common.network.payload.transfer.ServerboundTransferPayload;
import de.crafty.eiv.common.recipe.ClientRecipeCache;
import de.crafty.eiv.common.recipe.ServerRecipeManager;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import io.netty.buffer.Unpooled;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.HashMap;
import java.util.Optional;

/**
 * Network Manager for all EIV packets TODO re-implement all relevant packets
 */
public class EivNetworkManager {

    /**
     * The NetworkManager instance of EIV
     */
    public static final EivNetworkManager INSTANCE = new EivNetworkManager().registerPayloads();


    private final HashMap<ResourceLocation, EivClientPayloadHandler<?>> clientPayloadHandlers = new HashMap<>();
    private final HashMap<ResourceLocation, EivServerPayloadHandler<?>> serverPayloadHandlers = new HashMap<>();

    private final HashMap<ResourceLocation, PayloadFactory<?>> clientPayloadFactories = new HashMap<>();
    private final HashMap<ResourceLocation, PayloadFactory<?>> serverPayloadFactories = new HashMap<>();

    public <T extends ICustomEivPayload> void sendPayloadToServer(T payload) {
        if (Minecraft.getInstance().getConnection() == null)
            return;

        CompoundTag tag = new CompoundTag();
        payload.writeTag(tag);

        FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer());
        friendlyByteBuf.writeNbt(tag);

        Minecraft.getInstance().getConnection().send(new ServerboundCustomPayloadPacket(payload.getIdentifier(), friendlyByteBuf));

    }

    public <T extends ICustomEivPayload> void sendPayloadToClient(ServerPlayer player, T payload) {

        CompoundTag tag = new CompoundTag();
        payload.writeTag(tag);
        FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer());
        friendlyByteBuf.writeNbt(tag);

        player.connection.send(new ClientboundCustomPayloadPacket(friendlyByteBuf));
    }

    private EivNetworkManager registerPayloads() {

        this.registerServerbound(ServerboundTransferPayload.ID, (context, payload) -> {
            ServerRecipeManager.INSTANCE.performRecipeTransfer(context.sender(), payload.getTransferMap(), payload.getUsedPlayerSlots());
        }, ServerboundTransferPayload::new);

        //-------------- Item Transfer Cache --------------
        this.registerClientbound(ClientboundUpdateTransferCachePayload.ID, (context, payload) -> {
            if (context.client.screen instanceof RecipeViewScreen viewScreen)
                viewScreen.getMenu().updateTransferCache();

        }, ClientboundUpdateTransferCachePayload::new);

        //-------------- Cheat Mode --------------
        this.registerServerbound(ServerboundPickCheatmodeItemPayload.ID, (context, payload) -> {
            if (context.sender().hasPermissions(3)) {
                context.sender().sendSystemMessage(
                        Component.literal("Cheated x").withStyle(ChatFormatting.GRAY)
                                .append(
                                        Component.literal(String.valueOf(payload.getAmount())).withStyle(ChatFormatting.GOLD)
                                )
                                .append(" ")
                                .append(payload.getStack().getDisplayName().copy())
                );

                context.sender().addItem(payload.getStack().copyWithCount(payload.getAmount()));
                context.sender().level().playSound(
                        null,
                        context.sender().getX(),
                        context.sender().getY(),
                        context.sender().getZ(),
                        SoundEvents.ITEM_PICKUP,
                        SoundSource.PLAYERS,
                        0.2F,
                        ((context.sender().getRandom().nextFloat() - context.sender().getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
                );
            } else
                context.sender().sendSystemMessage(
                        Component.translatable("cheatmode.eiv.denied").withStyle(ChatFormatting.RED)
                );
        }, ServerboundPickCheatmodeItemPayload::new);


        //-------------- Recipe Sharing --------------
        this.registerClientbound(ClientboundShareRecipePayload.ID, (context, payload) -> {

            if(!Configs.CLIENT_SETTINGS.chatEmbeddings())
                return;

            Optional<GameProfile> profile = context.client().getConnection().getListedOnlinePlayers().stream().map(PlayerInfo::getProfile).filter(gameProfile -> gameProfile.getId().equals(payload.getSender())).findFirst();
            profile.ifPresent(gameProfile -> ChatEmbedding.addToChatQueue(new RecipeChatEmbedding(ClientRecipeCache.INSTANCE.getRecipe(payload.getRecipeId()), payload.getExtraData(), gameProfile.getName())));

        }, ClientboundShareRecipePayload::new);


        this.registerServerbound(ServerboundShareRecipePayload.ID, (context, payload) -> {
            context.server().getPlayerList().getPlayers().forEach(player -> {
                this.sendPayloadToClient(player, new ClientboundShareRecipePayload(payload.getRecipeId(), payload.getExtraData(), context.sender().getUUID()));
            });
        }, ServerboundShareRecipePayload::new);

        return this;
    }


    private <T extends ICustomEivPayload> void registerClientbound(ResourceLocation id, EivClientPayloadHandler<T> handler, PayloadFactory<T> factory) {
        this.clientPayloadHandlers.put(id, handler);
        this.clientPayloadFactories.put(id, factory);
    }

    private <T extends ICustomEivPayload> void registerServerbound(ResourceLocation id, EivServerPayloadHandler<T> handler, PayloadFactory<T> factory) {
        this.serverPayloadHandlers.put(id, handler);
        this.serverPayloadFactories.put(id, factory);
    }


    public HashMap<ResourceLocation, EivClientPayloadHandler<?>> getClientPayloadHandlers() {
        return this.clientPayloadHandlers;
    }

    public HashMap<ResourceLocation, EivServerPayloadHandler<?>> getServerPayloadHandlers() {
        return this.serverPayloadHandlers;
    }

    public HashMap<ResourceLocation, PayloadFactory<?>> getClientPayloadFactories() {
        return this.clientPayloadFactories;
    }

    public HashMap<ResourceLocation, PayloadFactory<?>> getServerPayloadFactories() {
        return this.serverPayloadFactories;
    }

    public <T extends ICustomEivPayload> T castPayload(ICustomEivPayload payload) {
        return (T) payload;
    }

    public record ClientContext(Minecraft client) {

    }

    public record ServerContext(MinecraftServer server, ServerPlayer sender) {

    }


    public interface EivClientPayloadHandler<T extends ICustomEivPayload> {

        void handle(ClientContext context, T payload);

    }

    public interface EivServerPayloadHandler<T extends ICustomEivPayload> {

        void handle(ServerContext context, T payload);
    }


    public interface PayloadFactory<T extends ICustomEivPayload> {

        T createEmpty();

    }
}
