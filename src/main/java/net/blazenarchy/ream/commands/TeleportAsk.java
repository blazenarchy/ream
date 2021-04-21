package net.blazenarchy.ream.commands;

import net.blazenarchy.ream.GeneralUtilities;
import net.blazenarchy.ream.Ream;
import net.blazenarchy.ream.player.PlayerDataUtilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeleportAsk implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            if (args.length > 0) {
                Player player = ((Player) sender).getPlayer();
                if (player == null) return false;
                UUID playerID = player.getUniqueId();
                if (PlayerDataUtilities.loadPlayerData(player).getTokens() < Ream.tpaCost) {
                    GeneralUtilities.notEnoughTokens(player, Ream.tpaCost);
                    return true;
                }
                if (Ream.teleportRequests.containsKey(playerID)) {
                    sender.sendMessage("Please wait for your current teleport request to get accepted or cancel it by running /tpc.");
                    return true;
                }
                Player onlinePlayer = Ream.PLUGIN.getServer().getPlayer(args[0]);
                if (onlinePlayer != null) {
                    Ream.teleportRequests.put(playerID, onlinePlayer.getUniqueId());
                    sender.sendMessage(ChatColor.DARK_GREEN + "Teleport request sent, to cancel type /tpc");
                    onlinePlayer.sendMessage(((Player) sender).displayName().toString() + ChatColor.GREEN + " has requested to teleport to you.");
                    return true;
                }
                sender.sendMessage("No player found with name " + args[0] + ".");
            } else {
                sender.sendMessage("You need to specify who you want to teleport to.");
            }
        }
        return true;
    }
}
