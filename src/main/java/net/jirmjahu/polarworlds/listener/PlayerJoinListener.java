package net.jirmjahu.polarworlds.listener;

import net.jirmjahu.polarworlds.PolarWorlds;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        var spawnWorld = Bukkit.getWorld(PolarWorlds.getInstance().getDefaultConfig().getConfiguration().getString("default-world"));

        if (spawnWorld == null) {
            Bukkit.getConsoleSender().sendMessage("[PolarWorlds] The configured spawn world was not found, the spawn position of the player is not changed!");
            return;
        }

        if (event.getPlayer().hasPlayedBefore()) {
            return;
        }

        event.getPlayer().teleport(spawnWorld.getSpawnLocation());
    }
}
