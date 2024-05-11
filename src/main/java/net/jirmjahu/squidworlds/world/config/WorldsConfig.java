package net.jirmjahu.squidworlds.world.config;

import lombok.RequiredArgsConstructor;
import net.jirmjahu.squidworlds.SquidWorlds;
import net.jirmjahu.squidworlds.world.SquidWorld;

@RequiredArgsConstructor
public class WorldsConfig {

    private final SquidWorld world;

    public void saveToConfig() {
        var config = SquidWorlds.getInstance().getWorldsConfig().getConfiguration();
        var configPath = "worlds." + world.getName() + ".";

        config.set(configPath + "environment", world.getEnvironment().toString());
        config.set(configPath + "difficulty", world.getDifficulty().toString());
        config.set(configPath + "generator", world.getGenerator());
        config.set(configPath + "seed", world.getSeed());
        config.set(configPath + "worldType", world.getWorldType().toString());
        config.set(configPath + "allowPvP", world.isAllowPvP());
        config.set(configPath + "spawnAnimals", world.isSpawnAnimals());
        config.set(configPath + "spawnMobs", world.isSpawnMobs());
        config.set(configPath + "generateStructures", world.isGenerateStructures());
        config.set(configPath + "keepSpawnInMemory", world.isKeepSpawnInMemory());

        SquidWorlds.getInstance().getWorldsConfig().saveConfig();
    }

    public void removeFromConfig() {
        var config = SquidWorlds.getInstance().getWorldsConfig().getConfiguration();
        config.set("worlds." + world.getName(), null);
        SquidWorlds.getInstance().getWorldsConfig().saveConfig();
    }
}
