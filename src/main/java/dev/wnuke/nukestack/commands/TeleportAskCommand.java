package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.PlayerDataUtilities;
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
            UUID playerID = ((Player) sender).getUniqueId();
            if (PlayerDataUtilities.loadPlayerData(playerID).getTokens() < NukeStack.tpaCost) {
                sender.sendMessage("You need at least " + NukeStack.dupeCost + " token(s) to teleport to someone.");
                return true;
            }
            if (plugin.teleportRequests.containsKey(playerID)) {
                sender.sendMessage("Please wait for your current teleport request to get accepted or cancel it by running /tpc.");
                return true;
            }
            if (args.length > 0) {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (player.getPlayerListName().equals(args[0])) {
                        plugin.teleportRequests.put(playerID, player.getUniqueId());
                        sender.sendMessage("Teleport request sent, to cancel type /tpc");
                        player.sendMessage(sender.getName() + " has requested to teleport to you.");
                        return true;
                    }
                }
                sender.sendMessage("No player found with name " + args[0] + ".");
            } else {
                sender.sendMessage("You need to specify who you want to teleport to.");
            }
        }
        return true;
    }
}
