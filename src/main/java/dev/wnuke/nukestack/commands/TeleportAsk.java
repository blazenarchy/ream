package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.GeneralUtilities;
import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.player.PlayerDataUtilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeleportAsk implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            Player player = ((Player) sender).getPlayer();
            if (player == null) return false;
            UUID playerID = player.getUniqueId();
            if (PlayerDataUtilities.loadPlayerData(player).getTokens() < NukeStack.tpaCost) {
                GeneralUtilities.notEnoughTokens(player, NukeStack.tpaCost);
                return true;
            }
            if (NukeStack.teleportRequests.containsKey(playerID)) {
                sender.sendMessage("Please wait for your current teleport request to get accepted or cancel it by running /tpc.");
                return true;
            }
            if (args.length > 0) {
                for (Player onlinePlayer : NukeStack.PLUGIN.getServer().getOnlinePlayers()) {
                    if (onlinePlayer.getPlayerListName().equals(args[0])) {
                        NukeStack.teleportRequests.put(playerID, onlinePlayer.getUniqueId());
                        sender.sendMessage(ChatColor.DARK_GREEN + "Teleport request sent, to cancel type /tpc");
                        onlinePlayer.sendMessage(((Player) sender).getDisplayName() + ChatColor.GREEN + " has requested to teleport to you.");
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
