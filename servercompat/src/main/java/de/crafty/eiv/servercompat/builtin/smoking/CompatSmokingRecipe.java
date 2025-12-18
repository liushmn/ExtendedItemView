package de.crafty.eiv.servercompat.builtin.smoking;

import de.crafty.eiv.servercompat.api.recipe.EivCompatRecipeType;
import de.crafty.eiv.servercompat.api.recipe.IEivCompatServerRecipe;
import de.crafty.eiv.servercompat.builtin.smelting.CompatSmeltingRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class CompatSmokingRecipe extends CompatSmeltingRecipe {


    public static final EivCompatRecipeType<CompatSmokingRecipe> TYPE = EivCompatRecipeType.register(
            Identifier.withDefaultNamespace("smoking")
    );

    public CompatSmokingRecipe(RecipeChoice input, ItemStack result) {
        super(input, result);
    }


    @Override
    public EivCompatRecipeType<? extends IEivCompatServerRecipe> getRecipeType() {
        return TYPE;
    }

}
