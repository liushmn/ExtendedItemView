package de.crafty.eiv.common.builtin.campfire;

import de.crafty.eiv.common.builtin.BuiltInEivIntegration;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.embeddings.container.RecipeChatEmbedding;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import de.crafty.eiv.common.recipe.rendering.AnimationTicker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;

import java.util.List;

public class CampfireViewRecipe implements IEivViewRecipe {

    private final ResourceLocation id;
    private final SlotContent input, result;
    private final AnimationTicker cookingTicker;

    public CampfireViewRecipe(CampfireCookingRecipe campfireCookingRecipe) {
        this.id = campfireCookingRecipe.getId();

        this.input = SlotContent.of(campfireCookingRecipe.getIngredients().get(0));
        this.result = SlotContent.of(campfireCookingRecipe.getResultItem(null));

        this.cookingTicker = AnimationTicker.create(new ResourceLocation("campfire_cooking_ticker"), 300);
    }

    private CampfireViewRecipe(ResourceLocation id, SlotContent input, SlotContent result, AnimationTicker cookingTicker) {
        this.id = id;
        this.input = input;
        this.result = result;
        this.cookingTicker = cookingTicker;
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return CampfireViewType.INSTANCE;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.input);
        slotFillContext.bindSlot(1, this.result);
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
        return List.of(this.cookingTicker);
    }

    @Override
    public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        guiGraphics.renderItem(new ItemStack(Items.CAMPFIRE), 1, 20);

        int cookingProgress = Math.round(this.cookingTicker.getProgress() * 24);
        guiGraphics.blit(BuiltInEivIntegration.WIDGETS, 25, 1, 14, 0, cookingProgress, 16, 128, 128);
    }

    @Override
    public void renderRecipeInChat(RecipeChatEmbedding.ChatRecipeRenderer renderer, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        renderer.renderItem(guiGraphics, new ItemStack(Items.CAMPFIRE), 4, 23);

        int cookingProgress = Math.round(this.cookingTicker.getProgress() * 24);
        renderer.renderTopLevelTexture(BuiltInEivIntegration.WIDGETS, guiGraphics, 28, 4, 14, 0, cookingProgress, 16, 128, 128);    }

    @Override
    public IEivViewRecipe asChatCopy() {
        return new CampfireViewRecipe(this.id, this.input.copy(), this.result.copy(), this.cookingTicker.copy());
    }
}
