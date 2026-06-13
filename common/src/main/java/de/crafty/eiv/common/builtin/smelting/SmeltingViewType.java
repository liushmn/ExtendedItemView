package de.crafty.eiv.common.builtin.smelting;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.embeddings.container.RecipeChatEmbedding;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class SmeltingViewType implements IEivRecipeViewType {

    public static final SmeltingViewType INSTANCE = new SmeltingViewType();

    private static final ResourceLocation SMELTING_LOCATION = new ResourceLocation(CommonEIV.MODID, "textures/gui/type/smelting.png");
    private static final ResourceLocation CHAT_BACKGROUND = new ResourceLocation(CommonEIV.MODID, "textures/gui/embeddings/container/smelting.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.eiv.type.smelting");
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation("furnace_smelting");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.FURNACE);
    }

    @Override
    public int getDisplayWidth() {
        return 82;
    }

    @Override
    public int getDisplayHeight() {
        return 54;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return SMELTING_LOCATION;
    }

    @Override
    public int getSlotCount() {
        return 3;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

        //Input Slot
        slotDefinition.addItemSlot(0, 1, 1);

        //Fuel Slot
        slotDefinition.addItemSlot(1, 1, 37);

        //Result Slot
        slotDefinition.addItemSlot(2, 61, 19);
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
        return new ChatRecipeBackground(CHAT_BACKGROUND, 0, 0, 88, 60);
    }

    @Override
    public void placeChatSlots(RecipeChatEmbedding.SlotDefinition slotDefinition) {

        //Input Slot
        slotDefinition.addSlot(0, 4, 4);

        //Fuel Slot
        slotDefinition.addSlot(1, 4, 40);

        //Result Slot
        slotDefinition.addSlot(2, 64, 22);

    }
}
