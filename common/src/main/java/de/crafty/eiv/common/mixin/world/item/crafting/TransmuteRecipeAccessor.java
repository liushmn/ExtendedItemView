package de.crafty.eiv.common.mixin.world.item.crafting;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.TransmuteRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TransmuteRecipe.class)
public interface TransmuteRecipeAccessor {

    @Accessor("input")
    Ingredient getInput();

    @Accessor("material")
    Ingredient getMaterial();

    @Accessor("result")
    Holder<Item> getResult();

}
