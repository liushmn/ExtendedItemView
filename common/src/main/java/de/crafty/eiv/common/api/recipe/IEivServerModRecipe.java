package de.crafty.eiv.common.api.recipe;

import net.minecraft.nbt.CompoundTag;

public interface IEivServerModRecipe {


    void writeToTag(CompoundTag tag);

    void loadFromTag(CompoundTag tag);


    ModRecipeType<? extends IEivServerModRecipe> getRecipeType();
}
