package de.crafty.eiv.fabric;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.IExtendedItemViewIntegration;
import de.crafty.eiv.common.api.recipe.ItemViewRecipes;
import de.crafty.eiv.common.command.EivCommand;
import de.crafty.eiv.common.recipe.item.FluidItem;
import de.crafty.eiv.fabric.network.FabricNetworkManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import static de.crafty.eiv.common.CommonEIV.*;

import java.util.HashMap;

public class ExtendedItemView implements ModInitializer {


    @Override
    public void onInitialize() {
        LOGGER.info("Hello Minecraft!");

        CommonEIV.setNetworkManager(new FabricNetworkManager());

        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) -> EivCommand.register(commandDispatcher));

        FabricLoader.getInstance().invokeEntrypoints("eiv", IExtendedItemViewIntegration.class, IExtendedItemViewIntegration::onIntegrationInitialize);

    }

}
