package net.jirmjahu.squidworlds.message;

import net.jirmjahu.squidworlds.config.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MessageProvider {

    private final ConfigManager defaultConfig;
    private final ConfigManager languageConfigEN;
    private final ConfigManager languageConfigDE;

    public MessageProvider(ConfigManager defaultConfig, ConfigManager languageConfigEN, ConfigManager languageConfigDE) {
        this.defaultConfig = defaultConfig;
        this.languageConfigEN = languageConfigEN;
        this.languageConfigDE = languageConfigDE;
    }

    public Component getMessage(String message) {
        var defaultConfig = this.defaultConfig.getConfiguration();
        var language = defaultConfig.getString("language");

        ConfigManager configManager;
        if ("de".equalsIgnoreCase(language)) {
            configManager = languageConfigEN;
        } else {
            configManager = languageConfigDE;
        }

        if (!configManager.getConfiguration().contains(message)) {
            return null;
        }

        return MiniMessage.miniMessage().deserialize(configManager.getConfiguration().getString("prefix") + configManager.getConfiguration().getString(message));
    }

    public Component getPrefix() {
        return getMessage("prefix");
    }

    public Component getPermissionMessage() {
        return getMessage("no-permission");
    }

    public Component getNoPlayerMessage() {
        return getMessage("no-player");
    }

    public Component getNoWorldMessage() {
        return getMessage("no-world");
    }

    public Component getSpecifiedWorldMessage() {
        return getMessage("no-world-specified");
    }

    public Component getOnlyPlayersMessage() {
        return getMessage("only-players");
    }
}
