package de.crafty.eiv.common.builtin.brewing;

import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class BrewingServerRecipe implements IEivServerRecipe {


    public static final EivRecipeType<BrewingServerRecipe> TYPE = EivRecipeType.register(
            new ResourceLocation("brewing"),
            () -> new BrewingServerRecipe(ItemStack.EMPTY, null, ItemStack.EMPTY)
    );

    private ItemStack result, bottleIngredient;
    private Ingredient magicIngredient;

    public BrewingServerRecipe(ItemStack result, Ingredient magicIngredient, ItemStack bottleIngredient) {
        this.result = result;
        this.magicIngredient = magicIngredient;
        this.bottleIngredient = bottleIngredient;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public Ingredient getMagicIngredient() {
        return this.magicIngredient;
    }

    public ItemStack getBottleIngredient() {
        return this.bottleIngredient;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("result", EivTagUtil.encodeItemStackOnServer(this.result));
        tag.put("magicIngredient", EivTagUtil.writeIngredient(this.magicIngredient));
        tag.put("bottleIngredient", EivTagUtil.encodeItemStackOnServer(this.bottleIngredient));

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.result = EivTagUtil.decodeItemStackOnClient(tag.getCompound("result"));
        this.magicIngredient = EivTagUtil.readIngredient(tag.getCompound("magicIngredient"));
        this.bottleIngredient = EivTagUtil.decodeItemStackOnClient(tag.getCompound("bottleIngredient"));

    }

    @Override
    public EivRecipeType<? extends IEivServerRecipe> getRecipeType() {
        return TYPE;
    }
}
