package de.crafty.eiv.common.recipe;

import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.api.recipe.ItemView;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class ClientRecipeCache {

    public static final ClientRecipeCache INSTANCE = new ClientRecipeCache();

    private final HashMap<ResourceLocation, IEivViewRecipe> recipeMap;
    private final HashMap<IEivViewRecipe, ResourceLocation> inversedRecipeMap;
    private final HashMap<Item, List<ResourceLocation>> byItemIngredient, byItemResult;

    private final HashMap<Item, List<ItemView.StackSensitive>> stackSensitives;

    private ClientRecipeCache() {

        this.recipeMap = new HashMap<>();
        this.inversedRecipeMap = new HashMap<>();
        this.byItemIngredient = new HashMap<>();
        this.byItemResult = new HashMap<>();

        this.stackSensitives = new HashMap<>();

    }

    public void clearStackSensitives() {
        this.stackSensitives.clear();
    }

    public void addStackSensitive(ItemView.StackSensitive stackSensitive) {
        List<ItemView.StackSensitive> present = this.stackSensitives.getOrDefault(stackSensitive.stack().getItem(), new ArrayList<>());
        present.add(stackSensitive);
        this.stackSensitives.put(stackSensitive.stack().getItem(), present);
    }

    public List<ItemView.StackSensitive> getStackSensitives(Item item) {
        return this.stackSensitives.getOrDefault(item, new ArrayList<>());
    }


    public IEivViewRecipe getRecipe(final ResourceLocation recipeId) {
        return this.recipeMap.getOrDefault(recipeId, null);
    }

    public ResourceLocation getIdFromRecipe(final IEivViewRecipe recipe) {
        return this.inversedRecipeMap.getOrDefault(recipe, null);
    }


    public List<IEivViewRecipe> getRecipesForCraftingInput(ItemStack inputStack) {
        List<IEivViewRecipe> recipes = new ArrayList<>();
        this.byItemIngredient.getOrDefault(inputStack.getItem(), List.of()).forEach(ResourceLocation -> {
            recipes.add(this.recipeMap.get(ResourceLocation));
        });

        recipes.removeIf(viewRecipe -> !viewRecipe.redirectsAsIngredient(inputStack) && (viewRecipe.getViewType().getCraftReferences().stream().noneMatch(itemStack -> itemStack.getItem() == inputStack.getItem()) || !viewRecipe.getViewType().getCraftReferenceCondition().matches(inputStack, viewRecipe)));

        return recipes;
    }

    public List<IEivViewRecipe> getRecipesForCraftingOutput(ItemStack outputStack) {

        List<IEivViewRecipe> recipes = new ArrayList<>();
        this.byItemResult.getOrDefault(outputStack.getItem(), List.of()).forEach(ResourceLocation -> {
            recipes.add(this.recipeMap.get(ResourceLocation));
        });

        recipes.removeIf(viewRecipe -> !viewRecipe.redirectsAsResult(outputStack));

        return recipes;
    }


    //Updating


    protected void updateRecipes(List<IEivViewRecipe> recipes) {
        this.recipeMap.clear();
        this.inversedRecipeMap.clear();
        this.byItemIngredient.clear();
        this.byItemResult.clear();

        recipes.forEach(recipe -> {
            this.recipeMap.put(recipe.getId(), recipe);
            this.inversedRecipeMap.put(recipe, recipe.getId());


            recipe.getIngredients().stream().map(SlotContent::getValidContents).forEach(list -> list.stream().map(ItemStack::getItem).forEach(item -> {
                List<ResourceLocation> present = this.byItemIngredient.getOrDefault(item, new ArrayList<>());
                present.add(recipe.getId());
                this.byItemIngredient.put(item, present);
            }));

            recipe.getResults().stream().map(SlotContent::getValidContents).forEach(list -> list.stream().map(ItemStack::getItem).forEach(item -> {
                List<ResourceLocation> present = this.byItemResult.getOrDefault(item, new ArrayList<>());
                present.add(recipe.getId());
                this.byItemResult.put(item, present);
            }));
        });

    }
}
