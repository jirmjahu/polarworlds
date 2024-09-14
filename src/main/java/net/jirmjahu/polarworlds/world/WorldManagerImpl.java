package net.jirmjahu.polarworlds.world;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.jirmjahu.polarworlds.PolarWorlds;
import net.jirmjahu.polarworlds.config.ConfigManager;
import net.jirmjahu.polarworlds.generator.EmptyChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.FileConfiguration;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.util.Set;

@RequiredArgsConstructor
public class WorldManagerImpl implements WorldManager {

    private final PolarWorlds plugin;
    private final FileConfiguration worldsConfig;

    @Override
    public void createWorld(WorldMeta meta) {
        WorldCreator worldCreator = new WorldCreator(meta.getName())
                .type(meta.getWorldType())
                .generateStructures(meta.isGenerateStructures())
                .seed(meta.getSeed());

        if ("voidGenerator".equals(meta.getGenerator())) {
            worldCreator.generator(new EmptyChunkGenerator());

            //todo place a bedrock block at the world spawn

            return;
        } else if (meta.getGenerator() != null) {
            worldCreator.generator(meta.getGenerator());
        }

        World world = worldCreator.createWorld();
        if (world == null) {
            plugin.getLogger().warning("Failed to create world " + meta.getName() + "!");
            return;
        }

        world.setSpawnFlags(meta.isSpawnMobs(), meta.isSpawnMobs());
        world.setPVP(meta.isAllowPvP());

        meta.setLoaded(true);
        save(meta);
    }

    @Override
    @SneakyThrows
    public void deleteWorld(String name) {
        World world = getWorld(name).getWorld();
        if (world != null) {
            Bukkit.unloadWorld(world, false);
        }

        worldsConfig.set("worlds." + world.getName(), null);
        plugin.getWorldsConfig().save();

        File worldFolder = new File(Bukkit.getWorldContainer(), name);
        FileUtils.deleteDirectory(worldFolder);
    }

    @Override
    public boolean exists(String name) {
        return getWorlds().contains(name);
    }

    @Override
    public PolarWorld getWorld(String name) {
        String configPath = "worlds." + name + ".";
        if (!worldsConfig.contains(configPath)) {
            return null;
        }

        return new PolarWorld(new WorldMeta(
                name,
                WorldType.valueOf(worldsConfig.getString(configPath + "worldType")),
                World.Environment.valueOf(worldsConfig.getString(configPath + "environment")),
                worldsConfig.getString(configPath + "generator"),
                worldsConfig.getLong(configPath + "seed"),
                worldsConfig.getBoolean(configPath + "allowPvP"),
                worldsConfig.getBoolean(configPath + "spawnAnimals"),
                worldsConfig.getBoolean(configPath + "spawnMobs"),
                worldsConfig.getBoolean(configPath + "generateStructures"),
                worldsConfig.getBoolean(configPath + "loaded")));
    }

    @Override
    public Set<String> getWorlds() {
        if (worldsConfig == null || !worldsConfig.isConfigurationSection("worlds")) {
            return Set.of();
        }
        return worldsConfig.getConfigurationSection("worlds").getKeys(false);
    }

    public PolarWorld importWorld(String name) {
        if (Bukkit.getWorld(name) == null) {
            Bukkit.createWorld(new WorldCreator(name));
        }

        World world = Bukkit.getWorld(name);
        WorldMeta meta = new WorldMeta(
                name,
                world.getWorldType(),
                world.getEnvironment(),
                world.getGenerator().toString(),
                world.getSeed(),
                world.getPVP(),
                world.getAllowAnimals(),
                world.getAllowMonsters(),
                world.canGenerateStructures(),
                true);

        createWorld(meta);
        return getWorld(name);
    }

    @Override
    public void loadWorld(String name) {
        if (exists(name)) {
            PolarWorld world = getWorld(name);
            world.meta().setLoaded(true);
            save(world);
            Bukkit.createWorld(new WorldCreator(name));
        }
    }

    @Override
    public void loadWorlds() {
        if (!worldsConfig.contains("worlds")) {
            plugin.getLogger().info("There are no worlds to load.");
            return;
        }

        plugin.getLogger().info("Loading " + getWorlds().size() + " worlds...");

        for (String worldName : getWorlds()) {
            PolarWorld world = getWorld(worldName);
            if (!world.meta().isLoaded()) {
                continue;
            }

            createWorld(world.meta());
            plugin.getLogger().info("[PolarWorlds] Successfully loaded world " + worldName + ".");
        }
    }

    @Override
    public void unloadWorld(String name) {
        if (exists(name)) {
            PolarWorld world = getWorld(name);
            world.meta().setLoaded(false);
            save(world);
            Bukkit.unloadWorld(world.getWorld(), false);
        }
    }

    @Override
    public void save(WorldMeta meta) {
        String configPath = "worlds." + meta.getName() + ".";
        worldsConfig.set(configPath + "generator", meta.getGenerator());
        worldsConfig.set(configPath + "seed", meta.getSeed());
        worldsConfig.set(configPath + "worldType", meta.getWorldType().toString());
        worldsConfig.set(configPath + "environment", meta.getEnvironment().toString());
        worldsConfig.set(configPath + "allowPvP", meta.isAllowPvP());
        worldsConfig.set(configPath + "spawnAnimals", meta.isSpawnAnimals());
        worldsConfig.set(configPath + "spawnMobs", meta.isSpawnMobs());
        worldsConfig.set(configPath + "generateStructures", meta.isGenerateStructures());
        worldsConfig.set(configPath + "loaded", meta.isLoaded());

        plugin.getWorldsConfig().save();
    }

    @Override
    public void saveWorlds() {
        for (String worldName : getWorlds()) {
            save(getWorld(worldName));
        }
    }
}
