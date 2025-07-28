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
    private static final int MAX_WOOL_ITEMS = 100;
    private static final long SPAWN_INTERVAL = 20L;

    public WoolSpawner(AgarioMC plugin) {
        this.plugin = plugin;
        this.woolItems = new HashSet<>();
    }

    public void startSpawning() {
        spawnTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Arena arena = plugin.getArenaManager().getCurrentArena();
            if (arena == null) return;

            if (woolItems.size() < MAX_WOOL_ITEMS) {
                spawnWoolItem(arena);
            }

            validateWoolItems();
        }, 0L, SPAWN_INTERVAL);
    }

    private void spawnWoolItem(Arena arena) {
        for (int attempts = 0; attempts < 10; attempts++) {
            Location location = arena.getRandomLocation();

            DyeColor[] colors = DyeColor.values();
            DyeColor randomColor = colors[(int)(Math.random() * colors.length)];

            ItemStack woolItem = new ItemStack(getWoolMaterial(randomColor));
            Item droppedItem = arena.getWorld().dropItem(location, woolItem);

            droppedItem.setVelocity(new Vector(0, 0, 0));
            droppedItem.setPickupDelay(0);
            droppedItem.setGlowing(true);

            woolItems.add(droppedItem);
            break;
        }
    }

    private Material getWoolMaterial(DyeColor color) {
        switch (color) {
            case WHITE: return Material.WHITE_WOOL;
            case ORANGE: return Material.ORANGE_WOOL;
            case MAGENTA: return Material.MAGENTA_WOOL;
            case LIGHT_BLUE: return Material.LIGHT_BLUE_WOOL;
            case YELLOW: return Material.YELLOW_WOOL;
            case LIME: return Material.LIME_WOOL;
            case PINK: return Material.PINK_WOOL;
            case GRAY: return Material.GRAY_WOOL;
            case LIGHT_GRAY: return Material.LIGHT_GRAY_WOOL;
            case CYAN: return Material.CYAN_WOOL;
            case PURPLE: return Material.PURPLE_WOOL;
            case BLUE: return Material.BLUE_WOOL;
            case BROWN: return Material.BROWN_WOOL;
            case GREEN: return Material.GREEN_WOOL;
            case RED: return Material.RED_WOOL;
            case BLACK: return Material.BLACK_WOOL;
            default: return Material.WHITE_WOOL;
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