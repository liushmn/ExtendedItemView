package de.crafty.eiv.servercompat.builtin.shapeless;

import de.crafty.eiv.servercompat.api.recipe.EivCompatRecipeType;
import de.crafty.eiv.servercompat.api.recipe.IEivCompatServerRecipe;
import de.crafty.eiv.servercompat.util.EivCompatTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.List;

public class CompatShapelessRecipe implements IEivCompatServerRecipe {


    public static final EivCompatRecipeType<CompatShapelessRecipe> TYPE = EivCompatRecipeType.register(
            Identifier.withDefaultNamespace("shapeless_crafting")
    );

    private List<RecipeChoice> choices;
    private ItemStack result;

    public CompatShapelessRecipe(List<RecipeChoice> choices, ItemStack result) {
        this.choices = choices;
        this.result = result;
    }


    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("ingredients", EivCompatTagUtil.writeList(this.choices, (origin, tag1) -> EivCompatTagUtil.writeChoice(origin)));
        tag.put("result", EivCompatTagUtil.encodeItemStackOnServer(this.result));
    }


    @Override
    public EivCompatRecipeType<? extends IEivCompatServerRecipe> getRecipeType() {
        return TYPE;
    }
}
