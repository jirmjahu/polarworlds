package net.jirmjahu.polarworlds.world;

import net.jirmjahu.polarworlds.PolarWorlds;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.jetbrains.annotations.NotNull;

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
            return;
        }

        Bukkit.getConsoleSender().sendMessage("[PolarWorlds] Loading " + getWorlds().size() + " worlds...");
        for (var worldName : getWorlds()) {
            var world = getWorld(worldName);
            if (!world.isLoaded()) {
                return;
            }
            getWorld(worldName).create();
        }
    }

    public @NotNull Set<String> getWorlds() {
        return PolarWorlds.getInstance().getWorldsConfig().getConfiguration().getConfigurationSection("worlds").getKeys(false);
    }
}
