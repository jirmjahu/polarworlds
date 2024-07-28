package net.jirmjahu.polarworlds.world;

import java.util.Set;

public interface WorldManager {

    void createWorld(WorldMeta meta);

    void deleteWorld(String name);

    boolean exists(String name);

    default boolean exists(PolarWorld world) {
        return this.exists(world.meta().getName());
    }

    PolarWorld getWorld(String name);

    Set<String> getWorlds();

    PolarWorld importWorld(String name);

    void loadWorld(String name);

    void loadWorlds();

    void unloadWorld(String name);

    void save(WorldMeta meta);

    default void save(PolarWorld world) {
        this.save(world.meta());
    }
}
