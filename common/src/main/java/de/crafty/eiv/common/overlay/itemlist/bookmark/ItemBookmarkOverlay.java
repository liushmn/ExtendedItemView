package de.crafty.eiv.common.overlay.itemlist.bookmark;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.overlay.AbstractEivOverlay;
import de.crafty.eiv.common.overlay.ItemSlot;
import de.crafty.eiv.common.overlay.OverlayManager;
import de.crafty.eiv.common.overlay.itemlist.AbstractEivItemListOverlay;
import de.crafty.eiv.common.overlay.itemlist.view.ItemViewOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ItemBookmarkOverlay extends AbstractEivItemListOverlay {

    public static final ItemBookmarkOverlay INSTANCE = new ItemBookmarkOverlay();

    private static final int HEADER_HEIGHT = 20;
    private static final int FOOTER_HEIGHT = 40;

    private int startIndex;


    private ItemBookmarkOverlay() {
        super (-1, -1, -1, -1);
        this.startIndex = 0;
    }


    public void bookmarkItem(ItemStack stack) {
        if (!this.availableItems().contains(stack)) {
            this.availableItems().add(stack);
            this.updateSlots();
        }
    }


    public void saveBookmarkedItems(JsonObject json) {

        JsonArray array = new JsonArray();
        this.availableItems.forEach(stack -> {
            array.add(ItemStack.CODEC.encode(stack, JsonOps.INSTANCE, new JsonObject()).getOrThrow().getAsJsonObject());
        });

        json.add("bookmarkedItems", array);
    }

    public void loadBookmarkedItems(JsonObject json) {
        this.availableItems.clear();

        if(!json.has("bookmarkedItems"))
            return;

        json.getAsJsonArray("bookmarkedItems").forEach(jsonE -> {
            JsonObject jsonItem = jsonE.getAsJsonObject();

            DataResult<Pair<ItemStack, JsonElement>> result = ItemStack.CODEC.decode(JsonOps.INSTANCE, jsonItem);
            if(result.isSuccess())
                this.availableItems.add(result.getOrThrow().getFirst());
        });

        this.updateSlots();
    }

    @Override
    public void onScreenChanged(InventoryPositionInfo info) {
        this.initForScreen(info.screen(), info);
    }


    @Override
    public boolean scrollMouse(double mouseX, double mouseY, double scrolledX, double scrolledY) {

        if (mouseX > this.width)
            return false;

        if (CommonEIVClient.isCheatmodeActive()) {
            for (ItemSlot slot : this.itemSlots()) {
                if (!slot.isHovered())
                    continue;

                slot.changeCheatmodeCount((scrolledY < 0 ? -1 : 1));
                return true;
            }
        }

        int fittingPerPage = this.fittingPerPage();

        if (scrolledY < 0)
            this.startIndex = Math.min(this.startIndex + fittingPerPage, this.availableItems.size() - (this.availableItems.size() - ((this.availableItems().size() - 1) / fittingPerPage) * fittingPerPage));

        if (scrolledY > 0)
            this.startIndex = Math.max(0, this.startIndex - fittingPerPage);

        if (scrolledY != 0)
            this.updateSlots();

        return true;
    }

    @Override
    protected void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.itemSlots().isEmpty())
            return;

        Minecraft client = Minecraft.getInstance();

        Font font = client.font;
        InventoryPositionInfo info = OverlayManager.INSTANCE.currentInfo();

        if(this.fittingPerPage() <= 0)
            return;

        guiGraphics.drawCenteredString(font, Component.translatable("eiv.bookmarks").getString(), Math.max(this.width / 2, font.width(Component.translatable("eiv.bookmarks").getString()) / 2 + 2), 6, -1);
        guiGraphics.fill(this.x, 0, this.width, info.screen().height, new Color(0, 0, 0, 64).getRGB());
        String pageString = (this.getPage() + 1) + "/" + Math.max(((this.availableItems.size() - 1) / this.fittingPerPage() + 1), this.getPage() + 1);
        guiGraphics.drawCenteredString(font, pageString, Math.max(this.width / 2, font.width(pageString) / 2 + 2), info.screen().height - 2 - 20 - 10, -1);

        for (ItemSlot slot : this.itemSlots()) {
            slot.render(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        super.keyPressed(i, j, k);

        for (ItemSlot slot : this.itemSlots()) {
            if (!slot.isHovered())
                continue;

            if (CommonEIVClient.ADD_BOOKMARK_KEYBIND.matches(i, j)) {
                this.availableItems.remove(slot.getStack());
                this.updateSlots();
                return true;
            }

            break;
        }

        return false;
    }


    public void initForScreen(AbstractContainerScreen<? extends AbstractContainerMenu> screen, InventoryPositionInfo currentInfo) {

        //-14 for Cleaner Appereance
        int spaceForOverlayX = (screen instanceof AbstractRecipeBookScreen<?> recipeBookScreen && recipeBookScreen.recipeBookComponent.isVisible()) ? recipeBookScreen.recipeBookComponent.getXOrigin() - 32 : currentInfo.leftPos();
        spaceForOverlayX -= spaceForOverlayX > 2 * ITEM_ENTRY_SIZE + 14 ? 14 : 0;

        this.x = 0;
        this.y = 0;

        this.width = spaceForOverlayX - ((spaceForOverlayX - 4) % ITEM_ENTRY_SIZE);
        this.height = screen.height;

        this.itemStartX = 2;
        this.itemStartY = HEADER_HEIGHT;

        this.itemEndX = this.x + this.width - 2;
        this.itemEndY = this.y + this.height - FOOTER_HEIGHT;

        this.updateSlots();
    }

}
