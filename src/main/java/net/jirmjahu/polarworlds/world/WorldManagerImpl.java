package net.jirmjahu.polarworlds.world;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.jirmjahu.polarworlds.PolarWorlds;
import net.jirmjahu.polarworlds.config.ConfigManager;
import net.jirmjahu.polarworlds.generator.EmptyChunkGenerator;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.util.Set;

@RequiredArgsConstructor
public class WorldManagerImpl implements WorldManager {

    private final PolarWorlds plugin;
    private final ConfigManager saveConfiguration;
    private final FileConfiguration worldsConfig;

    @Override
    public void createWorld(WorldMeta meta) {
        //create new world create with the properties given in the meta
        var worldCreator = new WorldCreator(meta.getName()).type(meta.getWorldType()).generateStructures(meta.isGenerateStructures()).seed(meta.getSeed());

        if ("voidGenerator".equals(meta.getGenerator())) {
            worldCreator.generator(new EmptyChunkGenerator());

            //place a bedrock block at the world spawn
            this.getWorld(meta.getName()).getWorld().setBlockData(this.getWorld(meta.getName()).getWorld().getSpawnLocation(), Material.BEDROCK.createBlockData());
            return;
        } else {
            if (meta.getGenerator() != null) {
                worldCreator.generator(meta.getGenerator());
            }
        }
        var world = worldCreator.createWorld();
        if (world == null) {
            plugin.getLogger().warning("Failed to create world " + meta.getName() + "!");
            return;
        }

        world.setSpawnFlags(meta.isSpawnMobs(), meta.isSpawnMobs());
        world.setPVP(meta.isAllowPvP());

        meta.setLoaded(true);

        //save the created world into the configuration
        this.save(meta);
    }

    @Override
    @SneakyThrows
    public void deleteWorld(String name) {
        var world = this.getWorld(name).getWorld();

        //unload the world
        if (world != null) {
            Bukkit.unloadWorld(world, false);
        }

        //remove the world from the config
        this.worldsConfig.set("worlds." + world.getName(), null);
        this.saveConfiguration.save();

        //delete the world folder
        var worldFolder = new File(Bukkit.getWorldContainer(), name);
        FileUtils.deleteDirectory(worldFolder);
    }

    @Override
    public boolean exists(String name) {
        return this.getWorlds().contains(name);
    }

    @Override
    public PolarWorld getWorld(String name) {
        var configPath = "worlds." + name + ".";
        if (!worldsConfig.contains(configPath)) {
            return null;
        }

        //read the world out of the configuration
        return new PolarWorld(new WorldMeta(name,
                WorldType.valueOf(this.worldsConfig.getString(configPath + "worldType")),
                World.Environment.valueOf(this.worldsConfig.getString(configPath + "environment")),
                this.worldsConfig.getString(configPath + "generator"),
                this.worldsConfig.getLong(configPath + "seed"),
                this.worldsConfig.getBoolean(configPath + "allowPvP"),
                this.worldsConfig.getBoolean(configPath + "spawnAnimals"),
                this.worldsConfig.getBoolean(configPath + "spawnMobs"),
                this.worldsConfig.getBoolean(configPath + "generateStructures"), worldsConfig.getBoolean(configPath + "shouldLoad")));
    }

    @Override
    public Set<String> getWorlds() {
        if (this.worldsConfig == null || !this.worldsConfig.isConfigurationSection("worlds")) {
            return Set.of();
        }
        return this.worldsConfig.getConfigurationSection("worlds").getKeys(false);
    }

    @Override
    public PolarWorld importWorld(String name) {
        var world = Bukkit.createWorld(new WorldCreator(name));

        //create a new world meta with the properties of the imported world
        var meta = new WorldMeta(name, world.getWorldType(), world.getEnvironment(), world.getGenerator().toString(), world.getSeed(), world.getPVP(), world.getAllowAnimals(), world.getAllowMonsters(), world.canGenerateStructures(), true);

        //finally create the polarWorld and return it
        this.createWorld(meta);
        return this.getWorld(name);
    }

    @Override
    public void loadWorld(String name) {
        if (this.exists(name)) {
            var world = this.getWorld(name);
            world.meta().setLoaded(true);
            this.save(world);

            //create the world
            Bukkit.createWorld(new WorldCreator(name));
        }
    }

    @Override
    public void loadWorlds() {
        if (!this.worldsConfig.contains("worlds")) {
            plugin.getLogger().info("There are no worlds to load.");
            return;
        }

        final var worlds = this.getWorlds();

        plugin.getLogger().info("Loading " + worlds.size() + " worlds...");

        //load all worlds found in the configuration
        for (var worldName : worlds) {

            var world = this.getWorld(worldName);

            if (!world.meta().isLoaded()) {
                continue;
            }

            this.createWorld(world.meta());
            plugin.getLogger().info("[PolarWorlds] Successfully loaded world " + worldName + ".");
        }
    }

    @Override
    public void unloadWorld(String name) {
        if (this.exists(name)) {
            var world = this.getWorld(name);
            world.meta().setLoaded(false);
            this.save(world);

            //create the world
            Bukkit.unloadWorld(world.getWorld(), false);
        }
    }

    @Override
    public void save(WorldMeta meta) {
        var configPath = "worlds." + meta.getName() + ".";

        this.worldsConfig.set(configPath + "generator", meta.getGenerator());
        this.worldsConfig.set(configPath + "seed", meta.getSeed());
        this.worldsConfig.set(configPath + "worldType", meta.getWorldType().toString());
        this.worldsConfig.set(configPath + "environment", meta.getEnvironment().toString());
        this.worldsConfig.set(configPath + "allowPvP", meta.isAllowPvP());
        this.worldsConfig.set(configPath + "spawnAnimals", meta.isSpawnAnimals());
        this.worldsConfig.set(configPath + "spawnMobs", meta.isSpawnMobs());
        this.worldsConfig.set(configPath + "generateStructures", meta.isGenerateStructures());
        this.worldsConfig.set(configPath + "loaded", meta.isLoaded());

        this.saveConfiguration.save();
    }
}
