package net.jirmjahu.polarworlds.world;

import lombok.RequiredArgsConstructor;
import net.jirmjahu.polarworlds.PolarWorlds;
import net.jirmjahu.polarworlds.config.ConfigManager;
import org.bukkit.*;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@RequiredArgsConstructor
public class WorldManager {

    private final PolarWorlds plugin;
    private final ConfigManager worldsConfig;

    public PolarWorld getWorld(String name) {
        var config = worldsConfig.getConfiguration();
        var configPath = "worlds." + name + ".";

        return PolarWorld.builder().name(name).difficulty(Difficulty.valueOf(config.getString(configPath + "difficulty", "NORMAL"))).environment(World.Environment.valueOf(config.getString(configPath + "environment", "NORMAL"))).generator(config.getString(configPath + "generator", null)).seed(config.getLong(configPath + "seed", 0)).worldType(WorldType.valueOf(config.getString(configPath + "worldType", "NORMAL"))).allowPvP(config.getBoolean(configPath + "allowPvP", true)).spawnAnimals(config.getBoolean(configPath + "spawnAnimals", true)).spawnMobs(config.getBoolean(configPath + "spawnMobs", true)).generateStructures(config.getBoolean(configPath + "generateStructures", true)).loaded(config.getBoolean(configPath + "loaded", true)).build();
    }

    public void loadWorlds() {
        if (!worldsConfig.getConfiguration().contains("worlds")) {
            plugin.getLogger().info("[PolarWorlds] No worlds to load.");
            return;
        }

        plugin.getLogger().info("[PolarWorlds] Loading " + getWorlds().size() + " worlds...");

        //load all worlds found in the configuration
        for (var worldName : this.getWorlds()) {
            var world = this.getWorld(worldName);
            if (!world.isLoaded()) {
                continue;
            }
            world.create();
            plugin.getLogger().info("[PolarWorlds] Successfully loaded world " + worldName + ".");
        }
    }

    public PolarWorld importWorld(String name) {
        final var world = Bukkit.createWorld(new WorldCreator(name));

        //get the generator of the world
        final var generator = world.getGenerator() != null ? world.getGenerator().toString() : null;

        //create a new polarworld
        final var polarWorld = PolarWorld.builder().name(name).environment(world.getEnvironment()).difficulty(world.getDifficulty()).generator(generator).seed(world.getSeed()).worldType(world.getWorldType()).allowPvP(world.getPVP()).spawnAnimals(world.getAllowAnimals()).spawnMobs(world.getAllowMonsters()).generateStructures(world.canGenerateStructures()).loaded(true).build();

        polarWorld.create();
        return polarWorld;
    }

    public @NotNull Set<String> getWorlds() {
        final var config = worldsConfig.getConfiguration();
        //if the worlds section is missing just return an empty set
        if (config == null || !config.isConfigurationSection("worlds")) {
            return Set.of();
        }
        return config.getConfigurationSection("worlds").getKeys(false);
    }
}
