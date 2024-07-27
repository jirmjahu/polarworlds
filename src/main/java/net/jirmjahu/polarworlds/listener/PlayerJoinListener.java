package net.jirmjahu.polarworlds.listener;

 import lombok.RequiredArgsConstructor;
import net.jirmjahu.polarworlds.PolarWorlds;
import net.jirmjahu.polarworlds.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {

    private final PolarWorlds plugin;
    private final ConfigManager defaultConfig;

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        var spawnWorld = Bukkit.getWorld(defaultConfig.getConfiguration().getString("default-world"));

        if (spawnWorld == null) {
            plugin.getLogger().warning("[PolarWorlds] The configured spawn world was not found, the spawn position of the player is not changed!");
            return;
        }

        if (event.getPlayer().hasPlayedBefore()) {
            return;
        }

        event.getPlayer().teleport(spawnWorld.getSpawnLocation());
    }
}
