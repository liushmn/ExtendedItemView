package de.crafty.eiv.common.builtin.shaped;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.embeddings.container.RecipeChatEmbedding;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class CraftingViewType implements IEivRecipeViewType {

    public static final CraftingViewType INSTANCE = new CraftingViewType();

    private static final ResourceLocation CRAFTING_LOCATION = ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, "textures/gui/type/crafting.png");
    private static final ResourceLocation CHAT_BACKGROUND = ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, "textures/gui/embeddings/container/crafting.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.eiv.type.crafting");
    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.withDefaultNamespace("crafting_shaped");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.CRAFTING_TABLE);
    }

    @Override
    public int getDisplayWidth() {
        return 116;
    }


    @Override
    public int getDisplayHeight() {
        return 54;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return CRAFTING_LOCATION;
    }


    @Override
    public int getSlotCount() {
        return 10;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

        //Input slots
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                slotDefinition.addItemSlot(x + y * 3, 1 + x * 18,  1 + y * 18);
            }
        }

        //Result Slot
        slotDefinition.addItemSlot(9, 95, 19);

    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.CRAFTING_TABLE), new ItemStack(Items.CRAFTER));
    }

    @Override
    public boolean supportsRecipeShare() {
        return true;
    }

    @Override
    public ChatRecipeBackground getChatRecipeBackground() {
        return new ChatRecipeBackground(CHAT_BACKGROUND, 0, 0, 122, 60);
    }

    @Override
    public void placeChatSlots(RecipeChatEmbedding.SlotDefinition slotDefinition) {

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                slotDefinition.addSlot(y * 3 + x, 4 + x * 18, 4 + y * 18);
            }
        }

        slotDefinition.addSlot(9, 98, 22);

    }

}
