package de.crafty.eiv.common.mixin.client.gui.screens;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.embeddings.ChatEmbedding;
import de.crafty.eiv.common.embeddings.util.EmbeddingComponentContents;
import de.crafty.eiv.common.mixin.client.gui.components.IChatComponentAccessor;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public abstract class MixinScreen extends AbstractContainerEventHandler implements Renderable {


    @Shadow
    protected Font font;

    @Shadow
    @Final
    protected Minecraft minecraft;


    @Redirect(method = "getTooltipFromItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getTooltipLines(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;"))
    private static List<Component> appendRecipeTag(ItemStack stack, Player player, TooltipFlag flag) {
        List<Component> tooltip = stack.getTooltipLines(player, flag);

        CompoundTag tagTag = stack.getTag() == null ? new CompoundTag() : stack.getTag();
        if (tagTag.contains(CommonEIV.MODID + "_recipeTag")) {
            ListTag tagList = tagTag.getList(CommonEIV.MODID + "_recipeTag", CompoundTag.TAG_STRING);
            tagList.forEach(tag -> {
                tooltip.add(
                        Component.translatable("view.eiv.tags").append(": ").withStyle(ChatFormatting.GOLD)
                        .append(Component.literal("#" + tag.getAsString()).withStyle(ChatFormatting.GRAY))
                );
            });
            tooltip.add(
                    Component.translatable("view.eiv.tags").append(": ").withStyle(ChatFormatting.GOLD)
                            .append(Component.literal("#" + tagTag.getString(CommonEIV.MODID + "_recipeTag")).withStyle(ChatFormatting.GRAY))

            );
        }

        if (Minecraft.getInstance().screen != null && Minecraft.getInstance().screen instanceof RecipeViewScreen viewScreen) {
            if (viewScreen.getHoveredSlot() != null && viewScreen.getHoveredSlot().hasItem())
                viewScreen.getMenu().getAdditionalStackModifier(viewScreen.getHoveredSlot().getContainerSlot()).addTooltip(stack, tooltip);
        }

        //TODO make more performance
        tooltip.add(Component.literal(CommonEIVClient.resolver().getModNameForItem(stack.getItem())).withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.ITALIC));

        return tooltip;
    }


    @Inject(method = "render", at = @At("TAIL"))
    private void addEmbeddings(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!ChatEmbedding.queue().isEmpty()) {
            ChatEmbedding embedding = ChatEmbedding.poll();
            if (embedding == null)
                return;

            for (int i = 0; i < embedding.getOccupiedLines(); i++) {
                this.minecraft.gui.getChat().addMessage(MutableComponent.create(EmbeddingComponentContents.createUnbound()));
            }

            ChatEmbedding.cache(embedding);

            if (!((IChatComponentAccessor) this.minecraft.gui.getChat()).getAllMessages().isEmpty())
                embedding.bindMsg(((IChatComponentAccessor) this.minecraft.gui.getChat()).getAllMessages().get(0));
        }
    }
}
