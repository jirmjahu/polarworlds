package net.jirmjahu.squidworlds;

import lombok.Getter;
import net.jirmjahu.squidworlds.command.TestCommand;
import net.jirmjahu.squidworlds.config.ConfigManager;
import net.jirmjahu.squidworlds.message.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class SquidWorlds extends JavaPlugin {

    @Getter
    private static SquidWorlds instance;

    private ConfigManager defaultConfig;
    private ConfigManager worldsConfig;

    private MessageProvider messageProvider;

    @Override
    public void onEnable() {
        instance = this;

        //load configurations and messages
        defaultConfig = new ConfigManager(this, "config.yml");
        worldsConfig = new ConfigManager(this, "worlds.yml");
        messageProvider = new MessageProvider(defaultConfig, new ConfigManager(this, "de.yml"), new ConfigManager(this, "en.yml"));

        getCommand("test").setExecutor(new TestCommand());

        Bukkit.getConsoleSender().sendMessage("[SquidWorlds] The Plugin has been enabled");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("[SquidWorlds] The Plugin has been disabled");
    }
}