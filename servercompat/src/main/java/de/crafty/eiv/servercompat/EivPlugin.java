package de.crafty.eiv.servercompat;

import de.crafty.eiv.servercompat.api.IEivCompatIntegration;
import de.crafty.eiv.servercompat.builtin.BuiltinEivCompatIntegration;
import de.crafty.eiv.servercompat.event.PlayerJoinListener;
import de.crafty.eiv.servercompat.log.Data;
import de.crafty.eiv.servercompat.recipe.CompatRecipeManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EivPlugin extends JavaPlugin {

    public static final String PLUGIN_ID = "eiv";
    public static final String MESSAGE_CHANNEL = "eiv:compat";

    private static EivPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        Data.log("Loading Extended ItemView...");

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, MESSAGE_CHANNEL);

        CompatRecipeManager.INSTANCE.registerIntegration(this, new BuiltinEivCompatIntegration());


        //Discovering Integrations
        this.discoverIntegrations();
        CompatRecipeManager.INSTANCE.loadIntegrations();

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);

        Data.log("Extended ItemView has been enabled.");
    }


    @Override
    public void onDisable() {

        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, MESSAGE_CHANNEL);

        Data.log("Extended ItemView has been disabled.");
    }

    public static EivPlugin getInstance() {
        return instance;
    }


    private void discoverIntegrations() {
        Data.log("Integration discovery started...");
        HashMap<Plugin, Class<? extends IEivCompatIntegration>> integrationClasses = new HashMap<>();

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (!plugin.isEnabled()) continue;

            if (plugin.getResource("plugin.yml") != null) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("plugin.yml")));

                if (config.contains("eiv-compat")) {
                    try {
                        integrationClasses.put(plugin, (Class<? extends IEivCompatIntegration>) Class.forName(config.getString("eiv-compat")));

                    } catch (ClassNotFoundException | ClassCastException e) {
                        Data.error("Invalid eiv-compat class in plugin.yml, skipping plugin...");
                    }
                }

            }
        }
        integrationClasses.forEach((plugin, integrationClass) -> {

            try {
                CompatRecipeManager.INSTANCE.registerIntegration(plugin, integrationClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                Data.error("Invalid eiv-compat class in plugin.yml, skipping plugin...");
            }

        });

        Data.log(String.format("Found %s EIV-Compat integration(s).", CompatRecipeManager.INSTANCE.getIntegrationCount()));

    }
}
