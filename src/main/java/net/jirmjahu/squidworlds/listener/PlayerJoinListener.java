package net.jirmjahu.squidworlds.listener;

import net.jirmjahu.squidworlds.SquidWorlds;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        var spawnWorld = Bukkit.getWorld(SquidWorlds.getInstance().getDefaultConfig().getConfiguration().getString("default-world"));

        if (spawnWorld == null) {
            Bukkit.getConsoleSender().sendMessage("[SquidWorlds] The configured spawn world was not found, the spawn position of the player is not changed!");
            return;
        }

        if (event.getPlayer().hasPlayedBefore()) {
            return;
        }

        event.getPlayer().teleport(spawnWorld.getSpawnLocation());
    }
}
