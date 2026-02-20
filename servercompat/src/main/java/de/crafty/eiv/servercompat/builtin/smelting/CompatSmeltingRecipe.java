package de.crafty.eiv.servercompat.builtin.smelting;

import de.crafty.eiv.servercompat.api.recipe.EivCompatRecipeType;
import de.crafty.eiv.servercompat.api.recipe.IEivCompatServerRecipe;
import de.crafty.eiv.servercompat.util.EivCompatTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.bukkit.inventory.RecipeChoice;

public class CompatSmeltingRecipe implements IEivCompatServerRecipe {


    public static final EivCompatRecipeType<CompatSmeltingRecipe> TYPE = EivCompatRecipeType.register(
            Identifier.withDefaultNamespace("smelting")
    );

    private RecipeChoice input;
    private ItemStack result;

    public CompatSmeltingRecipe(RecipeChoice input, ItemStack result) {
        this.input = input;
        this.result = result;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("input", EivCompatTagUtil.writeChoice(this.input));
        tag.put("result", EivCompatTagUtil.encodeItemStackOnServer(this.result));
    }


    @Override
    public EivCompatRecipeType<? extends IEivCompatServerRecipe> getRecipeType() {
        return TYPE;
    }
}
