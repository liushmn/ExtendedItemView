package de.crafty.eiv.common.overlay;

import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.overlay.itemlist.bookmark.ItemBookmarkOverlay;
import de.crafty.eiv.common.overlay.itemlist.view.ItemViewOverlay;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all overlays
 */
public abstract class AbstractEivOverlay {

    private final List<ItemSlot> itemSlots = new ArrayList<>();

    protected int x, y, width, height;
    private boolean enabled;

    protected AbstractEivOverlay(int defaultX, int defaultY, int defaultWidth, int defaultHeight) {
        this.x = defaultX;
        this.y = defaultY;
        this.width = defaultWidth;
        this.height = defaultHeight;

        this.enabled = true;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled(){
        return this.enabled;
    }

    protected boolean keyPressed(int i, int j, int k) {
        //Basic keybinds

        for (ItemSlot slot : this.itemSlots()) {
            if (!slot.isHovered())
                continue;

            if (CommonEIVClient.USAGE_KEYBIND.matches(i, j))
                ItemViewOverlay.INSTANCE.openRecipeView(slot.getStack(), ItemViewOverlay.ItemViewOpenType.INPUT);

            if (CommonEIVClient.RECIPE_KEYBIND.matches(i, j))
                ItemViewOverlay.INSTANCE.openRecipeView(slot.getStack(), ItemViewOverlay.ItemViewOpenType.RESULT);

            return true;
        }

        return false;
    }

    protected boolean charTyped(char c, int i) {
        return false;
    }

    protected abstract boolean mouseClicked(double mouseX, double mouseY, int mouseButton);

    protected abstract boolean scrollMouse(double mouseX, double mouseY, double scrolledX, double scrolledY);


    public List<ItemSlot> itemSlots() {
        return this.itemSlots;
    }

    public abstract void onScreenChanged(InventoryPositionInfo info);


    protected void placeWidgets(ScreenContext ctx) {

    }

    protected abstract void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks);


    protected BlockingPredicate getBlockingPredicate() {
        return comp -> true;
    }


    protected boolean isPositionBlocked(int x, int y) {
        List<BlockingGuiComponent> relevantComponents = OverlayManager.INSTANCE.allGuiBlockings().stream().filter(blockingGuiComponent -> this.getBlockingPredicate().isBlocking(blockingGuiComponent)).toList();

        for (BlockingGuiComponent blocking : relevantComponents) {
            if(x >= blocking.x() && x <= blocking.x() + blocking.width() && y >= blocking.y() && y <= blocking.y() + blocking.height())
                return true;
        }

        return false;
    }

    protected interface BlockingPredicate {

        boolean isBlocking(BlockingGuiComponent comp);

    }

    public record InventoryPositionInfo(AbstractContainerScreen<? extends AbstractContainerMenu> screen,
                                        int screenWidth, int screenHeight, int leftPos, int topPos, int imageWidth,
                                        int imageHeight) {

        public boolean matches(@Nullable InventoryPositionInfo info) {
            if (info == null)
                return false;

            return info.screenWidth == this.screenWidth
                    && info.screenHeight == this.screenHeight
                    && info.leftPos == this.leftPos
                    && info.topPos == this.topPos
                    && info.imageWidth == this.imageWidth
                    && info.imageHeight == this.imageHeight;
        }

    }

    public static class ScreenContext {

        private final List<GuiEventListener> renderables;
        private final List<GuiEventListener> nonRenderables;

        public ScreenContext() {
            this.renderables = new ArrayList<>();
            this.nonRenderables = new ArrayList<>();
        }

        public <T extends GuiEventListener & Renderable & NarratableEntry> void addRenderable(T renderable) {
            if (renderable != null)
                this.renderables.add(renderable);
        }

        public <T extends GuiEventListener & NarratableEntry> void addNonRendarable(T nonRenderable) {
            if (nonRenderable != null)
                this.nonRenderables.add(nonRenderable);
        }

        public List<GuiEventListener> renderables() {
            return this.renderables;
        }

        public List<GuiEventListener> nonRenderables() {
            return this.nonRenderables;
        }

    }
}
