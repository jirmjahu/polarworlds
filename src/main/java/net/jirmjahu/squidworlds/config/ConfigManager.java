package net.jirmjahu.squidworlds.config;

import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ConfigManager {

    private final JavaPlugin plugin;
    private final File configFile;

    @Getter
    private FileConfiguration configuration;

    public ConfigManager(JavaPlugin plugin, String configFileName) {
        this.plugin = plugin;
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        this.configFile = new File(plugin.getDataFolder(), configFileName);
        this.copyResource(configFileName, configFile);
        this.configuration = YamlConfiguration.loadConfiguration(configFile);
    }

    public void reloadConfig() {
        configuration = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        try {
            this.configuration.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    private void copyResource(String resourcePath, File file) {
        if (file.exists()) {
            return;
        }
        Files.copy(plugin.getResource(resourcePath), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}