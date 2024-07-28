package net.jirmjahu.polarworlds.command.sub;

import lombok.RequiredArgsConstructor;
import net.jirmjahu.polarworlds.command.SubCommand;
import net.jirmjahu.polarworlds.message.MessageProvider;
import net.jirmjahu.polarworlds.world.WorldManager;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class WorldListCommand implements SubCommand {

    private final WorldManager worldManager;
    private final MessageProvider messageProvider;

    @Override
    public boolean execute(Player player, String[] args) {
        player.sendMessage(messageProvider.getMessage("command.world.list.header"));

        var worlds = worldManager.getWorlds();
        if (worlds.isEmpty()) {
            player.sendMessage(messageProvider.getMessage("command.world.list.noWorlds"));
            return false;
        }

        worlds.stream()
                .filter(worldName -> worldManager.getWorld(worldName).meta().isLoaded())
                .forEach(worldName -> player.sendMessage(messageProvider.getMessage("command.world.list.loop").replaceText(text -> text.match("%world%").replacement(worldName))));
        return true;
    }
}
