package de.crafty.eiv.common.builtin.brewing;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.embeddings.container.RecipeChatEmbedding;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class BrewingViewType implements IEivRecipeViewType {

    protected static final BrewingViewType INSTANCE = new BrewingViewType();

    private static final ResourceLocation BREWING_LOCATION = new ResourceLocation(CommonEIV.MODID, "textures/gui/type/brewing.png");
    private static final ResourceLocation CHAT_BACKGROUND = new ResourceLocation(CommonEIV.MODID, "textures/gui/embeddings/container/brewing.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.eiv.type.brewing");
    }

    @Override
    public int getDisplayWidth() {
        return 133;
    }

    @Override
    public int getDisplayHeight() {
        return 61;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return BREWING_LOCATION;
    }

    @Override
    public int getSlotCount() {
        return 5;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        //Result
        slotDefinition.addItemSlot(0, 1, 2);

        //magic ingredient
        slotDefinition.addItemSlot(1, 58, 3);

        //Ingredient bottles
        slotDefinition.addItemSlot(2, 35, 37);
        slotDefinition.addItemSlot(3, 58, 44);
        slotDefinition.addItemSlot(4, 81, 37);
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation("brewing");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.BREWING_STAND);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.BREWING_STAND), new ItemStack(Items.POTION));
    }

    @Override
    public boolean supportsRecipeShare() {
        return true;
    }

    @Override
    public ChatRecipeBackground getChatRecipeBackground() {
        return new ChatRecipeBackground(CHAT_BACKGROUND, 0, 0, 139, 67);
    }

    @Override
    public void placeChatSlots(RecipeChatEmbedding.SlotDefinition slotDefinition) {

        //Result
        slotDefinition.addSlot(0, 4, 5);

        //magic ingredient
        slotDefinition.addSlot(1, 61, 6);

        //Ingredient bottles
        slotDefinition.addSlot(2, 38, 40);
        slotDefinition.addSlot(3, 61, 47);
        slotDefinition.addSlot(4, 84, 40);

    }
}
