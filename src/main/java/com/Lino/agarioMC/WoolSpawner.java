package com.Lino.agarioMC;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WoolSpawner {
    private final AgarioMC plugin;
    private final Set<Location> woolBlocks;
    private BukkitTask spawnTask;
    private static final int MAX_WOOL_BLOCKS = 100;
    private static final long SPAWN_INTERVAL = 20L;

    public WoolSpawner(AgarioMC plugin) {
        this.plugin = plugin;
        this.woolBlocks = new HashSet<>();
    }

    public void startSpawning() {
        spawnTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Arena arena = plugin.getArenaManager().getCurrentArena();
            if (arena == null) return;

            if (woolBlocks.size() < MAX_WOOL_BLOCKS) {
                spawnWoolBlock(arena);
            }

            validateWoolBlocks();
        }, 0L, SPAWN_INTERVAL);
    }

    private void spawnWoolBlock(Arena arena) {
        for (int attempts = 0; attempts < 10; attempts++) {
            Location location = arena.getRandomLocation();
            Block block = location.getBlock();

            if (block.getType() == Material.AIR && isValidSpawnLocation(block)) {
                DyeColor[] colors = DyeColor.values();
                DyeColor randomColor = colors[(int)(Math.random() * colors.length)];

                block.setType(getWoolMaterial(randomColor));
                woolBlocks.add(block.getLocation());
                break;
            }
        }
    }

    private Material getWoolMaterial(DyeColor color) {
        switch (color) {
            case WHITE: return Material.WHITE_WOOL;
            case ORANGE: return Material.ORANGE_WOOL;
            case MAGENTA: return Material.MAGENTA_WOOL;
            case LIGHT_BLUE: return Material.LIGHT_BLUE_WOOL;
            case YELLOW: return Material.YELLOW_WOOL;
            case LIME: return Material.LIME_WOOL;
            case PINK: return Material.PINK_WOOL;
            case GRAY: return Material.GRAY_WOOL;
            case LIGHT_GRAY: return Material.LIGHT_GRAY_WOOL;
            case CYAN: return Material.CYAN_WOOL;
            case PURPLE: return Material.PURPLE_WOOL;
            case BLUE: return Material.BLUE_WOOL;
            case BROWN: return Material.BROWN_WOOL;
            case GREEN: return Material.GREEN_WOOL;
            case RED: return Material.RED_WOOL;
            case BLACK: return Material.BLACK_WOOL;
            default: return Material.WHITE_WOOL;
        }
    }

    private boolean isValidSpawnLocation(Block block) {
        Block below = block.getRelative(0, -1, 0);
        return below.getType().isSolid() && !below.getType().name().contains("WOOL");
    }

    private void validateWoolBlocks() {
        Iterator<Location> iterator = woolBlocks.iterator();
        while (iterator.hasNext()) {
            Location location = iterator.next();
            Block block = location.getBlock();

            if (!block.getType().name().contains("WOOL")) {
                iterator.remove();
            }
        }
    }

    public void removeWoolBlock(Location location) {
        woolBlocks.remove(location);
    }

    public boolean isWoolBlock(Location location) {
        return woolBlocks.contains(location);
    }

    public void cleanup() {
        if (spawnTask != null) {
            spawnTask.cancel();
        }

        for (Location location : woolBlocks) {
            location.getBlock().setType(Material.AIR);
        }
        woolBlocks.clear();
    }
}