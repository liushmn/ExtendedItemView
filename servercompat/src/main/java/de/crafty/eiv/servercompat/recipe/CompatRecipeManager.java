package de.crafty.eiv.servercompat.recipe;

import de.crafty.eiv.servercompat.api.IEivCompatIntegration;
import de.crafty.eiv.servercompat.log.Data;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class CompatRecipeManager {

    public static final CompatRecipeManager INSTANCE = new CompatRecipeManager();

    private final HashMap<JavaPlugin, IEivCompatIntegration> PRESENT_INTEGRATIONS = new HashMap<>();


    public void registerIntegration(JavaPlugin plugin, IEivCompatIntegration integration) {
        PRESENT_INTEGRATIONS.put(plugin, integration);
    }


    public void load() {

        StringBuilder logBuilder = new StringBuilder(String.format("Loading %s EIV-Compat integration(s)...", PRESENT_INTEGRATIONS.size()));
        PRESENT_INTEGRATIONS.forEach((plugin, integration) -> {
           logBuilder.append(String.format("\n - %s", plugin.getName()));
        });

        Data.log(logBuilder.toString());

        PRESENT_INTEGRATIONS.values().forEach(IEivCompatIntegration::onInitialize);

    }


}
