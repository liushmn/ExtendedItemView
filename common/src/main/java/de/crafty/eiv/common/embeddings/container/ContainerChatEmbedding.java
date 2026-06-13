package de.crafty.eiv.common.embeddings.container;

import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.api.recipe.ItemView;
import de.crafty.eiv.common.embeddings.ChatEmbedding;
import de.crafty.eiv.common.embeddings.EmbeddingData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class ContainerChatEmbedding extends ChatEmbedding {

    private final List<ContainerSlot> slots;

    final float guiScaling;

    protected ContainerChatEmbedding(int width, int height, float guiScaling) {
        super(width, height);

        this.slots = new ArrayList<>();
        this.guiScaling = guiScaling;
    }


    protected void addSlot(int id, int x, int y) {
        if (id != slots.size())
            throw new IllegalArgumentException("Slot IDs need to count up straight");

        this.slots.add(new ContainerSlot(this, id, x, y, this.guiScaling));
    }

    protected ContainerSlot getSlot(int id) {
        if (id < slots.size())
            return this.slots.get(id);

        throw new IllegalArgumentException("Slot ID out of range");
    }

    public List<ContainerSlot> getSlots() {
        return this.slots;
    }

    public int getSize(){
        return this.slots.size();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.slots.forEach((slot) -> {

            if (slot.isHovered(mouseX, mouseY))
                slot.onClick(mouseX, mouseY, mouseButton);
        });
    }

    @Override
    protected void renderEmbedding(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);

        this.slots.forEach(slot -> slot.render(guiGraphics, mouseX, mouseY, partialTicks));
    }

    protected abstract void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks);

    protected void renderTexture(ResourceLocation texture, GuiGraphics guiGraphics, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight){
        this.renderTexture(texture, guiGraphics, x * this.guiScaling, y * this.guiScaling, u, v, width, height, textureWidth, textureHeight, this.guiScaling);
    }

    public static class ContainerSlot {

        private static final int SIZE = 16;

        private ItemStack stack;

        private final ChatEmbedding bound;
        private final int id;
        private final int x, y;

        private final float guiScaling;

        @Nullable
        private IEivRecipeViewType boundViewType = null;

        ContainerSlot(ChatEmbedding bound, int id, int x, int y, float guiScaling) {

            this.bound = bound;

            this.id = id;
            this.x = x;
            this.y = y;

            this.stack = ItemStack.EMPTY;

            this.guiScaling = guiScaling;

        }


        public ItemStack getStack() {
            return this.stack;
        }

        public void setStack(ItemStack stack) {
            this.stack = stack;
        }

        public void bindViewType(IEivRecipeViewType boundViewType) {
            this.boundViewType = boundViewType;
        }

        private void onClick(int mouseX, int mouseY, int mouseButton) {

            ItemStack copy = this.stack.copy();
            CompoundTag tag = copy.getTag() == null ? new CompoundTag() : copy.getTag();
            if (tag.contains("eiv_embedding_data"))
                tag.remove("eiv_embedding_data");


            if (mouseButton == 0)
                ItemView.openForStackResult(copy, this.boundViewType);
            else
                ItemView.openForStackIngredient(copy, this.boundViewType);
        }

        private void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

            float chatScaling = Minecraft.getInstance().options.chatScale().get().floatValue();

            if (this.isHovered(mouseX, mouseY)){
                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(this.guiScaling, this.guiScaling, 1.0F);
                guiGraphics.fill(this.x, this.y, this.x + SIZE, this.y + SIZE, new Color(32, 255, 255, 255).getRGB());
                guiGraphics.pose().popPose();
            }


            EmbeddingData data = new EmbeddingData(this.bound.getLineAlpha());
            this.stack.getOrCreateTag().put("eiv_embedding_data", EmbeddingData.CODEC.encode(data, NbtOps.INSTANCE, new CompoundTag()).result().orElseThrow());


            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(this.guiScaling, this.guiScaling, 1.0F);
            guiGraphics.pose().translate(this.x, this.y, 0);
            guiGraphics.renderItem(this.stack, 0, 0);
            guiGraphics.renderItemDecorations(Minecraft.getInstance().font, this.stack, 0, 0);
            guiGraphics.pose().popPose();

            if (this.isHovered(mouseX, mouseY) && !this.stack.isEmpty())
                guiGraphics.renderTooltip(Minecraft.getInstance().font, this.stack, mouseX + Math.round(ChatEmbedding.getEmbeddingXOffset() * this.guiScaling * chatScaling), mouseY + ChatEmbedding.getYPosition(this.bound));
        }

        private boolean isHovered(int mouseX, int mouseY) {
            float chatScale =  Minecraft.getInstance().options.chatScale().get().floatValue();
            return this.bound.isHovered() && mouseX >= Math.round(this.x * this.guiScaling * chatScale) && mouseX <= Math.round(this.x * this.guiScaling * chatScale) + Math.round(SIZE * this.guiScaling * chatScale) && mouseY >= Math.round(this.y * this.guiScaling * chatScale) && mouseY <= Math.round(this.y * this.guiScaling * chatScale) + Math.round(SIZE * this.guiScaling * chatScale);
        }
    }
}
