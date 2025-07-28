package com.Lino.agarioMC;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {
    private final Map<UUID, Scoreboard> playerScoreboards;
    private final String objectiveName = "agarioMass";

    public ScoreboardManager() {
        this.playerScoreboards = new HashMap<>();
    }

    public void addPlayer(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(objectiveName, Criteria.DUMMY, "§6§lAgarioMC Leaderboard");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        playerScoreboards.put(player.getUniqueId(), scoreboard);
        player.setScoreboard(scoreboard);
    }

    public void removePlayer(Player player) {
        playerScoreboards.remove(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public void updateScore(Player player, int mass) {
        for (Scoreboard scoreboard : playerScoreboards.values()) {
            Objective objective = scoreboard.getObjective(objectiveName);
            if (objective != null) {
                Score score = objective.getScore("§f" + player.getName());
                score.setScore(mass);
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
    }
}