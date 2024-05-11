package net.jirmjahu.squidworlds.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.*;

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
        //TODO: save world to config d
    }

    public void delete() {

    }

    public World getWorld() {
        return Bukkit.getWorld(this.name);
    }

    public boolean exits() {
        return Bukkit.getWorld(this.name) != null;
    }

}
