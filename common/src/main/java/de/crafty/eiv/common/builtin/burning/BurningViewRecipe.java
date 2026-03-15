package de.crafty.eiv.common.builtin.burning;

import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.builtin.BuiltInEivIntegration;
import de.crafty.eiv.common.embeddings.container.RecipeChatEmbedding;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import de.crafty.eiv.common.recipe.rendering.AnimationTicker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

public class BurningViewRecipe implements IEivViewRecipe {

    private final SlotContent fuel;
    private final int burnTime;

    private final AnimationTicker ticker;

    public BurningViewRecipe(BurningServerRecipe recipe) {
        this.fuel = SlotContent.of(recipe.getFuel());
        this.burnTime = recipe.getBurnTime();

        this.ticker = AnimationTicker.create(Identifier.withDefaultNamespace("burning_tick_" + this.burnTime), this.burnTime);
    }

    private BurningViewRecipe(SlotContent fuel,  int burnTime, AnimationTicker ticker) {
        this.fuel = fuel;
        this.burnTime = burnTime;
        this.ticker = ticker;
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return BurningViewType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.fuel);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.fuel);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of();
    }


    @Override
    public List<AnimationTicker> getAnimationTickers() {
        return List.of(this.ticker);
    }


    @Override
    public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int burnProgress = Math.round(this.ticker.getProgress() * 14);

        Font font = Minecraft.getInstance().font;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BuiltInEivIntegration.WIDGETS, 19, 2 + (14 - burnProgress), 0, 14 - burnProgress, 14, burnProgress, 128, 128);
        guiGraphics.drawString(font, Component.literal(this.burnTime + " ticks"), 38, 18 / 2 - font.lineHeight / 2, 0xFF808080, false);
    }


    @Override
    public void renderChatRecipeBackground(RecipeChatEmbedding.ChatRecipeRenderer renderer, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        IEivRecipeViewType.ChatRecipeBackground background = this.getViewType().getChatRecipeBackground();

        renderer.renderTexture(background.texture(), guiGraphics, 0, 0, 0, 0, 40, 24, 108, 24);

        int occupiedTextSpace = Minecraft.getInstance().font.width(Component.literal(this.burnTime + " ticks")) + 2;

        renderer.renderTexture(background.texture(), guiGraphics, 40, 0, 40, 0, Math.min(108 - 40 - 2, occupiedTextSpace), 24, 108, 24);
        renderer.renderTexture(background.texture(), guiGraphics, 40 + Math.min(108 - 38 - 2, occupiedTextSpace), 0, 108 - 2, 0, 2, 24, 108, 24);

    }

    @Override
    public void renderRecipeInChat(RecipeChatEmbedding.ChatRecipeRenderer renderer, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int burnProgress = Math.round(this.ticker.getProgress() * 14);

        Font font = Minecraft.getInstance().font;

        renderer.renderTopLevelTexture(BuiltInEivIntegration.WIDGETS, guiGraphics, 22, 5 + (14 - burnProgress), 0, 14 - burnProgress, 14, burnProgress, 128, 128);
        renderer.drawString(font, guiGraphics, Component.literal(this.burnTime + " ticks"), 40, 21 / 2.0F - font.lineHeight / 2.0F + 3, 0xFF808080, false);
    }


    @Override
    public int getSenderXPosition() {
        int occupiedTextSpace = Minecraft.getInstance().font.width(Component.literal(this.burnTime + " ticks")) + 2;
        return 40 + Math.min(108 - 38 - 2, occupiedTextSpace) + 2 + 4;
    }

    @Override
    public IEivViewRecipe asChatCopy() {
        return new BurningViewRecipe(this.fuel.copy(), this.burnTime, this.ticker.copy());
    }
}
