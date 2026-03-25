package de.crafty.eiv.common.embeddings.container;

import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.component.EivDataComponents;
import de.crafty.eiv.common.embeddings.ChatEmbedding;
import de.crafty.eiv.common.embeddings.EmbeddingData;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import de.crafty.eiv.common.recipe.rendering.AnimationTicker;
import de.crafty.eiv.common.rendering.EivGuiRenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeChatEmbedding extends ContainerChatEmbedding {


    private final IEivViewRecipe recipe;
    private final String sender;

    private final HashMap<Integer, SlotContent> contentBySlot = new HashMap<>();


    private int currentTick = 0;


    private final List<AnimationTicker> animationTickers = new ArrayList<>();

    public RecipeChatEmbedding(IEivViewRecipe recipe, CompoundTag extraData, String sender) {
        super(recipe.getViewType().getChatRecipeBackground().width() / 2, recipe.getViewType().getChatRecipeBackground().height() / 2 + 2, 0.5F);

        this.recipe = recipe.asChatCopy();
        this.sender = sender;

        if (this.recipe == null)
            throw new IllegalStateException("Recipes must implement a clone method to work in chats");

        this.recipe.loadExtraEmbeddingData(extraData);
        this.recipe.getViewType().placeChatSlots(new SlotDefinition(this));

        RecipeViewMenu.SlotFillContext ctx = new RecipeViewMenu.SlotFillContext();
        this.recipe.bindSlots(ctx);

        for (int i = 0; i < this.getSize(); i++) {
            SlotContent slotContent = ctx.contentBySlot(i);
            this.contentBySlot.put(i, slotContent);

            if (!ctx.contentDependencies().containsKey(i))
                this.getSlot(i).setStack(slotContent.getByIndex(slotContent.index()));

            if (ctx.contentDependencies().containsKey(i))
                this.getSlot(i).setStack(slotContent.getByIndex(ctx.contentDependencies().get(i).get()));
        }

        this.animationTickers.addAll(this.recipe.getAnimationTickers());

    }

    @Override
    protected void onInit() {
        this.recipe.initRecipe();
    }

    @Override
    protected void onRemove() {
        this.recipe.fadeRecipe();
    }

    @Override
    protected void tick() {
        this.recipe.tick();
        this.animationTickers.forEach(AnimationTicker::tick);

        this.currentTick++;

        if (this.currentTick >= 25) {
            this.currentTick = 0;
            this.tickContents();
        }

    }

    private void tickContents() {
        if (this.minecraft.hasShiftDown() && this.isChatOpen())
            return;

        this.recipe.tickContents();

        this.contentBySlot.forEach((slotId, slotContent) -> {
            this.getSlot(slotId).setStack(slotContent.getByIndex(slotContent.index()));
        });

    }

    @Override
    protected void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.recipe.renderChatRecipeBackground(new ChatRecipeRenderer(this), guiGraphics, mouseX, mouseY, partialTicks);
    }


    @Override
    protected void renderEmbedding(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.renderEmbedding(guiGraphics, mouseX, mouseY, partialTicks);

        ChatRecipeRenderer renderer = new ChatRecipeRenderer(this);

        this.recipe.renderRecipeInChat(renderer, guiGraphics, mouseX, mouseY, partialTicks);
        this.renderSender(renderer, guiGraphics, mouseX, mouseY, partialTicks);

    }

    private void renderSender(ChatRecipeRenderer renderer, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        Component senderComp = Component.empty().append(Component.translatable("embedding.sharedBy").append(": ").withStyle(ChatFormatting.GRAY)).append(this.sender);

        renderer.drawString(this.minecraft.font, guiGraphics, senderComp, this.recipe.getSenderXPosition(), this.recipe.getSenderYPosition(), -1, true);

    }


    public static class SlotDefinition {

        private final RecipeChatEmbedding bound;

        private SlotDefinition(RecipeChatEmbedding embedding) {
            this.bound = embedding;
        }


        // + 6 for margin
        public void addSlot(int id, int x, int y) {
            this.bound.addSlot(id, x, y + 2);
            this.bound.getSlot(id).bindViewType(this.bound.recipe.getViewType());
        }

    }

    public static class ChatRecipeRenderer {


        private final RecipeChatEmbedding embedding;

        private ChatRecipeRenderer(RecipeChatEmbedding embedding) {

            this.embedding = embedding;
        }

        public float getGuiScaling() {
            return this.embedding.guiScaling;
        }

        public float getCurrentAlpha() {
            return this.embedding.getLineAlpha();
        }

        public int getTotalXOffset() {
            return (int) ChatEmbedding.getEmbeddingXOffset();
        }

        public int getTotalYOffset() {
            return (int) ChatEmbedding.getEmbeddingYOffset(this.embedding);
        }

        public void renderEntity(GuiGraphics guiGraphics, LivingEntity livingEntity, float x0, float y0, float x1, float y1, float scale, Vector3f translation, Quaternionf rotation, Quaternionf cameraAngleOverride) {

            float chatScale = Minecraft.getInstance().options.chatScale().get().floatValue();

            scale *= this.embedding.guiScaling * chatScale;

            float xOff = ChatEmbedding.getEmbeddingXOffset() * chatScale;
            float yOff = ChatEmbedding.getEmbeddingYOffset(this.embedding) * chatScale;

            x0 *= this.getGuiScaling() * chatScale;
            x1 *= this.getGuiScaling() * chatScale;

            y0 *= this.getGuiScaling() * chatScale;
            y1 *= this.getGuiScaling() * chatScale;

            // + 2 for margin
            y0 += 2 * this.getGuiScaling() * chatScale;
            y1 += 2 * this.getGuiScaling() * chatScale;

            int xStart = Mth.floor(x0 + xOff);
            int xEnd = Mth.floor(x1 + xOff);
            int yStart = Mth.floor(y0 + yOff);
            int yEnd = Mth.floor(y1 + yOff);

            EivGuiRenderHelper.renderEntityOnScreen(guiGraphics, livingEntity, xStart, yStart, xEnd, yEnd, scale, translation.mul(this.getGuiScaling()).add(((x0 + xOff) - xStart) / scale, ((y0 + yOff) - yStart) / scale, 0.0F), rotation, cameraAngleOverride);
        }

        public void drawString(Font font, GuiGraphics guiGraphics, Component text, float x, float y, int color, boolean withShadow) {
            this.drawString(font, guiGraphics, text, x * this.embedding.guiScaling, y * this.embedding.guiScaling + 1, color, withShadow, this.embedding.guiScaling);
        }

        public void drawString(Font font, GuiGraphics guiGraphics, Component text, float x, float y, int color, boolean withShadow, float scaling) {
            this.embedding.drawString(font, guiGraphics, text, x, y, color, withShadow, scaling);
        }

        // + 3 for margin
        public void renderTexture(ResourceLocation texture, GuiGraphics guiGraphics, float x, float y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
            this.renderTexture(texture, guiGraphics, x * embedding.guiScaling, y * embedding.guiScaling + 1, u, v, width, height, textureWidth, textureHeight, embedding.guiScaling, false);
        }

        public void renderTexture(ResourceLocation texture, GuiGraphics guiGraphics, float x, float y, int u, int v, int width, int height, int textureWidth, int textureHeight, float scaling, boolean requiresFullAlpha) {
            this.embedding.renderTexture(texture, guiGraphics, x, y, u, v, width, height, textureWidth, textureHeight, scaling, requiresFullAlpha);
        }

        public void renderTopLevelTexture(ResourceLocation texture, GuiGraphics guiGraphics, float x, float y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
            this.renderTexture(texture, guiGraphics, x * embedding.guiScaling, y * embedding.guiScaling + 1, u, v, width, height, textureWidth, textureHeight, embedding.guiScaling, true);
        }

        public void renderItem(GuiGraphics guiGraphics, ItemStack stack, int x, int y) {

            stack.set(EivDataComponents.EMBEDDING_DATA, new EmbeddingData(this.getCurrentAlpha()));


            // + 3 for margin
            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(x * this.getGuiScaling(), y * this.getGuiScaling() + 1);
            guiGraphics.pose().scale(this.embedding.guiScaling, this.embedding.guiScaling);
            guiGraphics.renderItem(stack, 0, 0);
            guiGraphics.pose().popMatrix();
        }

    }
}
