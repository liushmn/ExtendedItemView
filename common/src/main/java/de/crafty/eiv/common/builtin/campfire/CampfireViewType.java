package de.crafty.eiv.common.builtin.campfire;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.embeddings.container.RecipeChatEmbedding;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class CampfireViewType implements IEivRecipeViewType {

    protected static final CampfireViewType INSTANCE = new CampfireViewType();

    private static final ResourceLocation CAMPFIRE_LOCATION = ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, "textures/gui/type/campfire.png");
    private static final ResourceLocation CHAT_BACKGROUND = ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, "textures/gui/embeddings/container/campfire.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.eiv.type.campfire");
    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.withDefaultNamespace("campfire_cooking");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.CAMPFIRE);
    }

    @Override
    public int getDisplayWidth() {
        return 74;
    }

    @Override
    public int getDisplayHeight() {
        return 36;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return CAMPFIRE_LOCATION;
    }

    @Override
    public int getSlotCount() {
        return 2;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

        //Ingredient
        slotDefinition.addItemSlot(0, 1, 1);

        //Cooked result
        slotDefinition.addItemSlot(1, 57, 1);
    }


    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.CAMPFIRE));
    }


    @Override
    public boolean supportsRecipeShare() {
        return true;
    }


    @Override
    public ChatRecipeBackground getChatRecipeBackground() {
        return new ChatRecipeBackground(CHAT_BACKGROUND, 0, 0, 80, 42);
    }


    @Override
    public void placeChatSlots(RecipeChatEmbedding.SlotDefinition slotDefinition) {
        //Ingredient
        slotDefinition.addSlot(0, 4, 4);

        //Cooked result
        slotDefinition.addSlot(1, 60, 4);
    }

}
