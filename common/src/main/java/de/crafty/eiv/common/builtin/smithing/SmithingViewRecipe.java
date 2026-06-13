package de.crafty.eiv.common.builtin.smithing;

import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.*;
import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//TODO split Smithing recipes in upgrade and trim recipes
public class SmithingViewRecipe implements IEivViewRecipe {

    private final ResourceLocation id;
    private final SlotContent additionIngredient;
    private final SlotContent base, template;
    private final SlotContent result;
    private final boolean isTrimType;

    public SmithingViewRecipe(ResourceLocation id, Ingredient additionIngredient, ItemStack base, ItemStack template, ItemStack result, boolean isTrimType) {
        this.id = id;

        this.isTrimType = isTrimType;

        this.additionIngredient = SlotContent.of(additionIngredient);
        this.base = SlotContent.of(base);
        this.template = SlotContent.of(template);

        if (isTrimType) {
            Optional<Holder.Reference<TrimPattern>> pattern = TrimPatterns.getFromTemplate(Minecraft.getInstance().level.registryAccess(), template);

            List<ItemStack> results = new ArrayList<>();
            this.additionIngredient.getValidContents().forEach(addition -> {
                Optional<Holder.Reference<TrimMaterial>> material = TrimMaterials.getFromIngredient(Minecraft.getInstance().level.registryAccess(), addition);

                if (material.isEmpty() || pattern.isEmpty())
                    return;

                ItemStack resultStack = base.copy();
                ArmorTrim trim = new ArmorTrim(material.get(), pattern.get());
                ArmorTrim.setTrim(Minecraft.getInstance().level.registryAccess(), resultStack, trim);
                results.add(resultStack);
            });

            this.result = SlotContent.of(results);
            return;
        }

        ItemStack resultStack = result.copy();
        if (base.hasTag())
            resultStack.setTag(base.getOrCreateTag().copy());

        this.result = SlotContent.of(resultStack);
    }

    private SmithingViewRecipe(ResourceLocation id, SlotContent additionIngredient, SlotContent base, SlotContent template, SlotContent result, boolean isTrimType) {
        this.id = id;

        this.isTrimType = isTrimType;

        this.additionIngredient = additionIngredient;
        this.base = base;
        this.template = template;
        this.result = result;
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return SmithingViewType.INSTANCE;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        slotFillContext.bindSlot(0, this.template);
        slotFillContext.bindSlot(1, this.base);
        slotFillContext.bindSlot(2, this.additionIngredient);

        slotFillContext.bindDependantSlot(3, this.additionIngredient::index, this.result);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.template, this.base, this.additionIngredient);
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
    public void mapRecipeItems(RecipeTransferMap transferMap, AbstractContainerScreen<?> screen) {

        transferMap.linkSlots(0, 0);
        transferMap.linkSlots(1, 1);
        transferMap.linkSlots(2, 2);

    }


    @Override
    public IEivViewRecipe asChatCopy() {
        return new SmithingViewRecipe(this.id, this.additionIngredient.copy(), this.base.copy(), this.template.copy(), this.result.copy(), this.isTrimType);
    }
}
