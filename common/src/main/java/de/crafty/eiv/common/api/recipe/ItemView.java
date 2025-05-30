package de.crafty.eiv.common.api.recipe;

import de.crafty.eiv.common.recipe.ItemViewRecipes;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemView {

    private static final List<Item> EXCLUDED = new ArrayList<>();

    public static void addRecipeProvider(ItemViewRecipes.ServerRecipeProvider provider) {
        ItemViewRecipes.INSTANCE.addRecipeProvider(provider);
    }

    public static <T extends IEivServerRecipe> void registerRecipeWrapper(EivRecipeType<T> recipeType, ItemViewRecipes.ClientRecipeWrapper<T> wrapper) {
        ItemViewRecipes.INSTANCE.registerRecipeWrapper(recipeType, wrapper);
    }


    public static void excludeItem(Item item) {
        excludeItems(item);
    }

    public static void excludeItems(Item... items) {
        Arrays.stream(items).filter(item -> !EXCLUDED.contains(item)).forEach(EXCLUDED::add);
    }


    public static List<Item> getExcluded() {
        return EXCLUDED;
    }
}
