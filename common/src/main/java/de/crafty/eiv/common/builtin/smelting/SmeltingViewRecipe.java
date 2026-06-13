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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.SmeltingRecipe;

import java.util.List;

public class SmeltingViewRecipe implements IEivViewRecipe {

    private final ResourceLocation id;
    private final SlotContent input, result;
    private final AnimationTicker smeltingTicker;

    public SmeltingViewRecipe(SmeltingRecipe recipe) {

        this.id = recipe.getId();
        this.input = SlotContent.of(recipe.getIngredients().get(0));
        this.result = SlotContent.of(recipe.getResultItem(null));

        this.smeltingTicker = AnimationTicker.create(new ResourceLocation("smelting_tick"), 200);
    }

    private SmeltingViewRecipe(ResourceLocation id, SlotContent input, SlotContent result, AnimationTicker ticker) {
        this.id = id;
        this.input = input;
        this.result = result;
        this.smeltingTicker = ticker;
    }

    @Override
    public SmeltingViewType getViewType() {
        return SmeltingViewType.INSTANCE;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
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

        guiGraphics.blit(BuiltInEivIntegration.WIDGETS, 1, 20 + (14 - litProgress), 0, 14 - litProgress, 14, litProgress, 128, 128);

        guiGraphics.blit(BuiltInEivIntegration.WIDGETS, 24, 19, 14, 0, smeltProgress, 16, 128, 128);
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
        return new SmeltingViewRecipe(this.id, this.input, this.result, this.smeltingTicker.copy());
    }
}
