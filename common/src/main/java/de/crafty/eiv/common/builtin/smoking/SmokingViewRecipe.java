package de.crafty.eiv.common.builtin.smoking;

import de.crafty.eiv.common.builtin.BuiltInEivIntegration;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.embeddings.container.RecipeChatEmbedding;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import de.crafty.eiv.common.recipe.rendering.AnimationTicker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.SmokerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.SmokingRecipe;

import java.util.List;

public class SmokingViewRecipe implements IEivViewRecipe {

    private final ResourceLocation id;
    private final SlotContent input, result;
    private final AnimationTicker smokingTicker;

    public SmokingViewRecipe(SmokingRecipe smokingRecipe) {
        this.id = smokingRecipe.getId();
        this.input = SlotContent.of(smokingRecipe.getIngredients().get(0));
        this.result = SlotContent.of(smokingRecipe.getResultItem(null));

        this.smokingTicker = AnimationTicker.create(new ResourceLocation("smoking_ticker"), 100);
    }

    private SmokingViewRecipe(ResourceLocation id, SlotContent input, SlotContent result, AnimationTicker ticker) {
        this.id = id;
        this.input = input;
        this.result = result;
        this.smokingTicker = ticker;
    }


    @Override
    public IEivRecipeViewType getViewType() {
        return SmokingViewType.INSTANCE;
    }

    @Override
    public ResourceLocation getId() {
        return null;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.input);
        slotFillContext.bindSlot(2, this.result);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.input);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.result);
    }

    @Override
    public List<AnimationTicker> getAnimationTickers() {
        return List.of(this.smokingTicker);
    }

    @Override
    public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int litProgress = Math.round(this.smokingTicker.getProgress() * 14);
        int smeltProgress = Math.round(this.smokingTicker.getProgress() * 24);

        guiGraphics.blit(BuiltInEivIntegration.WIDGETS, 1, 20 + (14 - litProgress), 0, 14 - litProgress, 14, litProgress, 128, 128);

        guiGraphics.blit(BuiltInEivIntegration.WIDGETS, 24, 19, 14, 0, smeltProgress, 16, 128, 128);
    }


    @Override
    public boolean supportsItemTransfer() {
        return true;
    }

    @Override
    public Class<? extends AbstractContainerScreen<?>> getTransferClass() {
        return SmokerScreen.class;
    }

    @Override
    public void mapRecipeItems(RecipeTransferMap transferMap, AbstractContainerScreen<?> screen) {

        transferMap.linkSlots(0, 0);

    }


    @Override
    public void renderRecipeInChat(RecipeChatEmbedding.ChatRecipeRenderer renderer, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int litProgress = Math.round(this.smokingTicker.getProgress() * 14);
        int smeltProgress = Math.round(this.smokingTicker.getProgress() * 24);

        renderer.renderTopLevelTexture(BuiltInEivIntegration.WIDGETS, guiGraphics, 4, 23 + (14 - litProgress), 0, 14 - litProgress, 14, litProgress, 128, 128);

        renderer.renderTopLevelTexture(BuiltInEivIntegration.WIDGETS, guiGraphics, 27, 22, 14, 0, smeltProgress, 16, 128, 128);
    }


    @Override
    public IEivViewRecipe asChatCopy() {
        return new SmokingViewRecipe(this.id, this.input.copy(), this.result.copy(), this.smokingTicker.copy());
    }
}
