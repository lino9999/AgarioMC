package com.Lino.agarioMC;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CellRenderer {
    private final Map<Location, BlockState> originalBlocks = new HashMap<>();

    public void renderCell(Player player, PlayerCell cell) {
        Location playerLoc = player.getLocation();

        Set<Location> oldBlocks = new HashSet<>(cell.getCellBlocks());
        Set<Location> newCellBlocks = generateCircle(playerLoc, cell.getRadius());

        // Remove old blocks that are no longer part of the cell
        for (Location loc : oldBlocks) {
            if (!newCellBlocks.contains(loc)) {
                restoreBlock(loc);
            }
        }

        // Place new blocks
        for (Location loc : newCellBlocks) {
            if (!oldBlocks.contains(loc)) {
                Block block = loc.getBlock();
                AgarioMC plugin = AgarioMC.getInstance();
                if (plugin != null) {
                    Arena arena = plugin.getArenaManager().getCurrentArena();
                    if (arena != null && arena.isInArena(loc)) {
                        if (!originalBlocks.containsKey(loc)) {
                            originalBlocks.put(loc, block.getState());
                        }
                        block.setType(getWoolMaterial(cell.getColor()));
                    }
                }
            }
        }

        cell.setCellBlocks(newCellBlocks);
        cell.setLastLocation(playerLoc);
    }

    public void clearCell(PlayerCell cell) {
        for (Location loc : cell.getCellBlocks()) {
            restoreBlock(loc);
        }
        cell.getCellBlocks().clear();
    }

    private void restoreBlock(Location loc) {
        BlockState originalState = originalBlocks.get(loc);
        if (originalState != null) {
            originalState.update(true, false);
            originalBlocks.remove(loc);
        }
    }

    private Set<Location> generateCircle(Location center, int radius) {
        Set<Location> blocks = new HashSet<>();
        int centerX = center.getBlockX();
        int centerY = center.getBlockY() - 1;
        int centerZ = center.getBlockZ();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) {
                    Location blockLoc = new Location(center.getWorld(), centerX + x, centerY, centerZ + z);
                    blocks.add(blockLoc);
                }
            }
        }

        return blocks;
    }

    private Material getWoolMaterial(org.bukkit.DyeColor color) {
        switch (color) {
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
            default: return Material.RED_WOOL;
        }
    }
}