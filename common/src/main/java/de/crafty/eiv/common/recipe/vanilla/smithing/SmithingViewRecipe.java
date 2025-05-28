package de.crafty.eiv.common.recipe.vanilla.smithing;

import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.builtin.smoking.SmokingServerRecipe;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SmithingViewRecipe implements IEivViewRecipe {

    private final SlotContent additionIngredient;
    private final ItemStack base, template;
    private final SlotContent result;

    private final boolean isTrimType;

    public SmithingViewRecipe(boolean isTrimType, Ingredient additionIngredient, ItemStack base, ItemStack template, TrimPattern trimPattern) {
        this.isTrimType = isTrimType;

        this.template = template;
        this.base = base;
        this.additionIngredient = additionIngredient != null ? SlotContent.of(additionIngredient) : SlotContent.of(Items.AIR);


        if (Minecraft.getInstance().player == null) {
            this.result = SlotContent.of(Items.AIR);
            return;
        }

        HolderLookup.Provider provider = Minecraft.getInstance().player.clientLevel.registryAccess();

        if (this.isTrimType) {
            List<ItemStack> possibleResults = new ArrayList<>();

            this.additionIngredient.getValidContents().forEach(addition -> {
                possibleResults.add(SmithingTrimRecipe.applyTrim(provider, this.base, addition, Holder.direct(trimPattern)));
            });

            this.result = SlotContent.of(possibleResults);

            return;
        }

        this.result = SlotContent.of(new TransmuteResult(this.template.getItem()).apply(this.base));
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return SmithingViewType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        slotFillContext.bindSlot(0, SlotContent.of(this.template));
        slotFillContext.bindSlot(1, SlotContent.of(this.base));
        slotFillContext.bindSlot(2, this.additionIngredient);

        slotFillContext.bindDepedantSlot(3, this.additionIngredient::index, this.result);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(SlotContent.of(this.template), SlotContent.of(this.base), this.additionIngredient);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.result);
    }

    @Override
    public int getPriority() {
        return this.isTrimType ? 1 : 0;
    }


    @Override
    public boolean supportsItemTransfer() {
        return true;
    }

    @Override
    public Class<? extends AbstractContainerScreen<?>> getTransferClass() {
        return SmithingScreen.class;
    }

    @Override
    public void mapRecipeItems(RecipeTransferMap transferMap) {

        transferMap.linkSlots(0, 0);
        transferMap.linkSlots(1, 1);
        transferMap.linkSlots(2, 2);

    }
}
