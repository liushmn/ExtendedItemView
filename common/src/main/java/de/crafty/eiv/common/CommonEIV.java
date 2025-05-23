package de.crafty.eiv.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.crafty.eiv.common.api.recipe.ItemViewRecipes;
import de.crafty.eiv.common.network.IEivNetworkManager;
import de.crafty.eiv.common.overlay.ItemBookmarkOverlay;
import de.crafty.eiv.common.recipe.item.FluidItem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class CommonEIV {

    public static final String MODID = "eiv";

    public static final Logger LOGGER = LoggerFactory.getLogger("Extended ItemView");


    private static IEivNetworkManager NETWORK_MANAGER = null;

    public static void setNetworkManager(final IEivNetworkManager networkManager) {
        CommonEIV.NETWORK_MANAGER = networkManager;
        LOGGER.info("Network Manager has been set");

        NETWORK_MANAGER.registerPayloads();
        NETWORK_MANAGER.registerServerHandlers();

        try {
            Class.forName("net.minecraft.client.main.Main");
            NETWORK_MANAGER.registerClientHandlers();

        }catch (ClassNotFoundException ignored){
        }

    }

    public static IEivNetworkManager networkManager() {
        if (NETWORK_MANAGER != null)
            return NETWORK_MANAGER;

        throw new IllegalStateException("Network manager not set");
    }



    public static void buildFluidItems() {
        //Add FluidItems
        HashMap<Fluid, Item> fluidItemMap = new HashMap<>();

        BuiltInRegistries.FLUID.forEach(fluid -> {

            if (fluid == Fluids.EMPTY)
                return;
            if (!fluid.isSource(fluid.defaultFluidState()))
                return;

            ResourceLocation itemLocation = BuiltInRegistries.FLUID.getKey(fluid);
            Item item = Registry.register(
                    BuiltInRegistries.ITEM,
                    itemLocation,
                    new FluidItem(fluid.defaultFluidState().createLegacyBlock().getBlock(),
                            new FluidItem.FluidItemProperties()
                                    .fluid(fluid)
                                    .setItemId(ResourceKey.create(Registries.ITEM, itemLocation))
                    ));
            fluidItemMap.put(fluid, item);
        });

        ItemViewRecipes.INSTANCE.setFluidItemMap(fluidItemMap);
    }


    public static void onExit() {

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
