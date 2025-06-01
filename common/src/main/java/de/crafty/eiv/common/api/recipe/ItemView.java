package de.crafty.eiv.common.api.recipe;

import de.crafty.eiv.common.recipe.ItemViewRecipes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ItemView {

    private static final List<Item> EXCLUDED = new ArrayList<>();
    private static final HashMap<Item, List<ItemStack>> STACK_SENSITIVE = new HashMap<>();

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

    public static void addStackSensitive(ItemStack stack) {
        List<ItemStack> present = STACK_SENSITIVE.getOrDefault(stack.getItem(), new ArrayList<>());
        present.add(stack);
        STACK_SENSITIVE.put(stack.getItem(), present);
    }


    public static HashMap<Item, List<ItemStack>> getStackSensitive() {
        return STACK_SENSITIVE;
    }

    public static List<Item> getExcluded() {
        return EXCLUDED;
    }
}
