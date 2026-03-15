package de.crafty.eiv.common.recipe.inventory;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.config.Configs;
import de.crafty.eiv.common.embeddings.ChatEmbedding;
import de.crafty.eiv.common.embeddings.container.RecipeChatEmbedding;
import de.crafty.eiv.common.embeddings.container.SingleItemChatEmbedding;
import de.crafty.eiv.common.network.EivNetworkManager;
import de.crafty.eiv.common.network.payload.embedding.ServerboundShareRecipePayload;
import de.crafty.eiv.common.recipe.ClientRecipeCache;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RecipeShareButton extends SpriteIconButton.CenteredIcon {


    private static final WidgetSprites SHARE_BUTTON_SPRITES = new WidgetSprites(Identifier.fromNamespaceAndPath(CommonEIV.MODID, "share"));

    private final IEivViewRecipe recipe;

    protected RecipeShareButton(IEivViewRecipe shared, int buttonWidth, int buttonHeight, int spriteWidth, int spriteHeight, OnPress onPress) {
        super(buttonWidth, buttonHeight, Component.literal(""), spriteWidth, spriteHeight, SHARE_BUTTON_SPRITES, onPress, null, null);

        this.recipe = shared;

    }


    public IEivViewRecipe getShared() {
        return this.recipe;
    }

    protected void shareRecipe() {

        if(!Configs.CLIENT_SETTINGS.chatEmbeddings())
            return;

        Identifier recipeId = ClientRecipeCache.INSTANCE.getIdFromRecipe(this.recipe);

        if (recipeId != null) {

            CompoundTag extraData = new CompoundTag();
            this.recipe.saveExtraEmbeddingData(extraData);

            EivNetworkManager.INSTANCE.sendPacketToServer(new ServerboundShareRecipePayload(recipeId, extraData));
        }

    }


}
