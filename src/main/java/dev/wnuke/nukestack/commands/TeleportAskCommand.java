package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeleportAskCommand implements CommandExecutor {
    NukeStack plugin;

    public TeleportAskCommand(NukeStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            if (sender.hasPermission("nukestack.tpa")) {
                UUID playerID = ((Player) sender).getUniqueId();
                if (plugin.loadPlayerData(playerID).getTokens() < 2) {
                    sender.sendMessage("You need at least 2 tokens to teleport to someone.");
                    return true;
                }
                if (plugin.teleportRequests.containsKey(playerID)) {
                    sender.sendMessage("Please wait for your current teleport request to expire or cancel it by running /tpc.");
                    return true;
                }
                if (args.length > 0) {
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        if (player.getPlayerListName().equals(args[0])) {
                            plugin.teleportRequests.put(playerID, player.getUniqueId());
                            return true;
                        }
                    }
                    sender.sendMessage("No player found with name " + args[0] + ".");
                } else {
                    sender.sendMessage("You need to specify who you want to teleport to.");
                }
            } else {
                return false;
            }
        }
        return true;
    }
}
