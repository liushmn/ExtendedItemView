package de.crafty.eiv.servercompat.api.recipe;

import net.minecraft.nbt.CompoundTag;

/**
 * IMPORTANT: when there's already a mod or builtin recipetype this compat recipe should belong to,
 * then this compat recipe should use the same encding format as the mod server recipe
 */
public interface IEivCompatServerRecipe {

    /**
     * We only need the encoding method because we're running exclusively on the server
     * @param out Tag containing the encoded data
     */
    void writeToTag(CompoundTag out);


    /**
     * NOTE: compat recipe types returned here should have the same id as their corresponding mod recipe type (if there's a mod type)
     * @return The server recipe's type registered by <b>EivCompatRecipeType.register();</b>
     */
    EivCompatRecipeType<? extends IEivCompatServerRecipe> getRecipeType();
}
