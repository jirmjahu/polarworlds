package net.jirmjahu.polarworlds.generator;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class EmptyChunkGenerator extends ChunkGenerator {

    @Override
    public ChunkGenerator.@NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int x, int z, @NotNull ChunkGenerator.BiomeGrid biomeGrid) {
        return createChunkData(world);
    }

    public String getName() {
        return "voidGenerator";
    }
}
