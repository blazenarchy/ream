package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.PlayerData;
import dev.wnuke.nukestack.PlayerDataUtilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeleportYesCommand implements CommandExecutor {
    NukeStack plugin;

    public TeleportYesCommand(NukeStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            if (args.length > 0) {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (player.getPlayerListName().equals(args[0])) {
                        if (NukeStack.teleportRequests.get(player.getUniqueId()) == ((Player) sender).getUniqueId()) {
                            UUID playerID = player.getUniqueId();
                            NukeStack.teleportRequests.remove(playerID);
                            PlayerData playerData = PlayerDataUtilities.loadPlayerData(player);
                            long tokens = playerData.getTokens();
                            if (tokens < NukeStack.tpaCost) {
                                player.sendMessage(ChatColor.RED + "Teleport cancelled, you no longer have enough tokens.");
                                sender.sendMessage(ChatColor.RED + player.getPlayerListName() + " no longer has enough tokens, teleport cancelled.");
                                return true;
                            }
                            player.sendMessage(ChatColor.GREEN + "Teleport request accepted, teleporting...");
                            sender.sendMessage(ChatColor.GREEN + "Teleporting...");
                            NukeStack.UTILITIES.hidePlayer(player);
                            NukeStack.playerPosTracking.remove(playerID);
                            Player sendingPlayer = ((Player) sender).getPlayer();
                            if (sendingPlayer == null) return false;
                            player.teleport(sendingPlayer);
                            NukeStack.playerPosTracking.remove(playerID);
                            NukeStack.UTILITIES.unhidePlayer(player);
                            playerData.increaseLifeTimeTPs().removeTokens(NukeStack.tpaCost).save();
                            return true;
                        }
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
