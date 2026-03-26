package de.crafty.eiv.common.mixin.world.item.crafting;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.TransmuteRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TransmuteRecipe.class)
public interface TransmuteRecipeAccessor {

    @Accessor(value = "input", remap = false)
    Ingredient getInput();

    @Accessor(value = "material", remap = false)
    Ingredient getMaterial();

    @Accessor(value = "result", remap = false)
    ItemStackTemplate getResult();

}
