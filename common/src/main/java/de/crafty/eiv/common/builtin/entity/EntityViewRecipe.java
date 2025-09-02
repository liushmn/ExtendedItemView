package de.crafty.eiv.common.builtin.entity;

import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class EntityViewRecipe implements IEivViewRecipe {

    private final EntityType<?> entityType;
    private final List<SlotContent> drops;

    private LivingEntity previewEntity;

    private int animationTick = 0;
    private boolean hovered = false;

    public EntityViewRecipe(EntityServerRecipe serverRecipe) {
        this.entityType = serverRecipe.getEntityType();

        List<ItemStack> drops = serverRecipe.getDrops();
        List<SlotContent> dropContents = new ArrayList<>();

        for (int i = 0; i < this.getViewType().getSlotCount(); i++) {
            if (drops.size() > i)
                dropContents.add(SlotContent.of(drops.get(i)));
            else
                dropContents.add(SlotContent.of());
        }

        this.drops = dropContents;
    }

    public EntityType<?> getEntityType() {
        return this.entityType;
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return EntityViewType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        for (int i = 0; i < this.drops.size(); i++) {
            if (i < 9)
                slotFillContext.bindSlot(i, this.drops.get(i));
            else
                slotFillContext.bindOptionalSlot(i, this.drops.get(i), RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
        }

    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of();
    }

    @Override
    public List<SlotContent> getResults() {
        return this.drops;
    }


    @Override
    public void tick() {

        if (this.hovered)
            return;

        this.animationTick++;
        if (this.animationTick >= 360)
            this.animationTick = 0;
    }

    @Override
    public void initRecipe() {

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return;

        Entity entity = this.getEntityType().create(level, EntitySpawnReason.LOAD);
        if (entity instanceof LivingEntity livingEntity) {
            this.previewEntity = livingEntity;
            this.previewEntity.setYBodyRot(30.0F);
            this.previewEntity.setYHeadRot(30.0F);
        }
    }

    @Override
    public void fadeRecipe() {
        if (this.previewEntity != null)
            this.previewEntity.remove(Entity.RemovalReason.DISCARDED);
    }

    @Override
    public void renderRecipe(RecipeViewScreen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        Component entityName = this.entityType.getDescription();

        this.renderEntity(guiGraphics, mouseX, mouseY, partialTicks);

        if (mouseX >= 65 && mouseX <= 65 + 32 && mouseY >= 0 && mouseY <= 32)
            this.hovered = true;
        else
            this.hovered = false;

        if (this.hovered)
            guiGraphics.renderTooltip(screen.getFont(), Component.empty().append(entityName).withStyle(ChatFormatting.GOLD), mouseX, mouseY);

    }

    private void renderEntity(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        if (this.previewEntity == null)
            return;

        float scale = 12.0F;

        AABB boundingBox = this.previewEntity.getBoundingBox();
        if (boundingBox.getYsize() * scale > 26)
            scale = (float) (26.0F / boundingBox.getYsize());

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(81, 29, 0);
        InventoryScreen.renderEntityInInventory(guiGraphics, 0, 0, scale, new Vector3f(), new Quaternionf().rotationXYZ((float) Math.toRadians(180.0F), (this.animationTick + partialTicks) / 180.0F * Mth.PI, 0.0F), null, this.previewEntity);
        guiGraphics.pose().popPose();

    }
}
