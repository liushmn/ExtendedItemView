package de.crafty.eiv.servercompat.recipe;

import de.crafty.eiv.servercompat.api.CompatItemView;
import de.crafty.eiv.servercompat.api.IEivCompatIntegration;
import de.crafty.eiv.servercompat.api.recipe.EivCompatRecipeType;
import de.crafty.eiv.servercompat.api.recipe.IEivCompatServerRecipe;
import de.crafty.eiv.servercompat.log.Data;
import de.crafty.eiv.servercompat.network.CompatNetworking;
import de.crafty.eiv.servercompat.network.payload.recipe.*;
import de.crafty.eiv.servercompat.network.payload.stack.ClientboundFinishStackSensitivesPayload;
import de.crafty.eiv.servercompat.network.payload.stack.ClientboundStackSensitivePayload;
import de.crafty.eiv.servercompat.network.payload.stack.ClientboundStartStackSensitivesPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CompatRecipeManager {

    public static final CompatRecipeManager INSTANCE = new CompatRecipeManager();

    private final HashMap<Plugin, IEivCompatIntegration> PRESENT_INTEGRATIONS = new HashMap<>();
    private static final HashMap<EivCompatRecipeType<?>, List<CompatRecipeEntry>> PRESENT_RECIPES = new LinkedHashMap<>();


    public DedicatedServer getServer(){
        return ((CraftServer) Bukkit.getServer()).getServer();
    }


    public void registerIntegration(Plugin plugin, IEivCompatIntegration integration) {
        PRESENT_INTEGRATIONS.put(plugin, integration);
    }

    public int getIntegrationCount() {
        return PRESENT_INTEGRATIONS.size();
    }

    public void loadIntegrations() {

        Data.log(String.format("Loading %s EIV-Compat integration(s)...", PRESENT_INTEGRATIONS.size()));
        PRESENT_INTEGRATIONS.forEach((plugin, integration) -> {
            Data.log(String.format(" - %s", plugin.getName()));
        });

        CompatItemView.clearProviders();
        PRESENT_INTEGRATIONS.values().forEach(IEivCompatIntegration::onInitialize);
        Data.log("All integrations have been loaded.");
        this.reload();
    }

    public void reload() {

        CompatItemView.getStackSensitive().clear();
        CompatItemView.getReloadCallbacks().forEach(CompatItemView.ReloadCallback::onReload);

        this.broadcastStackSensitives();

        this.reloadRecipes();
        this.broadcastRecipes();
    }

    private void broadcastStackSensitives() {

        if (CompatItemView.getStackSensitive().isEmpty())
            return;

        Data.log("Informing " + Bukkit.getServer().getOnlinePlayers().size() + " players about " + CompatItemView.getStackSensitive().size() + " stack sensitives");
        Bukkit.getServer().getOnlinePlayers().forEach(this::updateStackSensitives);


    }

    public void updateStackSensitives(Player player) {
        List<CompatItemView.StackSensitive> collected = new ArrayList<>();
        CompatItemView.getStackSensitive().forEach((item, stackSensitives) -> {
            collected.addAll(stackSensitives);
        });
        CompatNetworking.INSTANCE.sendPayload(player, new ClientboundStartStackSensitivesPayload(collected.size()));

        collected.forEach(stackSensitive -> {
            CompatNetworking.INSTANCE.sendPayload(player, new ClientboundStackSensitivePayload(stackSensitive));
        });

        CompatNetworking.INSTANCE.sendPayload(player, new ClientboundFinishStackSensitivesPayload());
    }


    private void broadcastRecipes() {

        if (PRESENT_RECIPES.isEmpty())
            return;

        Bukkit.getServer().getOnlinePlayers().forEach(this::updateRecipes);

    }

    public void updateRecipes(Player player) {
        Data.log("Informing " + player.getName() + " about " + PRESENT_RECIPES.size() + " recipe types");

        CompatNetworking.INSTANCE.sendPayload(player, new ClientboundStartUpdatesPayload());

        CompatNetworking.INSTANCE.sendPayload(player, new ClientboundCacheStartPayload(PRESENT_RECIPES.size()));
        PRESENT_RECIPES.forEach((type, entries) -> {
            CompatNetworking.INSTANCE.sendPayload(player, new ClientboundTypeUpdateStartPayload(type, entries.size()));
            entries.forEach(recipe -> {
                CompatNetworking.INSTANCE.sendPayload(player, new ClientboundTypeUpdatePayload(recipe));
            });
            CompatNetworking.INSTANCE.sendPayload(player, new ClientboundTypeUpdateEndPayload(type));
        });
        CompatNetworking.INSTANCE.sendPayload(player, new ClientboundFinishUpdatesPayload());
    }

    private void reloadRecipes() {
        PRESENT_RECIPES.clear();

        List<IEivCompatServerRecipe> serverRecipes = new ArrayList<>();
        CompatItemView.getRecipeProviders().forEach(serverModRecipeProvider -> {
            List<IEivCompatServerRecipe> recipes = new ArrayList<>();
            serverModRecipeProvider.provide(recipes);
            serverRecipes.addAll(recipes);
        });

        serverRecipes.forEach(iEivServerModRecipe -> {

            Identifier typeId = iEivServerModRecipe.getRecipeType().getId();
            List<CompatRecipeEntry> list = PRESENT_RECIPES.getOrDefault(iEivServerModRecipe.getRecipeType(), new ArrayList<>());
            list.add(new CompatRecipeEntry(Identifier.fromNamespaceAndPath(typeId.getNamespace(), typeId.getPath() + "/" + UUID.randomUUID()), iEivServerModRecipe));
            PRESENT_RECIPES.put(iEivServerModRecipe.getRecipeType(), list);
        });
    }


    /**
     * A server recipe entry containing only server side functionality
     *
     * @param modRecipeId  The unique id of the recipe
     * @param compatRecipe The recipe instance
     */
    public record CompatRecipeEntry(Identifier modRecipeId, IEivCompatServerRecipe compatRecipe) {

        public CompoundTag createFullTagWithId() {

            CompoundTag fullTag = new CompoundTag();
            fullTag.putString("recipeId", this.modRecipeId().toString());

            CompoundTag recipeTag = new CompoundTag();
            recipeTag.putString("recipeType", this.compatRecipe().getRecipeType().getId().toString());
            CompoundTag dataTag = new CompoundTag();
            this.compatRecipe().writeToTag(dataTag);
            recipeTag.put("recipeData", dataTag);

            fullTag.put("recipe", recipeTag);

            return fullTag;
        }

    }

}
