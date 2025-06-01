package de.crafty.eiv.common.recipe;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.network.payload.recipe.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.*;

//TODO block incoming requests while update sending
public class ServerRecipeManager {

    public static final ServerRecipeManager INSTANCE = new ServerRecipeManager();

    private static final HashMap<EivRecipeType<?>, List<ServerRecipeEntry>> PRESENT_RECIPES = new LinkedHashMap<>();

    private MinecraftServer server;
    private RecipeManager recipeManager;

    private ServerRecipeManager() {

    }

    public void setServer(MinecraftServer server) {
        this.server = server;
        this.reload();
        this.broadcastAllRecipes();
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public void setRecipeManager(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    public RecipeManager getVanillaRecipeManager() {
        return this.recipeManager;
    }

    //Helper functionality
    public <T extends Recipe<?>> List<T> getRecipesForType(RecipeType<T> recipeType) {
        return (List<T>) this.recipeManager.getRecipes().stream().filter(holder -> holder.value().getType().equals(recipeType)).map(RecipeHolder::value).toList();
    }


    public void reload() {
        if(this.server == null || this.recipeManager == null)
            return;

        CommonEIV.LOGGER.info("Reloading all Recipes...");

        this.reloadRecipes();
        this.broadcastAllRecipes();

    }


    private void reloadRecipes() {
        PRESENT_RECIPES.clear();

        List<IEivServerRecipe> serverRecipes = new ArrayList<>();
        ItemViewRecipes.INSTANCE.getRecipeProviders().forEach(serverModRecipeProvider -> {
            List<IEivServerRecipe> recipes = new ArrayList<>();
            serverModRecipeProvider.provide(recipes);
            serverRecipes.addAll(recipes);
        });

        serverRecipes.forEach(iEivServerModRecipe -> {

            ResourceLocation typeId = iEivServerModRecipe.getRecipeType().getId();
            List<ServerRecipeEntry> list = PRESENT_RECIPES.getOrDefault(iEivServerModRecipe.getRecipeType(), new ArrayList<>());
            list.add(new ServerRecipeEntry(ResourceLocation.fromNamespaceAndPath(typeId.getNamespace(), typeId.getPath() + "/" + UUID.randomUUID()), iEivServerModRecipe));
            PRESENT_RECIPES.put(iEivServerModRecipe.getRecipeType(), list);
        });
    }

    //TODO make broadcast by type
    private void broadcastAllRecipes() {
        if (this.server == null) {
            return;
        }
        CommonEIV.LOGGER.info("Broadcasting recipes...");
        this.server.getPlayerList().getPlayers().forEach(this::informAboutRecipes);
    }


    public void informAboutRecipes(ServerPlayer serverPlayer) {
        if (PRESENT_RECIPES.isEmpty())
            return;

        CommonEIV.LOGGER.info("Informing {} about {} recipe types", serverPlayer.getName(), PRESENT_RECIPES.size());

        CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundStartUpdatesPayload());

        CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundCacheStartPayload(PRESENT_RECIPES.size()));
        PRESENT_RECIPES.forEach((type, entries) -> {
            CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundTypeUpdateStartPayload(type, entries.size()));
            entries.forEach(recipe -> {
                CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundTypeUpdatePayload(recipe));
            });
            CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundTypeUpdateEndPayload(type));
        });
        CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundFinishUpdatesPayload());

    }


    public record ServerRecipeEntry(ResourceLocation modRecipeId, IEivServerRecipe recipe) {

        public static final StreamCodec<FriendlyByteBuf, ServerRecipeEntry> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                entry -> entry.modRecipeId().toString(),
                ByteBufCodecs.COMPOUND_TAG,
                ServerRecipeEntry::createFullTag,
                (s, compoundTag) -> new ServerRecipeEntry(ResourceLocation.tryParse(s), ServerRecipeEntry.fromTag(compoundTag))
        );

        public <T extends IEivServerRecipe> T asWrapped() {
            return (T) this.recipe;
        }

        private CompoundTag createFullTag() {
            CompoundTag tag = new CompoundTag();
            tag.putString("recipeType", this.recipe().getRecipeType().getId().toString());
            CompoundTag dataTag = new CompoundTag();
            this.recipe().writeToTag(dataTag);
            tag.put("recipeData", dataTag);
            return tag;
        }

        private static IEivServerRecipe fromTag(CompoundTag tag) {
            if (!tag.contains("recipeType"))
                return null;

            EivRecipeType<?> recipeType = EivRecipeType.byId(ResourceLocation.parse(tag.getString("recipeType").orElseThrow()));
            if (recipeType == null)
                return null;

            IEivServerRecipe modRecipe = recipeType.getEmptyConstructor().construct();
            modRecipe.loadFromTag(tag.getCompound("recipeData").orElseGet(CompoundTag::new));
            return modRecipe;
        }
    }


    //Transfer
    public void performRecipeTransfer(ServerPlayer player, HashMap<Integer, Integer> transferMap, HashMap<Integer, HashMap<Integer, ItemStack>> usedPlayerSlots) {

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
