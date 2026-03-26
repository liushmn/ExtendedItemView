package de.crafty.eiv.common.recipe.inventory;

import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.config.Configs;
import de.crafty.eiv.common.network.payload.transfer.ServerboundTransferPayload;
import de.crafty.eiv.common.overlay.itemlist.view.ItemViewOverlay;
import de.crafty.eiv.common.recipe.rendering.AnimationTicker;
import de.crafty.eiv.common.recipe.util.EivUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeViewScreen extends AbstractContainerScreen<RecipeViewMenu> {

    private static final Identifier VIEW_LOCATION = Identifier.fromNamespaceAndPath(CommonEIV.MODID, "textures/gui/recipe_view.png");

    //Timestamp when opening the view
    private final long timestamp;

    private Button prevRecipe, nextRecipe;
    private Component guiTitle, page;

    private final List<AnimationTicker> animationTickers;
    private final HashMap<Identifier, Integer> animationTickCache;

    private final List<Button> transferButtons;
    private final List<RecipeShareButton> shareButtons;

    //View Type
    private final List<ViewTypeButton> viewTypeButtons;
    private int viewTypePage;
    private Button prevTypePage, nextTypePage;
    
    private int viewHeight, viewWidth;

    public RecipeViewScreen(RecipeViewMenu recipeViewMenu, Inventory inventory, Component component) {
        super(recipeViewMenu, inventory, component);

        this.transferButtons = new ArrayList<>();
        this.shareButtons = new ArrayList<>();
        this.viewTypeButtons = new ArrayList<>();
        this.viewTypePage = 0;

        this.animationTickers = new ArrayList<>();
        this.animationTickCache = new HashMap<>();

        this.viewHeight = this.getMenu().getHeight();
        this.viewWidth = this.getMenu().getWidth();

        this.guiTitle = component;
        this.page = this.createPageComponent();


        this.timestamp = inventory.player.level().getGameTime();
        recipeViewMenu.setViewScreen(this);
    }


    private Component createPageComponent() {
        return Component.literal((this.getMenu().getCurrentPage() + 1) + "/" + (this.getMenu().getMaxPageIndex() + 1));
    }


    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {

        if (CommonEIVClient.GO_BACK_RECIPE.matchesMouse(mouseButtonEvent) && this.getMenu().goBack())
            return true;
        if (CommonEIVClient.GO_FORWARD_RECIPE.matchesMouse(mouseButtonEvent) && this.getMenu().goForward())
            return true;

        return super.mouseReleased(mouseButtonEvent);
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {

        if (CommonEIVClient.GO_BACK_RECIPE.matches(keyEvent) && this.getMenu().goBack())
            return true;

        if (CommonEIVClient.GO_FORWARD_RECIPE.matches(keyEvent) && this.getMenu().goForward())
            return true;

        return super.keyPressed(keyEvent);
    }


    @Override
    protected void init() {
        super.init();

        this.prevRecipe = Button.builder(Component.literal("<"), button -> {
                    this.getMenu().prevPage();
                })
                .size(12, 12)
                .build();

        this.nextRecipe = Button.builder(Component.literal(">"), button -> {
                    this.getMenu().nextRecipe();
                })
                .size(12, 12)
                .build();

        this.prevTypePage = Button.builder(Component.literal("<"), button -> {
                    this.viewTypePage = Math.max(this.viewTypePage - 1, 0);
                    this.checkGui();
                })
                .size(12, 12)
                .build();

        this.nextTypePage = Button.builder(Component.literal(">"), button -> {
                    this.viewTypePage = Math.min(this.viewTypePage + 1, this.getMenu().getViewTypeOrder().size() / 5);
                    this.checkGui();
                })
                .size(12, 12)
                .build();

        this.checkGui();

        this.addRenderableWidget(this.prevRecipe);
        this.addRenderableWidget(this.nextRecipe);
        this.addRenderableWidget(this.prevTypePage);
        this.addRenderableWidget(this.nextTypePage);

        int width = 24;
        int height = 24;

        this.viewTypeButtons.clear();
        for (int i = 0; i < this.getMenu().getViewTypeOrder().size(); i++) {
            int tempId = i % 5;

            int xPos = this.width / 2 - (5 * width / 2 + 4 * 2 / 2) + tempId * width + tempId * 2;
            int yPos = this.topPos - height - 1;

            this.viewTypeButtons.add(new ViewTypeButton(this, xPos, yPos, width, height, this.getMenu().getViewTypeOrder().get(i), i));
        }
    }


    protected void checkGui() {

        this.prevRecipe.active = this.getMenu().hasPrevRecipe();
        this.nextRecipe.active = this.getMenu().hasNextRecipe();

        this.prevTypePage.visible = this.viewTypePage > 0;
        this.nextTypePage.visible = this.viewTypePage < (this.getMenu().getViewTypeOrder().size() - 1) / 5;

        this.viewHeight = this.getMenu().getHeight();
        this.viewWidth = this.getMenu().getWidth();

        this.topPos = 32;

        this.prevRecipe.setPosition(this.leftPos + 8, this.topPos + 4);
        this.nextRecipe.setPosition(this.leftPos + this.viewWidth - 8 - 12, this.topPos + 4);

        this.prevTypePage.setPosition(this.width / 2 - (5 * 24 + 4 * 2) / 2 - 2 - 12, this.topPos - 1 - 12 - 6);
        this.nextTypePage.setPosition(this.width / 2 + (5 * 24 + 4 * 2) / 2 + 2, this.topPos - 1 - 12 - 6);

        this.guiTitle = this.getMenu().getViewType().getDisplayName();
        this.titleLabelX = this.viewWidth / 2 - this.font.width(this.guiTitle) / 2;

        this.page = this.createPageComponent();

        this.animationTickCache.clear();
        this.checkTickers();


        //Transfer Button Logic
        this.transferButtons.forEach(this::removeWidget);
        this.transferButtons.clear();

        int guiLeft = this.leftPos + this.getMenu().guiOffsetLeft();

        for (int i = 0; i < this.getMenu().getCurrentDisplay().size(); i++) {
            final IEivViewRecipe currentView = this.getMenu().getCurrentDisplay().get(i);

            int guiTop = this.topPos + this.getMenu().guiOffsetTop(i);

            int finalI = i;
            Button button = Button.builder(Component.literal("+"), button1 -> {
                        if (!currentView.supportsItemTransfer())
                            return;

                        Minecraft.getInstance().setScreen(this.getMenu().getParentScreen());
                        LocalPlayer player = Minecraft.getInstance().player;

                        if (player != null && EivUtil.matchesAnyTransferClass(currentView, Minecraft.getInstance().screen)) {

                            if (!currentView.canTransferToScreen((AbstractContainerScreen<?>) Minecraft.getInstance().screen))
                                return;

                            IEivViewRecipe.RecipeTransferMap map = new IEivViewRecipe.RecipeTransferMap();
                            currentView.mapRecipeItems(map, (AbstractContainerScreen<?>) Minecraft.getInstance().screen);


                            RecipeTransferData transferData = this.getMenu().getTransferData().get(finalI);

                            HashMap<Integer, HashMap<Integer, ItemStack>> usedPlayerSlots = Minecraft.getInstance().hasShiftDown() ? transferData.getStackedData().getUsedPlayerSlots() : transferData.getUsedPlayerSlots();
                            //TODO make component required in recipes
                            CommonEIV.networkManager().sendPacketToServer(new ServerboundTransferPayload(map.getTransferMap(), usedPlayerSlots));

                        }

                    })
                    .size(12, 12)
                    .pos(guiLeft + currentView.getViewType().getDisplayWidth() + 4, guiTop + currentView.getViewType().getDisplayHeight() / 2 - 6)
                    .build();

            RecipeTransferData data = this.getMenu().getTransferData().get(i);
            button.active = data.isSuccess() && currentView.supportsItemTransfer() && EivUtil.matchesAnyTransferClass(currentView, this.getMenu().getParentScreen()) && currentView.canTransferToScreen((AbstractContainerScreen<?>) this.getMenu().getParentScreen());
            button.visible = currentView.supportsItemTransfer();

            this.addRenderableWidget(button);
            this.transferButtons.add(button);

        }

        this.shareButtons.forEach(this::removeWidget);
        this.shareButtons.clear();


        for (int i = 0; i < this.getMenu().getCurrentDisplay().size(); i++) {
            IEivViewRecipe currentView = this.getMenu().getCurrentDisplay().get(i);
            IEivRecipeViewType viewType = currentView.getViewType();
            ;

            if (!viewType.supportsRecipeShare())
                continue;

            int topPos = this.topPos + this.getMenu().guiOffsetTop(i) + viewType.getShareButtonLocation().y();
            int leftPos = this.leftPos + this.getMenu().guiOffsetLeft() + viewType.getShareButtonLocation().x();

            RecipeShareButton shareButton = new RecipeShareButton(currentView, 14, 14, 12, 12, (button -> ((RecipeShareButton) button).shareRecipe()));
            shareButton.setPosition(leftPos, topPos);

            shareButton.active = Configs.CLIENT_SETTINGS.chatEmbeddings();

            this.shareButtons.add(shareButton);
            this.addRenderableWidget(shareButton);
        }


    }

    private void checkTickers() {
        this.animationTickers.forEach(animationTicker -> {
            this.animationTickCache.put(animationTicker.id(), animationTicker.getTick());
        });

        this.animationTickers.clear();

        this.getMenu().getCurrentDisplay().forEach(recipe -> {
            recipe.getAnimationTickers().forEach(animationTicker -> {
                this.animationTickers.add(animationTicker);

                if (this.animationTickCache.containsKey(animationTicker.id()))
                    animationTicker.setTick(this.animationTickCache.get(animationTicker.id()));
                else
                    animationTicker.resetTick();
            });
        });
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphicsExtractor, int xm, int ym) {
        guiGraphicsExtractor.text(this.font, this.guiTitle, this.titleLabelX, this.titleLabelY, -12566464, false);
        guiGraphicsExtractor.text(this.font, this.page, (this.viewWidth - font.width(this.page)) / 2, this.viewHeight - 12, -12566464, false);
    }

    @Override
    public void extractContents(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float a) {
        super.extractContents(guiGraphicsExtractor, mouseX, mouseY, a);
        this.extractTooltip(guiGraphicsExtractor, mouseX, mouseY);
    }
    

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {

        if (mouseX <= this.leftPos && mouseX >= this.leftPos - 25 && mouseY >= this.topPos && mouseY <= this.topPos + this.viewHeight) {
            if (scrollY < 0)
                this.getMenu().nextReference();

            if (scrollY > 0)
                this.getMenu().prevReference();

            return true;
        }

        if (!(mouseX >= this.leftPos && mouseX <= this.leftPos + this.viewWidth && mouseY >= this.topPos && mouseY <= this.topPos + this.viewHeight))
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);


        if (scrollY < 0) {
            this.getMenu().nextPage();
            this.checkTickers();
        }
        if (scrollY > 0) {
            this.getMenu().prevPage();
            this.checkTickers();
        }

        if (scrollY != 0)
            this.page = this.createPageComponent();

        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (mouseButtonEvent.button() == 1 && this.hoveredSlot != null) {
            ItemViewOverlay.INSTANCE.openRecipeView(this.hoveredSlot.getItem(), ItemViewOverlay.ItemViewOpenType.INPUT);
            return true;
        }

        if (mouseButtonEvent.button() == 0 && this.hoveredSlot != null) {
            ItemViewOverlay.INSTANCE.openRecipeView(this.hoveredSlot.getItem(), ItemViewOverlay.ItemViewOpenType.RESULT);
            return true;
        }

        if (mouseButtonEvent.button() == 0) {

            for (int i = this.viewTypePage * 5; i < this.viewTypePage * 5 + 5 && this.viewTypeButtons.size() > i; i++) {
                if (this.viewTypeButtons.get(i).onClick(mouseButtonEvent.button(), (int) mouseButtonEvent.x(), (int) mouseButtonEvent.y()))
                    return true;
            }

        }

        return super.mouseClicked(mouseButtonEvent, bl);
    }

    private boolean isPrevTypeHovered(double mouseX, double mouseY) {
        return mouseX >= this.leftPos - 14 - 2 && mouseX <= this.leftPos - 2 && mouseY >= this.topPos + 2 && mouseY <= this.topPos + 2 + 14;
    }

    private boolean isNextTypeHovered(double mouseX, double mouseY) {
        return mouseX >= this.leftPos + this.viewWidth + 2 && mouseX <= this.leftPos + this.viewWidth + 2 + 14 && mouseY >= this.topPos + 2 && mouseY <= this.topPos + 2 + 14;
    }

    @Override
    protected void containerTick() {
        this.animationTickers.forEach(AnimationTicker::tick);

        if (this.minecraft == null || this.minecraft.player == null)
            return;

        long timeOpen = (this.minecraft.player.level().getGameTime() - this.timestamp);

        if (timeOpen % 25 == 0 && timeOpen >= 25)
            this.getMenu().tickContents();

        this.getMenu().getCurrentDisplay().forEach(IEivViewRecipe::tick);
    }

    public int getLeftPos() {
        return this.leftPos;
    }

    public int getTopPos() {
        return this.topPos;
    }

    public int getGuiWidth() {
        return this.viewWidth;
    }

    public int getGuiHeight() {
        return this.viewHeight;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(guiGraphicsExtractor, mouseX, mouseY, partialTicks);
        guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.leftPos, this.topPos, 0.0F, 0.0F, this.viewWidth, this.viewHeight - 3, 256, 256);
        guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.leftPos, this.topPos + (this.viewHeight - 3), 0, 256 - 3, this.viewWidth, 3, 256, 256);


        IEivRecipeViewType viewType = this.getMenu().getViewType();

        //Render icons

        int current = this.getMenu().getCurrentTypeIndex();

        for (int i = 0; i < 5; i++) {

            guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.width / 2 - (5 * 24 + 4 * 2) / 2 + i * 24 + i * 2, this.topPos - 24 - 1, 208, 0, 24, 24, 256, 256);
        }

        for (int i = this.viewTypePage * 5; i < this.viewTypePage * 5 + 5 && this.viewTypeButtons.size() > i; i++) {
            this.viewTypeButtons.get(i).render(guiGraphicsExtractor, mouseX, mouseY, partialTicks);
        }


        //Render craft references

        for (int i = 0; i < this.getMenu().getDisplayableCraftReferences(); i++) {
            guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.leftPos - 25, this.topPos + 4 + i * 24 + i, 231, 48, 25, 24, 256, 256);
        }

        if (this.getMenu().getCurrentCraftReference() > 0)
            guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.leftPos - 4 - 5 - 8, this.topPos + 4 - 1 - 4, 248, 72, 8, 4, 256, 256);

        if (this.getMenu().getCurrentCraftReference() < this.getMenu().getViewType().getCraftReferences().size() - this.getMenu().getDisplayableCraftReferences())
            guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.leftPos - 4 - 5 - 8, this.topPos + 4 + (this.getMenu().getDisplayableCraftReferences()) * 25, 248, 76, 8, 4, 256, 256);

        int guiLeft = this.leftPos + this.getMenu().guiOffsetLeft();

        for (int i = 0; i < this.getMenu().getCurrentDisplay().size(); i++) {

            int guiTop = this.topPos + this.getMenu().guiOffsetTop(i);

            guiGraphicsExtractor.pose().pushMatrix();
            guiGraphicsExtractor.pose().translate(guiLeft, guiTop);

            guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, viewType.getGuiTexture(), 0, 0, 0, 0, viewType.getDisplayWidth(), viewType.getDisplayHeight(), viewType.getDisplayWidth(), viewType.getDisplayHeight());

            //Optional slot rendering
            this.getMenu().slots.stream().filter(slot -> this.getMenu().isOptionalSlot(slot.index) && slot.hasItem()).forEach(slot -> {
                guiGraphicsExtractor.pose().pushMatrix();
                guiGraphicsExtractor.pose().translate(slot.x - (guiLeft - this.leftPos) - 1, slot.y - (guiTop - this.topPos) - 1);
                this.getMenu().getOptionalSlotRenderer(slot.index).render(guiGraphicsExtractor, mouseX - guiLeft, mouseY - guiTop, partialTicks);
                guiGraphicsExtractor.pose().popMatrix();
            });
            this.renderInvalidSlots(guiGraphicsExtractor, i);
            this.getMenu().getCurrentDisplay().get(i).renderRecipe(this, new IEivViewRecipe.RecipePosition(guiLeft, guiTop, viewType.getDisplayWidth(), viewType.getDisplayHeight()), guiGraphicsExtractor, mouseX - guiLeft, mouseY - guiTop, partialTicks);
            guiGraphicsExtractor.pose().popMatrix();
        }    }




    private void renderInvalidSlots(GuiGraphicsExtractor guiGraphicsExtractor, int displayId) {
        Button button = this.transferButtons.get(displayId);
        if (!button.isHovered())
            return;

        IEivViewRecipe current = this.getMenu().getCurrentDisplay().get(displayId);

        RecipeTransferData data = this.getMenu().getTransferData().get(displayId);
        if (data.isSuccess())
            return;

        for (int slotId : data.getSlotResults().keySet()) {

            if (data.getSlotResults().get(slotId))
                continue;

            int actualSlotId = slotId + (displayId * current.getViewType().getSlotCount());
            Slot invSlot = this.getMenu().getSlot(actualSlotId);

            int x = invSlot.x;
            int y = invSlot.y;

            guiGraphicsExtractor.pose().pushMatrix();
            guiGraphicsExtractor.pose().translate(-this.getMenu().guiOffsetLeft(), -this.getMenu().guiOffsetTop(displayId));
            guiGraphicsExtractor.fill(x, y, x + 16, y + 16, new Color(255, 0, 0, 64).getRGB());
            guiGraphicsExtractor.pose().popMatrix();

        }
    }


    public Slot getHoveredSlot() {
        return this.hoveredSlot;
    }

    record ViewTypeButton(RecipeViewScreen viewScreen, int x, int y, int width, int height, IEivRecipeViewType viewType,
                          int viewTypeId) {


        private boolean onClick(int mouseButton, int mouseX, int mouseY) {
            if (!(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height))
                return false;

            this.viewScreen.getMenu().setViewType(this.viewTypeId);
            AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
            return true;
        }

        private void onHover(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY) {
            if (!(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height))
                return;

            guiGraphicsExtractor.setComponentTooltipForNextFrame(Minecraft.getInstance().font, List.of(this.viewType.getDisplayName()), mouseX, mouseY);
        }

        private void render(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTicks) {
            guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, VIEW_LOCATION, this.x(), this.y(), 232, this.viewType() == this.viewScreen.getMenu().getViewType() ? 24 : 0, 24, 24, 256, 256);
            guiGraphicsExtractor.fakeItem(this.viewType().getIcon(), this.x() + 4, this.y() + 4);

            this.onHover(guiGraphicsExtractor, mouseX, mouseY);
        }

    }
}

