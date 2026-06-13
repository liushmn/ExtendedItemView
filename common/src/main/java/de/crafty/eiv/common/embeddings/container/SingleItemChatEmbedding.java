package de.crafty.eiv.common.embeddings.container;

import de.crafty.eiv.common.CommonEIV;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SingleItemChatEmbedding extends ContainerChatEmbedding {

    private static final ResourceLocation TEXTURE = new ResourceLocation(CommonEIV.MODID, "textures/gui/embeddings/container/single_item.png");

    public SingleItemChatEmbedding(ItemStack stack) {
        super(24, 26, 1.0F);

        this.addSlot(0, 4, 6);
        this.getSlot(0).setStack(stack);

    }


    @Override
    protected void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        this.renderTexture(TEXTURE, guiGraphics, 0, 2, 0, 0, 24, 24, 24, 24);

    }
}
