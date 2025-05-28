package de.crafty.eiv.neoforge;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.api.IExtendedItemViewIntegration;
import de.crafty.eiv.common.extra.FluidItemModel;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.neoforge.resolver.NeoForgeEivResolver;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Optional;

@Mod(CommonEIV.MODID)
public class NeoForgeEIV {

    public NeoForgeEIV(IEventBus eventBus) {
        CommonEIV.LOGGER.info("Hello Minecraft!");

        //CommonEIV.setNetworkManager(new ForgeNetworkManager());

        eventBus.addListener(this::onClientInit);
        eventBus.addListener(this::onMenuRegistry);
        eventBus.addListener(this::onMenuScreenRegistry);
        eventBus.addListener(this::onModelLayerRegistry);
        eventBus.addListener(this::onKeyMappingRegistry);


        CommonEIV.LOGGER.info("Scanning for integrations...");
        FMLLoader.getLoadingModList().getMods().forEach(modInfo -> {
            Optional<String> optional = modInfo.getConfigElement("eiv");
            if (optional.isPresent()) {
                CommonEIV.LOGGER.info("Loading integration: {}", optional.get());
                try {
                    Class<?> clazz = Class.forName(optional.get());
                    IExtendedItemViewIntegration integration = ((IExtendedItemViewIntegration) clazz.getConstructor().newInstance());
                    integration.onIntegrationInitialize();
                    CommonEIV.LOGGER.info("Integration initialized for mod: {}", modInfo.getModId());
                    return;

                } catch (Exception ignored) {
                }

                CommonEIV.LOGGER.error("Failed to load integration: {}", optional.get());
            }
        });
    }


    private void onMenuRegistry(RegisterEvent event) {
        event.register(Registries.MENU, menuTypeRegisterHelper -> {
            menuTypeRegisterHelper.register(ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, "recipe_view"), CommonEIVClient.RECIPE_VIEW_MENU);
        });
    }

    private void onMenuScreenRegistry(RegisterMenuScreensEvent event) {
        event.register(CommonEIVClient.RECIPE_VIEW_MENU, RecipeViewScreen::new);
    }

    private void onModelLayerRegistry(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CommonEIVClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);
    }

    private void onClientInit(FMLClientSetupEvent event) {
        CommonEIVClient.setResolver(new NeoForgeEivResolver());
        CommonEIVClient.loadBookmarks();
    }

    private void onKeyMappingRegistry(RegisterKeyMappingsEvent event) {
        CommonEIVClient.EIV_KEY_MAPPINGS.forEach(event::register);
    }
}
