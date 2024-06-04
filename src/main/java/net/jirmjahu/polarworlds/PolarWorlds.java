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
        defaultConfig = new ConfigManager(this, "config.yml");
        worldsConfig = new ConfigManager(this, "worlds.yml");

        var languageConfigDE = new ConfigManager(this, "de.yml");
        var languageConfigEN = new ConfigManager(this, "en.yml");
        messageProvider = new MessageProvider(defaultConfig, languageConfigDE, languageConfigEN);

        worldManager = new WorldManager();
        worldManager.loadWorlds();

        getCommand("world").setExecutor(new WorldCommand());

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);

        Bukkit.getConsoleSender().sendMessage("[PolarWorlds] The Plugin has been enabled");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("[PolarWorlds] The Plugin has been disabled");
    }
}