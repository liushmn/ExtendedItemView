package de.crafty.eiv.servercompat.builtin.brewing;

import de.crafty.eiv.servercompat.api.recipe.EivCompatRecipeType;
import de.crafty.eiv.servercompat.api.recipe.IEivCompatServerRecipe;
import de.crafty.eiv.servercompat.util.EivCompatTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class CompatBrewingRecipe implements IEivCompatServerRecipe {


    public static final EivCompatRecipeType<CompatBrewingRecipe> TYPE = EivCompatRecipeType.register(
            ResourceLocation.withDefaultNamespace("brewing")
    );

    private ItemStack result, bottleIngredient;
    private RecipeChoice magicIngredient;

    public CompatBrewingRecipe(ItemStack result, RecipeChoice magicIngredient, ItemStack bottleIngredient) {
        this.result = result;
        this.magicIngredient = magicIngredient;
        this.bottleIngredient = bottleIngredient;
    }


    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("result", EivCompatTagUtil.encodeItemStackOnServer(this.result));
        tag.put("magicIngredient", EivCompatTagUtil.writeChoice(this.magicIngredient));
        tag.put("bottleIngredient", EivCompatTagUtil.encodeItemStackOnServer(this.bottleIngredient));

    }

    @Override
    public EivCompatRecipeType<? extends IEivCompatServerRecipe> getRecipeType() {
        return TYPE;
    }
}
