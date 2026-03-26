package de.crafty.eiv.common.builtin.smoking;

import de.crafty.eiv.common.builtin.BuiltInEivIntegration;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.embeddings.container.RecipeChatEmbedding;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import de.crafty.eiv.common.recipe.rendering.AnimationTicker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.SmokerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.List;

public class SmokingViewRecipe implements IEivViewRecipe {

    private final SlotContent input, result;
    private final AnimationTicker smokingTicker;

    public SmokingViewRecipe(SmokingServerRecipe smokingRecipe) {

        this.input = SlotContent.of(smokingRecipe.getInput());
        this.result = SlotContent.of(smokingRecipe.getResult());

        this.smokingTicker = AnimationTicker.create(Identifier.withDefaultNamespace("smoking_ticker"), 100);
    }

    private SmokingViewRecipe(SlotContent input, SlotContent result, AnimationTicker ticker) {
        this.input = input;
        this.result = result;
        this.smokingTicker = ticker;
    }


    @Override
    public IEivRecipeViewType getViewType() {
        return SmokingViewType.INSTANCE;
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
    public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTicks) {
        int litProgress = Math.round(this.smokingTicker.getProgress() * 14);
        int smeltProgress = Math.round(this.smokingTicker.getProgress() * 24);

        guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, BuiltInEivIntegration.WIDGETS, 1, 20 + (14 - litProgress), 0, 14 - litProgress, 14, litProgress, 128, 128);

        guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, BuiltInEivIntegration.WIDGETS, 24, 19, 14, 0, smeltProgress, 16, 128, 128);
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
    public void renderRecipeInChat(RecipeChatEmbedding.ChatRecipeRenderer renderer, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTicks) {
        int litProgress = Math.round(this.smokingTicker.getProgress() * 14);
        int smeltProgress = Math.round(this.smokingTicker.getProgress() * 24);

        renderer.renderTopLevelTexture(BuiltInEivIntegration.WIDGETS, guiGraphicsExtractor, 4, 23 + (14 - litProgress), 0, 14 - litProgress, 14, litProgress, 128, 128);

        renderer.renderTopLevelTexture(BuiltInEivIntegration.WIDGETS, guiGraphicsExtractor, 27, 22, 14, 0, smeltProgress, 16, 128, 128);
    }


    @Override
    public IEivViewRecipe asChatCopy() {
        return new SmokingViewRecipe(this.input.copy(), this.result.copy(), this.smokingTicker.copy());
    }
}
