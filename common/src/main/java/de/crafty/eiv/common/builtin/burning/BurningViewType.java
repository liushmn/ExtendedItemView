package de.crafty.eiv.common.builtin.burning;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.embeddings.container.RecipeChatEmbedding;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class BurningViewType implements IEivRecipeViewType {

    public static final BurningViewType INSTANCE = new BurningViewType();


    private static final ResourceLocation BURNING_LOCATION = ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, "textures/gui/type/burning.png");
    private static final ResourceLocation CHAT_BACKGROUND = ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, "textures/gui/embeddings/container/burning.png");


    @Override
    public Component getDisplayName() {
        return Component.translatable("view.eiv.type.burning");
    }

    @Override
    public int getDisplayWidth() {
        return 72;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return BURNING_LOCATION;
    }

    @Override
    public int getSlotCount() {
        return 1;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, 1, 1);
    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.withDefaultNamespace("furnace_burning");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.COAL);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.FURNACE));
    }


    @Override
    public boolean supportsRecipeShare() {
        return true;
    }

    @Override
    public ChatRecipeBackground getChatRecipeBackground() {
        return new ChatRecipeBackground(CHAT_BACKGROUND, 0, 0, 108, 24);
    }

    @Override
    public void placeChatSlots(RecipeChatEmbedding.SlotDefinition slotDefinition) {
        slotDefinition.addSlot(0, 4, 4);
    }
}
