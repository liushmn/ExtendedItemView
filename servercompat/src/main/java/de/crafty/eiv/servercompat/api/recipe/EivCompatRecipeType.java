package de.crafty.eiv.servercompat.api.recipe;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public interface EivCompatRecipeType<T extends IEivCompatServerRecipe> {

    HashMap<ResourceLocation, EivCompatRecipeType<?>> EIV_RECIPE_TYPES = new HashMap<>();


    /**
     *
     * @return A unique id for the recipe type used in network communication
     */
    ResourceLocation getId();


    /**
     *
     * @param id A unique id
     * @return The recipe type
     * @param <S> The server recipe class
     */
    static <S extends IEivCompatServerRecipe> EivCompatRecipeType<S> register(ResourceLocation id) {

        EivCompatRecipeType<S> type = new EivCompatRecipeType<S>() {
            @Override
            public ResourceLocation getId() {
                return id;
            }

        };
        EIV_RECIPE_TYPES.put(id, type);
        return type;
    }

    /**
     *
     * @param id The id
     * @return The server recipe type by id
     */
    static EivCompatRecipeType<?> byId(ResourceLocation id){
        return EIV_RECIPE_TYPES.getOrDefault(id, null);
    }

    /**
     *
     * @param recipeType The recipe type
     * @return The id of the type
     */
    static ResourceLocation idFromType(EivCompatRecipeType<?> recipeType) {
        return recipeType.getId();
    }
}
