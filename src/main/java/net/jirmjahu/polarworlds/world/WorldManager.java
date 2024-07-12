package net.jirmjahu.polarworlds.world;

import net.jirmjahu.polarworlds.PolarWorlds;
import org.bukkit.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

public class WorldManager {

    public PolarWorld getWorld(String name) {
        var config = PolarWorlds.getInstance().getWorldsConfig().getConfiguration();
        var configPath = "worlds." + name + ".";

        return PolarWorld.builder()
                .name(name)
                .difficulty(Difficulty.valueOf(config.getString(configPath + "difficulty", "NORMAL")))
                .environment(World.Environment.valueOf(config.getString(configPath + "environment", "NORMAL")))
                .generator(config.getString(configPath + "generator", null))
                .seed(config.getLong(configPath + "seed", 0))
                .worldType(WorldType.valueOf(config.getString(configPath + "worldType", "NORMAL")))
                .allowPvP(config.getBoolean(configPath + "allowPvP", true))
                .spawnAnimals(config.getBoolean(configPath + "spawnAnimals", true))
                .spawnMobs(config.getBoolean(configPath + "spawnMobs", true))
                .generateStructures(config.getBoolean(configPath + "generateStructures", true))
                .loaded(config.getBoolean(configPath + "loaded", true))
                .build();
    }

    public void loadWorlds() {
        if (!PolarWorlds.getInstance().getWorldsConfig().getConfiguration().contains("worlds")) {
            Bukkit.getConsoleSender().sendMessage("[PolarWorlds] No worlds to load.");
            return;
        }

        Bukkit.getConsoleSender().sendMessage("[PolarWorlds] Loading " + getWorlds().size() + " worlds...");

        for (var worldName : getWorlds()) {
            var world = getWorld(worldName);
            if (!world.isLoaded()) {
                continue;
            }
            world.create();
            Bukkit.getConsoleSender().sendMessage("[PolarWorlds] Successfully loaded world " + worldName + ".");
        }
    }

    public PolarWorld importWorld(String name) {
        final var world = Bukkit.createWorld(new WorldCreator(name));

        final var generator = world.getGenerator() != null ? world.getGenerator().toString() : null;
        final var polarWorld = PolarWorld.builder()
                .name(name)
                .environment(world.getEnvironment())
                .difficulty(world.getDifficulty())
                .generator(generator)
                .seed(world.getSeed())
                .worldType(world.getWorldType())
                .allowPvP(world.getPVP())
                .spawnAnimals(world.getAllowAnimals())
                .spawnMobs(world.getAllowMonsters())
                .generateStructures(world.canGenerateStructures())
                .loaded(true)
                .build();

        polarWorld.create();
        return polarWorld;
    }

    public @NotNull Set<String> getWorlds() {
        final var config = PolarWorlds.getInstance().getWorldsConfig().getConfiguration();
        if (config == null || !config.isConfigurationSection("worlds")) {
            return Collections.emptySet();
        }
        return config.getConfigurationSection("worlds").getKeys(false);
    }
}
