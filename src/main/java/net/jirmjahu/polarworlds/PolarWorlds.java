package net.jirmjahu.polarworlds;

import lombok.Getter;
import net.jirmjahu.polarworlds.command.WorldCommand;
import net.jirmjahu.polarworlds.config.ConfigManager;
import net.jirmjahu.polarworlds.listener.PlayerJoinListener;
import net.jirmjahu.polarworlds.message.MessageProvider;
import net.jirmjahu.polarworlds.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PolarWorlds extends JavaPlugin {

    @Getter
    private static PolarWorlds instance;

    private ConfigManager defaultConfig;
    private ConfigManager worldsConfig;

    private MessageProvider messageProvider;
    private WorldManager worldManager;

    @Override
    public void onEnable() {
        instance = this;

        //load configurations and messages
        this.defaultConfig = new ConfigManager(this, "config.yml");
        this.worldsConfig = new ConfigManager(this, "worlds.yml");

        //load language files and messages
        final var languageConfigDE = new ConfigManager(this, "de.yml");
        final var languageConfigEN = new ConfigManager(this, "en.yml");
        this.messageProvider = new MessageProvider(defaultConfig, languageConfigDE, languageConfigEN);

        this.worldManager = new WorldManager(this, this.worldsConfig);
        this.worldManager.loadWorlds();

        getCommand("world").setExecutor(new WorldCommand(this, this.messageProvider));

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this, this.defaultConfig), this);
    }
}