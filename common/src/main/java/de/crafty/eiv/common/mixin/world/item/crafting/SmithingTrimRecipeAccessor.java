package de.crafty.eiv.common.mixin.world.item.crafting;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTrimRecipe.class)
public interface SmithingTrimRecipeAccessor {


    @Accessor("base")
    Ingredient getBase();

    @Accessor("addition")
    Ingredient getAddition();

    @Accessor("template")
    Ingredient getTemplate();
}
