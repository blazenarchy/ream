package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.PlayerData;
import dev.wnuke.nukestack.PlayerDataUtilities;
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
                            PlayerData playerData = PlayerDataUtilities.loadPlayerData(playerID);
                            long tokens = playerData.getTokens();
                            if (tokens < NukeStack.tpaCost) {
                                player.sendMessage("Teleport cancelled, you no longer have enough tokens.");
                                sender.sendMessage(player.getPlayerListName() + " no longer has enough tokens, teleport cancelled.");
                                return true;
                            }
                            player.sendMessage("Teleport request accepted, teleporting...");
                            sender.sendMessage("Teleporting...");
                            NukeStack.UTILITIES.hidePlayer(player);
                            NukeStack.playerPosTracking.remove(playerID);
                            player.teleport((Player) sender);
                            NukeStack.playerPosTracking.remove(playerID);
                            for (Player other : plugin.getServer().getOnlinePlayers()) {
                                other.showPlayer(plugin, player);
                            }
                            playerData.increaseLifeTimeTPs();
                            playerData.removeTokens(NukeStack.tpaCost);
                            return true;
                        }
                    }
                }
                sender.sendMessage("No player with name " + args[0] + " has requested to teleport to you.");
            } else {
                sender.sendMessage("You need to specify who you want to accept.");
            }
        }
        return true;
    }
}
