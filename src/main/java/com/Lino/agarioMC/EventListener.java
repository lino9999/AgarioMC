package com.Lino.agarioMC;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
            player.sendMessage(plugin.getMessage("arena.cannot-leave"));
            return;
        }

        double collisionRadius = plugin.getConfig().getDouble("rendering.collision-radius", 1.5);

        for (Item item : player.getNearbyEntities(collisionRadius, collisionRadius, collisionRadius).stream()
                .filter(entity -> entity instanceof Item)
                .map(entity -> (Item) entity)
                .toList()) {

            if (item.getItemStack().getType().name().contains("CARPET")) {
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (plugin.getGameManager().isInGame(player)) {
            if (event.getItem().getItemStack().getType().name().contains("CARPET")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (plugin.getGameManager().isInGame(player)) {
            event.setCancelled(true);
            player.setFoodLevel(20);
        }
    }
}