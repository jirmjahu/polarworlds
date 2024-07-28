package net.jirmjahu.polarworlds;

import lombok.Getter;
import net.jirmjahu.polarworlds.command.WorldCommand;
import net.jirmjahu.polarworlds.config.ConfigManager;
import net.jirmjahu.polarworlds.listener.PlayerJoinListener;
import net.jirmjahu.polarworlds.message.MessageProvider;
import net.jirmjahu.polarworlds.world.WorldManager;
import net.jirmjahu.polarworlds.world.WorldManagerImpl;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PolarWorlds extends JavaPlugin {

    private ConfigManager defaultConfig;
    private ConfigManager worldsConfig;

    private MessageProvider messageProvider;
    private WorldManager worldManager;

    @Override
    public void onEnable() {
        //load configurations
        this.defaultConfig = new ConfigManager(this, "config.yml");
        this.worldsConfig = new ConfigManager(this, "worlds.yml");

        //load language files and messages
        final var configDE = new ConfigManager(this, "de.yml");
        final var configEN = new ConfigManager(this, "en.yml");
        this.messageProvider = new MessageProvider(defaultConfig, configDE, configEN);

        this.worldManager = new WorldManagerImpl(this, this.worldsConfig, this.worldsConfig.getConfiguration());
        this.worldManager.loadWorlds();

        getCommand("world").setExecutor(new WorldCommand(this, this.worldManager, this.messageProvider));

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this, this.defaultConfig), this);
    }
}