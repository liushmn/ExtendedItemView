package de.crafty.eiv.common.builtin.smelting;

import com.mojang.datafixers.util.Either;
import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.builtin.blasting.BlastingServerRecipe;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class SmeltingServerRecipe implements IEivServerRecipe {

    public static final EivRecipeType<SmeltingServerRecipe> TYPE = EivRecipeType.register(
            ResourceLocation.withDefaultNamespace("smelting"),
            () -> new SmeltingServerRecipe(null, ItemStack.EMPTY)
    );

    private Ingredient input;
    private ItemStack result;

    public SmeltingServerRecipe(Ingredient input, ItemStack result) {
        this.input = input;
        this.result = result;
    }

    public Ingredient getInput() {
        return this.input;
    }

    public ItemStack getResult() {
        return this.result;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("input", EivTagUtil.writeIngredient(this.input));
        tag.put("result", EivTagUtil.encodeItemStack(this.result));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.input = EivTagUtil.readIngredient(tag.getCompound("input").orElseGet(CompoundTag::new));
        this.result = EivTagUtil.decodeItemStack(tag.getCompound("result").orElseGet(CompoundTag::new));
    }

    @Override
    public EivRecipeType<? extends IEivServerRecipe> getRecipeType() {
        return TYPE;
    }
}
