package net.jirmjahu.polarworlds.message;

import lombok.AllArgsConstructor;
import net.jirmjahu.polarworlds.config.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@AllArgsConstructor
public class MessageProvider {

    private final ConfigManager defaultConfig;
    private final ConfigManager messagesDE;
    private final ConfigManager messagesEN;

    public Component getMessage(String message, boolean prefix) {
        var defaultConfig = this.defaultConfig.getConfiguration();
        var language = defaultConfig.getString("language");

        final var config = "de".equals(language) ? messagesDE : messagesEN;

        if (config == null || !config.getConfiguration().contains(message)) {
            return MiniMessage.miniMessage().deserialize("<red>Not found");
        }

        var configMessage = config.getConfiguration().getString(message);
        if (prefix) {
            configMessage = config.getConfiguration().getString("prefix", "") + configMessage;
        }

        return MiniMessage.miniMessage().deserialize(configMessage);
    }


    public Component getMessage(String message) {
        return getMessage(message, true);
    }

    public Component prefix() {
        return getMessage("prefix");
    }

    public Component noPermissionMessage() {
        return getMessage("no-permission");
    }

    public Component noWorldMessage() {
        return getMessage("no-world");
    }

    public Component noSpecifiedWorldMessage() {
        return getMessage("no-world-specified");
    }

    public Component onlyPlayersMessage() {
        return getMessage("only-players");
    }
}
