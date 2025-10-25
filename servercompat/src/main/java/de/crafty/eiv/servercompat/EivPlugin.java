package de.crafty.eiv.servercompat;

import de.crafty.eiv.servercompat.builtin.BuiltinEivCompatIntegration;
import de.crafty.eiv.servercompat.event.PlayerJoinListener;
import de.crafty.eiv.servercompat.event.ServerReloadListener;
import de.crafty.eiv.servercompat.log.Data;
import de.crafty.eiv.servercompat.recipe.CompatRecipeManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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
        CompatRecipeManager.INSTANCE.loadIntegrations();

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new ServerReloadListener(), this);

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

}
