package com.Lino.agarioMC;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
    private final AgarioMC plugin;

    public EventListener(AgarioMC plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!plugin.getGameManager().isInGame(player)) {
            return;
        }

        event.setCancelled(true);

        if (block.getType().name().contains("WOOL")) {
            Arena arena = plugin.getArenaManager().getCurrentArena();
            if (arena != null && arena.isInArena(block.getLocation())) {
                WoolSpawner spawner = plugin.getGameManager().woolSpawner;
                if (spawner.isWoolBlock(block.getLocation())) {
                    block.setType(Material.AIR);
                    spawner.removeWoolBlock(block.getLocation());
                    plugin.getGameManager().collectWool(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getGameManager().isInGame(player)) {
            return;
        }

        Arena arena = plugin.getArenaManager().getCurrentArena();
        if (arena != null && !arena.isInArena(event.getTo())) {
            event.setCancelled(true);
            player.sendMessage("§cYou cannot leave the arena!");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.getGameManager().isInGame(player)) {
            plugin.getGameManager().leaveGame(player);
        }
    }
}