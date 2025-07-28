package com.Lino.agarioMC;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArenaManager {
    private final Map<UUID, Location[]> selectionCache;
    private Arena currentArena;

    public ArenaManager() {
        this.selectionCache = new HashMap<>();
    }

    public void setFirstPosition(Player player, Location location) {
        Location[] positions = selectionCache.computeIfAbsent(player.getUniqueId(), k -> new Location[2]);
        positions[0] = location;
        player.sendMessage("§aFirst position set!");
    }

    public void setSecondPosition(Player player, Location location) {
        Location[] positions = selectionCache.computeIfAbsent(player.getUniqueId(), k -> new Location[2]);
        positions[1] = location;
        player.sendMessage("§aSecond position set!");
    }

    public boolean createArena(Player player) {
        Location[] positions = selectionCache.get(player.getUniqueId());

        if (positions == null || positions[0] == null || positions[1] == null) {
            player.sendMessage("§cYou must set both positions first!");
            return false;
        }

        if (!positions[0].getWorld().equals(positions[1].getWorld())) {
            player.sendMessage("§cBoth positions must be in the same world!");
            return false;
        }

        currentArena = new Arena(positions[0], positions[1]);
        selectionCache.remove(player.getUniqueId());
        player.sendMessage("§aArena created successfully!");
        return true;
    }

    public Arena getCurrentArena() {
        return currentArena;
    }

    public boolean hasArena() {
        return currentArena != null;
    }

    public void clearSelection(Player player) {
        selectionCache.remove(player.getUniqueId());
    }
}