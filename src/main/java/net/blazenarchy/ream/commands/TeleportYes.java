package net.blazenarchy.ream.commands;

import net.blazenarchy.ream.GeneralUtilities;
import net.blazenarchy.ream.Ream;
import net.blazenarchy.ream.player.PlayerData;
import net.blazenarchy.ream.player.PlayerDataUtilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeleportYes implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            if (args.length > 0) {
                Player player = Ream.PLUGIN.getServer().getPlayer(args[0]);
                if (player != null) {
                    if (Ream.teleportRequests.get(player.getUniqueId()) == ((Player) sender).getUniqueId()) {
                        UUID playerID = player.getUniqueId();
                        Ream.teleportRequests.remove(playerID);
                        PlayerData playerData = PlayerDataUtilities.loadPlayerData(player);
                        long tokens = playerData.getTokens();
                        if (tokens < Ream.tpaCost) {
                            player.sendMessage(ChatColor.RED + "Teleport cancelled, you no longer have enough tokens.");
                            sender.sendMessage(ChatColor.RED + player.displayName().toString() + " no longer has enough tokens, teleport cancelled.");
                            return true;
                        }
                        player.sendMessage(ChatColor.GREEN + "Teleport request accepted, teleporting...");
                        sender.sendMessage(ChatColor.GREEN + "Teleporting...");
                        Player destinationPlayer = ((Player) sender).getPlayer();
                        if (destinationPlayer == null) return false;
                        GeneralUtilities.teleportPlayer(player, destinationPlayer.getLocation());
                        playerData.increaseLifeTimeTPs().removeTokens(Ream.tpaCost).save();
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.RED + "No player with name " + args[0] + " has requested to teleport to you.");
            } else {
                sender.sendMessage(ChatColor.RED + "You need to specify who you want to accept.");
            }
        }
        return true;
    }
}
