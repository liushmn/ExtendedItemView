package de.crafty.eiv.servercompat.event;

import de.crafty.eiv.servercompat.recipe.CompatRecipeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        CompatRecipeManager.INSTANCE.updateStackSensitives(event.getPlayer());
        CompatRecipeManager.INSTANCE.updateRecipes(event.getPlayer());
    }
}
