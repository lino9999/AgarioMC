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
            sender.sendMessage(plugin.getMessage("commands.console-only"));
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
                    player.sendMessage(plugin.getMessage("commands.no-permission"));
                    return true;
                }
                plugin.getArenaManager().setFirstPosition(player, player.getLocation());
                break;

            case "pos2":
                if (!player.hasPermission("agariomc.admin")) {
                    player.sendMessage(plugin.getMessage("commands.no-permission"));
                    return true;
                }
                plugin.getArenaManager().setSecondPosition(player, player.getLocation());
                break;

            case "create":
                if (!player.hasPermission("agariomc.admin")) {
                    player.sendMessage(plugin.getMessage("commands.no-permission"));
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

            case "reload":
                if (!player.hasPermission("agariomc.admin")) {
                    player.sendMessage(plugin.getMessage("commands.no-permission"));
                    return true;
                }
                plugin.reloadConfig();
                plugin.reloadMessagesConfig();
                player.sendMessage(plugin.getMessage("commands.config-reloaded"));
                break;

            default:
                showHelp(player);
                break;
        }

        return true;
    }

    private void showHelp(Player player) {
        player.sendMessage(plugin.getMessage("commands.help.header"));
        player.sendMessage(plugin.getMessage("commands.help.join"));
        player.sendMessage(plugin.getMessage("commands.help.leave"));

        if (player.hasPermission("agariomc.admin")) {
            player.sendMessage(plugin.getMessage("commands.help.pos1"));
            player.sendMessage(plugin.getMessage("commands.help.pos2"));
            player.sendMessage(plugin.getMessage("commands.help.create"));
            player.sendMessage(plugin.getMessage("commands.help.reload"));
        }
    }
}