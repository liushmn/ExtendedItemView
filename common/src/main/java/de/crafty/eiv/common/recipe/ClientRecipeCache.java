package de.crafty.eiv.common.recipe;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.ItemView;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class ClientRecipeCache {

    public static final ClientRecipeCache INSTANCE = new ClientRecipeCache();


    private final LinkedHashMap<EivRecipeType<?>, List<ServerRecipeManager.ServerRecipeEntry>> serverEntryMap;

    private final HashMap<ResourceLocation, List<ResourceLocation>> multiRecipeMap;
    private final HashMap<ResourceLocation, IEivViewRecipe> recipeMap;
    private final HashMap<IEivViewRecipe, ResourceLocation> inversedRecipeMap;
    private final HashMap<Item, List<ResourceLocation>> byItemIngredient, byItemResult;

    private final HashMap<Item, List<ItemView.StackSensitive>> stackSensitives;

    private ClientRecipeCache() {
        this.serverEntryMap = new LinkedHashMap<>();

        this.multiRecipeMap = new LinkedHashMap<>();
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


    public void updateType(EivRecipeType<?> type, List<ServerRecipeManager.ServerRecipeEntry> recipes) {
        this.serverEntryMap.getOrDefault(type, new ArrayList<>()).forEach(entry -> {
            this.multiRecipeMap.getOrDefault(entry.recipeId(), new ArrayList<>()).forEach(ResourceLocation -> {
                this.inversedRecipeMap.remove(this.recipeMap.get(ResourceLocation));
                this.recipeMap.remove(ResourceLocation);

                this.byItemIngredient.forEach((item, ResourceLocations) -> {
                    ResourceLocations.remove(ResourceLocation);
                });

                this.byItemResult.forEach((item, ResourceLocations) -> {
                    ResourceLocations.remove(ResourceLocation);
                });
            });
        });

        this.serverEntryMap.put(type, recipes);
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

    public void sortModType(EivRecipeType<?> type) {
        ItemViewRecipes.ClientRecipeWrapper<?> wrapper = ItemViewRecipes.INSTANCE.wrapperMap().getOrDefault(type, null);

        if (wrapper == null || !this.serverEntryMap.containsKey(type))
            return;


        for (ServerRecipeManager.ServerRecipeEntry modEntry : this.serverEntryMap.get(type)) {
            List<? extends IEivViewRecipe> wrappedRecipes;

            try {
                wrappedRecipes = wrapper.wrap(modEntry.asWrapped());
            } catch (Exception e) {
                CommonEIV.LOGGER.error("Failed to wrap recipe entry {}: {}, skipping it...", modEntry.recipeId(), e.getMessage());
                continue;
            }

            if (wrappedRecipes.isEmpty())
                continue;

            for (int id = 0; id < wrappedRecipes.size(); id++) {
                IEivViewRecipe wrapped = wrappedRecipes.get(id);

                ResourceLocation uniqueId = this.getUniqueId(modEntry, id);
                List<ResourceLocation> summarized = this.multiRecipeMap.getOrDefault(modEntry.recipeId(), new ArrayList<>());
                summarized.add(uniqueId);
                this.multiRecipeMap.put(modEntry.recipeId(), summarized);

                this.recipeMap.put(uniqueId, wrapped);
                this.inversedRecipeMap.put(wrapped, uniqueId);

                wrapped.getIngredients().forEach(ingredient -> {
                    ingredient.getValidContents().forEach(stack -> {

                        List<ResourceLocation> byIngredient = this.byItemIngredient.getOrDefault(stack.getItem(), new ArrayList<>());
                        byIngredient.remove(uniqueId);
                        byIngredient.add(uniqueId);
                        this.byItemIngredient.put(stack.getItem(), byIngredient);
                    });
                });

                wrapped.getViewType().getCraftReferences().forEach(reference -> {

                    if (!wrapped.getViewType().getCraftReferenceCondition().matches(reference, wrapped))
                        return;

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
        return ResourceLocation.fromNamespaceAndPath(modEntry.recipeId().getNamespace(), modEntry.recipeId().getPath() + "/" + index);
    }


}
