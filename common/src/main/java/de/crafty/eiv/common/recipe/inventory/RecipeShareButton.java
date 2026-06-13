package de.crafty.eiv.common.recipe.inventory;

import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.config.Configs;
import de.crafty.eiv.common.network.EivNetworkManager;
import de.crafty.eiv.common.network.payload.embedding.ServerboundShareRecipePayload;
import de.crafty.eiv.common.recipe.ClientRecipeCache;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

//TODO change locations of sprites
public class RecipeShareButton extends ImageButton {


    private static final ResourceLocation SHARE_BUTTON_LOCATION = new ResourceLocation("textures/gui/sprites/widget/share.png");

    private final IEivViewRecipe recipe;

    protected RecipeShareButton(IEivViewRecipe shared, int x, int y, int width, int height, int textureWidth, int textureHeight, OnPress onPress) {
        super(x, y, width, height, 0, 0, 0, SHARE_BUTTON_LOCATION, textureWidth, textureHeight, onPress);

        this.recipe = shared;

    }


    public IEivViewRecipe getShared() {
        return this.recipe;
    }

    protected void shareRecipe() {

        if(!Configs.CLIENT_SETTINGS.chatEmbeddings())
            return;

        ResourceLocation recipeId = ClientRecipeCache.INSTANCE.getIdFromRecipe(this.recipe);

        if (recipeId != null) {

            CompoundTag extraData = new CompoundTag();
            this.recipe.saveExtraEmbeddingData(extraData);

            EivNetworkManager.INSTANCE.sendPayloadToServer(new ServerboundShareRecipePayload(recipeId, extraData));
        }

    }


}
