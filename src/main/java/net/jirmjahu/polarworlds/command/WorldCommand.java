package net.jirmjahu.polarworlds.command;

import net.jirmjahu.polarworlds.PolarWorlds;
import net.jirmjahu.polarworlds.command.sub.*;
import net.jirmjahu.polarworlds.message.MessageProvider;
import net.jirmjahu.polarworlds.world.PolarWorld;
import net.jirmjahu.polarworlds.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WorldCommand implements CommandExecutor, TabCompleter {

    private final PolarWorlds plugin;
    private final MessageProvider messageProvider;

    private final WorldCreateCommand createCommand;
    private final WorldDeleteCommand deleteCommand;
    private final WorldTeleportCommand teleportCommand;
    private final WorldInformationCommand informationCommand;
    private final WorldListCommand listCommand;
    private final WorldImportCommand importCommand;
    private final WorldLoadCommand loadCommand;
    private final WorldUnloadCommand unloadCommand;
    private final WorldEditCommand editCommand;

    public WorldCommand(PolarWorlds plugin, WorldManager worldManager, MessageProvider messageProvider) {
        this.plugin = plugin;
        this.messageProvider = messageProvider;
        this.createCommand = new WorldCreateCommand(this, worldManager, messageProvider);
        this.deleteCommand = new WorldDeleteCommand(this, worldManager, messageProvider);
        this.teleportCommand = new WorldTeleportCommand(messageProvider);
        this.informationCommand = new WorldInformationCommand(worldManager, messageProvider);
        this.listCommand = new WorldListCommand(worldManager, messageProvider);
        this.importCommand = new WorldImportCommand(worldManager, messageProvider);
        this.loadCommand = new WorldLoadCommand(worldManager, messageProvider);
        this.unloadCommand = new WorldUnloadCommand(this, worldManager, messageProvider);
        this.editCommand = new WorldEditCommand(worldManager, messageProvider);
    }

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

        switch (args[0].toLowerCase()) {
            case "create":
                return createCommand.execute(player, args);
            case "delete":
                return deleteCommand.execute(player, args);
            case "teleport":
            case "tp":
                return teleportCommand.execute(player, args);
            case "information":
                return informationCommand.execute(player, args);
            case "import":
                return importCommand.execute(player, args);
            case "list":
                return listCommand.execute(player, args);
            case "unload":
                return unloadCommand.execute(player, args);
            case "load":
                return loadCommand.execute(player, args);
            case "edit":
                return editCommand.execute(player, args);
            default:
                this.sendUsage(player);
                return false;
        }
    }

    public void teleportToDefaultWorld(PolarWorld world) {
        World defaultWorld = Bukkit.getWorld(plugin.getDefaultConfig().getConfiguration().getString("default-world"));
        if (defaultWorld == null) {
            plugin.getLogger().warning("The configured default world was not found, can't kick players of the world " + world.meta().getName() + "!");
            return;
        }

        world.getWorld().getPlayers().forEach(all -> all.teleport(defaultWorld.getSpawnLocation()));
    }

    public void sendUsage(Player player) {
        player.sendMessage(messageProvider.getMessage("command.world.usage.create"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.delete"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.teleport"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.information"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.import"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.list"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.unload"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.load"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.edit"));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "delete", "teleport", "tp", "information", "import", "list", "unload", "load", "edit");
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("import")) {
            return List.of("-force");
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            List<String> create = new ArrayList<>();
            Arrays.stream(World.Environment.values()).forEach(it -> create.add(it.name().toLowerCase()));
            Arrays.stream(WorldType.values()).forEach(it -> create.add(it.name().toLowerCase()));
            create.add("void");
            return create;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("edit")) {
            return List.of("pvp", "spawnAnimals", "spawnMobs");
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("edit")) {
            return List.of("true", "false");
        }

        if (args.length == 2 && Arrays.asList("teleport", "tp", "delete", "information", "unload", "load", "edit").contains(args[0].toLowerCase())) {
            return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
        }

        return List.of();
    }
}
