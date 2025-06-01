package de.crafty.eiv.common.recipe;

import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.api.recipe.EivRecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class ClientRecipeCache {

    public static final ClientRecipeCache INSTANCE = new ClientRecipeCache();


    private final LinkedHashMap<EivRecipeType<?>, List<ServerRecipeManager.ServerRecipeEntry>> serverEntryMap;

    private final HashMap<ResourceLocation, List<ResourceLocation>> multiRecipeMap;
    private final HashMap<ResourceLocation, IEivViewRecipe> recipeMap;
    private final HashMap<Item, List<ResourceLocation>> byItemIngredient, byItemResult;


    private ClientRecipeCache() {
        this.serverEntryMap = new LinkedHashMap<>();

        this.multiRecipeMap = new LinkedHashMap<>();
        this.recipeMap = new HashMap<>();
        this.byItemIngredient = new HashMap<>();
        this.byItemResult = new HashMap<>();

    }

    public IEivViewRecipe getRecipe(final ResourceLocation recipeId) {
        return recipeMap.getOrDefault(recipeId, null);
    }


    public void updateType(EivRecipeType<?> type, List<ServerRecipeManager.ServerRecipeEntry> recipes) {
        this.serverEntryMap.getOrDefault(type, new ArrayList<>()).forEach(entry -> {
            this.multiRecipeMap.getOrDefault(entry.modRecipeId(), new ArrayList<>()).forEach(resourceLocation -> {
                this.recipeMap.remove(resourceLocation);

                this.byItemIngredient.forEach((item, resourceLocations) -> {
                    resourceLocations.remove(resourceLocation);
                });

                this.byItemResult.forEach((item, resourceLocations) -> {
                    resourceLocations.remove(resourceLocation);
                });
            });
        });

        this.serverEntryMap.put(type, recipes);
    }


    public List<IEivViewRecipe> getRecipesForCraftingInput(ItemStack inputStack) {
        List<IEivViewRecipe> recipes = new ArrayList<>();
        this.byItemIngredient.getOrDefault(inputStack.getItem(), List.of()).forEach(resourceLocation -> {
            recipes.add(this.recipeMap.get(resourceLocation));
        });

        recipes.removeIf(viewRecipe -> !viewRecipe.redirectsAsIngredient(inputStack) && !viewRecipe.getViewType().getCraftReferences().contains(inputStack));
        return recipes;
    }

    public List<IEivViewRecipe> getRecipesForCraftingOutput(ItemStack outputStack) {

        List<IEivViewRecipe> recipes = new ArrayList<>();
        this.byItemResult.getOrDefault(outputStack.getItem(), List.of()).forEach(resourceLocation -> {
            recipes.add(this.recipeMap.get(resourceLocation));
        });

        recipes.removeIf(viewRecipe -> !viewRecipe.redirectsAsResult(outputStack));
        return recipes;
    }

    public void sortModType(EivRecipeType<?> type) {
        ItemViewRecipes.ClientRecipeWrapper<?> wrapper = ItemViewRecipes.INSTANCE.wrapperMap().getOrDefault(type, null);

        if (wrapper == null || !this.serverEntryMap.containsKey(type))
            return;


        for (ServerRecipeManager.ServerRecipeEntry modEntry : this.serverEntryMap.get(type)) {
            List<? extends IEivViewRecipe> wrappedRecipes = wrapper.wrap(modEntry.asWrapped());
            if (wrappedRecipes.isEmpty())
                continue;

            for (int id = 0; id < wrappedRecipes.size(); id++) {
                IEivViewRecipe wrapped = wrappedRecipes.get(id);

                ResourceLocation uniqueId = this.getUniqueId(modEntry, id);
                List<ResourceLocation> summarized = this.multiRecipeMap.getOrDefault(modEntry.modRecipeId(), new ArrayList<>());
                summarized.add(uniqueId);
                this.multiRecipeMap.put(modEntry.modRecipeId(), summarized);

                this.recipeMap.put(uniqueId, wrapped);

                wrapped.getIngredients().forEach(ingredient -> {
                    ingredient.getValidContents().forEach(stack -> {

                        List<ResourceLocation> byIngredient = this.byItemIngredient.getOrDefault(stack.getItem(), new ArrayList<>());
                        byIngredient.remove(uniqueId);
                        byIngredient.add(uniqueId);
                        this.byItemIngredient.put(stack.getItem(), byIngredient);
                    });
                });

                wrapped.getViewType().getCraftReferences().forEach(reference -> {
                    List<ResourceLocation> byIngredient = this.byItemIngredient.getOrDefault(reference.getItem(), new ArrayList<>());
                    byIngredient.remove(uniqueId);
                    byIngredient.add(uniqueId);
                    this.byItemIngredient.put(reference.getItem(), byIngredient);
                });

                wrapped.getResults().forEach(result -> {
                    result.getValidContents().forEach(stack -> {
                        List<ResourceLocation> byResult = this.byItemResult.getOrDefault(stack.getItem(), new ArrayList<>());
                        byResult.remove(uniqueId);
                        byResult.add(uniqueId);
                        this.byItemResult.put(stack.getItem(), byResult);
                    });
                });
            }
        }
    }


    private ResourceLocation getUniqueId(ServerRecipeManager.ServerRecipeEntry modEntry, int index) {
        return ResourceLocation.fromNamespaceAndPath(modEntry.modRecipeId().getNamespace(), modEntry.modRecipeId().getPath() + "/" + index);
    }

}
