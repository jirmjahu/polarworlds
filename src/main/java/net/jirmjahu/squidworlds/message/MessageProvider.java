package net.jirmjahu.squidworlds.message;

import lombok.AllArgsConstructor;
import net.jirmjahu.squidworlds.config.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@AllArgsConstructor
public class MessageProvider {

    private final ConfigManager defaultConfig;
    private final ConfigManager languageConfigEN;
    private final ConfigManager languageConfigDE;

    public Component getMessage(String message) {
        var language = this.defaultConfig.getConfiguration().getString("language");

        ConfigManager config;
        if (language.equalsIgnoreCase("de")) {
            config = languageConfigEN;
        } else {
            config = languageConfigDE;
        }
        return MiniMessage.miniMessage().deserialize(config.getConfiguration().getString("prefix" + config.getConfiguration().getString(message)));
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

    public Component getOnlyPlayersMessage() {
        return getMessage("only-players");
    }
}
