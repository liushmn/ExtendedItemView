package de.crafty.eiv.forge;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.IExtendedItemViewIntegration;
import de.crafty.eiv.common.command.EivCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.Optional;

@Mod(CommonEIV.MODID)
public class ForgeEIV {


    public ForgeEIV(FMLJavaModLoadingContext context) {
        CommonEIV.LOGGER.info("Hello Minecraft!");

        //CommonEIV.setNetworkManager(new ForgeNetworkManager());

        RegisterCommandsEvent.BUS.addListener(this::onCommandRegistry);


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


    private void onCommandRegistry(RegisterCommandsEvent event) {
        EivCommand.register(event.getDispatcher());
    }

}
