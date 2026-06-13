package de.crafty.eiv.common.mixin.world.item.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTransformRecipe.class)
public interface SmithingTransformRecipeAccessor {


    @Accessor("base")
    Ingredient getBase();

    @Accessor("addition")
    Ingredient getAddition();

    @Accessor("template")
    Ingredient getTemplate();

    @Accessor("result")
    ItemStack getResult();
}
