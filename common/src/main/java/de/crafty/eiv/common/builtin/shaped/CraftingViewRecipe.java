package de.crafty.eiv.common.builtin.shaped;

import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.HashMap;
import java.util.List;

public class CraftingViewRecipe implements IEivViewRecipe {

    private final HashMap<Integer, SlotContent> ingredientSlotContents = new HashMap<>();
    private final SlotContent result;
    private final int width, height;

    private ResourceLocation id;

    public CraftingViewRecipe(ShapedRecipe recipe) {

        this.id = recipe.getId();

        this.width = recipe.getWidth();
        this.height = recipe.getHeight();

        for(int i = 0; i < recipe.getIngredients().size(); i++){
            this.ingredientSlotContents.put(i, SlotContent.of(recipe.getIngredients().get(i)));
        }

        this.result = SlotContent.of(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));

    }

    private CraftingViewRecipe(HashMap<Integer, SlotContent> ingredientSlotContents, SlotContent result, int width, int height) {
        this.ingredientSlotContents.putAll(ingredientSlotContents);
        this.result = result;
        this.width = width;
        this.height = height;
    }

    //TODO reimplement tipped arrow recipes

    @Override
    public IEivRecipeViewType getViewType() {
        return CraftingViewType.INSTANCE;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
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

        } else {
            //For smaller grid
            map.linkSlots(0, 1);
            map.linkSlots(1, 2);
            map.linkSlots(3, 3);
            map.linkSlots(4, 4);
        }
    }


    @Override
    public IEivViewRecipe asChatCopy() {

        HashMap<Integer, SlotContent> map = new HashMap<>();
        this.ingredientSlotContents.forEach((slotId, slotContent) -> map.put(slotId, slotContent.copy()));

        return new CraftingViewRecipe(map, this.result.copy(), this.width, this.height);
    }
}
