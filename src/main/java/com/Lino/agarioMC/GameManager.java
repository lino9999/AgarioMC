package com.Lino.agarioMC;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
            player.sendMessage(plugin.getMessage("arena.not-setup"));
            return;
        }

        if (playerCells.containsKey(player.getUniqueId())) {
            player.sendMessage(plugin.getMessage("game.already-in-game"));
            return;
        }

        originalLocations.put(player.getUniqueId(), player.getLocation());
        originalGameModes.put(player.getUniqueId(), player.getGameMode());

        PlayerCell cell = new PlayerCell(player);
        playerCells.put(player.getUniqueId(), cell);

        player.teleport(arena.getSpawnLocation());
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20);

        plugin.getScoreboardManager().addPlayer(player);
        plugin.getScoreboardManager().updateScore(player, cell.getMass());

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                cellRenderer.renderCell(player, cell);
            }
        }, 10L);

        player.sendMessage(plugin.getMessage("game.joined"));

        cellRenderer.renderCell(player, cell);

        if (gameTask == null) {
            startGameLoop();
        }
    }

    public void leaveGame(Player player) {
        if (!playerCells.containsKey(player.getUniqueId())) {
            player.sendMessage(plugin.getMessage("game.not-in-game"));
            return;
        }

        removePlayer(player);
        player.sendMessage(plugin.getMessage("game.left"));
    }

    public void handlePlayerQuit(Player player) {
        if (!playerCells.containsKey(player.getUniqueId())) {
            return;
        }

        PlayerCell cell = playerCells.get(player.getUniqueId());
        if (cell != null) {
            cellRenderer.clearCell(cell);
        }

        playerCells.remove(player.getUniqueId());
        originalLocations.remove(player.getUniqueId());
        originalGameModes.remove(player.getUniqueId());

        plugin.getScoreboardManager().removePlayer(player);

        if (playerCells.isEmpty() && gameTask != null) {
            gameTask.cancel();
            gameTask = null;
            woolSpawner.cleanup();
        }
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

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        if (playerCells.isEmpty() && gameTask != null) {
            gameTask.cancel();
            gameTask = null;
            woolSpawner.cleanup();
        }
    }

    private void startGameLoop() {
        woolSpawner.startSpawning();

        plugin.getLogger().info(plugin.getMessage("plugin.game-loop-start"));

        long renderInterval = plugin.getConfig().getLong("rendering.render-interval", 2L);

        gameTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Map.Entry<UUID, PlayerCell> entry : new HashMap<>(playerCells).entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());
                PlayerCell cell = entry.getValue();

                if (player == null || !player.isOnline()) {
                    continue;
                }

                cellRenderer.renderCell(player, cell);
                checkCollisions(player, cell);
                updatePlayerSpeed(player, cell);
                plugin.getScoreboardManager().updateScore(player, cell.getMass());
            }
        }, 0L, renderInterval);
    }

    private void updatePlayerSpeed(Player player, PlayerCell cell) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.SLOWNESS)) {
                player.removePotionEffect(PotionEffectType.SLOWNESS);
            }
        }

        int mass = cell.getMass();
        int slow1Mass = plugin.getConfig().getInt("game.speed.slowness-1-mass", 15);
        int slow2Mass = plugin.getConfig().getInt("game.speed.slowness-2-mass", 30);
        int slow3Mass = plugin.getConfig().getInt("game.speed.slowness-3-mass", 50);

        if (mass > slow1Mass) {
            int slowLevel = 0;
            if (mass > slow3Mass) {
                slowLevel = 3;
            } else if (mass > slow2Mass) {
                slowLevel = 2;
            } else {
                slowLevel = 1;
            }

            if (slowLevel > 0) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, slowLevel - 1, false, false));
            }
        }
    }

    private void checkCollisions(Player player, PlayerCell cell) {
        double collisionRadius = plugin.getConfig().getDouble("rendering.collision-radius", 1.5);

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
                player.sendMessage(plugin.getMessage("game.player-eaten", "{player}", otherPlayer.getName()));
                otherPlayer.sendMessage(plugin.getMessage("game.eaten-by-player", "{player}", player.getName()));
            }
        }
    }

    private void respawnPlayer(Player player) {
        PlayerCell cell = playerCells.get(player.getUniqueId());
        if (cell == null) return;

        cellRenderer.clearCell(cell);
        cell.setMass(plugin.getConfig().getInt("game.starting-mass", 1));

        Arena arena = plugin.getArenaManager().getCurrentArena();
        if (arena != null) {
            player.teleport(arena.getSpawnLocation());
        }
    }

    public void collectWool(Player player) {
        PlayerCell cell = playerCells.get(player.getUniqueId());
        if (cell != null) {
            int massPerWool = plugin.getConfig().getInt("game.mass-per-wool", 1);
            cell.addMass(massPerWool);
        }
    }

    public boolean isInGame(Player player) {
        return playerCells.containsKey(player.getUniqueId());
    }

    public WoolSpawner getWoolSpawner() {
        return woolSpawner;
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