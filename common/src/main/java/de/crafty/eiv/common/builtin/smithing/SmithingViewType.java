package de.crafty.eiv.common.builtin.smithing;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.embeddings.container.RecipeChatEmbedding;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class SmithingViewType implements IEivRecipeViewType {

    protected static final SmithingViewType INSTANCE = new SmithingViewType();

    private static final ResourceLocation SMITHING_LOCATION = new ResourceLocation(CommonEIV.MODID, "textures/gui/type/smithing.png");
    private static final ResourceLocation CHAT_BACKGROUND = new ResourceLocation(CommonEIV.MODID, "textures/gui/embeddings/container/smithing.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.eiv.type.smithing");
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation("smithing");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.SMITHING_TABLE);
    }

    @Override
    public int getDisplayWidth() {
        return 108;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return SMITHING_LOCATION;
    }

    @Override
    public int getSlotCount() {
        return 4;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

        //Base
        slotDefinition.addItemSlot(0, 1, 1);

        //Addition
        slotDefinition.addItemSlot(1, 19, 1);

        //Template
        slotDefinition.addItemSlot(2, 37, 1);

        //Result
        slotDefinition.addItemSlot(3, 91, 1);
    }


    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.SMITHING_TABLE));
    }


    @Override
    public boolean supportsRecipeShare() {
        return true;
    }

    @Override
    public ChatRecipeBackground getChatRecipeBackground() {
        return new ChatRecipeBackground(CHAT_BACKGROUND, 0, 0, 114, 24);
    }

    @Override
    public void placeChatSlots(RecipeChatEmbedding.SlotDefinition slotDefinition) {

        //Base
        slotDefinition.addSlot(0, 4, 4);

        //Addition
        slotDefinition.addSlot(1, 22, 4);

        //Template
        slotDefinition.addSlot(2, 40, 4);

        //Result
        slotDefinition.addSlot(3, 94, 4);

    }
}
