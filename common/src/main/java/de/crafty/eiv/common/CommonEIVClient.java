package de.crafty.eiv.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.InputConstants;
import de.crafty.eiv.common.overlay.ItemBookmarkOverlay;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.resolver.IEivClientResolver;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static de.crafty.eiv.common.CommonEIV.LOGGER;
import static de.crafty.eiv.common.CommonEIV.MODID;

public class CommonEIVClient {

    public static final ModelLayerLocation FLUID_ITEM_MODEL_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MODID, "fluiditem"), "inventory");

    public static final MenuType<RecipeViewMenu> RECIPE_VIEW_MENU = new MenuType<>(RecipeViewMenu::new, FeatureFlagSet.of());


    public static final KeyMapping USAGE_KEYBIND = new KeyMapping("key.eiv.usage", 85, "key.categories.eiv");

    public static final KeyMapping RECIPE_KEYBIND = new KeyMapping("key.eiv.recipe", 82, "key.categories.eiv");

    public static final KeyMapping TOGGLE_OVERLAY_KEYBIND = new KeyMapping("key.eiv.toggle_overlay", 79, "key.categories.eiv");

    public static final KeyMapping ADD_BOOKMARK_KEYBIND = new KeyMapping("key.eiv.bookmark", 65, "key.categories.eiv");

    public static final List<KeyMapping> EIV_KEY_MAPPINGS = List.of(USAGE_KEYBIND, RECIPE_KEYBIND, TOGGLE_OVERLAY_KEYBIND, ADD_BOOKMARK_KEYBIND);

    private static IEivClientResolver HELPER = null;


    public static void setResolver(final IEivClientResolver helper) {
        HELPER = helper;
        LOGGER.info("Helper has been set");
    }

    public static IEivClientResolver resolver() {
        if (HELPER != null)
            return HELPER;

        throw new IllegalStateException("Helper not set");
    }


    public static void excludeEivMappings() {
        Map<InputConstants.Key, KeyMapping> map = CommonEIVClient.resolver().getKeyMap();
        map.clear();

        for (KeyMapping keyMapping : KeyMapping.ALL.values()) {
            if (map.containsKey(keyMapping.key) && EIV_KEY_MAPPINGS.contains(keyMapping)) {
                continue;
            }

            map.put(keyMapping.key, keyMapping);
        }
    }

    public static void loadBookmarks() {
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

    public static void saveBookmarks() {

        JsonObject encoded = new JsonObject();
        ItemBookmarkOverlay.INSTANCE.saveBookmarkedItems(encoded);

        File bookmarkFile = new File("config/eiv/bookmarks.json");

        try {
            if (!bookmarkFile.exists())
                bookmarkFile.createNewFile();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileUtils.writeStringToFile(bookmarkFile, gson.toJson(encoded));
        } catch (Exception e) {
            LOGGER.error("Failed to save bookmarks to file", e);
        }


    }
}
