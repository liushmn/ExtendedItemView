package de.crafty.eiv.common.api.recipe;

import net.minecraft.nbt.CompoundTag;

public interface IEivServerRecipe {


    void writeToTag(CompoundTag tag);

    void loadFromTag(CompoundTag tag);


    EivRecipeType<? extends IEivServerRecipe> getRecipeType();
}
