package com.Lino.agarioMC;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {
    private final Map<UUID, Scoreboard> playerScoreboards;
    private final Map<UUID, Integer> playerMasses;
    private final String objectiveName = "agarioMass";

    public ScoreboardManager() {
        this.playerScoreboards = new HashMap<>();
        this.playerMasses = new HashMap<>();
    }

    public void addPlayer(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        String title = AgarioMC.getInstance().getMessage("scoreboard.title");
        Objective objective = scoreboard.registerNewObjective(objectiveName, Criteria.DUMMY, title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (Map.Entry<UUID, Integer> entry : playerMasses.entrySet()) {
            Player existingPlayer = Bukkit.getPlayer(entry.getKey());
            if (existingPlayer != null && existingPlayer.isOnline()) {
                String playerFormat = AgarioMC.getInstance().getMessage("scoreboard.player-format", "{player}", existingPlayer.getName());
                Score score = objective.getScore(playerFormat);
                score.setScore(entry.getValue());
            }
        }

        playerScoreboards.put(player.getUniqueId(), scoreboard);
        playerMasses.put(player.getUniqueId(), 1);
        player.setScoreboard(scoreboard);

        updateAllScoreboards();
    }

    public void removePlayer(Player player) {
        playerScoreboards.remove(player.getUniqueId());
        playerMasses.remove(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

        updateAllScoreboards();
    }

    public void updateScore(Player player, int mass) {
        playerMasses.put(player.getUniqueId(), mass);
        updateAllScoreboards();
    }

    private void updateAllScoreboards() {
        for (Map.Entry<UUID, Scoreboard> entry : playerScoreboards.entrySet()) {
            Scoreboard scoreboard = entry.getValue();
            Objective objective = scoreboard.getObjective(objectiveName);

            if (objective != null) {
                for (String entryName : scoreboard.getEntries()) {
                    scoreboard.resetScores(entryName);
                }

                for (Map.Entry<UUID, Integer> massEntry : playerMasses.entrySet()) {
                    Player massPlayer = Bukkit.getPlayer(massEntry.getKey());
                    if (massPlayer != null && massPlayer.isOnline()) {
                        String playerFormat = AgarioMC.getInstance().getMessage("scoreboard.player-format", "{player}", massPlayer.getName());
                        Score score = objective.getScore(playerFormat);
                        score.setScore(massEntry.getValue());
                    }
                }
            }
        }
    }

    public void cleanup() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (playerScoreboards.containsKey(player.getUniqueId())) {
                player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }
        }
        playerScoreboards.clear();
        playerMasses.clear();
    }
}