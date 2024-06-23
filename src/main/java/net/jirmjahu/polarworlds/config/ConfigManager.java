package net.jirmjahu.polarworlds.config;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ConfigManager {

    private final File configFile;

    @Getter
    private FileConfiguration configuration;

    @SneakyThrows
    public ConfigManager(JavaPlugin plugin, String configFileName) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        this.configFile = new File(plugin.getDataFolder(), configFileName);
        if (!configFile.exists()) {
            Files.copy(plugin.getResource(configFileName), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        this.configuration = YamlConfiguration.loadConfiguration(configFile);
    }

    public void reloadConfiguration() {
        configuration = YamlConfiguration.loadConfiguration(configFile);
    }

    @SneakyThrows
    public void saveConfiguration() {
        this.configuration.save(configFile);
    }
}