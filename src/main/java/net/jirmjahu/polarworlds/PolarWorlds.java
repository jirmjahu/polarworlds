package net.jirmjahu.polarworlds;

import lombok.Getter;
import net.jirmjahu.polarworlds.command.WorldCommand;
import net.jirmjahu.polarworlds.config.ConfigManager;
import net.jirmjahu.polarworlds.listener.PlayerJoinListener;
import net.jirmjahu.polarworlds.message.MessageProvider;
import net.jirmjahu.polarworlds.world.WorldManager;
import net.jirmjahu.polarworlds.world.WorldManagerImpl;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PolarWorlds extends JavaPlugin {

    private ConfigManager defaultConfig;
    private ConfigManager worldsConfig;
    private MessageProvider messageProvider;
    private WorldManager worldManager;

    @Override
    public void onEnable() {
        ConfigManager configDE = new ConfigManager(this, "de.yml");
        ConfigManager configEN = new ConfigManager(this, "en.yml");

        this.defaultConfig = new ConfigManager(this, "config.yml");
        this.worldsConfig = new ConfigManager(this, "worlds.yml");
        this.messageProvider = new MessageProvider(defaultConfig, configDE, configEN);
        this.worldManager = new WorldManagerImpl(this, worldsConfig.getConfiguration());

        worldManager.loadWorlds();

        getCommand("world").setExecutor(new WorldCommand(this, worldManager, messageProvider));
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }

    @Override
    public void onDisable() {
        if (worldManager != null) {
            worldManager.saveWorlds();
        }
    }
}
