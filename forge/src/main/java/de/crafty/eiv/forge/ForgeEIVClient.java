package de.crafty.eiv.forge;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.extra.FluidItemModel;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.forge.resolver.ForgeEivResolver;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = CommonEIV.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ForgeEIVClient {


    @SubscribeEvent
    public static void onMenuRegistry(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.MENU_TYPES, helper -> {
            helper.register(ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, "recipe_view"), CommonEIVClient.RECIPE_VIEW_MENU);
        });
    }

    @SubscribeEvent
    public static void onModelLayerRegistry(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CommonEIVClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);
    }

    @SubscribeEvent
    public static void onClientInit(FMLClientSetupEvent event) {
        CommonEIVClient.boostrap();
        CommonEIVClient.setResolver(new ForgeEivResolver());

        MenuScreens.register(CommonEIVClient.RECIPE_VIEW_MENU, RecipeViewScreen::new);
        CommonEIVClient.loadConfigs();

    }

    @SubscribeEvent
    public static void onKeyMappingRegistry(RegisterKeyMappingsEvent event) {
        CommonEIVClient.EIV_KEY_MAPPINGS.forEach(event::register);
    }

}
