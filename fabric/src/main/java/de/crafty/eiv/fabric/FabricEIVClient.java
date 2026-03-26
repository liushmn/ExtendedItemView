package de.crafty.eiv.fabric;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.extra.FluidItemModel;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.fabric.resolver.FabricEivResolver;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.impl.client.keymapping.KeyMappingRegistryImpl;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class FabricEIVClient implements ClientModInitializer {


    private static FabricEIVClient instance;

    @Override
    public void onInitializeClient() {
        instance = this;

        CommonEIVClient.boostrap();

        CommonEIVClient.EIV_KEY_MAPPINGS.forEach(KeyMappingRegistryImpl::registerKeyMapping);
        CommonEIVClient.setResolver(new FabricEivResolver());

        Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(CommonEIV.MODID, "recipe_view"), CommonEIVClient.RECIPE_VIEW_MENU);
        MenuScreens.register(CommonEIVClient.RECIPE_VIEW_MENU, RecipeViewScreen::new);

        ModelLayerRegistry.registerModelLayer(CommonEIVClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);


        CommonEIVClient.loadConfigs();
    }


    public static FabricEIVClient getInstance() {
        return instance;
    }

}
