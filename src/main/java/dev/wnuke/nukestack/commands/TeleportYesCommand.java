package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

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
                        if (plugin.teleportRequests.get(player.getUniqueId()) == ((Player) sender).getUniqueId()) {
                            PlayerData playerData = plugin.loadPlayerData(player.getUniqueId());
                            long tokens = playerData.getTokens();
                            if (tokens < 2) {
                                player.sendMessage("Teleport cancelled, not enough tokens.");
                                sender.sendMessage(player.getPlayerListName() + " does not have enough tokens, teleport cancelled.");
                                return true;
                            }
                            player.sendMessage("Teleport request accepted, teleporting...");
                            sender.sendMessage("Teleporting...");
                            for (Player other : plugin.getServer().getOnlinePlayers()) {
                                other.hidePlayer(plugin, player);
                            }
                            player.teleport((Player) sender);
                            for (Player other : plugin.getServer().getOnlinePlayers()) {
                                other.showPlayer(plugin, player);
                            }
                            playerData.increaseLifeTimeTPs();
                            playerData.setTokens(tokens - 2);
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
