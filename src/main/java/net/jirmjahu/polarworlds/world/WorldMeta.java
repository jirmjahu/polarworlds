package net.jirmjahu.polarworlds.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;
import org.bukkit.WorldType;

@Getter
@Setter
@AllArgsConstructor
public class WorldMeta {

    private final String name;
    private WorldType worldType;
    private World.Environment environment;
    private String generator;
    private long seed;
    private boolean allowPvP;
    private boolean spawnAnimals;
    private boolean spawnMobs;
    private boolean generateStructures;
    private boolean loaded;

}
