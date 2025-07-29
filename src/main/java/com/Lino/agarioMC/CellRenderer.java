package com.Lino.agarioMC;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;

public class CellRenderer {

    public void renderCell(Player player, PlayerCell cell) {
        Location playerLoc = player.getLocation();

        Set<Location> oldBlocks = new HashSet<>(cell.getCellBlocks());
        Set<Location> newCellBlocks = generateCircle(playerLoc, cell.getRadius());

        for (Location loc : oldBlocks) {
            if (!newCellBlocks.contains(loc)) {
                Block block = loc.getBlock();
                if (block.getType().name().contains("CARPET")) {
                    block.setType(Material.AIR);
                }
            }
        }

        for (Location loc : newCellBlocks) {
            Block block = loc.getBlock();
            if (block.getType() == Material.AIR || block.getType().name().contains("CARPET")) {
                block.setType(getCarpetMaterial(cell.getColor()));
            }
        }

        cell.setCellBlocks(newCellBlocks);
    }

    public void clearCell(PlayerCell cell) {
        for (Location loc : cell.getCellBlocks()) {
            Block block = loc.getBlock();
            if (block.getType().name().contains("CARPET")) {
                block.setType(Material.AIR);
            }
        }
        cell.getCellBlocks().clear();
    }

    private Set<Location> generateCircle(Location center, int radius) {
        Set<Location> blocks = new HashSet<>();
        int centerX = center.getBlockX();
        int centerZ = center.getBlockZ();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) {
                    Location loc = new Location(center.getWorld(), centerX + x, center.getBlockY() - 1, centerZ + z);

                    while (loc.getBlockY() > 0 && !loc.getBlock().getType().isSolid()) {
                        loc.subtract(0, 1, 0);
                    }

                    if (loc.getBlock().getType().isSolid()) {
                        loc.add(0, 1, 0);
                        blocks.add(loc.clone());
                    }
                }
            }
        }

        return blocks;
    }

    private Material getCarpetMaterial(org.bukkit.DyeColor color) {
        switch (color) {
            case ORANGE: return Material.ORANGE_CARPET;
            case MAGENTA: return Material.MAGENTA_CARPET;
            case LIGHT_BLUE: return Material.LIGHT_BLUE_CARPET;
            case YELLOW: return Material.YELLOW_CARPET;
            case LIME: return Material.LIME_CARPET;
            case PINK: return Material.PINK_CARPET;
            case GRAY: return Material.GRAY_CARPET;
            case LIGHT_GRAY: return Material.LIGHT_GRAY_CARPET;
            case CYAN: return Material.CYAN_CARPET;
            case PURPLE: return Material.PURPLE_CARPET;
            case BLUE: return Material.BLUE_CARPET;
            case BROWN: return Material.BROWN_CARPET;
            case GREEN: return Material.GREEN_CARPET;
            case RED: return Material.RED_CARPET;
            case BLACK: return Material.BLACK_CARPET;
            default: return Material.RED_CARPET;
        }
    }
}