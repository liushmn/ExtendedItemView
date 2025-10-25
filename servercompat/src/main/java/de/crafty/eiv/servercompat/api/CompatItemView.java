package de.crafty.eiv.servercompat.api;


import de.crafty.eiv.servercompat.api.recipe.IEivCompatServerRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Utility class for server integrations of EIV
 */
public class CompatItemView {

    private static final List<CompatRecipeProvider> RECIPE_PROVIDERS = new ArrayList<>();


    /**
     * Server-Side map of "item-variants", the client gets informed about on every server reload
     */
    private static final HashMap<Item, List<StackSensitive>> STACK_SENSITIVE = new HashMap<>();

    /**
     * A list of Callbacks used for mods to hook into a server reload
     * <br>
     * <br>
     * Stack-Sensitives should also be registered here
     */
    private static final List<ReloadCallback> RELOAD_CALLBACKS = new ArrayList<>();



    public static void addRecipeProvider(CompatRecipeProvider recipeProvider) {
        RECIPE_PROVIDERS.add(recipeProvider);
    }

    public static List<CompatRecipeProvider> getRecipeProviders() {
        return RECIPE_PROVIDERS;
    }

    public static void clearProviders() {
        RECIPE_PROVIDERS.clear();
    }


    /**
     * Add "item-variants", called stack-sensitives to the overlay
     * <br>
     * <br>
     * These sensitives are also used to make proper ingredient/result redirections
     *
     * @param stack The stack-sensitive
     */
    public static void addStackSensitive(ItemStack stack) {
        List<StackSensitive> present = STACK_SENSITIVE.getOrDefault(stack.getItem(), new ArrayList<>());
        present.add(new StackSensitive(stack));
        STACK_SENSITIVE.put(stack.getItem(), present);
    }


    /**
     * @return The list of currently present stack-sensitives (server-side)
     */
    public static HashMap<Item, List<StackSensitive>> getStackSensitive() {
        return STACK_SENSITIVE;
    }



    /**
     * Plugins can add a ReloadCallback to hook into a server reload
     * <br>
     * <br>
     * They should register their stack-sensitives here, because the list of stack-sensitives is cleared before every reload
     *
     * @param callback The reload callback
     */
    public static void addReloadCallback(ReloadCallback callback) {
        RELOAD_CALLBACKS.add(callback);
    }


    /**
     * @return A list of currently present reload callbacks
     */
    public static List<ReloadCallback> getReloadCallbacks() {
        return RELOAD_CALLBACKS;
    }






    public interface ReloadCallback {

        void onReload();

    }


    public interface CompatRecipeProvider {

        void provide(List<IEivCompatServerRecipe> recipeList);

    }

    /**
     * Representation of a stack-sensitive
     *
     * @param stack The itemStack used as an item-variant
     */
    public record StackSensitive(ItemStack stack) {

        @Override
        public ItemStack stack() {
            return stack.copy();
        }


    }

}
