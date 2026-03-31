package de.crafty.eiv.common.builtin.brewing;

import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.builtin.BuiltInEivIntegration;
import de.crafty.eiv.common.embeddings.container.RecipeChatEmbedding;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import de.crafty.eiv.common.recipe.rendering.AnimationTicker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class BrewingViewRecipe implements IEivViewRecipe {

    private static final int[] BUBBLELENGTHS = new int[]{29, 24, 20, 16, 11, 6, 0};


    private final SlotContent bottle1, bottle2, bottle3;
    private final SlotContent result, magicIngredient;

    private final AnimationTicker brewProgressTicker;

    public BrewingViewRecipe(BrewingServerRecipe brewingServerRecipe) {

        this.bottle1 = SlotContent.of(brewingServerRecipe.getBottleIngredient().copy());
        this.bottle2 = SlotContent.of(brewingServerRecipe.getBottleIngredient().copy());
        this.bottle3 = SlotContent.of(brewingServerRecipe.getBottleIngredient().copy());

        this.result = SlotContent.of(brewingServerRecipe.getResult());
        this.magicIngredient = SlotContent.of(brewingServerRecipe.getMagicIngredient());

        this.brewProgressTicker = AnimationTicker.create(new ResourceLocation("brew_progress_tick"), 400);
    }

    private BrewingViewRecipe(SlotContent bottle1, SlotContent bottle2, SlotContent bottle3, SlotContent result, SlotContent magicIngredient, AnimationTicker brewProgressTicker) {
        this.bottle1 = bottle1;
        this.bottle2 = bottle2;
        this.bottle3 = bottle3;
        this.result = result;
        this.magicIngredient = magicIngredient;
        this.brewProgressTicker = brewProgressTicker;
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return BrewingViewType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        slotFillContext.bindSlot(0, this.result);
        slotFillContext.bindSlot(1, this.magicIngredient);

        slotFillContext.bindSlot(2, this.bottle1);
        slotFillContext.bindSlot(3, this.bottle2);
        slotFillContext.bindSlot(4, this.bottle3);

    }



    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.bottle1, this.bottle2, this.bottle3, this.magicIngredient);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.result);
    }


    @Override
    public List<AnimationTicker> getAnimationTickers() {
        return List.of(this.brewProgressTicker);
    }

    @Override
    public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        guiGraphics.blit(BuiltInEivIntegration.WIDGETS, 39, 30, 38, 0, 18, 4, 128, 128);

        int brewProgress = Math.round(this.brewProgressTicker.getProgress() * 28);
        guiGraphics.blit(BuiltInEivIntegration.WIDGETS, 76, 2, 56, 0, 9, brewProgress, 128, 128);

        int bubbleProgress = 29 - BUBBLELENGTHS[this.brewProgressTicker.getTick() / 2 % 7];
        guiGraphics.blit(BuiltInEivIntegration.WIDGETS, 42, 29 - bubbleProgress, 64, 29 - bubbleProgress, 13, bubbleProgress, 128, 128);
    }


    @Override
    public void renderRecipeInChat(RecipeChatEmbedding.ChatRecipeRenderer renderer, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderer.renderTopLevelTexture(BuiltInEivIntegration.WIDGETS, guiGraphics, (39 + 3), (30 + 3), 38, 0, 18, 4, 128, 128);

        int brewProgress = Math.round(this.brewProgressTicker.getProgress() * 28);
        renderer.renderTopLevelTexture(BuiltInEivIntegration.WIDGETS, guiGraphics, (76 + 3), (2 + 3), 56, 0, 9, brewProgress, 128, 128);

        int bubbleProgress = 29 - BUBBLELENGTHS[this.brewProgressTicker.getTick() / 2 % 7];
        renderer.renderTopLevelTexture(BuiltInEivIntegration.WIDGETS, guiGraphics, (42 + 3), 29 + 3 - bubbleProgress, 64, 29 - bubbleProgress, 13, bubbleProgress, 128, 128);    }

    @Override
    public IEivViewRecipe asChatCopy() {
        return new BrewingViewRecipe(this.bottle1.copy(), this.bottle2.copy(), this.bottle3.copy(), this.result.copy(), this.magicIngredient.copy(), this.brewProgressTicker.copy());
    }
}
