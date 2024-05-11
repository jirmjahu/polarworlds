package net.jirmjahu.squidworlds.world;

import net.jirmjahu.squidworlds.SquidWorlds;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;

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
        var spawnLocation = config.getLocation("spawnLocation");

        return new SquidWorld(name, environment, difficulty, generator, seed, worldType, spawnLocation, allowPvP, spawnAnimals, spawnMobs, generateStructures, keepSpawnInMemory);
    }
}
