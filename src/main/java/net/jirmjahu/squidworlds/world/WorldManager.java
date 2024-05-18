package net.jirmjahu.squidworlds.world;

import net.jirmjahu.squidworlds.SquidWorlds;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class WorldManager {

    public SquidWorld getWorld(String name) {
        var config = SquidWorlds.getInstance().getWorldsConfig().getConfiguration();
        var configPath = "worlds." + name + ".";

        var environment = World.Environment.valueOf(config.getString(configPath + "environment", "NORMAL"));
        var difficulty = Difficulty.valueOf(config.getString(configPath + "difficulty", "NORMAL"));
        var generator = config.getString(configPath + "generator", null);
        var seed = config.getLong(configPath + "seed", 0);
        var worldType = WorldType.valueOf(config.getString(configPath + "worldType", "NORMAL"));
        var allowPvP = config.getBoolean(configPath + "allowPvP", true);
        var spawnAnimals = config.getBoolean(configPath + "spawnAnimals", true);
        var spawnMobs = config.getBoolean(configPath + "spawnMobs", true);
        var generateStructures = config.getBoolean(configPath + "generateStructures", true);
        var keepSpawnInMemory = config.getBoolean(configPath + "keepSpawnInMemory", true);

        return new SquidWorld(name, environment, difficulty, generator, seed, worldType, allowPvP, spawnAnimals, spawnMobs, generateStructures, keepSpawnInMemory);
    }

    public void loadWorlds() {
        if (!SquidWorlds.getInstance().getWorldsConfig().getConfiguration().contains("worlds")) {
            return;
        }

        Bukkit.getConsoleSender().sendMessage("[SquidWorlds] Loading " + getAllWorlds().size() + " worlds...");
        for (var worldName : getAllWorlds()) {
            getWorld(worldName).create();
        }
    }

    public @NotNull Set<String> getAllWorlds() {
        return SquidWorlds.getInstance().getWorldsConfig().getConfiguration().getConfigurationSection("worlds").getKeys(false);
    }
}
