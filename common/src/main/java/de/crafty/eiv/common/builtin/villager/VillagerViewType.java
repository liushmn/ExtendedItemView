package de.crafty.eiv.common.builtin.villager;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class VillagerViewType implements IEivRecipeViewType {

    protected static final VillagerViewType INSTANCE = new VillagerViewType();

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.eiv.type.trading");
    }

    @Override
    public int getDisplayWidth() {
        return 140;
    }

    @Override
    public int getDisplayHeight() {
        return 36;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, "textures/gui/type/villager.png");
    }

    @Override
    public int getSlotCount() {
        return 3;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

        //Currency
        slotDefinition.addItemSlot(0, 35, 9);
        slotDefinition.addItemSlot(1, 61, 9);

        //offer
        slotDefinition.addItemSlot(2, 119, 10);
    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.withDefaultNamespace("villager_trading");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.EMERALD);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.VILLAGER_SPAWN_EGG));
    }
}
