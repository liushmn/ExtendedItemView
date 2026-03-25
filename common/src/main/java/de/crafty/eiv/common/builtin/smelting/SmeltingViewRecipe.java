package de.crafty.eiv.common.builtin.smelting;

import de.crafty.eiv.common.builtin.BuiltInEivIntegration;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.embeddings.container.RecipeChatEmbedding;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import de.crafty.eiv.common.recipe.rendering.AnimationTicker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class SmeltingViewRecipe implements IEivViewRecipe {

    private final SlotContent input, result;
    private final AnimationTicker smeltingTicker;

    public SmeltingViewRecipe(SmeltingServerRecipe recipe) {

        this.input = SlotContent.of(recipe.getInput());
        this.result = SlotContent.of(recipe.getResult());

        this.smeltingTicker = AnimationTicker.create(ResourceLocation.withDefaultNamespace("smelting_tick"), 200);
    }

    private SmeltingViewRecipe(SlotContent input, SlotContent result, AnimationTicker ticker) {
        this.input = input;
        this.result = result;
        this.smeltingTicker = ticker;
    }

    @Override
    public SmeltingViewType getViewType() {
        return SmeltingViewType.INSTANCE;
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
        return List.of(this.smeltingTicker);
    }

    @Override
    public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        int litProgress = Math.round(this.smeltingTicker.getProgress() * 14);
        int smeltProgress = Math.round(this.smeltingTicker.getProgress() * 24);

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BuiltInEivIntegration.WIDGETS, 1, 20 + (14 - litProgress), 0, 14 - litProgress, 14, litProgress, 128, 128);

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BuiltInEivIntegration.WIDGETS, 24, 19, 14, 0, smeltProgress, 16, 128, 128);
    }


    @Override
    public boolean supportsItemTransfer() {
        return true;
    }

    @Override
    public Class<? extends AbstractContainerScreen<?>> getTransferClass() {
        return FurnaceScreen.class;
    }

    @Override
    public void mapRecipeItems(RecipeTransferMap transferMap, AbstractContainerScreen<?> screen) {

        transferMap.linkSlots(0, 0);
    }


    @Override
    public void renderRecipeInChat(RecipeChatEmbedding.ChatRecipeRenderer renderer, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int litProgress = Math.round(this.smeltingTicker.getProgress() * 14);
        int smeltProgress = Math.round(this.smeltingTicker.getProgress() * 24);

        //renderer.renderTexture(BuiltInEivIntegration.WIDGETS, guiGraphics, Math.round(4 / 2.0F), Math.round(23 / 2.0F), 0, 0, 14, 14, 128, 128);

        renderer.renderTopLevelTexture(BuiltInEivIntegration.WIDGETS, guiGraphics, 4, 23 + (14 - litProgress), 0, 14 - litProgress, 14, litProgress, 128, 128);

        renderer.renderTopLevelTexture(BuiltInEivIntegration.WIDGETS, guiGraphics, 27, 22, 14, 0, smeltProgress, 16, 128, 128);
    }


    @Override
    public IEivViewRecipe asChatCopy() {
        return new SmeltingViewRecipe(this.input, this.result, this.smeltingTicker.copy());
    }
}
