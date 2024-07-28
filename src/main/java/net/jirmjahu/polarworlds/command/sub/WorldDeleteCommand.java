package net.jirmjahu.polarworlds.command.sub;

import lombok.RequiredArgsConstructor;
import net.jirmjahu.polarworlds.command.SubCommand;
import net.jirmjahu.polarworlds.command.WorldCommand;
import net.jirmjahu.polarworlds.message.MessageProvider;
import net.jirmjahu.polarworlds.world.WorldManager;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class WorldDeleteCommand implements SubCommand {

    private final WorldCommand parent;
    private final WorldManager worldManager;
    private final MessageProvider messageProvider;

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(messageProvider.noSpecifiedWorldMessage());
            return false;
        }

        final var world = worldManager.getWorld(args[1]);
        if (!worldManager.exists(world)) {
            player.sendMessage(messageProvider.noWorldMessage());
            return false;
        }

        //teleport all players that are still on the world to the default world
        parent.teleportToDefaultWorld(world);

        worldManager.deleteWorld(world.meta().getName());
        player.sendMessage(messageProvider.getMessage("command.world.delete.deleted").replaceText(it -> it.match("%world%").replacement(world.meta().getName())));
        return true;
    }
}
