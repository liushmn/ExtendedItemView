package de.crafty.eiv.servercompat.api.recipe;

import net.minecraft.resources.Identifier;

import java.util.HashMap;

public interface EivCompatRecipeType<T extends IEivCompatServerRecipe> {

    HashMap<Identifier, EivCompatRecipeType<?>> EIV_RECIPE_TYPES = new HashMap<>();


    /**
     *
     * @return A unique id for the recipe type used in network communication
     */
    Identifier getId();


    /**
     *
     * @param id A unique id
     * @return The recipe type
     * @param <S> The server recipe class
     */
    static <S extends IEivCompatServerRecipe> EivCompatRecipeType<S> register(Identifier id) {

        EivCompatRecipeType<S> type = new EivCompatRecipeType<S>() {
            @Override
            public Identifier getId() {
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
    static EivCompatRecipeType<?> byId(Identifier id){
        return EIV_RECIPE_TYPES.getOrDefault(id, null);
    }

    /**
     *
     * @param recipeType The recipe type
     * @return The id of the type
     */
    static Identifier idFromType(EivCompatRecipeType<?> recipeType) {
        return recipeType.getId();
    }
}
