package de.crafty.eiv.common.api.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemViewRecipes {

    public static final ItemViewRecipes INSTANCE = new ItemViewRecipes();


    private final HashMap<EivRecipeType<?>, ClientRecipeWrapper<?>> recipeWrappers;
    private final List<ServerRecipeProvider> recipeProviders;

    private final HashMap<Fluid, Item> fluidItemMap;


    private ItemViewRecipes() {
        this.recipeWrappers = new HashMap<>();
        this.recipeProviders = new ArrayList<>();
        this.fluidItemMap = new HashMap<>();
    }



    public <T extends IEivServerRecipe> void registerRecipeWrapper(EivRecipeType<T> recipeType, ClientRecipeWrapper<T> wrapper) {
        this.recipeWrappers.put(recipeType, wrapper);
    }

    public void addRecipeProvider(ServerRecipeProvider provider) {
        this.recipeProviders.add(provider);
    }


    public HashMap<EivRecipeType<?>, ClientRecipeWrapper<?>> wrapperMap() {
        return this.recipeWrappers;
    }

    public List<ServerRecipeProvider> getRecipeProviders() {
        return this.recipeProviders;
    }

    public void setFluidItemMap(HashMap<Fluid, Item> fluidItemMap) {
        this.fluidItemMap.clear();
        this.fluidItemMap.putAll(fluidItemMap);
    }

    public Item itemForFluid(Fluid fluid) {
        return this.fluidItemMap.getOrDefault(fluid, Items.AIR);
    }



    public interface ClientRecipeWrapper<T extends IEivServerRecipe> {

        List<? extends IEivViewRecipe> wrap(T unwrapped);

    }


    public interface ServerRecipeProvider {

        void provide(List<IEivServerRecipe> recipeList);

    }

}
