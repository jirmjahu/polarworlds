package net.jirmjahu.polarworlds.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.World;

@Getter
@RequiredArgsConstructor
public class PolarWorld {

    @Accessors(fluent = true)
    private final WorldMeta meta;

    public World getWorld() {
        return Bukkit.getWorld(meta.getName());
    }
}
