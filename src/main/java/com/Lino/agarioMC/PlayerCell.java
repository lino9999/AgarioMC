package com.Lino.agarioMC;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerCell {
    private final UUID playerId;
    private int mass;
    private DyeColor color;
    private Set<Location> cellBlocks;
    private Location lastLocation;

    public PlayerCell(Player player) {
        this.playerId = player.getUniqueId();
        this.mass = 1;
        this.color = getRandomColor();
        this.cellBlocks = new HashSet<>();
        this.lastLocation = player.getLocation();
    }

    private DyeColor getRandomColor() {
        DyeColor[] colors = {DyeColor.RED, DyeColor.BLUE, DyeColor.GREEN,
                DyeColor.YELLOW, DyeColor.PURPLE, DyeColor.ORANGE,
                DyeColor.PINK, DyeColor.CYAN, DyeColor.LIME,
                DyeColor.MAGENTA, DyeColor.LIGHT_BLUE, DyeColor.GRAY,
                DyeColor.LIGHT_GRAY, DyeColor.BROWN, DyeColor.BLACK};
        return colors[(int)(Math.random() * colors.length)];
    }

    public void addMass(int amount) {
        this.mass += amount;
    }

    public void setMass(int mass) {
        this.mass = Math.max(1, mass);
    }

    public int getMass() {
        return mass;
    }

    public int getRadius() {
        return (int)Math.sqrt(mass) + 1;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public DyeColor getColor() {
        return color;
    }

    public Set<Location> getCellBlocks() {
        return new HashSet<>(cellBlocks);
    }

    public void setCellBlocks(Set<Location> blocks) {
        this.cellBlocks = new HashSet<>(blocks);
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location location) {
        this.lastLocation = location;
    }

    public boolean canEat(PlayerCell other) {
        return this.mass > other.mass * 1.1;
    }
}