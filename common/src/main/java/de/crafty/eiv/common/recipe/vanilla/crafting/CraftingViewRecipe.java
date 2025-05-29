package de.crafty.eiv.common.recipe.vanilla.crafting;

import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.builtin.shaped.ShapedServerRecipe;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.HashMap;
import java.util.List;

public class CraftingViewRecipe implements IEivViewRecipe {

    private final HashMap<Integer, SlotContent> ingredientSlotContents = new HashMap<>();
    private final SlotContent result;
    private final int width, height;

    public CraftingViewRecipe(ShapedServerRecipe recipe) {

        this.width = recipe.getWidth();
        this.height = recipe.getHeight();

        recipe.getIngredients().forEach((slotId, ingredient) -> this.ingredientSlotContents.put(slotId, SlotContent.of(ingredient)));
        this.result = SlotContent.of(recipe.getResult());


    }

    @Override
    public IEivRecipeViewType getViewType() {
        return CraftingViewType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        this.ingredientSlotContents.forEach(slotFillContext::bindSlot);
        slotFillContext.bindSlot(9, this.result);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return this.ingredientSlotContents.values().stream().toList();
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.result);
    }

    @Override
    public boolean supportsItemTransfer() {
        return true;
    }

    @Override
    public List<Class<? extends AbstractContainerScreen<?>>> getTransferClasses() {
        return List.of(CraftingScreen.class, InventoryScreen.class);
    }

    @Override
    public boolean canTransferToScreen(AbstractContainerScreen<?> screen) {
        return screen instanceof CraftingScreen || this.width <= 2 && this.height <= 2;
    }

    @Override
    public void mapRecipeItems(RecipeTransferMap map, AbstractContainerScreen<?> screen) {

        if (!(screen instanceof InventoryScreen invScreen)) {
            map.linkSlots(0, 1);
            map.linkSlots(1, 2);
            map.linkSlots(2, 3);
            map.linkSlots(3, 4);
            map.linkSlots(4, 5);
            map.linkSlots(5, 6);
            map.linkSlots(6, 7);
            map.linkSlots(7, 8);
            map.linkSlots(8, 9);

        }else {
            //For smaller grid
            map.linkSlots(0, 1);
            map.linkSlots(1, 2);
            map.linkSlots(3, 3);
            map.linkSlots(4, 4);
        }


    }
}
