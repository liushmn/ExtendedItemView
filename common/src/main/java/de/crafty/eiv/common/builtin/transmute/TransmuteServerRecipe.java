package de.crafty.eiv.common.builtin.transmute;

import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class TransmuteServerRecipe implements IEivServerRecipe {

    public static final EivRecipeType<TransmuteServerRecipe> TYPE = EivRecipeType.register(
            ResourceLocation.withDefaultNamespace("transmutation_crafting"),
            () -> new TransmuteServerRecipe(null, null, ItemStack.EMPTY)

    );

    private Ingredient input;
    private Ingredient material;
    private ItemStack result;

    public TransmuteServerRecipe(Ingredient input, Ingredient material, ItemStack result) {
        this.input = input;
        this.material = material;
        this.result = result;
    }

    public Ingredient getInput() {
        return this.input;
    }

    public Ingredient getMaterial() {
        return this.material;
    }

    public ItemStack getResult() {
        return this.result;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("input", EivTagUtil.writeIngredient(this.input));

        tag.put("materials", EivTagUtil.writeIngredient(this.material));

        tag.put("result", EivTagUtil.encodeItemStack(this.result));

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.input = EivTagUtil.readIngredient(tag.getCompound("input"));
        this.material = EivTagUtil.readIngredient(tag.getCompound("materials"));

        this.result = EivTagUtil.decodeItemStack(tag.getCompound("result"));
    }

    @Override
    public EivRecipeType<? extends IEivServerRecipe> getRecipeType() {
        return TYPE;
    }
}
