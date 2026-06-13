package de.crafty.eiv.common.overlay.itemlist.view;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.api.recipe.ItemView;
import de.crafty.eiv.common.config.Configs;
import de.crafty.eiv.common.gui.EivClientSettingsScreen;
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
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ItemViewOverlay extends AbstractEivItemListOverlay {

    public static final ItemViewOverlay INSTANCE = new ItemViewOverlay();
    private static final ResourceLocation SETTINGS_WHEEL = new ResourceLocation(CommonEIV.MODID, "settings_wheel");

    private EditBox searchbar = null;

    private static final int HEADER_HEIGHT = 20;
    private static final int FOOTER_HEIGHT = 40;

    private long lastSearchbarClick = -1;

    private String currentQuery;
    private boolean itemFilterMode;

    public ItemViewOverlay() {
        super(-1, -1, -1, -1);
        this.currentQuery = "";
        this.itemFilterMode = false;
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
        super.onScreenChanged(info);
        this.updateQuery(this.getCurrentQuery());
        this.createSearchbarElement(OverlayManager.INSTANCE.currentInfo());
    }


    @Override
    protected void placeWidgets(ScreenContext ctx) {

        ctx.addRenderable(this.searchbar);

        InventoryPositionInfo info = OverlayManager.INSTANCE.currentInfo();


        //---- Client Settings Button ----
        ImageButton btn = new ImageButton(0, info.screenHeight() - 18, 18, 18, 0, 0, 18, SETTINGS_WHEEL, 14, 14, (button) -> {
             Minecraft.getInstance().setScreen(new EivClientSettingsScreen(info.screen()));
        });
        btn.setMessage(Component.translatable("eiv.client_settings.btn"));

        ctx.addRenderable(btn);
    }

    private void initForScreen(AbstractContainerScreen<? extends AbstractContainerMenu> screen, InventoryPositionInfo invInfo) {

        //-14 for cleaner appearance
        this.width = invInfo.screenWidth() - ((invInfo.screenWidth() - 176) / 2 + 176) - 14;
        this.width -= (this.width - 4) % ITEM_ENTRY_SIZE;

        this.height = screen.height;

        this.x = invInfo.screenWidth() - this.width;
        this.y = 0;

        this.itemStartX = this.x + 2;
        this.itemStartY = HEADER_HEIGHT;

        this.itemEndX = this.x + this.width - 2;
        this.itemEndY = this.y + this.height - FOOTER_HEIGHT;
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
    protected boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);


        for (ItemSlot slot : this.itemSlots()) {
            if (!slot.isHovered())
                continue;

            if (CommonEIVClient.ADD_BOOKMARK_KEYBIND.matches(keyCode, scanCode))
                ItemBookmarkOverlay.INSTANCE.bookmarkItem(slot.getStack());
        }

        return false;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton, boolean doubleClick) {
        super.mouseClicked(mouseX, mouseY, mouseButton, doubleClick);

        if (this.searchbar.isHovered() && mouseButton == 1) {
            this.searchbar.setValue("");
            this.searchbar.setFocused(true);
            OverlayManager.INSTANCE.currentInfo().screen().setFocused(this.searchbar);
        }


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
    protected void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.fittingPerPage() == 0)
            return;

        if (Configs.CLIENT_SETTINGS.isItemWrapMode())
            guiGraphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, new Color(0, 0, 0, 64).getRGB());
        else
            guiGraphics.fill(this.effectiveX, this.effectiveY, this.effectiveX + this.effectiveWidth, this.effectiveY + this.effectiveHeight, new Color(0, 0, 0, 64).getRGB());
    }

    @Override
    protected void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        Minecraft client = Minecraft.getInstance();
        Font font = client.font;


        if (Configs.CLIENT_SETTINGS.isItemWrapMode())
            this.drawScaledString(font, guiGraphics, Component.literal("ItemView"), this.x + this.width / 2, this.y + 6, -1);
        else
            this.drawScaledString(font, guiGraphics, Component.literal("ItemView"), this.effectiveX + this.effectiveWidth / 2, this.effectiveY + 6, -1);


        if (this.fittingPerPage() > 0) {


            if (Configs.CLIENT_SETTINGS.isItemWrapMode())
                guiGraphics.drawCenteredString(font, (this.getPage() + 1) + "/" + (this.getMaxPageIndex() + 1), this.x + this.width - this.width / 2, this.y + this.height - 2 - 20 - 10, -1);
            else
                guiGraphics.drawCenteredString(font, (this.getPage() + 1) + "/" + (this.getMaxPageIndex() + 1), this.effectiveX + this.effectiveWidth / 2, this.effectiveY + this.effectiveHeight - 2 - 20 - 10, -1);
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

            if (!slot.isActive() || !slot.isHighlightable())
                return;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(OverlayManager.INSTANCE.currentInfo().leftPos() - 1, OverlayManager.INSTANCE.currentInfo().topPos() - 1, 0.0F);
            if (!slot.hasItem() || this.availableItems.stream().noneMatch(stack -> stack.getItem() == slot.getItem().getItem())) {
                guiGraphics.fill(slot.x, slot.y, slot.x + 18, slot.y + 18, new Color(0, 0, 0, 128).getRGB());
            }
            guiGraphics.pose().popPose();

        });
    }


    public void createSearchbarElement(InventoryPositionInfo info) {
        boolean wrapMode = Configs.CLIENT_SETTINGS.isItemWrapMode();
        int boxWidth = Math.min(100, (wrapMode ? this.width : this.effectiveWidth) - 4);

        int x = wrapMode ? (this.x + this.width / 2 - boxWidth / 2) : (this.effectiveX + this.effectiveWidth / 2 - boxWidth / 2);
        int y = info.screenHeight() - 22;


        if (this.searchbar != null && info.screen().getFocused() instanceof EditBox box && box.getMessage().equals(Component.literal("eiv:searchbar")))
            this.searchbar.setFocused(false);

        if (this.searchbar != null && boxWidth == this.searchbar.getWidth() && x == this.searchbar.getX() && y == this.searchbar.getY())
            return;


        EditBox newSearchbar = new EditBox(Minecraft.getInstance().font, x, y, boxWidth, 20, Component.literal("eiv:searchbar"));
        newSearchbar.setMaxLength(32);
        newSearchbar.setValue(this.getCurrentQuery());
        newSearchbar.setResponder(this::updateQuery);

        newSearchbar.visible = this.isEnabled();

        this.searchbar = newSearchbar;
    }


    public void openRecipeView(ItemStack stack, ItemViewOpenType openType) {
        this.openRecipeView(stack, openType, null);
    }

    public void openRecipeView(ItemStack stack, ItemViewOpenType openType, @Nullable IEivRecipeViewType type) {
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

            int containerId = parent instanceof AbstractContainerScreen<? extends AbstractContainerMenu> containerScreen ? containerScreen.getMenu().containerId : 0;

            RecipeViewScreen screen = new RecipeViewScreen(new RecipeViewMenu(parent, containerId, clientPlayer.getInventory(), foundRecipes, stack, openType == ItemViewOpenType.RESULT ? SlotContent.Type.RESULT : SlotContent.Type.INGREDIENT, viewHistory), clientPlayer.getInventory(), Component.empty());

            Minecraft.getInstance().setScreen(screen);

            if (type != null)
                screen.getMenu().setViewType(type);
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
