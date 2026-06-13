package de.crafty.eiv.servercompat.builtin.blasting;

import de.crafty.eiv.servercompat.api.recipe.EivCompatRecipeType;
import de.crafty.eiv.servercompat.api.recipe.IEivCompatServerRecipe;
import de.crafty.eiv.servercompat.builtin.smelting.CompatSmeltingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class CompatBlastingRecipe extends CompatSmeltingRecipe {

    public static final EivCompatRecipeType<CompatBlastingRecipe> TYPE = EivCompatRecipeType.register(
            new ResourceLocation("blasting")
    );


    public CompatBlastingRecipe(RecipeChoice input, ItemStack result) {
        super(input, result);
    }

    @Override
    public EivCompatRecipeType<? extends IEivCompatServerRecipe> getRecipeType() {
        return TYPE;
    }

}
