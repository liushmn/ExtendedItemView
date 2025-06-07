package de.crafty.eiv.common.builtin.tipped_arrow;

import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;

public class TippedArrowServerRecipe implements IEivServerRecipe {

    public static final EivRecipeType<TippedArrowServerRecipe> TYPE = EivRecipeType.register(
            ResourceLocation.withDefaultNamespace("tipped_arrow_crafting"),
            () -> new TippedArrowServerRecipe(ItemStack.EMPTY)
    );

    private ItemStack potionStack;

    public TippedArrowServerRecipe(ItemStack potionStack) {
        this.potionStack = potionStack;
    }

    public ItemStack getPotion() {
        return this.potionStack;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("potionStack", EivTagUtil.encodeItemStack(this.potionStack));

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.potionStack = EivTagUtil.decodeItemStack(tag.getCompound("potionStack").orElseGet(CompoundTag::new));

    }

    @Override
    public EivRecipeType<? extends IEivServerRecipe> getRecipeType() {
        return TYPE;
    }
}
