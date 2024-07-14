package net.jirmjahu.polarworlds.command;

import net.jirmjahu.polarworlds.PolarWorlds;
import net.jirmjahu.polarworlds.generator.EmptyChunkGenerator;
import net.jirmjahu.polarworlds.message.MessageProvider;
import net.jirmjahu.polarworlds.world.PolarWorld;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
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
import java.util.Set;
import java.util.stream.Collectors;

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
            sendUsage(player);
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                return handleCreate(player, args);
            case "delete":
                return handleDelete(player, args);
            case "teleport":
            case "tp":
                return handleTeleport(player, args);
            case "information":
                return handleInformation(player, args);
            case "import":
                return handleImport(player, args);
            case "list":
                return handleList(player);
            case "unload":
                return handleUnload(player, args);
            case "load":
                return handleLoad(player, args);
            default:
                sendUsage(player);
                return false;
        }
    }

    private boolean handleCreate(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(messageProvider.noSpecifiedWorldMessage());
            return false;
        }

        WorldType worldType;
        World.Environment environment;
        String generator = null;

        switch (args[2].toLowerCase()) {
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
            case "void":
                worldType = WorldType.NORMAL;
                environment = World.Environment.NORMAL;
                generator = new EmptyChunkGenerator().name();
                break;
            default:
                sendUsage(player);
                return false;
        }

        PolarWorld world = PolarWorld.builder()
                .name(args[1])
                .environment(environment)
                .difficulty(Difficulty.NORMAL)
                .generator(generator)
                .seed(0L)
                .worldType(worldType)
                .allowPvP(true)
                .spawnAnimals(true)
                .spawnMobs(true)
                .generateStructures(true)
                .loaded(true)
                .build();

        if (world.exists()) {
            player.sendMessage(messageProvider.getMessage("command.world.create.alreadyExists"));
            return false;
        }

        world.create();

        if (world.getGenerator() != null) {
            if (world.getGenerator().equals("void")) {
                world.getWorld().setBlockData(world.getWorld().getSpawnLocation(), Material.BEDROCK.createBlockData());
            }
        }

        player.sendMessage(messageProvider.getMessage("command.world.create.create-success").replaceText(it -> it.match("%world%").replacement(world.getName())));
        return true;
    }

    private boolean handleDelete(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(messageProvider.noSpecifiedWorldMessage());
            return false;
        }

        final var world = PolarWorlds.getInstance().getWorldManager().getWorld(args[1]);
        if (!world.exists()) {
            player.sendMessage(messageProvider.noWorldMessage());
            return false;
        }

        teleportToDefaultWorld(player, world);

        world.delete();
        player.sendMessage(messageProvider.getMessage("command.world.delete.deleted").replaceText(it -> it.match("%world%").replacement(world.getName())));
        return true;
    }

    private boolean handleTeleport(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(messageProvider.noSpecifiedWorldMessage());
            return false;
        }

        final var world = Bukkit.getWorld(args[1]);
        if (world == null) {
            player.sendMessage(messageProvider.noWorldMessage());
            return false;
        }

        player.teleport(world.getSpawnLocation());
        player.sendMessage(messageProvider.getMessage("command.world.teleport.teleported").replaceText(it -> it.match("%world%").replacement(world.getName())));
        return true;
    }

    private boolean handleInformation(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(messageProvider.noSpecifiedWorldMessage());
            return false;
        }

        final var world = PolarWorlds.getInstance().getWorldManager().getWorld(args[1]);
        if (world == null) {
            player.sendMessage(messageProvider.noWorldMessage());
            return false;
        }

        player.sendMessage(messageProvider.getMessage("command.world.information.header").replaceText(it -> it.match("%world%").replacement(world.getName())));
        player.sendMessage(messageProvider.getMessage("command.world.information.type").replaceText(it -> it.match("%type%").replacement(world.getWorldType().getName())));
        player.sendMessage(messageProvider.getMessage("command.world.information.players").replaceText(it -> it.match("%players%").replacement(String.valueOf(world.getWorld().getPlayers().size()))));
        player.sendMessage(messageProvider.getMessage("command.world.information.mobs").replaceText(it -> it.match("%mobs%").replacement(String.valueOf(world.isSpawnMobs()))));
        player.sendMessage(messageProvider.getMessage("command.world.information.animals").replaceText(it -> it.match("%animals%").replacement(String.valueOf(world.isSpawnAnimals()))));
        return true;
    }

    private boolean handleImport(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(messageProvider.noSpecifiedWorldMessage());
            return false;
        }

        boolean force = args.length > 2 && args[2].equalsIgnoreCase("-force");

        var world = Bukkit.getWorld(args[1]);
        if (world != null && !force) {
            player.sendMessage(messageProvider.getMessage("command.world.import.alreadyExists"));
            return false;
        }

        if (!(new File(System.getProperty("user.dir") + "/" + args[1])).exists()) {
            player.sendMessage(messageProvider.noWorldMessage());
            return true;
        }

        final var polarWorld = PolarWorlds.getInstance().getWorldManager().importWorld(args[1]);

        player.sendMessage(messageProvider.getMessage("command.world.import.success").replaceText(text -> text.match("%world%").replacement(polarWorld.getName())));
        return true;
    }

    private boolean handleList(Player player) {
        player.sendMessage(messageProvider.getMessage("command.world.list.header"));

        var worlds = PolarWorlds.getInstance().getWorldManager().getWorlds();
        if (worlds.isEmpty()) {
            player.sendMessage(messageProvider.getMessage("command.world.list.noWorlds"));
            return true;
        }

        worlds.stream()
                .filter(worldName -> PolarWorlds.getInstance().getWorldManager().getWorld(worldName).isLoaded())
                .forEach(worldName -> player.sendMessage(messageProvider.getMessage("command.world.list.loop").replaceText(text -> text.match("%world%").replacement(worldName))));
        return true;
    }

    private boolean handleUnload(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(messageProvider.noSpecifiedWorldMessage());
            return false;
        }

        PolarWorld world = PolarWorlds.getInstance().getWorldManager().getWorld(args[1]);
        if (!world.exists()) {
            player.sendMessage(messageProvider.noWorldMessage());
            return false;
        }

        teleportToDefaultWorld(player, world);

        world.unload();
        player.sendMessage(messageProvider.getMessage("command.world.unload.success").replaceText(text -> text.match("%world%").replacement(world.getName())));
        return true;
    }

    private boolean handleLoad(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(messageProvider.noSpecifiedWorldMessage());
            return false;
        }

        PolarWorld world = PolarWorlds.getInstance().getWorldManager().getWorld(args[1]);
        if (!world.exists()) {
            player.sendMessage(messageProvider.noWorldMessage());
            return false;
        }

        world.load();
        player.sendMessage(messageProvider.getMessage("command.world.load.success").replaceText(text -> text.match("%world%").replacement(world.getName())));
        return true;
    }

    private void teleportToDefaultWorld(Player player, PolarWorld world) {
        final var defaultWorld = Bukkit.getWorld(PolarWorlds.getInstance().getDefaultConfig().getConfiguration().getString("default-world"));
        if (defaultWorld == null) {
            Bukkit.getConsoleSender().sendMessage("[PolarWorlds] The configured default world was not found, can't kick players of the world " + world.getName() + "!");
        } else {
            for (Player all : world.getWorld().getPlayers()) {
                all.teleport(defaultWorld.getSpawnLocation());
            }
        }
    }

    private void sendUsage(Player player) {
        player.sendMessage(messageProvider.getMessage("command.world.usage.create"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.delete"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.teleport"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.information"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.import"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.list"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.unload"));
        player.sendMessage(messageProvider.getMessage("command.world.usage.load"));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "delete", "teleport", "tp", "information", "import", "list", "unload", "load");
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

        if (args.length == 2 && Arrays.asList("teleport", "tp", "delete", "information", "unload", "load").contains(args[0].toLowerCase())) {
            return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
        }

        return List.of();
    }
}
