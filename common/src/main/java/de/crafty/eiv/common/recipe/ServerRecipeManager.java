package de.crafty.eiv.common.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.IEivServerModRecipe;
import de.crafty.eiv.common.api.recipe.ItemViewRecipes;
import de.crafty.eiv.common.api.recipe.ModRecipeType;
import de.crafty.eiv.common.network.payload.ClientboundAllUpdatesFinishedPayload;
import de.crafty.eiv.common.network.payload.ClientboundGeneralUpdateStartedPayload;
import de.crafty.eiv.common.network.payload.mod.ClientboundModRecipeUpdatePayload;
import de.crafty.eiv.common.network.payload.mod.ClientboundModTypeUpdateEndPayload;
import de.crafty.eiv.common.network.payload.mod.ClientboundModTypeUpdatePayload;
import de.crafty.eiv.common.network.payload.mod.ClientboundModTypeUpdateStartPayload;
import de.crafty.eiv.common.network.payload.vanillalike.ClientboundVanillaLikeTypeUpdateEndPayload;
import de.crafty.eiv.common.network.payload.vanillalike.ClientboundVanillaLikeTypeUpdatePayload;
import de.crafty.eiv.common.network.payload.vanillalike.ClientboundVanillaLikeTypeUpdateStartPayload;
import de.crafty.eiv.common.network.payload.vanillalike.ClientboundVanillaLikeRecipeUpdatePayload;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.*;

//TODO block incoming requests while update sending
public class ServerRecipeManager {

    public static final ServerRecipeManager INSTANCE = new ServerRecipeManager();

    private static final HashMap<RecipeType<?>, List<VanillaRecipeEntry>> VANILLA_LIKE_RECIPES = new LinkedHashMap<>();
    private static final HashMap<ModRecipeType<?>, List<ModRecipeEntry>> MOD_RECIPES = new LinkedHashMap<>();

    private static final HashMap<TagKey<Item>, List<Item>> TAGS = new HashMap<>();

    private MinecraftServer server;

    private ServerRecipeManager() {

    }

