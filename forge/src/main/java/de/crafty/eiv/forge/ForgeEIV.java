package de.crafty.eiv.forge;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.api.IExtendedItemViewIntegration;
import de.crafty.eiv.common.command.EivCommand;
import de.crafty.eiv.common.extra.FluidItemModel;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.forge.resolver.ForgeEivResolver;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mod(CommonEIV.MODID)
public class ForgeEIV {


    public ForgeEIV(FMLJavaModLoadingContext context) {
        CommonEIV.LOGGER.info("Hello Minecraft!");

        //CommonEIV.setNetworkManager(new ForgeNetworkManager());

        context.getModEventBus().addListener(this::onClientInit);
        context.getModEventBus().addListener(this::onMenuRegistry);
        context.getModEventBus().addListener(this::onModelLayerRegistry);
        context.getModEventBus().addListener(this::onKeyMappingRegistry);
        MinecraftForge.EVENT_BUS.addListener(this::onCommandRegistry);


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
        event.register(ForgeRegistries.Keys.MENU_TYPES, helper -> {
            helper.register(ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, "recipe_view"), CommonEIVClient.RECIPE_VIEW_MENU);
        });
    }

    private void onModelLayerRegistry(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CommonEIVClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);
    }

    private void onClientInit(FMLClientSetupEvent event) {
        CommonEIVClient.setResolver(new ForgeEivResolver());

        MenuScreens.register(CommonEIVClient.RECIPE_VIEW_MENU, RecipeViewScreen::new);
        CommonEIVClient.loadBookmarks();

    }

    private void onKeyMappingRegistry(RegisterKeyMappingsEvent event) {
        CommonEIVClient.EIV_KEY_MAPPINGS.forEach(event::register);
    }

    private void onCommandRegistry(RegisterCommandsEvent event) {
        EivCommand.register(event.getDispatcher());
    }

}
