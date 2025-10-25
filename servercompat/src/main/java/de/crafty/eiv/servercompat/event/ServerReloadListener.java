package de.crafty.eiv.servercompat.event;

import de.crafty.eiv.servercompat.recipe.CompatRecipeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerReloadListener implements Listener {


    @EventHandler
    public void onResourceReload(ServerLoadEvent event) {

        if(event.getType() == ServerLoadEvent.LoadType.RELOAD)
            CompatRecipeManager.INSTANCE.reload();
        else
            CompatRecipeManager.INSTANCE.loadIntegrations();

    }
}
