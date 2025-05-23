package de.crafty.eiv.common.api.recipe;

import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.common.recipe.rendering.AnimationTicker;
import de.crafty.eiv.common.recipe.vanilla.crafting.CraftingViewType;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.util.HashMap;
import java.util.List;

public interface IEivViewRecipe {

    List<IEivViewRecipe> PLACEHOLDER = List.of(
            new IEivViewRecipe() {


                @Override
                public IEivRecipeViewType getViewType() {
                    return CraftingViewType.INSTANCE;
                }

                @Override
                public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
                }

                @Override
                public List<SlotContent> getIngredients() {
                    return List.of();
                }

                @Override
                public List<SlotContent> getResults() {
                    return List.of();
                }

            }
    );

    IEivRecipeViewType getViewType();

    void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext);

    List<SlotContent> getIngredients();

    List<SlotContent> getResults();

    default int getPriority() {
        return 0;
    }


    default List<AnimationTicker> getAnimationTickers() {
        return List.of();
    }

    default void renderRecipe(RecipeViewScreen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

    }


    default boolean supportsItemTransfer() {
        return false;
    }

    default Class<? extends AbstractContainerScreen<?>> getTransferClass() {
        return null;
    }

    default void mapRecipeItems(RecipeTransferMap transferMap) {
    }


    class RecipeTransferMap {

        private HashMap<Integer, Integer> map;

        public RecipeTransferMap() {
            map = new HashMap<>();
        }

        public void linkSlots(int recipeSlot, int destSlot) {
            this.map.put(recipeSlot, destSlot);
        }

        public HashMap<Integer, Integer> getTransferMap() {
            return this.map;
        }
    }

}
