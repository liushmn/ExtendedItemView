package de.crafty.eiv.servercompat.builtin.burning;

import de.crafty.eiv.servercompat.api.recipe.EivCompatRecipeType;
import de.crafty.eiv.servercompat.api.recipe.IEivCompatServerRecipe;
import de.crafty.eiv.servercompat.util.EivCompatTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class CompatBurningRecipe implements IEivCompatServerRecipe {

    public static final EivCompatRecipeType<CompatBurningRecipe> TYPE = EivCompatRecipeType.register(
            ResourceLocation.withDefaultNamespace("burning")
    );

    private Item fuel;
    private int burnTime;

    public CompatBurningRecipe(Item fuel, int burnTime) {
        this.fuel = fuel;
        this.burnTime = burnTime;
    }

    public Item getFuel() {
        return this.fuel;
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.putString("fuel", EivCompatTagUtil.itemToString(this.fuel));
        tag.putInt("burnTime", this.burnTime);

    }


    @Override
    public EivCompatRecipeType<? extends IEivCompatServerRecipe> getRecipeType() {
        return TYPE;
    }

}
