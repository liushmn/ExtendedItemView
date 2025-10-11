package de.crafty.eiv.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.extra.FluidItemModel;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.fabric.mixin.KeyMappingAccessor;
import de.crafty.eiv.fabric.resolver.FabricEivResolver;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class FabricEIVClient implements ClientModInitializer {


    private static FabricEIVClient instance;

    @Override
    public void onInitializeClient() {
        instance = this;


        CommonEIVClient.boostrap();

        CommonEIVClient.EIV_KEY_MAPPINGS.forEach(KeyBindingHelper::registerKeyBinding);
        CommonEIVClient.setResolver(new FabricEivResolver());

        Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, "recipe_view"), CommonEIVClient.RECIPE_VIEW_MENU);
        MenuScreens.register(CommonEIVClient.RECIPE_VIEW_MENU, RecipeViewScreen::new);

        EntityModelLayerRegistry.registerModelLayer(CommonEIVClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);


        CommonEIVClient.loadConfigs();
    }

    public static void excludeEivMappings() {
        Map<InputConstants.Key, KeyMapping> map = KeyMappingAccessor.getKeyMap();
        map.clear();

        for (KeyMapping keyMapping : KeyMappingAccessor.getAllKeyMap().values()) {
            if (map.containsKey(keyMapping.key) && CommonEIVClient.EIV_KEY_MAPPINGS.contains(keyMapping)) {
                continue;
            }

            map.put(keyMapping.key, keyMapping);
        }
    }

    public static FabricEIVClient getInstance() {
        return instance;
    }

}
