package de.crafty.eiv.common.builtin.blasting;

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
import net.minecraft.client.gui.screens.inventory.BlastFurnaceScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class BlastingViewRecipe implements IEivViewRecipe {

    private final SlotContent input, result;
    private final AnimationTicker blastingTicker;

    public BlastingViewRecipe(BlastingServerRecipe blastingRecipe) {

        this.input = SlotContent.of(blastingRecipe.getInput());
        this.result = SlotContent.of(blastingRecipe.getResult());

        this.blastingTicker = AnimationTicker.create(ResourceLocation.withDefaultNamespace("blasting_ticker"), 100);
    }

    private BlastingViewRecipe(SlotContent input, SlotContent result, AnimationTicker ticker) {
        this.input = input;
        this.result = result;
        this.blastingTicker = ticker;
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return BlastingViewType.INSTANCE;
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
        return List.of(this.blastingTicker);
    }

    @Override
    public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int litProgress = Math.round(this.blastingTicker.getProgress() * 14);
        int smeltProgress = Math.round(this.blastingTicker.getProgress() * 24);

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BuiltInEivIntegration.WIDGETS, 1, 20 + (14 - litProgress), 0, 14 - litProgress, 14, litProgress, 128, 128);

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BuiltInEivIntegration.WIDGETS, 24, 19, 14, 0, smeltProgress, 16, 128, 128);
    }


    @Override
    public boolean supportsItemTransfer() {
        return true;
    }

    @Override
    public Class<? extends AbstractContainerScreen<?>> getTransferClass() {
        return BlastFurnaceScreen.class;
    }

    @Override
    public void mapRecipeItems(RecipeTransferMap transferMap, AbstractContainerScreen<?> screen) {

        transferMap.linkSlots(0, 0);

    }


    @Override
    public void renderRecipeInChat(RecipeChatEmbedding.ChatRecipeRenderer renderer, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int litProgress = Math.round(this.blastingTicker.getProgress() * 14);
        int smeltProgress = Math.round(this.blastingTicker.getProgress() * 24);

        renderer.renderTopLevelTexture(BuiltInEivIntegration.WIDGETS, guiGraphics, 4, 23 + (14 - litProgress), 0, 14 - litProgress, 14, litProgress, 128, 128);

        renderer.renderTopLevelTexture(BuiltInEivIntegration.WIDGETS, guiGraphics, 27, 22, 14, 0, smeltProgress, 16, 128, 128);
    }


    @Override
    public IEivViewRecipe asChatCopy() {
        return new BlastingViewRecipe(this.input.copy(), this.result.copy(), this.blastingTicker.copy());
    }
}
