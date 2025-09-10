package de.crafty.eiv.servercompat;

import de.crafty.eiv.servercompat.builtin.BuiltinEivCompatIntegration;
import de.crafty.eiv.servercompat.log.Data;
import de.crafty.eiv.servercompat.recipe.CompatRecipeManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerEIV extends JavaPlugin {


    @Override
    public void onEnable() {
        Data.log("Loading Extended ItemView...");

        CompatRecipeManager.INSTANCE.registerIntegration(this, new BuiltinEivCompatIntegration());
        CompatRecipeManager.INSTANCE.load();

        Data.log("Extended ItemView has been enabled.");
    }


    @Override
    public void onDisable() {


        Data.log("Extended ItemView has been disabled.");
    }
}
