package de.crafty.eiv.common.builtin.smithing;

import com.mojang.datafixers.util.Pair;
import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.builtin.shapeless.ShapelessServerRecipe;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.equipment.trim.TrimPattern;

import java.util.List;

public class SmithingServerRecipe implements IEivServerRecipe {

    public static final EivRecipeType<SmithingServerRecipe> TYPE = EivRecipeType.register(
            ResourceLocation.withDefaultNamespace("smithing"),
            () -> new SmithingServerRecipe(false, null, null, null, null)
    );

    private boolean isTrim;
    private Ingredient base, template, addition;
    private ItemStack result;

    public SmithingServerRecipe(boolean isTrim, Ingredient base, Ingredient template, Ingredient addition, ItemStack result) {
        this.isTrim = isTrim;
        this.base = base;
        this.template = template;
        this.addition = addition;

        this.result = result;
    }

    public boolean isTrim() {
        return this.isTrim;
    }

    public Ingredient getBase() {
        return this.base;
    }

    public Ingredient getTemplate() {
        return this.template;
    }

    public Ingredient getAddition() {
        return this.addition;
    }

    public ItemStack getResult() {
        return this.result;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.putBoolean("isTrim", this.isTrim);
        tag.put("base", EivTagUtil.writeIngredient(this.base));
        tag.put("template", EivTagUtil.writeIngredient(this.template));
        tag.put("addition", EivTagUtil.writeIngredient(this.addition));
        tag.put("result", EivTagUtil.encodeItemStack(this.result));

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.isTrim = tag.getBoolean("isTrim");
        this.base = EivTagUtil.readIngredient(tag.getCompound("base"));
        this.template = EivTagUtil.readIngredient(tag.getCompound("template"));
        this.addition = EivTagUtil.readIngredient(tag.getCompound("addition"));
        this.result = EivTagUtil.decodeItemStack(tag.getCompound("result"));

    }

    @Override
    public EivRecipeType<? extends IEivServerRecipe> getRecipeType() {
        return TYPE;
    }
}
