package de.crafty.eiv.common.recipe;

import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.RecipeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ClientRecipeManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("ClientRecipeManager");

    public static final ClientRecipeManager INSTANCE = new ClientRecipeManager();


    private final Minecraft mc;

    private RecipeManager recipeManager;

    private ClientRecipeManager() {
        this.mc = Minecraft.getInstance();
    }

    public Minecraft getMinecraft() {
        return this.mc;
    }

    public void setVanillaRecipeManager(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    public RecipeManager getVanillaRecipeManager() {
        return this.recipeManager;
    }

    public void reload() {

        List<IEivViewRecipe> recipes = new ArrayList<>();
        ItemViewRecipes.INSTANCE.getRecipeProviders().forEach(recipeProvider -> {
            recipeProvider.provide(recipes);
        });

        ClientRecipeCache.INSTANCE.updateRecipes(recipes);
    }
}
