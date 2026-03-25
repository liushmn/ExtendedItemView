package de.crafty.eiv.common.embeddings;

import de.crafty.eiv.common.embeddings.util.EmbeddingComponentContents;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.ChatVisiblity;

import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class ChatEmbedding {

    private static final LinkedList<ChatEmbedding> QUEUED = new LinkedList<>();
    private static final List<ChatEmbedding> CACHED = new ArrayList<>();
    private static final HashMap<GuiMessage, ChatEmbedding> STORED = new HashMap<>();

    private static int SYNCED_MESSAGE_COUNT = 0;
    private static int SYNCED_CHAT_SCROLLBAR_POS = 0;
    private static List<GuiMessage.Line> SYNCED_MESSAGE_LINES = new ArrayList<>();


    protected final int width, height;
    private int chatPos = 0;

    private final int occupiedLines;
    private final int lineOffset;

    private int currentTime = -1;
    private boolean chatOpen = false;

    private float alpha = 1.0F;


    protected final Minecraft minecraft;

    private boolean hovered;
    private final UUID uuid;

    protected ChatEmbedding(int width, int height) {

        this.uuid =  UUID.randomUUID();

        this.minecraft = Minecraft.getInstance();

        this.width = width;
        this.height = height;

        this.occupiedLines = Mth.ceil((double) height / (int) (9.0 * (Minecraft.getInstance().options.chatLineSpacing().get() + 1.0)));
        this.lineOffset = this.occupiedLines * (int) (9.0 * (Minecraft.getInstance().options.chatLineSpacing().get() + 1.0)) - this.height;

    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void bindMsg(GuiMessage msg) {

        if(msg.content().getContents() instanceof EmbeddingComponentContents embeddingComponentContents)
            embeddingComponentContents.bindUUID(this.uuid);

        STORED.put(msg, this);
    }

    protected int getChatPos() {
        return this.chatPos;
    }


    protected void setChatPos(int chatPos) {
        this.chatPos = chatPos;
    }

    protected void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    protected void setChatOpen(boolean chatOpen) {
        this.chatOpen = chatOpen;
    }

    public List<GuiMessage.Line> getRespectiveLines() {
        List<GuiMessage.Line> lines = new ArrayList<>();

        for (int i = this.chatPos; i < this.chatPos + this.occupiedLines && i < SYNCED_MESSAGE_LINES.size(); i++) {
            lines.add(SYNCED_MESSAGE_LINES.get(i));
        }

        return lines;
    }

    public int getOccupiedLines() {
        return this.occupiedLines;
    }


    public float getLineAlpha() {
        float textOpacity = Minecraft.getInstance().options.chatOpacity().get().floatValue();

        return this.chatOpen ? textOpacity : this.alpha * textOpacity;
    }

    protected void updateLineAlpha(int currentTime) {

        if (SYNCED_MESSAGE_LINES.size() <= this.chatPos)
            this.alpha = 0.0F;

        GuiMessage.Line line = SYNCED_MESSAGE_LINES.get(this.chatPos);
        this.alpha = ChatEmbedding.retrieveLineVisibility(currentTime, line);
    }


    public boolean isChatOpen() {
        return this.chatOpen;
    }

    private void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderEmbedding(guiGraphics, mouseX, mouseY, partialTicks);
    }

    protected abstract void renderEmbedding(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks);

    protected void mouseClicked(MouseButtonEvent event, boolean doubleClick) {
    }

    protected void mouseScrolled(int mouseX, int mouseY, double horizontalAmount, double verticalAmount) {

    }

    protected void keyPressed(KeyEvent event) {

    }

    protected void tick() {

    }

    protected void onInit() {

    }

    protected void onRemove() {

    }

    protected void drawString(Font font, GuiGraphics guiGraphics, Component text, float x, float y, int color, boolean withShadow, float scaling) {

        Color old = new Color(color);

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(scaling, scaling);
        guiGraphics.pose().translate(x / scaling, y / scaling);
        guiGraphics.drawString(font, text, 0, 0, ARGB.color(Math.round(old.getAlpha() * this.getLineAlpha()), old.getRed(), old.getGreen(), old.getBlue()), withShadow);
        guiGraphics.pose().popMatrix();

    }


    protected void renderTexture(ResourceLocation texture, GuiGraphics guiGraphics, float x, float y, int u, int v, int width, int height, int textureWidth, int textureHeight, float scaling) {
        this.renderTexture(texture, guiGraphics, x, y, u, v, width, height, textureWidth, textureHeight, scaling, false);
    }

    protected void renderTexture(ResourceLocation texture, GuiGraphics guiGraphics, float x, float y, int u, int v, int width, int height, int textureWidth, int textureHeight, float scaling, boolean requiresFullAlpha) {

        if (requiresFullAlpha && this.getLineAlpha() < 1.0F)
            return;

        int occupiedLineSpace = ChatEmbedding.getOccupiedSpacePerLine();

        int startLine = Mth.floor(y / (float) occupiedLineSpace);
        int endLine = Mth.floor((y + height * scaling) / (float) occupiedLineSpace);

        int remainingTextureHeight = height;

        this.updateLineAlpha(this.currentTime);

        for (int i = startLine; i <= endLine; i++) {
            int allowedSpace = Math.round(Math.min(Math.min((i + 1) * occupiedLineSpace - y, occupiedLineSpace) / scaling, remainingTextureHeight));

            float yStart = Math.max(startLine * occupiedLineSpace + (y - startLine * occupiedLineSpace), i * occupiedLineSpace);


            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().scale(scaling, scaling);
            guiGraphics.pose().translate(x / scaling, yStart / scaling);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, 0, 0, u, v + (height - remainingTextureHeight), width, allowedSpace, textureWidth, textureHeight, ARGB.color(Math.round(255 * this.getLineAlpha()), 255, 255, 255));
            guiGraphics.pose().popMatrix();

            remainingTextureHeight -= allowedSpace;
        }


    }

    public boolean isHovered() {
        return this.hovered;
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= 0 && mouseX <= this.width && mouseY >= 0 && mouseY <= this.height;
    }


    //---------------- Caching ----------------

    public static LinkedList<ChatEmbedding> queue() {
        return QUEUED;
    }

    public static void addToChatQueue(ChatEmbedding embedding) {
        QUEUED.push(embedding);
    }

    public static ChatEmbedding poll() {
        if (QUEUED.isEmpty())
            return null;

        return QUEUED.poll();
    }

    public static void cache(ChatEmbedding embedding) {
        embedding.setChatPos(0);
        CACHED.add(embedding);
        embedding.onInit();
    }

    public static void clearCache() {
        CACHED.forEach(ChatEmbedding::onRemove);
        CACHED.clear();
    }

    public static HashMap<GuiMessage, ChatEmbedding> stored() {
        return STORED;
    }

    public static void updateChatPositions() {
        CACHED.forEach(embedding -> embedding.setChatPos(embedding.getChatPos() + 1));
        List<ChatEmbedding> toRemove = CACHED.stream().filter(embedding -> embedding.getChatPos() >= 100).toList();
        toRemove.forEach(ChatEmbedding::onRemove);
        CACHED.removeAll(toRemove);
    }

    public static void setSyncedChatScrollbarPos(int syncedChatScrollbarPos) {
        ChatEmbedding.SYNCED_CHAT_SCROLLBAR_POS = syncedChatScrollbarPos;
    }

    public static void setSyncedMessageCount(int syncedMessageCount) {
        ChatEmbedding.SYNCED_MESSAGE_COUNT = syncedMessageCount;
    }

    public static void setSyncedMessageLines(List<GuiMessage.Line> syncedMessageLines) {
        SYNCED_MESSAGE_LINES = syncedMessageLines;
    }


    //---------------- Input Events ----------------

    public static void onMouseClicked(MouseButtonEvent event, boolean doubleClick) {
        CACHED.forEach(embedding -> {
            MouseButtonEvent withCorrectCoords = new MouseButtonEvent(event.x() - ChatEmbedding.getXPosition(), event.y() - ChatEmbedding.getYPosition(embedding), event.buttonInfo());
            embedding.mouseClicked(withCorrectCoords, doubleClick);
        });
    }

    public static void onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        CACHED.forEach(embedding -> {
            embedding.mouseScrolled((int) Math.round(mouseX - ChatEmbedding.getXPosition()), (int) Math.round(mouseY - ChatEmbedding.getYPosition(embedding)), horizontalAmount, verticalAmount);
        });
    }

    public static void onKeyPressed(KeyEvent event) {
        CACHED.forEach(embedding -> embedding.keyPressed(event));
    }


    public static void tickEmbeddings() {
        CACHED.forEach(ChatEmbedding::tick);
    }

    //---------------- Rendering ----------------

    public static void renderEmbeddings(GuiGraphics guiGraphics, int mouseX, int mouseY, int currentTime, boolean chatOpen) {

        if (Minecraft.getInstance().options.chatVisibility().get() == ChatVisiblity.HIDDEN)
            return;

        //max line index
        int displayableMessages = (Math.min(SYNCED_MESSAGE_COUNT - SYNCED_CHAT_SCROLLBAR_POS, Minecraft.getInstance().gui.getChat().getLinesPerPage()) - 1);


        float chatScale = Minecraft.getInstance().options.chatScale().get().floatValue();
        final float yStartPos = (guiGraphics.guiHeight() - 40) / chatScale;


        List<ChatEmbedding> renderCandidates = CACHED.stream().filter(embedding -> embedding.getChatPos() + embedding.getOccupiedLines() >= SYNCED_CHAT_SCROLLBAR_POS && embedding.getChatPos() < SYNCED_CHAT_SCROLLBAR_POS + (displayableMessages + 1)).toList();
        List<ChatEmbedding> renderables = new ArrayList<>(renderCandidates);

        int totalHiddenLines = 0;
        for (int i = 0; i < renderCandidates.size() && !chatOpen; i++) {

            ChatEmbedding embedding = renderCandidates.get(i);

            int hiddenLines = 0;

            for (int lineId = embedding.getChatPos(); lineId < embedding.getChatPos() + embedding.getOccupiedLines() && lineId <= displayableMessages; lineId++) {
                GuiMessage.Line line = SYNCED_MESSAGE_LINES.get(lineId);

                if (ChatEmbedding.retrieveLineVisibility(currentTime, line) <= 1.0E-5F) {
                    totalHiddenLines++;
                    hiddenLines++;
                }

            }

            //Just for performance
            if (hiddenLines == embedding.getOccupiedLines())
                renderables.remove(embedding);

        }

        int occupiedSpace = ChatEmbedding.getOccupiedSpacePerLine();


        int scissorTop = Mth.floor(yStartPos) - occupiedSpace * (displayableMessages + 1) + totalHiddenLines * occupiedSpace;
        int scissorBot = Mth.floor(yStartPos);

        guiGraphics.pose().pushMatrix();
        guiGraphics.enableScissor(0, scissorTop, ChatComponent.getWidth(Minecraft.getInstance().options.chatWidth().get()), scissorBot);

        for (int i = 0; i < renderables.size(); i++) {
            ChatEmbedding embedding = renderables.get(i);

            int relMouseX = mouseX - ChatEmbedding.getXPosition();
            int relMouseY = mouseY - ChatEmbedding.getYPosition(embedding);

            embedding.hovered = embedding.isHovered(relMouseX, relMouseY) && guiGraphics.containsPointInScissor(mouseX, mouseY);

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(0, ChatEmbedding.getEmbeddingYOffset(embedding));
            embedding.setCurrentTime(currentTime);
            embedding.setChatOpen(chatOpen);
            embedding.render(guiGraphics, relMouseX, relMouseY, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaTicks());
            guiGraphics.pose().popMatrix();
        }

        guiGraphics.disableScissor();
        guiGraphics.pose().popMatrix();


    }

    private static float retrieveLineVisibility(int currentTime, GuiMessage.Line line) {
        int j = currentTime - line.addedTime();
        double d = j / 200.0;
        d = 1.0 - d;
        d *= 10.0;
        d = Mth.clamp(d, 0.0, 1.0);
        d *= d;
        return (float) d;
    }

    private static int getOccupiedSpacePerLine() {
        double chatLineSpacing = Minecraft.getInstance().options.chatLineSpacing().get();

        final int lineSize = 9;
        return (int) (lineSize * (chatLineSpacing + 1.0));
    }

    protected static float getEmbeddingYOffset(ChatEmbedding embedding) {
        float chatScale = Minecraft.getInstance().options.chatScale().get().floatValue();
        final float yStartPos = (Minecraft.getInstance().getWindow().getGuiScaledHeight() - 40) / chatScale;

        return yStartPos - (embedding.getChatPos() - SYNCED_CHAT_SCROLLBAR_POS) * ChatEmbedding.getOccupiedSpacePerLine() - embedding.height - embedding.lineOffset;
    }

    protected static float getEmbeddingXOffset() {
        return 4.0F;
    }

    protected static int getXPosition() {
        float chatScale = Minecraft.getInstance().options.chatScale().get().floatValue();
        return (int) (ChatEmbedding.getEmbeddingXOffset() * chatScale);
    }

    protected static int getYPosition(ChatEmbedding embedding) {
        float chatScale = Minecraft.getInstance().options.chatScale().get().floatValue();

        int bottomPos = (int) ((Minecraft.getInstance().getWindow().getGuiScaledHeight()) - 40);
        return (int) (bottomPos - (embedding.getChatPos() - SYNCED_CHAT_SCROLLBAR_POS) * ChatEmbedding.getOccupiedSpacePerLine() * chatScale - embedding.height * chatScale - embedding.lineOffset * chatScale);
    }
}
