package com.Lino.agarioMC;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArenaManager {
    private final AgarioMC plugin;
    private final Map<UUID, Location[]> selectionCache;
    private Arena currentArena;
    private File arenaFile;
    private FileConfiguration arenaConfig;

    public ArenaManager(AgarioMC plugin) {
        this.plugin = plugin;
        this.selectionCache = new HashMap<>();
        loadArenaFile();
        loadArena();
    }

    private void loadArenaFile() {
        arenaFile = new File(plugin.getDataFolder(), "arena.yml");
        if (!arenaFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                arenaFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create arena.yml!");
            }
        }
        arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);
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

        saveArena();
        player.sendMessage("§aArena created successfully!");
        return true;
    }

    private void saveArena() {
        if (currentArena == null) return;

        arenaConfig.set("arena.world", currentArena.getWorld().getName());
        arenaConfig.set("arena.pos1.x", currentArena.getMinX());
        arenaConfig.set("arena.pos1.y", currentArena.getMinY());
        arenaConfig.set("arena.pos1.z", currentArena.getMinZ());
        arenaConfig.set("arena.pos2.x", currentArena.getMaxX());
        arenaConfig.set("arena.pos2.y", currentArena.getMaxY());
        arenaConfig.set("arena.pos2.z", currentArena.getMaxZ());

        try {
            arenaConfig.save(arenaFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save arena to file!");
        }
    }

    private void loadArena() {
        if (!arenaConfig.contains("arena")) return;

        String worldName = arenaConfig.getString("arena.world");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            plugin.getLogger().warning("Arena world '" + worldName + "' not found!");
            return;
        }

        int x1 = arenaConfig.getInt("arena.pos1.x");
        int y1 = arenaConfig.getInt("arena.pos1.y");
        int z1 = arenaConfig.getInt("arena.pos1.z");
        int x2 = arenaConfig.getInt("arena.pos2.x");
        int y2 = arenaConfig.getInt("arena.pos2.y");
        int z2 = arenaConfig.getInt("arena.pos2.z");

        Location pos1 = new Location(world, x1, y1, z1);
        Location pos2 = new Location(world, x2, y2, z2);

        currentArena = new Arena(pos1, pos2);
        plugin.getLogger().info("Arena loaded successfully!");
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