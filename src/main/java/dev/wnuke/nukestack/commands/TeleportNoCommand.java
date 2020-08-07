package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class TeleportNoCommand implements CommandExecutor {
    NukeStack plugin;

    public TeleportNoCommand(NukeStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            if (sender.hasPermission("nukestack.tpn")) {
                if (args.length > 0) {
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        if (player.getPlayerListName().equals(args[0])) {
                            if (plugin.teleportRequests.get(player.getUniqueId()) == ((Player) sender).getUniqueId()) {
                                player.sendMessage("Teleport denied.");
                                sender.sendMessage("Teleport denied.");
                                return true;
                            }
                        }
                    }
                    sender.sendMessage("No player with name " + args[0] + " has requested to teleport to you.");
                } else {
                    sender.sendMessage("You need to specify who you want to deny.");
                }
            } else {
                return false;
            }
        }
        return true;
    }
}
