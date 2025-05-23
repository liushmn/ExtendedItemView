package de.crafty.eiv.fabric;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.extra.FluidItemModel;
import de.crafty.eiv.common.overlay.ItemBookmarkOverlay;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.fabric.resolver.FabricEivResolver;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static de.crafty.eiv.common.CommonEIV.*;

public class ExtendedItemViewClient implements ClientModInitializer {


    private static ExtendedItemViewClient instance;

    @Override
    public void onInitializeClient() {
        instance = this;

        CommonEIVClient.EIV_KEY_MAPPINGS.forEach(KeyBindingHelper::registerKeyBinding);
        CommonEIVClient.setResolver(new FabricEivResolver());

        MenuScreens.register(CommonEIVClient.RECIPE_VIEW_MENU, RecipeViewScreen::new);

        EntityModelLayerRegistry.registerModelLayer(CommonEIVClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);


        //Save bookmarks
        File eivFolder = new File("config/eiv");
        if (eivFolder.mkdirs())
            LOGGER.info("EIV folder not present, creating...");

        File bookmarks = new File("config/eiv/bookmarks.json");
        if (bookmarks.exists()) {
            try {
                JsonObject contentJson = JsonParser.parseString(FileUtils.readFileToString(bookmarks, StandardCharsets.UTF_8)).getAsJsonObject();
                ItemBookmarkOverlay.INSTANCE.loadBookmarkedItems(contentJson);
            } catch (Exception e) {
                LOGGER.error("Failed to load bookmarks from file, skipping...", e);
            }
        }
    }

    public static ExtendedItemViewClient getInstance() {
        return instance;
    }

}
