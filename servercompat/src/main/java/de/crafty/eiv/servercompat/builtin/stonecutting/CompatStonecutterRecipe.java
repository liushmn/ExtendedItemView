package de.crafty.eiv.servercompat.builtin.stonecutting;

import de.crafty.eiv.servercompat.api.recipe.EivCompatRecipeType;
import de.crafty.eiv.servercompat.api.recipe.IEivCompatServerRecipe;
import de.crafty.eiv.servercompat.util.EivCompatTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class CompatStonecutterRecipe implements IEivCompatServerRecipe {


    public static final EivCompatRecipeType<CompatStonecutterRecipe> TYPE = EivCompatRecipeType.register(
            new ResourceLocation("stonecutting")
    );

    private RecipeChoice input;
    private ItemStack result;

    public CompatStonecutterRecipe(RecipeChoice input, ItemStack result) {
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
