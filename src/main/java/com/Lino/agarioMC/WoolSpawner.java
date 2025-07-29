package com.Lino.agarioMC;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WoolSpawner {
    private final AgarioMC plugin;
    private final Set<Item> woolItems;
    private BukkitTask spawnTask;

    public WoolSpawner(AgarioMC plugin) {
        this.plugin = plugin;
        this.woolItems = new HashSet<>();
    }

    public void startSpawning() {
        long spawnInterval = plugin.getConfig().getLong("arena.wool-spawn-interval", 20L);

        spawnTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Arena arena = plugin.getArenaManager().getCurrentArena();
            if (arena == null) return;

            int maxWoolItems = plugin.getConfig().getInt("arena.max-wool-items", 100);

            if (woolItems.size() < maxWoolItems) {
                spawnWoolItem(arena);
            }

            validateWoolItems();
        }, 0L, spawnInterval);
    }

    private void spawnWoolItem(Arena arena) {
        for (int attempts = 0; attempts < 10; attempts++) {
            Location location = arena.getRandomLocation();

            DyeColor[] colors = DyeColor.values();
            DyeColor randomColor = colors[(int)(Math.random() * colors.length)];

            ItemStack woolItem = new ItemStack(getWoolMaterial(randomColor));
            Item droppedItem = arena.getWorld().dropItem(location, woolItem);

            droppedItem.setVelocity(new Vector(0, 0, 0));
            droppedItem.setPickupDelay(Integer.MAX_VALUE);
            droppedItem.setGlowing(true);
            droppedItem.setInvulnerable(true);

            woolItems.add(droppedItem);
            break;
        }
    }

    private Material getWoolMaterial(DyeColor color) {
        switch (color) {
            case WHITE: return Material.WHITE_CARPET;
            case ORANGE: return Material.ORANGE_CARPET;
            case MAGENTA: return Material.MAGENTA_CARPET;
            case LIGHT_BLUE: return Material.LIGHT_BLUE_CARPET;
            case YELLOW: return Material.YELLOW_CARPET;
            case LIME: return Material.LIME_CARPET;
            case PINK: return Material.PINK_CARPET;
            case GRAY: return Material.GRAY_CARPET;
            case LIGHT_GRAY: return Material.LIGHT_GRAY_CARPET;
            case CYAN: return Material.CYAN_CARPET;
            case PURPLE: return Material.PURPLE_CARPET;
            case BLUE: return Material.BLUE_CARPET;
            case BROWN: return Material.BROWN_CARPET;
            case GREEN: return Material.GREEN_CARPET;
            case RED: return Material.RED_CARPET;
            case BLACK: return Material.BLACK_CARPET;
            default: return Material.WHITE_CARPET;
        }
    }

    private void validateWoolItems() {
        Iterator<Item> iterator = woolItems.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();

            if (!item.isValid() || item.isDead()) {
                iterator.remove();
            }
        }
    }

    public void removeWoolItem(Item item) {
        woolItems.remove(item);
    }

    public boolean isWoolItem(Item item) {
        return woolItems.contains(item);
    }

    public void cleanup() {
        if (spawnTask != null) {
            spawnTask.cancel();
        }

        for (Item item : woolItems) {
            if (item.isValid()) {
                item.remove();
            }
        }
        woolItems.clear();
    }
}