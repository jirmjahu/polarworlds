package net.jirmjahu.polarworlds.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import net.jirmjahu.polarworlds.PolarWorlds;
import org.bukkit.*;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

@Getter
@AllArgsConstructor
public class PolarWorld {

    private final String name;
    private final World.Environment environment;
    private Difficulty difficulty;
    private String generator;
    private Long seed;
    private WorldType worldType;
    private boolean allowPvP;
    private boolean spawnAnimals;
    private boolean spawnMobs;
    private boolean generateStructures;
    private boolean keepSpawnInMemory;
    private boolean loaded;

    public void create() {
        var worldCreator = new WorldCreator(this.name);
        worldCreator.environment(this.environment);
        worldCreator.type(this.worldType);
        worldCreator.generateStructures(this.generateStructures);

        if (generator != null) {
            worldCreator.generator(this.generator);
        }

        if (this.seed != null) {
            worldCreator.seed(this.seed);
        }

        var world = worldCreator.createWorld();
        if (world == null) {
            Bukkit.getConsoleSender().sendMessage("[PolarWorlds] Failed to create world: " + this.name);
            return;
        }

        world.setSpawnFlags(this.spawnMobs, this.spawnAnimals);
        world.setKeepSpawnInMemory(this.keepSpawnInMemory);
        world.setPVP(this.allowPvP);
        world.setDifficulty(this.difficulty);

        this.loaded = true;

        //save the created world into the configuration
        this.save();
    }

    @SneakyThrows
    public void delete() {
        var world = Bukkit.getWorld(this.name);

        //unload the world
        if (world != null) {
            Bukkit.unloadWorld(world, false);
        }

        //remove the world from the config
        var config = PolarWorlds.getInstance().getWorldsConfig().getConfiguration();
        config.set("worlds." + world.getName(), null);
        PolarWorlds.getInstance().getWorldsConfig().saveConfig();

        //delete the world folder
        var worldFolder = new File(Bukkit.getWorldContainer(), this.name);
        FileUtils.deleteDirectory(worldFolder);
    }

    public void save() {
        var config = PolarWorlds.getInstance().getWorldsConfig().getConfiguration();
        var configPath = "worlds." + this.name + ".";

        config.set(configPath + "environment", this.environment.toString());
        config.set(configPath + "difficulty", this.difficulty.name());
        config.set(configPath + "generator", this.generator);
        config.set(configPath + "seed", this.seed);
        config.set(configPath + "worldType", this.worldType.toString());
        config.set(configPath + "allowPvP", this.allowPvP);
        config.set(configPath + "spawnAnimals", this.spawnAnimals);
        config.set(configPath + "spawnMobs", this.spawnMobs);
        config.set(configPath + "generateStructures", this.generateStructures);
        config.set(configPath + "keepSpawnInMemory", this.keepSpawnInMemory);
        config.set(configPath + "loaded", this.loaded);

        PolarWorlds.getInstance().getWorldsConfig().saveConfig();
    }

    public void load() {
        this.loaded = true;
        PolarWorlds.getInstance().getWorldsConfig().getConfiguration().set("worlds." + this.name + "." + "loaded", true);
        Bukkit.createWorld(new WorldCreator(this.name));
    }

    public void unload() {
        this.loaded = false;
        Bukkit.unloadWorld(this.getWorld(), true);

        //set loaded to false in the world configuration
        PolarWorlds.getInstance().getWorldsConfig().getConfiguration().set("worlds." + this.name + "." + "loaded", false);
    }

    public World getWorld() {
        return Bukkit.getWorld(this.name);
    }

    public int getOnlinePlayers() {
        return (int) Bukkit.getOnlinePlayers().stream().filter(it -> it.getWorld().getName().equals(this.name)).count();
    }

    public boolean exits() {
        return PolarWorlds.getInstance().getWorldsConfig().getConfiguration().getStringList("worlds").contains(this.name);
    }
}
