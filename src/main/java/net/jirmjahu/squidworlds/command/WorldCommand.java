package net.jirmjahu.squidworlds.command;

import net.jirmjahu.squidworlds.SquidWorlds;
import net.jirmjahu.squidworlds.world.SquidWorld;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(SquidWorlds.getInstance().getMessageProvider().getOnlyPlayersMessage());
            return false;
        }

        if (!player.hasPermission("squidworlds.command.world")) {
            player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getNoWorldMessage());
            return false;
        }

        if (args.length == 0) {
            this.sendUsage(player);
            return false;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (args[1].isEmpty()) {
                player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getMessage("command.world.unspecifiedWorldName"));
                return false;
            }

            if (args[2].isEmpty()) {
                player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getMessage("command.world.unspecifiedType"));
                return false;
            }

            WorldType worldType = null;
            World.Environment environment = null;

            switch (args[2]) {
                case "normal":
                    worldType = WorldType.NORMAL;
                    environment = World.Environment.NORMAL;
                    break;
                case "flat":
                    worldType = WorldType.FLAT;
                    environment = World.Environment.NORMAL;
                    break;
                case "amplified":
                    worldType = WorldType.AMPLIFIED;
                    environment = World.Environment.NORMAL;
                    break;
                case "large_biomes":
                    worldType = WorldType.LARGE_BIOMES;
                    environment = World.Environment.NORMAL;
                    break;
                case "nether":
                    worldType = WorldType.NORMAL;
                    environment = World.Environment.NETHER;
                    break;
                case "end":
                    worldType = WorldType.NORMAL;
                    environment = World.Environment.THE_END;
                    break;
            }

            var world = new SquidWorld(args[1], environment, Difficulty.NORMAL, null, null, worldType, true, true, true, true, true);

            if (world.exits()) {
                player.sendMessage("command.world.alreadyExists");
                return false;
            }

            world.create();

            player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getMessage("command.world.create-success").replaceText(it -> it.match("%world%").replacement(world.getName())));
            return true;
        }



        this.sendUsage(player);
        return false;
    }

    private void sendUsage(Player player) {
        player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getMessage("command.world.usage.create"));
        player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getMessage("command.world.usage.delete"));
        player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getMessage("command.world.usage.teleport"));
        player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getMessage("command.world.usage.information"));
        player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getMessage("command.world.usage.list"));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> arg1 = new ArrayList<>();
            arg1.add("create");
            arg1.add("teleport");
            arg1.add("tp");
            arg1.add("information");
            return arg1;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            List<String> create = new ArrayList<>();
            Arrays.stream(World.Environment.values()).forEach(it -> create.add(it.name().toLowerCase()));
            Arrays.stream(WorldType.values()).forEach(it -> create.add(it.name().toLowerCase()));
            return create;
        }
        return List.of();
    }
}
