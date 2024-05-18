package net.jirmjahu.squidworlds;

import lombok.Getter;
import net.jirmjahu.squidworlds.command.WorldCommand;
import net.jirmjahu.squidworlds.config.ConfigManager;
import net.jirmjahu.squidworlds.listener.PlayerJoinListener;
import net.jirmjahu.squidworlds.message.MessageProvider;
import net.jirmjahu.squidworlds.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class SquidWorlds extends JavaPlugin {

    @Getter
    private static SquidWorlds instance;

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

        Bukkit.getConsoleSender().sendMessage("[SquidWorlds] The Plugin has been enabled");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("[SquidWorlds] The Plugin has been disabled");
    }
}