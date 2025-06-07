package de.crafty.eiv.common.builtin.transmute;

import com.mojang.datafixers.util.Either;
import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.mixin.world.item.crafting.IngredientAccessor;
import de.crafty.eiv.common.mixin.world.item.crafting.TransmuteRecipeAccessor;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.TransmuteRecipe;

import java.util.ArrayList;
import java.util.List;

public class TransmuteServerRecipe implements IEivServerRecipe {

    public static final EivRecipeType<TransmuteServerRecipe> TYPE = EivRecipeType.register(
            ResourceLocation.withDefaultNamespace("transmutation_crafting"),
            () -> new TransmuteServerRecipe(null, null, List.of())

    );

    private Ingredient input;
    private Ingredient material;
    private List<ItemStack> results;

    public TransmuteServerRecipe(Ingredient input, Ingredient material, List<ItemStack> results) {
        this.input = input;
        this.material = material;
        this.results = results;
    }

    public Ingredient getInput() {
        return this.input;
    }

    public Ingredient getMaterial() {
        return this.material;
    }

    public List<ItemStack> getResults() {
        return this.results;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("input", EivTagUtil.writeIngredient(this.input));

        tag.put("materials", EivTagUtil.writeIngredient(this.material));

        tag.put("results", EivTagUtil.writeList(this.results, (origin, tag1) -> EivTagUtil.encodeItemStack(origin)));

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.input = EivTagUtil.readIngredient(tag.getCompound("input").orElseGet(CompoundTag::new));
        this.material = EivTagUtil.readIngredient(tag.getCompound("materials").orElseGet(CompoundTag::new));

        this.results = EivTagUtil.readList(tag, "results", EivTagUtil::decodeItemStack);
    }

    @Override
    public EivRecipeType<? extends IEivServerRecipe> getRecipeType() {
        return TYPE;
    }
}
