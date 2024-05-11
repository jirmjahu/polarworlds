package net.jirmjahu.squidworlds.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import net.jirmjahu.squidworlds.SquidWorlds;
import net.jirmjahu.squidworlds.world.config.WorldsConfig;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.*;
import org.bukkit.util.FileUtil;
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

        //save the created world into the configuration
        var config = new WorldsConfig(this);
        config.saveToConfig();
    }

    @SneakyThrows
    public void delete() {
        var config = new WorldsConfig(this);
        var world = Bukkit.getWorld(this.name);

        //remove the world from the config
        config.removeFromConfig();

        if (world != null) {
            Bukkit.unloadWorld(world, false);
        }

        //delete world folder
        var worldFolder = new File(Bukkit.getWorldContainer(), this.name);
        FileUtils.deleteDirectory(worldFolder);
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
