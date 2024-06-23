package net.jirmjahu.polarworlds.command;

import net.jirmjahu.polarworlds.PolarWorlds;
import net.jirmjahu.polarworlds.message.MessageProvider;
import net.jirmjahu.polarworlds.world.PolarWorld;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldCommand implements CommandExecutor, TabCompleter {

    private final MessageProvider messageProvider = PolarWorlds.getInstance().getMessageProvider();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(messageProvider.onlyPlayersMessage());
            return false;
        }

        if (!player.hasPermission("polarworlds.command.world")) {
            player.sendMessage(messageProvider.noPermissionMessage());
            return false;
        }

        if (args.length == 0) {
            this.sendUsage(player);
            return false;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (args.length == 1) {
                player.sendMessage(messageProvider.noSpecifiedWorldMessage());
                return false;
            }

            if (args.length == 2) {
                player.sendMessage(messageProvider.getMessage("command.world.create.unspecifiedType"));
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
                    return false;
            }

            //create world with default and given parameters
            var world = PolarWorld.builder().name(args[1]).environment(environment).difficulty(Difficulty.NORMAL).generator(null).seed(0L).worldType(worldType).allowPvP(true).spawnAnimals(true).spawnMobs(true).generateStructures(true).loaded(true).build();

            if (world.exits()) {
                player.sendMessage("command.world.create.alreadyExists");
                return false;
            }

            world.create();
            player.sendMessage(messageProvider.getMessage("command.world.create.create-success").replaceText(it -> it.match("%world%").replacement(world.getName())));
            return true;
        }

        if (args[0].equalsIgnoreCase("delete")) {
            if (args.length < 2) {
                player.sendMessage(messageProvider.noSpecifiedWorldMessage());
                return false;
            }

            var world = PolarWorlds.getInstance().getWorldManager().getWorld(args[1]);
            if (!world.exits()) {
                player.sendMessage(messageProvider.noWorldMessage());
                return false;
            }

            world.delete();
            player.sendMessage(messageProvider.getMessage("command.world.delete.deleted").replaceText(it -> it.match("%world%").replacement(world.getName())));
            return true;
        }

        if (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp")) {
            if (args.length < 2) {
                player.sendMessage(messageProvider.noSpecifiedWorldMessage());
                return false;
            }

            var world = Bukkit.getWorld(args[1]);
            if (world == null) {
                player.sendMessage(messageProvider.noWorldMessage());
                return false;
            }

            player.teleport(world.getSpawnLocation());
            player.sendMessage(messageProvider.getMessage("command.world.teleport.teleported").replaceText(it -> it.match("%world%").replacement(world.getName())));
            return true;
        }

        if (args[0].equalsIgnoreCase("information")) {
            if (args.length < 2) {
                player.sendMessage(messageProvider.noSpecifiedWorldMessage());
                return false;
            }

            var world = PolarWorlds.getInstance().getWorldManager().getWorld(args[1]);
            if (world == null) {
                player.sendMessage(messageProvider.noWorldMessage());
                return false;
            }

            player.sendMessage(messageProvider.getMessage("command.world.information.header").replaceText(it -> it.match("%world%").replacement(world.getName())));
            player.sendMessage(messageProvider.getMessage("command.world.information.type").replaceText(it -> it.match("%type%").replacement(world.getWorldType().getName())));
            player.sendMessage(messageProvider.getMessage("command.world.information.players").replaceText(it -> it.match("%players%").replacement(String.valueOf(world.getOnlinePlayers()))));
            player.sendMessage(messageProvider.getMessage("command.world.information.mobs").replaceText(it -> it.match("%mobs%").replacement(String.valueOf(world.isSpawnMobs()))));
            player.sendMessage(messageProvider.getMessage("command.world.information.animals").replaceText(it -> it.match("%animals%").replacement(String.valueOf(world.isSpawnAnimals()))));
            return true;
        }

        if (args[0].equalsIgnoreCase("import")) {
            if (args.length < 2) {
                player.sendMessage(messageProvider.noSpecifiedWorldMessage());
                return false;
            }

            var world = Bukkit.getWorld(args[1]);
            if (world != null) {
                player.sendMessage(messageProvider.getMessage("command.world.import.alreadyExists"));
                return false;
            }

            if (!(new File(System.getProperty("user.dir") + "/" + args[1])).exists()) {
                player.sendMessage(messageProvider.noWorldMessage());
                return true;
            }

            world = Bukkit.createWorld(new WorldCreator(args[1]));

            var generator = world.getGenerator() != null ? world.getGenerator().toString() : null;
            var polarWorld = PolarWorld.builder().name(args[1]).environment(world.getEnvironment()).difficulty(world.getDifficulty()).generator(generator).seed(world.getSeed()).worldType(world.getWorldType()).allowPvP(world.getPVP()).spawnAnimals(world.getAllowAnimals()).spawnMobs(world.getAllowMonsters()).generateStructures(world.canGenerateStructures()).loaded(true).build();
            polarWorld.create();
            player.sendMessage(messageProvider.getMessage("command.world.import.success").replaceText(text -> text.match("%world%").replacement(polarWorld.getName())));
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            player.sendMessage(messageProvider.getMessage("command.world.list.header"));
            PolarWorlds.getInstance().getWorldManager().getWorlds().stream().filter(it -> PolarWorlds.getInstance().getWorldManager().getWorld(it).isLoaded()).forEach(it -> {
                player.sendMessage(messageProvider.getMessage("command.world.list.loop").replaceText(text -> text.match("%world%").replacement(it)));
            });
            return true;
        }

        if (args[0].equalsIgnoreCase("unload")) {
            if (args.length < 2) {
                player.sendMessage(messageProvider.noSpecifiedWorldMessage());
                return false;
            }

            var world = PolarWorlds.getInstance().getWorldManager().getWorld(args[1]);
            if (!world.exits()) {
                player.sendMessage(messageProvider.noWorldMessage());
                return false;
            }

            world.unload();
            player.sendMessage(messageProvider.getMessage("command.world.unload.success").replaceText(text -> text.match("%world%").replacement(world.getName())));
            return true;
        }

        if (args[0].equalsIgnoreCase("load")) {
            if (args.length < 2) {
                player.sendMessage(messageProvider.noSpecifiedWorldMessage());
                return false;
            }

            var world = PolarWorlds.getInstance().getWorldManager().getWorld(args[1]);
            if (!world.exits()) {
                player.sendMessage(messageProvider.noWorldMessage());
            }

            world.load();
            player.sendMessage(messageProvider.getMessage("command.world.load.success").replaceText(text -> text.match("%world%").replacement(world.getName())));
            return true;
        }

        this.sendUsage(player);
        return false;
    }

    private void sendUsage(Player player) {
        player.sendMessage(messageProvider.getMessage("command.world.usage.create"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.delete"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.teleport"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.information"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.import"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.list"));
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
            arg1.add("import");
            arg1.add("list");
            arg1.add("unload");
            return arg1;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            List<String> create = new ArrayList<>();
            Arrays.stream(World.Environment.values()).forEach(it -> create.add(it.name().toLowerCase()));
            Arrays.stream(WorldType.values()).forEach(it -> create.add(it.name().toLowerCase()));
            return create;
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("information") || args[0].equalsIgnoreCase("unload"))) {
            List<String> args2 = new ArrayList<>();
            Bukkit.getWorlds().forEach(world -> args2.add(world.getName()));
            return args2;
        }
        return List.of();
    }
}
