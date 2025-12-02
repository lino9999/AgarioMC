# 🔴 AgarioMC - The Agar.io Experience in Minecraft (1.21+)

> **Bring the addicting gameplay of Agar.io to your Spigot server.**
> Players spawn as cells, eat wool to grow, and consume smaller players to dominate the arena.
> **No mods required** — uses clever block manipulation to visualize size!

![Java](https://img.shields.io/badge/Java-21-orange) ![Spigot](https://img.shields.io/badge/API-1.21-yellow) ![License](https://img.shields.io/badge/License-MIT-blue)

---

## 🎮 How it Works
**AgarioMC** transforms Minecraft movement into a cell-eating arcade game. The plugin handles complex logic to simulate the original web game:

* **Eat to Grow:** Collect scattered wool items to increase your mass.
* **PvP Consumption:** If you are 10% larger than another player, you can "eat" them by walking over them!.
* **Mass Physics:** As you get bigger, you get slower. The plugin applies dynamic slowness effects based on your size thresholds.

### ✨ Key Features

* **🎨 Dynamic Cell Rendering**
    * The plugin dynamically draws a circle of colored carpets/blocks around the player to visualize their current size in real-time.
    * Colors are randomly assigned upon joining (Red, Blue, Green, etc.).

* **🏟️ Easy Arena Management**
    * Set arena boundaries easily with `/agario pos1` and `/agario pos2`.
    * Supports automated wool spawning within bounds.

* **📊 Live Scoreboard**
    * Tracks current mass and leaderboard position directly on the sidebar.

---

## ⚙️ Configuration
You can tweak the gameplay balance in `config.yml`. Adjust how fast players grow, collision radiuses, and speed penalties.

```yaml
game:
  starting-mass: 1
  mass-per-wool: 1
  eat-ratio: 1.1 # You must be 10% bigger to eat someone
  
  speed:
    slowness-1-mass: 30 # Apply Slowness I at 30 mass
    slowness-2-mass: 50 # Apply Slowness II at 50 mass
