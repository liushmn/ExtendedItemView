package de.crafty.eiv.servercompat.builtin.shaped;


import de.crafty.eiv.servercompat.api.recipe.EivCompatRecipeType;
import de.crafty.eiv.servercompat.api.recipe.IEivCompatServerRecipe;
import de.crafty.eiv.servercompat.util.EivCompatTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.HashMap;

public class CompatShapedRecipe implements IEivCompatServerRecipe {

    public static final EivCompatRecipeType<CompatShapedRecipe> TYPE = EivCompatRecipeType.register(
            ResourceLocation.withDefaultNamespace("shaped_crafting")
    );


    private HashMap<Integer, RecipeChoice> choices;
    private ItemStack result;
    private int width, height;

    public CompatShapedRecipe(int width, int height, HashMap<Integer, RecipeChoice> choices, ItemStack result) {
        this.choices = choices;
        this.result = result;
        this.width = width;
        this.height = height;
    }


    @Override
    public void writeToTag(CompoundTag tag) {

        tag.putInt("width", this.width);
        tag.putInt("height", this.height);

        this.choices.forEach((slotId, choice) -> {
            tag.put("ci_" + slotId, EivCompatTagUtil.writeChoice(choice));
        });

        tag.put("result", EivCompatTagUtil.encodeItemStackOnServer(this.result));
    }


    @Override
    public EivCompatRecipeType<? extends IEivCompatServerRecipe> getRecipeType() {
        return TYPE;
    }
}
