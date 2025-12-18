package de.crafty.eiv.servercompat.builtin.campfire;

import de.crafty.eiv.servercompat.api.recipe.EivCompatRecipeType;
import de.crafty.eiv.servercompat.api.recipe.IEivCompatServerRecipe;
import de.crafty.eiv.servercompat.builtin.smelting.CompatSmeltingRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class CompatCampfireRecipe extends CompatSmeltingRecipe {

    public static final EivCompatRecipeType<CompatCampfireRecipe> TYPE = EivCompatRecipeType.register(
            Identifier.withDefaultNamespace("campfire_cooking")
    );


    public CompatCampfireRecipe(RecipeChoice input, ItemStack result) {
        super(input, result);
    }

    @Override
    public EivCompatRecipeType<? extends IEivCompatServerRecipe> getRecipeType() {
        return TYPE;
    }

}
