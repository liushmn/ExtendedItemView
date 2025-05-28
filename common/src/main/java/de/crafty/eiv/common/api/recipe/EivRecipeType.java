package de.crafty.eiv.common.api.recipe;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public interface EivRecipeType<T extends IEivServerRecipe> {

    HashMap<ResourceLocation, EivRecipeType<?>> EIV_RECIPE_TYPES = new HashMap<>();


    ResourceLocation getId();

    EmptyRecipeConstructor<T> getEmptyConstructor();

    static <S extends IEivServerRecipe> EivRecipeType<S> register(ResourceLocation id, EmptyRecipeConstructor<S> emptyRecipeConstructor) {

        EivRecipeType<S> type = new EivRecipeType<S>() {
            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public EmptyRecipeConstructor<S> getEmptyConstructor() {
                return emptyRecipeConstructor;
            }


        };
        EIV_RECIPE_TYPES.put(id, type);
        return type;
    }

    static EivRecipeType<?> byId(ResourceLocation id){
        return EIV_RECIPE_TYPES.getOrDefault(id, null);
    }

    static ResourceLocation idFromType(EivRecipeType<?> recipeType) {
        return recipeType.getId();
    }



    interface EmptyRecipeConstructor<T extends IEivServerRecipe> {

        T construct();

    }
}
