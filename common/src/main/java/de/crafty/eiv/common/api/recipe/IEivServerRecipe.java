package de.crafty.eiv.common.api.recipe;

import net.minecraft.nbt.CompoundTag;

/**
 * Server-Side representation of a recipe used to update the client efficiently
 * <br>
 * <br>
 * Only send neccessary data here (everything else can be done in the {@link de.crafty.eiv.common.recipe.ItemViewRecipes.ClientRecipeWrapper})
 */
public interface IEivServerRecipe {


    /**
     *
     * @param tag The compoundTag containing the encoded data
     */
    void writeToTag(CompoundTag tag);

    /**
     *
     * @param tag The compoundTag containing the decoded data
     */
    void loadFromTag(CompoundTag tag);


    /**
     *
     * @return The server recipe's type registered by <b>EivRecipeTye.register();</b>
     */
    EivRecipeType<? extends IEivServerRecipe> getRecipeType();
}