    public void setServer(MinecraftServer server) {
        this.server = server;
        this.broadcastAllRecipes();
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public void reload(RecipeManager recipeManager) {
        CommonEIV.LOGGER.info("Reloading all Recipes...");


        this.reloadVanillaLikeRecipes(recipeManager);
        this.reloadModRecipes();

        this.broadcastAllRecipes();

    }

    public void reloadAndSendModOnly() {

        this.reloadModRecipes();
        this.broadcastModRecipes();
    }

    public void reloadAndSendVanillaOnly() {
        if (this.server == null) return;

        this.reloadVanillaLikeRecipes(this.server.getRecipeManager());
        this.broadcastVanillaRecipes();
    }

    private void reloadVanillaLikeRecipes(RecipeManager recipeManager) {
        VANILLA_LIKE_RECIPES.clear();

        for (RecipeHolder<?> recipe : recipeManager.getRecipes()) {
            List<VanillaRecipeEntry> list = VANILLA_LIKE_RECIPES.getOrDefault(recipe.value().getType(), new ArrayList<>());
            list.add(new VanillaRecipeEntry(recipe.id().location(), recipe.value()));
            VANILLA_LIKE_RECIPES.put(recipe.value().getType(), list);
        }
    }

    private void reloadModRecipes() {
        MOD_RECIPES.clear();

        List<IEivServerModRecipe> serverRecipes = new ArrayList<>();
        ItemViewRecipes.INSTANCE.getModRecipeProviders().forEach(serverModRecipeProvider -> {
            serverModRecipeProvider.provide(serverRecipes);
        });

        serverRecipes.forEach(iEivServerModRecipe -> {

            ResourceLocation typeId = iEivServerModRecipe.getRecipeType().getId();
            List<ModRecipeEntry> list = MOD_RECIPES.getOrDefault(iEivServerModRecipe.getRecipeType(), new ArrayList<>());
            list.add(new ModRecipeEntry(ResourceLocation.fromNamespaceAndPath(typeId.getNamespace(), typeId.getPath() + "/" + UUID.randomUUID()), iEivServerModRecipe));
            MOD_RECIPES.put(iEivServerModRecipe.getRecipeType(), list);
        });
    }

    private void broadcastAllRecipes() {
        if (this.server == null) {
            return;
        }
        CommonEIV.LOGGER.info("Broadcasting all recipes...");

        this.server.getPlayerList().getPlayers().forEach(this::informAboutAllRecipes);
    }

    private void broadcastVanillaRecipes() {
        if (this.server == null) {
            return;
        }
        CommonEIV.LOGGER.info("Broadcasting vanilla-like recipes...");

        this.server.getPlayerList().getPlayers().forEach(serverPlayer -> {
            CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundGeneralUpdateStartedPayload());
            this.informAboutVanillaLikeRecipes(serverPlayer);
            CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundAllUpdatesFinishedPayload());
        });
    }

    //TODO make possible to reload by modid
    private void broadcastModRecipes() {
        if (this.server == null) {
            return;
        }
        CommonEIV.LOGGER.info("Broadcasting Mod recipes...");

        this.server.getPlayerList().getPlayers().forEach(serverPlayer -> {
            CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundGeneralUpdateStartedPayload());
            this.informAboutModRecipes(serverPlayer);
            CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundAllUpdatesFinishedPayload());
        });
    }


    public void informAboutAllRecipes(ServerPlayer serverPlayer) {
        System.out.println("I've got something! Wooow");
        if (VANILLA_LIKE_RECIPES.isEmpty() && MOD_RECIPES.isEmpty())
            return;

        CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundGeneralUpdateStartedPayload());
        this.informAboutVanillaLikeRecipes(serverPlayer);
        this.informAboutModRecipes(serverPlayer);
        CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundAllUpdatesFinishedPayload());

    }

    private void informAboutVanillaLikeRecipes(ServerPlayer serverPlayer) {
        if (VANILLA_LIKE_RECIPES.isEmpty())
            return;

        CommonEIV.LOGGER.info("Informing {} about {} vanilla-like recipe types", serverPlayer.getName(), VANILLA_LIKE_RECIPES.size());
        CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundVanillaLikeRecipeUpdatePayload(VANILLA_LIKE_RECIPES.size()));
        VANILLA_LIKE_RECIPES.forEach((recipeType, recipes) -> {
            CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundVanillaLikeTypeUpdateStartPayload(recipeType, recipes.size()));
            recipes.forEach(recipe -> {
                CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundVanillaLikeTypeUpdatePayload(recipe));
            });
            CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundVanillaLikeTypeUpdateEndPayload(recipeType));
        });

    }

    private void informAboutModRecipes(ServerPlayer serverPlayer) {
        if (MOD_RECIPES.isEmpty())
            return;

        System.out.println(this.getServer() == null);

        CommonEIV.LOGGER.info("Informing {} about {} mod recipe types", serverPlayer.getName(), MOD_RECIPES.size());
        CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundModRecipeUpdatePayload(MOD_RECIPES.size()));
        MOD_RECIPES.forEach((type, entries) -> {
            CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundModTypeUpdateStartPayload(type, entries.size()));
            entries.forEach(recipe -> {
                CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundModTypeUpdatePayload(recipe));
            });
            CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundModTypeUpdateEndPayload(type));
        });
    }


    public record VanillaRecipeEntry(ResourceLocation id, Recipe<?> recipe) {

        public static final StreamCodec<RegistryFriendlyByteBuf, VanillaRecipeEntry> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                recipeEntry -> recipeEntry.id().toString(),
                Recipe.STREAM_CODEC,
                VanillaRecipeEntry::recipe,
                (s, r) -> new VanillaRecipeEntry(ResourceLocation.tryParse(s), r)
        );

    }

    public record ModRecipeEntry(ResourceLocation modRecipeId, IEivServerModRecipe recipe) {

        public static final StreamCodec<FriendlyByteBuf, ModRecipeEntry> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                entry -> entry.modRecipeId().toString(),
                ByteBufCodecs.COMPOUND_TAG,
                ModRecipeEntry::createFullTag,
                (s, compoundTag) -> new ModRecipeEntry(ResourceLocation.tryParse(s), ModRecipeEntry.fromTag(compoundTag))
        );


        private CompoundTag createFullTag() {
            CompoundTag tag = new CompoundTag();
            tag.putString("recipeType", this.recipe().getRecipeType().getId().toString());
            CompoundTag dataTag = new CompoundTag();
            this.recipe().writeToTag(dataTag);
            tag.put("recipeData", dataTag);
            return tag;
        }

        private static IEivServerModRecipe fromTag(CompoundTag tag) {
            if (!tag.contains("recipeType"))
                return null;

            ModRecipeType<?> recipeType = ModRecipeType.byId(ResourceLocation.parse(tag.getString("recipeType").orElseThrow()));
            if (recipeType == null)
                return null;

            IEivServerModRecipe modRecipe = recipeType.getEmptyConstructor().construct();
            modRecipe.loadFromTag(tag.getCompound("recipeData").orElseGet(CompoundTag::new));
            return modRecipe;
        }
    }


    //Transfer
    public void performRecipeTransfer(ServerPlayer player, HashMap<Integer, Integer> transferMap, HashMap<Integer, HashMap<Integer, ItemStack>> usedPlayerSlots) {

        if (!player.hasContainerOpen())
            return;

        transferMap.forEach((recipeSlot, destSlot) -> {

            HashMap<Integer, ItemStack> usedSlots = usedPlayerSlots.getOrDefault(recipeSlot, new HashMap<>());

            usedSlots.forEach((playerSlot, stack) -> {
                ItemStack currentInDest = player.containerMenu.getSlot(destSlot).getItem();

                if (currentInDest.isEmpty())
                    player.containerMenu.getSlot(destSlot).set(player.getInventory().removeItem(playerSlot, stack.getCount()));
                else
                    player.containerMenu.getSlot(destSlot).set(currentInDest.copyWithCount(currentInDest.getCount() + player.getInventory().removeItem(playerSlot, stack.getCount()).getCount()));

            });

        });

    }

}
