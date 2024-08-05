package net.jirmjahu.polarworlds.command.sub;

import lombok.RequiredArgsConstructor;
import net.jirmjahu.polarworlds.command.SubCommand;
import net.jirmjahu.polarworlds.message.MessageProvider;
import net.jirmjahu.polarworlds.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;

@RequiredArgsConstructor
public class WorldEditCommand implements SubCommand {

    private final WorldManager worldManager;
    private final MessageProvider messageProvider;

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(messageProvider.getMessage("command.world.usage.edit"));
            return false;
        }

        final var worldName = args[1];
        final var setting = args[2];

        final var world = Bukkit.getWorld(args[1]);
        if (world == null) {
            player.sendMessage(messageProvider.noWorldMessage());
            return false;
        }

        final var polarWorld = worldManager.getWorld(args[1]);

        switch (setting.toLowerCase()) {
            case "pvp":
                if (args.length < 4) {
                    player.sendMessage(messageProvider.getMessage("command.world.edit.specifyValue").replaceText(text -> text.match("%setting%").replacement("PvP")));
                    return false;
                }

                boolean pvp = Boolean.parseBoolean(args[3]);
                polarWorld.getWorld().setPVP(pvp);

                polarWorld.meta().setAllowPvP(pvp);
                player.sendMessage(messageProvider.getMessage("command.world.edit.pvpSuccess").replaceText(it -> it.match("%world%").replacement(worldName)).replaceText(it -> it.match("%value%").replacement(String.valueOf(pvp))));
                break;

            case "spawnanimals":
                if (args.length < 4) {
                    player.sendMessage(messageProvider.getMessage("command.world.edit.specifyValue").replaceText(text -> text.match("%setting%").replacement("Spawn Animals")));
                    return false;
                }
                boolean spawnAnimals = Boolean.parseBoolean(args[3]);
                polarWorld.meta().setSpawnAnimals(spawnAnimals);

                for (Entity entity : polarWorld.getWorld().getEntities()) {
                    if (entity instanceof Animals && !(entity instanceof ArmorStand)) {
                        entity.remove();
                    }
                }

                player.sendMessage(messageProvider.getMessage("command.world.edit.spawnAnimalsSuccess").replaceText(text -> text.match("%world%").replacement(worldName)).replaceText(it -> it.match("%value%").replacement(String.valueOf(spawnAnimals))));
                break;

            case "spawnmobs":
                if (args.length < 4) {
                    player.sendMessage(messageProvider.getMessage("command.world.edit.specifyValue").replaceText(text -> text.match("%setting%").replacement("Spawn Mobs")));
                    return false;
                }
                boolean spawnMobs = Boolean.parseBoolean(args[3]);
                polarWorld.meta().setSpawnMobs(spawnMobs);

                for (Entity entity : polarWorld.getWorld().getEntities()) {
                    if (entity instanceof Mob && !(entity instanceof ArmorStand)) {
                        entity.remove();
                    }
                }

                player.sendMessage(messageProvider.getMessage("command.world.edit.spawnMobsSuccess").replaceText(text -> text.match("%world%").replacement(worldName)).replaceText(it -> it.match("%value%").replacement(String.valueOf(spawnMobs))));
                break;
            default:
                player.sendMessage(messageProvider.getMessage("command.world.edit.invalidSetting").replaceText(text -> text.match("%setting%").replacement(setting)));
                return false;
        }

        worldManager.save(polarWorld);
        return true;
    }
}
