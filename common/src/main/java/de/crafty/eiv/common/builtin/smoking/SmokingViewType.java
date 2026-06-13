package de.crafty.eiv.common.builtin.smoking;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.builtin.smelting.SmeltingViewType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class SmokingViewType extends SmeltingViewType {

    public static final SmokingViewType INSTANCE = new SmokingViewType();

    private static final ResourceLocation BLASTING_LOCATION = new ResourceLocation(CommonEIV.MODID, "textures/gui/type/smoking.png");
    private static final ResourceLocation CHAT_BACKGROUND = new ResourceLocation(CommonEIV.MODID, "textures/gui/embeddings/container/smoking.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.eiv.type.smoking");
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return BLASTING_LOCATION;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.SMOKER);
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(CommonEIV.MODID, "furnace_smoking");
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.SMOKER));
    }


    @Override
    public ChatRecipeBackground getChatRecipeBackground() {
        return new ChatRecipeBackground(CHAT_BACKGROUND, 0, 0, 88, 60);
    }

}
