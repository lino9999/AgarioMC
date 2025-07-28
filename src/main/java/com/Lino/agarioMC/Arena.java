package com.Lino.agarioMC;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;

public class Arena {
    private final World world;
    private final int minX, minY, minZ;
    private final int maxX, maxY, maxZ;
    private final BoundingBox bounds;

    public Arena(Location pos1, Location pos2) {
        this.world = pos1.getWorld();

        this.minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        this.minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        this.minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        this.maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        this.maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        this.maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        this.bounds = BoundingBox.of(pos1, pos2);
    }

    public boolean isInArena(Location location) {
        if (!location.getWorld().equals(world)) {
            return false;
        }
        return bounds.contains(location.toVector());
    }

    public Location getRandomLocation() {
        int x = minX + (int)(Math.random() * (maxX - minX + 1));
        int z = minZ + (int)(Math.random() * (maxZ - minZ + 1));
        int y = minY;

        for (int checkY = maxY; checkY >= minY; checkY--) {
            Block block = world.getBlockAt(x, checkY, z);
            if (block.getType().isSolid()) {
                y = checkY + 1;
                break;
            }
        }

        return new Location(world, x + 0.5, y, z + 0.5);
    }

    public Location getSpawnLocation() {
        return getRandomLocation();
    }

    public World getWorld() {
        return world;
    }

    public int getMinX() { return minX; }
    public int getMinY() { return minY; }
    public int getMinZ() { return minZ; }
    public int getMaxX() { return maxX; }
    public int getMaxY() { return maxY; }
    public int getMaxZ() { return maxZ; }
}