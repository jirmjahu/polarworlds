package net.jirmjahu.squidworlds.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import net.jirmjahu.squidworlds.SquidWorlds;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

@Getter
@AllArgsConstructor
public class SquidWorld {

    private final String name;
    private final World.Environment environment;
    private Difficulty difficulty;
    private String generator;
    private Long seed;
    private WorldType worldType;
    private Location spawnLocation;
    private boolean allowPvP;
    private boolean spawnAnimals;
    private boolean spawnMobs;
    private boolean generateStructures;
    private boolean keepSpawnInMemory;

    public void create() {
        var worldCreator = new WorldCreator(this.name);
        worldCreator.environment(this.environment);
        worldCreator.generateStructures(this.generateStructures);

        if (generator != null) {
            worldCreator.generator(this.generator);
        }

        if (this.seed != null) {
            worldCreator.seed(this.seed);
        }

        var world = worldCreator.createWorld();
        if (world == null) {
            Bukkit.getConsoleSender().sendMessage("[SquidWorlds] Failed to create world: " + this.name);
            return;
        }

        world.setSpawnFlags(this.spawnMobs, this.spawnAnimals);
        world.setKeepSpawnInMemory(this.keepSpawnInMemory);
        world.setPVP(this.allowPvP);
        world.setDifficulty(this.difficulty);

        if (this.spawnLocation == null) {
            world.setSpawnLocation(world.getSpawnLocation());
            return;
        }

        world.setSpawnLocation(this.spawnLocation);

        //save the created world into the configuration
        this.save();
    }

    @SneakyThrows
    public void delete() {
        var world = Bukkit.getWorld(this.name);

        //remove the world from the config
        var config = SquidWorlds.getInstance().getWorldsConfig().getConfiguration();
        config.set("worlds." + world.getName(), null);
        SquidWorlds.getInstance().getWorldsConfig().saveConfig();

        //unload world
        if (world != null) {
            Bukkit.unloadWorld(world, false);
        }

        //delete world folder
        var worldFolder = new File(Bukkit.getWorldContainer(), this.name);
        FileUtils.deleteDirectory(worldFolder);
    }

    public void save() {
        var config = SquidWorlds.getInstance().getWorldsConfig().getConfiguration();
        var configPath = "worlds." + this.name + ".";

        config.set(configPath + "environment", this.environment.toString());
        config.set(configPath + "difficulty", this.difficulty.toString());
        config.set(configPath + "generator", this.generator);
        config.set(configPath + "seed", this.seed);
        config.set(configPath + "worldType", this.worldType.toString());
        config.set(configPath + "allowPvP", this.allowPvP);
        config.set(configPath + "spawnAnimals", this.spawnAnimals);
        config.set(configPath + "spawnMobs", this.spawnMobs);
        config.set(configPath + "generateStructures", this.generateStructures);
        config.set(configPath + "keepSpawnInMemory", this.keepSpawnInMemory);

        if (this.spawnLocation != null) {
            var locationSection = config.createSection(configPath + "spawnLocation");
            locationSection.set("x", this.spawnLocation.getX());
            locationSection.set("y", this.spawnLocation.getY());
            locationSection.set("z", this.spawnLocation.getZ());
            locationSection.set("yaw", this.spawnLocation.getYaw());
            locationSection.set("pitch", this.spawnLocation.getPitch());
        }

        SquidWorlds.getInstance().getWorldsConfig().saveConfig();
    }

    public World getWorld() {
        return Bukkit.getWorld(this.name);
    }

    public int getWorldPlayers() {
        return (int) Bukkit.getOnlinePlayers().stream().filter(it -> it.getWorld().getName().equals(this.name)).count();
    }

    public boolean exits() {
        return Bukkit.getWorld(this.name) != null;
    }
}
