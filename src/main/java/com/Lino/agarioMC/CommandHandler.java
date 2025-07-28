package com.Lino.agarioMC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
    private final AgarioMC plugin;

    public CommandHandler(AgarioMC plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            showHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "pos1":
                if (!player.hasPermission("agariomc.admin")) {
                    player.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }
                plugin.getArenaManager().setFirstPosition(player, player.getLocation());
                break;

            case "pos2":
                if (!player.hasPermission("agariomc.admin")) {
                    player.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }
                plugin.getArenaManager().setSecondPosition(player, player.getLocation());
                break;

            case "create":
                if (!player.hasPermission("agariomc.admin")) {
                    player.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }
                plugin.getArenaManager().createArena(player);
                break;

            case "join":
                plugin.getGameManager().joinGame(player);
                break;

            case "leave":
                plugin.getGameManager().leaveGame(player);
                break;

            default:
                showHelp(player);
                break;
        }

        return true;
    }

    private void showHelp(Player player) {
        player.sendMessage("§6§lAgarioMC Commands:");
        player.sendMessage("§e/agario join §7- Join the game");
        player.sendMessage("§e/agario leave §7- Leave the game");

        if (player.hasPermission("agariomc.admin")) {
            player.sendMessage("§e/agario pos1 §7- Set first arena position");
            player.sendMessage("§e/agario pos2 §7- Set second arena position");
            player.sendMessage("§e/agario create §7- Create arena from selected positions");
        }
    }
}