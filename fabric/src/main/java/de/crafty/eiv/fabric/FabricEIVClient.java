package de.crafty.eiv.fabric;

import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.extra.FluidItemModel;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.fabric.resolver.FabricEivResolver;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

public class FabricEIVClient implements ClientModInitializer {


    private static FabricEIVClient instance;

    @Override
    public void onInitializeClient() {
        instance = this;

        CommonEIVClient.EIV_KEY_MAPPINGS.forEach(KeyBindingHelper::registerKeyBinding);
        CommonEIVClient.setResolver(new FabricEivResolver());

        MenuScreens.register(CommonEIVClient.RECIPE_VIEW_MENU, RecipeViewScreen::new);

        EntityModelLayerRegistry.registerModelLayer(CommonEIVClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);


        CommonEIVClient.loadBookmarks();
    }

    public static FabricEIVClient getInstance() {
        return instance;
    }

}
