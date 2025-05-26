package de.crafty.eiv.fabric;

import de.crafty.eiv.common.api.IExtendedItemViewIntegration;
import de.crafty.eiv.common.command.EivCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

import static de.crafty.eiv.common.CommonEIV.*;

public class FabricEIV implements ModInitializer {


    @Override
    public void onInitialize() {
        LOGGER.info("Hello Minecraft!");

        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) -> EivCommand.register(commandDispatcher));

        FabricLoader.getInstance().invokeEntrypoints("eiv", IExtendedItemViewIntegration.class, IExtendedItemViewIntegration::onIntegrationInitialize);

    }

}
