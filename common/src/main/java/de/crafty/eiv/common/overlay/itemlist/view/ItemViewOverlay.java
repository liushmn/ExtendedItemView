package de.crafty.eiv.common.overlay.itemlist.view;

import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.api.recipe.ItemView;
import de.crafty.eiv.common.overlay.AbstractEivOverlay;
import de.crafty.eiv.common.overlay.itemlist.AbstractEivItemListOverlay;
import de.crafty.eiv.common.overlay.itemlist.bookmark.ItemBookmarkOverlay;
import de.crafty.eiv.common.overlay.ItemSlot;
import de.crafty.eiv.common.overlay.OverlayManager;
import de.crafty.eiv.common.recipe.ClientRecipeCache;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ItemViewOverlay extends AbstractEivItemListOverlay {

    public static final ItemViewOverlay INSTANCE = new ItemViewOverlay();

    private EditBox searchbar = null;

    private static final int HEADER_HEIGHT = 20;
    private static final int FOOTER_HEIGHT = 40;

    private long lastSearchbarClick = -1;

    private String currentQuery;
    private boolean itemFilterMode;

    private int startIndex;

    public ItemViewOverlay() {
        super(-1, -1, -1, -1);
        this.currentQuery = "";
        this.itemFilterMode = false;
        this.startIndex = 0;
    }


    @Override
    public void setEnabled(boolean enabled) {
        boolean prev = this.isEnabled();
        super.setEnabled(enabled);

        if (prev != enabled && enabled)
            this.searchbar.visible = true;

        if (prev != enabled && !enabled)
            this.searchbar.visible = false;
    }


    @Override
    public void onScreenChanged(InventoryPositionInfo info) {
        this.initForScreen(info.screen(), info);
        this.createSearchbarElement(OverlayManager.INSTANCE.currentInfo());
    }


    @Override
    protected void placeWidgets(ScreenContext ctx) {

        ctx.addRenderable(this.searchbar);
    }

    private void initForScreen(AbstractContainerScreen<? extends AbstractContainerMenu> screen, InventoryPositionInfo invInfo) {

        //-14 for Cleaner Appereance
        int spaceForOverlayX = screen.width - (invInfo.leftPos() + invInfo.imageWidth());
        spaceForOverlayX -= spaceForOverlayX > 2 * ITEM_ENTRY_SIZE + 14 ? 14 : 0;

        this.width = spaceForOverlayX - ((spaceForOverlayX - 4) % ITEM_ENTRY_SIZE);
        this.height = screen.height;

        this.x = screen.width - this.width;
        this.y = 0;

        this.itemStartX = this.x + 2;
        this.itemStartY = HEADER_HEIGHT;

        this.itemEndX = this.x + this.width - 2;
        this.itemEndY = this.y + this.height - FOOTER_HEIGHT;

        this.updateQuery(this.getCurrentQuery());
    }


    /**
     * Handles searchbar changes => responsible for custom prefixes
     *
     * @param newQuery
     */
    private void updateQuery(String newQuery) {
        if (!newQuery.equals(this.currentQuery))
            this.startIndex = 0;

        this.currentQuery = newQuery;

        if (newQuery.startsWith("@"))
            this.availableItems = ItemFilters.modId(newQuery.substring(1));
        else if (newQuery.startsWith("#"))
            this.availableItems = ItemFilters.tag(newQuery.substring(1));
        else
            this.availableItems = ItemFilters.defaultFilter(newQuery);

        this.availableItems().removeIf(stack -> ItemView.getExcluded().contains(stack.getItem()));

        this.updateSlots();
    }


    @Override
    protected boolean keyPressed(int i, int j, int k) {
        super.keyPressed(i, j, k);


        for (ItemSlot slot : this.itemSlots()) {
            if (!slot.isHovered())
                continue;

            if (CommonEIVClient.ADD_BOOKMARK_KEYBIND.matches(i, j))
                ItemBookmarkOverlay.INSTANCE.bookmarkItem(slot.getStack());

            return true;
        }

        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.searchbar.isHovered() && mouseButton == 1) {
            this.searchbar.setValue("");
            OverlayManager.INSTANCE.currentInfo().screen().setFocused(this.searchbar);
        }

        if (mouseButton == 0 && !this.searchbar.isHovered() && this.searchbar.isFocused())
            this.searchbar.setFocused(false);


        if (this.searchbar.isHovered() && mouseButton == 0) {

            if (this.lastSearchbarClick != -1 && System.currentTimeMillis() - this.lastSearchbarClick <= 400) {
                this.itemFilterMode = !this.itemFilterMode;
                this.lastSearchbarClick = -1;
            } else
                this.lastSearchbarClick = System.currentTimeMillis();

        }

        return false;
    }


    @Override
    protected boolean charTyped(char c, int i) {
        return this.searchbar.isFocused() && this.searchbar.charTyped(c, i);
    }


    @Override
    protected void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        Minecraft client = Minecraft.getInstance();
        InventoryPositionInfo invInfo = OverlayManager.INSTANCE.currentInfo();


        Font font = client.font;


        guiGraphics.drawCenteredString(font, "ItemView", invInfo.screen().width - this.getWidth() / 2, 6, -1);
        guiGraphics.fill(this.x, 0, invInfo.screen().width, invInfo.screen().height, new Color(0, 0, 0, 64).getRGB());

        if (this.fittingPerPage() > 0) {
            int maxPageIndex = (this.availableItems().size() / (this.fittingPerPage()));
            guiGraphics.drawCenteredString(font, (this.getPage() + 1) + "/" + (maxPageIndex + 1), invInfo.screen().width - this.width / 2, invInfo.screen().height - 2 - 20 - 10, -1);
        }


        for (ItemSlot slot : this.itemSlots()) {
            slot.render(guiGraphics, mouseX, mouseY, partialTicks);
        }


        this.renderItemHighlighting(OverlayManager.INSTANCE.currentInfo().screen(), guiGraphics, mouseX, mouseY, partialTicks);

    }


    public void renderItemHighlighting(AbstractContainerScreen<?> screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (!this.itemFilterMode)
            return;


        screen.getMenu().slots.forEach(slot -> {

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(OverlayManager.INSTANCE.currentInfo().leftPos() - 1, OverlayManager.INSTANCE.currentInfo().topPos() - 1);
            if (!slot.hasItem() || this.getAvailableItems().stream().noneMatch(stack -> stack.getItem() == slot.getItem().getItem())) {
                guiGraphics.fill(slot.x, slot.y, slot.x + 18, slot.y + 18, new Color(0, 0, 0, 128).getRGB());
            }
            guiGraphics.pose().popMatrix();

        });
    }


    public void createSearchbarElement(InventoryPositionInfo info) {
        int boxWidth = Math.min(100, info.screenWidth() - this.x - 4);

        int x = info.screenWidth() - this.getWidth() / 2 - boxWidth / 2;
        int y = info.screenHeight() - 22;

        if (this.searchbar != null && boxWidth == this.searchbar.getWidth() && x == this.searchbar.getX() && y == this.searchbar.getY())
            return;

        this.searchbar = new EditBox(Minecraft.getInstance().font, x, y, boxWidth, 20, Component.literal("eiv:searchbar"));
        this.searchbar.setMaxLength(32);
        this.searchbar.setValue(this.getCurrentQuery());
        this.searchbar.setResponder(this::updateQuery);


        this.searchbar.visible = this.isEnabled();
    }


    public void openRecipeView(ItemStack stack, ItemViewOpenType openType) {
        if (stack.isEmpty())
            return;

        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null)
            return;

        List<IEivViewRecipe> foundRecipes = openType.recipeProvider().retrieveRecipes(stack);

        if (!foundRecipes.isEmpty()) {
            Screen parent = Minecraft.getInstance().screen;

            ArrayList<RecipeViewScreen> viewHistory = new ArrayList<>();

            if (parent instanceof RecipeViewScreen viewScreen) {
                parent = viewScreen.getMenu().getParentScreen();
                viewHistory = viewScreen.getMenu().getViewHistory();
            }

            Minecraft.getInstance().setScreen(new RecipeViewScreen(new RecipeViewMenu(parent, 0, clientPlayer.getInventory(), foundRecipes, stack, openType == ItemViewOpenType.RESULT ? SlotContent.Type.RESULT : SlotContent.Type.INGREDIENT, viewHistory), clientPlayer.getInventory(), Component.empty()));
        }


    }


    public EditBox getSearchbar() {
        return this.searchbar;
    }

    public boolean isItemFilterMode() {
        return this.itemFilterMode;
    }


    public String getCurrentQuery() {
        return this.currentQuery;
    }


    public List<ItemStack> getAvailableItems() {
        return this.availableItems();
    }


    public enum ItemViewOpenType {
        INPUT(ClientRecipeCache.INSTANCE::getRecipesForCraftingInput),
        RESULT(ClientRecipeCache.INSTANCE::getRecipesForCraftingOutput);

        final RecipeProvider recipeProvider;

        ItemViewOpenType(RecipeProvider recipeProvider) {
            this.recipeProvider = recipeProvider;
        }

        RecipeProvider recipeProvider() {
            return this.recipeProvider;
        }

        interface RecipeProvider {

            List<IEivViewRecipe> retrieveRecipes(ItemStack stack);
        }
    }


}
