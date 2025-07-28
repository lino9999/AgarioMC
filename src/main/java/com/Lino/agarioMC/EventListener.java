package com.Lino.agarioMC;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
    private final AgarioMC plugin;

    public EventListener(AgarioMC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getGameManager().isInGame(player)) {
            return;
        }

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Arena arena = plugin.getArenaManager().getCurrentArena();
        if (arena != null && arena.isInArena(event.getFrom()) && !arena.isInArena(event.getTo())) {
            event.setCancelled(true);
            player.sendMessage("§cYou cannot leave the arena!");
            return;
        }

        for (Item item : player.getNearbyEntities(1.5, 1.5, 1.5).stream()
                .filter(entity -> entity instanceof Item)
                .map(entity -> (Item) entity)
                .toList()) {

            if (item.getItemStack().getType().name().contains("WOOL")) {
                WoolSpawner spawner = plugin.getGameManager().getWoolSpawner();
                if (spawner.isWoolItem(item)) {
                    spawner.removeWoolItem(item);
                    item.remove();
                    plugin.getGameManager().collectWool(player);
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.5f);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.getGameManager().isInGame(player)) {
            plugin.getGameManager().handlePlayerQuit(player);
        }
    }
}