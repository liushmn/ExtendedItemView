package de.crafty.eiv.common.api.recipe;

import net.minecraft.nbt.CompoundTag;

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
