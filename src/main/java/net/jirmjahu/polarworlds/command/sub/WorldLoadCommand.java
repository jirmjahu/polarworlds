package net.jirmjahu.polarworlds.command.sub;

import lombok.RequiredArgsConstructor;
import net.jirmjahu.polarworlds.command.SubCommand;
import net.jirmjahu.polarworlds.message.MessageProvider;
import net.jirmjahu.polarworlds.world.WorldManager;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class WorldLoadCommand implements SubCommand {

    private final WorldManager worldManager;
    private final MessageProvider messageProvider;

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(messageProvider.noSpecifiedWorldMessage());
            return false;
        }

        final var world = worldManager.getWorld(args[1]);
        if (!this.worldManager.exists(world)) {
            player.sendMessage(messageProvider.noWorldMessage());
            return false;
        }

        this.worldManager.loadWorld(world.meta().getName());
        player.sendMessage(messageProvider.getMessage("command.world.load.success").replaceText(text -> text.match("%world%").replacement(world.meta().getName())));
        return true;
    }
}
