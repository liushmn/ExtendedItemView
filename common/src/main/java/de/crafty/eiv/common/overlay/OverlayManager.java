package de.crafty.eiv.common.overlay;

import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.overlay.itemlist.AbstractEivItemListOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

public class OverlayManager {

    public static final OverlayManager INSTANCE = new OverlayManager();


    private AbstractEivOverlay.InventoryPositionInfo currentInvInfo = null;
    private final HashMap<AbstractEivOverlay, AbstractEivOverlay.ScreenContext> screenContextMap = new HashMap<>();

    private final List<BlockingGuiComponent> guiBlockings = new ArrayList<>();

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

        if (CommonEIVClient.TOGGLE_OVERLAY_KEYBIND.matches(i, j)) {
            PRESENT_OVERLAYS.forEach(abstractEivOverlay -> abstractEivOverlay.setEnabled(!abstractEivOverlay.isEnabled()));
            return true;
        }

        for (AbstractEivOverlay overlay : PRESENT_OVERLAYS) {
            if (!overlay.isEnabled())
                continue;

            if (overlay.keyPressed(i, j, k))
                b = true;
        }

        return b;
    }

    public boolean charTyped(char c, int i) {
        boolean b = false;

        for (AbstractEivOverlay overlay : PRESENT_OVERLAYS) {
            if (!overlay.isEnabled())
                continue;

            if (overlay.charTyped(c, i))
                b = true;
        }

        return b;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean b = false;

        this.screenContextMap.forEach((abstractEivOverlay, screenContext) -> {
            screenContext.renderables().forEach(guiEventListener -> {
                if (guiEventListener.isFocused() && !guiEventListener.isMouseOver(mouseX, mouseY))
                    guiEventListener.setFocused(false);
            });
        });

        for (AbstractEivOverlay overlay : PRESENT_OVERLAYS) {
            if (!overlay.isEnabled())
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
            if (!overlay.isEnabled())
                continue;

            if (!(mouseX >= overlay.getX() && mouseX <= overlay.getX() + overlay.getWidth() && mouseY >= overlay.getY() && mouseY <= overlay.getY() + overlay.getHeight()))
                continue;

            if (overlay.scrollMouse(mouseX, mouseY, scrolledX, scrolledY))
                b = true;
        }

        return b;
    }


    public void renderAllBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        PRESENT_OVERLAYS.stream().filter(AbstractEivOverlay::isEnabled).forEach(overlay -> overlay.renderBackground(guiGraphics, mouseX, mouseY, partialTicks));
    }

    public void renderAll(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        PRESENT_OVERLAYS.stream().filter(AbstractEivOverlay::isEnabled).forEach(overlay -> overlay.render(guiGraphics, mouseX, mouseY, partialTicks));

        if (Minecraft.getInstance().gui.getDebugOverlay().showDebugScreen())
            this.renderDebug(guiGraphics, mouseX, mouseY, partialTicks);
    }

    public void renderDebug(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {


        this.guiBlockings.forEach(blockingGuiComponent -> {

            Random rand = new Random(blockingGuiComponent.id().toString().chars().sum());
            int debugColor = new Color(rand.nextInt(255 + 1), rand.nextInt(255 + 1), rand.nextInt(255 + 1)).getRGB();

            guiGraphics.hLine(blockingGuiComponent.x(), blockingGuiComponent.x() + blockingGuiComponent.width(), blockingGuiComponent.y(), debugColor);
            guiGraphics.hLine(blockingGuiComponent.x(), blockingGuiComponent.x() + blockingGuiComponent.width(), blockingGuiComponent.y() + blockingGuiComponent.height(), debugColor);

            guiGraphics.vLine(blockingGuiComponent.x(), blockingGuiComponent.y(), blockingGuiComponent.y() + blockingGuiComponent.height(), debugColor);
            guiGraphics.vLine(blockingGuiComponent.x() + blockingGuiComponent.width(), blockingGuiComponent.y(), blockingGuiComponent.y() + blockingGuiComponent.height(), debugColor);

        });

    }


    public void removeGuiBlocking(ResourceLocation id, boolean updateSlots) {
        this.guiBlockings.removeIf(blockingGuiComponent -> blockingGuiComponent.id().equals(id));

        if(updateSlots)
            PRESENT_OVERLAYS.stream().filter(abstractEivOverlay -> abstractEivOverlay instanceof AbstractEivItemListOverlay).forEach(abstractEivOverlay -> ((AbstractEivItemListOverlay) abstractEivOverlay).updateSlots());
    }

    public void removeGuiBlocking(Predicate<ResourceLocation> filter, boolean updateSlots){
        this.guiBlockings.removeIf(blockingGuiComponent -> filter.test(blockingGuiComponent.id()));

        if(updateSlots)
            PRESENT_OVERLAYS.stream().filter(abstractEivOverlay -> abstractEivOverlay instanceof AbstractEivItemListOverlay).forEach(abstractEivOverlay -> ((AbstractEivItemListOverlay) abstractEivOverlay).updateSlots());
    }

    public void setGuiBlocking(BlockingGuiComponent comp) {
        List<BlockingGuiComponent> old = new ArrayList<>(this.guiBlockings);
        this.removeGuiBlocking(comp.id(), false);
        this.guiBlockings.add(comp);

        if (!old.equals(this.guiBlockings))
            this.updateAllSlots();

    }

    private void updateAllSlots(){
        PRESENT_OVERLAYS.stream()
                .filter(abstractEivOverlay -> abstractEivOverlay instanceof AbstractEivItemListOverlay)
                .map(abstractEivOverlay -> (AbstractEivItemListOverlay) abstractEivOverlay)
                .forEach(AbstractEivItemListOverlay::updateSlots);
    }

    public List<BlockingGuiComponent> allGuiBlockings() {
        return this.guiBlockings;
    }


    private static final List<AbstractEivOverlay> PRESENT_OVERLAYS = new ArrayList<>();

    public static void registerOverlay(AbstractEivOverlay overlay) {
        PRESENT_OVERLAYS.add(overlay);
    }

}
