package net.jirmjahu.squidworlds.command;

import net.jirmjahu.squidworlds.SquidWorlds;
import net.jirmjahu.squidworlds.world.SquidWorld;
import org.bukkit.Bukkit;
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
            if (args.length == 1) {
                player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getSpecifiedWorldMessage());
                return false;
            }

            if (args.length == 2) {
                player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getMessage("command.world.create.unspecifiedType"));
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
                default:
                    player.sendMessage("gibts nicht");
                    return false;
            }

            var world = new SquidWorld(args[1], environment, Difficulty.NORMAL, null, null, worldType, true, true, true, true, true);

            if (world.exits()) {
                player.sendMessage("command.world.create.alreadyExists");
                return false;
            }

            world.create();

            player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getMessage("command.world.create.create-success").replaceText(it -> it.match("%world%").replacement(world.getName())));
            return true;
        }

        if (args[0].equalsIgnoreCase("delete")) {
            if (args.length < 2) {
                player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getSpecifiedWorldMessage());
                return false;
            }

            var world = SquidWorlds.getInstance().getWorldManager().getWorld(args[1]);
            if (!world.exits()) {
                player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getNoWorldMessage());
                return false;
            }

            world.delete();
            player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getMessage("command.world.delete.deleted").replaceText(it -> it.match("%world%").replacement(world.getName())));
            return true;
        }

        if (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp")) {
            if (args.length < 2) {
                player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getSpecifiedWorldMessage());
                return false;
            }

            var world = Bukkit.getWorld(args[1]);
            if (world == null) {
                player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getNoWorldMessage());
                return false;
            }

            player.teleport(world.getSpawnLocation());
            player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getMessage("command.world.teleport.teleported").replaceText(it -> it.match("%world%").replacement(world.getName())));
            return true;
        }

        if (args[0].equalsIgnoreCase("information")) {
            if (args.length < 2) {
                player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getSpecifiedWorldMessage());
                return false;
            }

            player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getMessage("command.world.information.header"));
            SquidWorlds.getInstance().getWorldManager().getAllWorlds().forEach(it -> {
                player.sendMessage(SquidWorlds.getInstance().getMessageProvider().getMessage("command.world.information.loop").replaceText(text -> text.match("%world%").replacement(it)));
            });
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {

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
            arg1.add("delete");
            arg1.add("teleport");
            arg1.add("tp");
            arg1.add("information");
            arg1.add("list");
            return arg1;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            List<String> create = new ArrayList<>();
            Arrays.stream(World.Environment.values()).forEach(it -> create.add(it.name().toLowerCase()));
            Arrays.stream(WorldType.values()).forEach(it -> create.add(it.name().toLowerCase()));
            return create;
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("information"))) {
            List<String> args2 = new ArrayList<>();
            Bukkit.getWorlds().forEach(world -> args2.add(world.getName()));
            return args2;
        }

        return List.of();
    }
}
