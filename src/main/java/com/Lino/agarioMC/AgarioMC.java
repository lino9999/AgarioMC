package com.Lino.agarioMC;

import org.bukkit.plugin.java.JavaPlugin;

public class AgarioMC extends JavaPlugin {
    private static AgarioMC instance;
    private ArenaManager arenaManager;
    private GameManager gameManager;
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        instance = this;

        arenaManager = new ArenaManager();
        gameManager = new GameManager(this);
        scoreboardManager = new ScoreboardManager();

        getCommand("agario").setExecutor(new CommandHandler(this));
        getServer().getPluginManager().registerEvents(new EventListener(this), this);

        getLogger().info("AgarioMC has been enabled!");
    }

    @Override
    public void onDisable() {
        if (gameManager != null) {
            gameManager.shutdown();
        }
        if (scoreboardManager != null) {
            scoreboardManager.cleanup();
        }

        getLogger().info("AgarioMC has been disabled!");
    }

    public static AgarioMC getInstance() {
        return instance;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
}