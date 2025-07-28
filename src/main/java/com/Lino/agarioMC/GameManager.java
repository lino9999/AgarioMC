package com.Lino.agarioMC;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager {
    private final AgarioMC plugin;
    private final Map<UUID, PlayerCell> playerCells;
    private final Map<UUID, Location> originalLocations;
    private final Map<UUID, GameMode> originalGameModes;
    private WoolSpawner woolSpawner;
    private CellRenderer cellRenderer;
    private BukkitTask gameTask;

    public GameManager(AgarioMC plugin) {
        this.plugin = plugin;
        this.playerCells = new HashMap<>();
        this.originalLocations = new HashMap<>();
        this.originalGameModes = new HashMap<>();
        this.woolSpawner = new WoolSpawner(plugin);
        this.cellRenderer = new CellRenderer();
    }

    public void joinGame(Player player) {
        Arena arena = plugin.getArenaManager().getCurrentArena();
        if (arena == null) {
            player.sendMessage("§cNo arena has been set up!");
            return;
        }

        if (playerCells.containsKey(player.getUniqueId())) {
            player.sendMessage("§cYou are already in the game!");
            return;
        }

        originalLocations.put(player.getUniqueId(), player.getLocation());
        originalGameModes.put(player.getUniqueId(), player.getGameMode());

        PlayerCell cell = new PlayerCell(player);
        playerCells.put(player.getUniqueId(), cell);

        player.teleport(arena.getSpawnLocation());
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);

        plugin.getScoreboardManager().addPlayer(player);
        plugin.getScoreboardManager().updateScore(player, cell.getMass());

        player.sendMessage("§aYou joined the game!");

        if (gameTask == null) {
            startGameLoop();
        }
    }

    public void leaveGame(Player player) {
        if (!playerCells.containsKey(player.getUniqueId())) {
            player.sendMessage("§cYou are not in the game!");
            return;
        }

        removePlayer(player);
        player.sendMessage("§aYou left the game!");
    }

    private void removePlayer(Player player) {
        UUID playerId = player.getUniqueId();
        PlayerCell cell = playerCells.get(playerId);

        if (cell != null) {
            cellRenderer.clearCell(cell);
        }

        playerCells.remove(playerId);
        plugin.getScoreboardManager().removePlayer(player);

        Location originalLocation = originalLocations.remove(playerId);
        GameMode originalGameMode = originalGameModes.remove(playerId);

        if (originalLocation != null) {
            player.teleport(originalLocation);
        }
        if (originalGameMode != null) {
            player.setGameMode(originalGameMode);
        }

        if (playerCells.isEmpty() && gameTask != null) {
            gameTask.cancel();
            gameTask = null;
            woolSpawner.cleanup();
        }
    }

    private void startGameLoop() {
        woolSpawner.startSpawning();

        gameTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (PlayerCell cell : playerCells.values()) {
                Player player = Bukkit.getPlayer(cell.getPlayerId());
                if (player == null || !player.isOnline()) {
                    continue;
                }

                cellRenderer.renderCell(player, cell);
                checkCollisions(player, cell);
                plugin.getScoreboardManager().updateScore(player, cell.getMass());
            }
        }, 0L, 5L);
    }

    private void checkCollisions(Player player, PlayerCell cell) {
        for (PlayerCell otherCell : playerCells.values()) {
            if (cell.getPlayerId().equals(otherCell.getPlayerId())) {
                continue;
            }

            Player otherPlayer = Bukkit.getPlayer(otherCell.getPlayerId());
            if (otherPlayer == null || !otherPlayer.isOnline()) {
                continue;
            }

            double distance = player.getLocation().distance(otherPlayer.getLocation());

            if (distance < cell.getRadius() && cell.canEat(otherCell)) {
                cell.addMass(otherCell.getMass());
                respawnPlayer(otherPlayer);
                player.sendMessage("§aYou ate " + otherPlayer.getName() + "!");
                otherPlayer.sendMessage("§cYou were eaten by " + player.getName() + "!");
            }
        }
    }

    private void respawnPlayer(Player player) {
        PlayerCell cell = playerCells.get(player.getUniqueId());
        if (cell == null) return;

        cellRenderer.clearCell(cell);
        cell.setMass(1);

        Arena arena = plugin.getArenaManager().getCurrentArena();
        if (arena != null) {
            player.teleport(arena.getSpawnLocation());
        }
    }

    public void collectWool(Player player) {
        PlayerCell cell = playerCells.get(player.getUniqueId());
        if (cell != null) {
            cell.addMass(1);
        }
    }

    public boolean isInGame(Player player) {
        return playerCells.containsKey(player.getUniqueId());
    }

    public void shutdown() {
        if (gameTask != null) {
            gameTask.cancel();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isInGame(player)) {
                removePlayer(player);
            }
        }

        woolSpawner.cleanup();
    }
}