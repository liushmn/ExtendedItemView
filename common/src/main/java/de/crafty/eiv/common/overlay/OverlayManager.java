package de.crafty.eiv.common.overlay;

import de.crafty.eiv.common.CommonEIVClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OverlayManager {

    public static final OverlayManager INSTANCE = new OverlayManager();


    private AbstractEivOverlay.InventoryPositionInfo currentInvInfo = null;
    private final HashMap<AbstractEivOverlay, AbstractEivOverlay.ScreenContext> screenContextMap = new HashMap<>();

    private List<BlockingGuiComponent> guiBlockings = new ArrayList<>();
    private List<BlockingGuiComponent> lastGuiBlockings = new ArrayList<>();

    public boolean checkForScreenChange(AbstractEivOverlay.InventoryPositionInfo newInfo) {
        if (!newInfo.matches(this.currentInvInfo)) {
            this.currentInvInfo = newInfo;

            PRESENT_OVERLAYS.forEach(overlay -> overlay.onScreenChanged(newInfo));

            PRESENT_OVERLAYS.forEach(present -> {
                AbstractEivOverlay.ScreenContext screenContext = new AbstractEivOverlay.ScreenContext();
                present.placeWidgets(screenContext);
                screenContextMap.put(present, screenContext);
            });


            return true;
        }

        return false;
    }

    public AbstractEivOverlay.InventoryPositionInfo currentInfo() {
        return this.currentInvInfo;
    }

    public boolean isOverlayWidgetFocused() {
        return this.screenContextMap.values().stream().anyMatch(screenContext -> screenContext.renderables().stream().anyMatch(GuiEventListener::isFocused) || screenContext.nonRenderables().stream().anyMatch(GuiEventListener::isFocused));
    }

    public HashMap<AbstractEivOverlay, AbstractEivOverlay.ScreenContext> screenContextMap() {
        return this.screenContextMap;
    }

    public boolean keyPressed(int i, int j, int k) {
        boolean b = false;

        if (CommonEIVClient.TOGGLE_OVERLAY_KEYBIND.matches(i, j)){
            PRESENT_OVERLAYS.forEach(abstractEivOverlay -> abstractEivOverlay.setEnabled(!abstractEivOverlay.isEnabled()));
            return true;
        }

        for (AbstractEivOverlay overlay : PRESENT_OVERLAYS) {
            if(!overlay.isEnabled())
                continue;

            if (overlay.keyPressed(i, j, k))
                b = true;
        }

        return b;
    }

    public boolean charTyped(char c, int i) {
        boolean b = false;

        for (AbstractEivOverlay overlay : PRESENT_OVERLAYS) {
            if(!overlay.isEnabled())
                continue;

            if (overlay.charTyped(c, i))
                b = true;
        }

        return b;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean b = false;

        for (AbstractEivOverlay overlay : PRESENT_OVERLAYS) {
            if(!overlay.isEnabled())
                continue;

            if (!(mouseX >= overlay.getX() && mouseX <= overlay.getX() + overlay.getWidth() && mouseY >= overlay.getY() && mouseY <= overlay.getY() + overlay.getHeight()))
                continue;

            if (overlay.mouseClicked(mouseX, mouseY, mouseButton))
                b = true;
        }

        return b;
    }

    public boolean scrollMouse(double mouseX, double mouseY, double scrolledX, double scrolledY) {
        boolean b = false;
        for (AbstractEivOverlay overlay : PRESENT_OVERLAYS) {
            if(!overlay.isEnabled())
                continue;

            if (!(mouseX >= overlay.getX() && mouseX <= overlay.getX() + overlay.getWidth() && mouseY >= overlay.getY() && mouseY <= overlay.getY() + overlay.getHeight()))
                continue;

            if (overlay.scrollMouse(mouseX, mouseY, scrolledX, scrolledY))
                b = true;
        }

        return b;
    }


    public void renderAll(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        PRESENT_OVERLAYS.stream().filter(AbstractEivOverlay::isEnabled).forEach(overlay -> overlay.render(guiGraphics, mouseX, mouseY, partialTicks));


        //If both are empty => skip swapping
        if (this.lastGuiBlockings.isEmpty() && this.guiBlockings.isEmpty())
            return;

        //Swap caches
        this.lastGuiBlockings.clear();
        List<BlockingGuiComponent> temp = this.lastGuiBlockings;
        this.lastGuiBlockings = this.guiBlockings;
        this.guiBlockings = temp;

    }


    public void addGuiBlocking(BlockingGuiComponent comp) {
        if (this.guiBlockings.stream().anyMatch(blockingGuiComponent -> blockingGuiComponent.id().equals(comp.id())))
            return;

        this.guiBlockings.add(comp);
    }

    public List<BlockingGuiComponent> allGuiBlockings() {
        return this.guiBlockings;
    }



    private static final List<AbstractEivOverlay> PRESENT_OVERLAYS = new ArrayList<>();

    public static void registerOverlay(AbstractEivOverlay overlay) {
        PRESENT_OVERLAYS.add(overlay);
    }

}
