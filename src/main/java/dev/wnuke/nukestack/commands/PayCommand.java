package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PayCommand implements CommandExecutor {
    NukeStack plugin;

    public PayCommand(NukeStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("nukestack.pay")) {
            if (args.length >= 2) {
                try {
                    int amount = Integer.parseInt(args[1]);
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        if (player.getName().toLowerCase().equals(args[0].toLowerCase())) {
                            if (sender instanceof ConsoleCommandSender) {
                                UUID playerID = player.getUniqueId();
                                PlayerData receiving = plugin.loadPlayerData(player.getUniqueId());
                                receiving.addTokens(amount);
                                plugin.savePlayerData(playerID, receiving);
                                player.sendMessage("You have received " + amount + " token(s) from Console.");
                                sender.sendMessage(player.getName() + "received " + amount + " token(s).");
                            } else {
                                UUID senderID = ((Player) sender).getUniqueId();
                                UUID receiverID = player.getUniqueId();
                                PlayerData sending = plugin.loadPlayerData(senderID);
                                if (sending.getTokens() >= amount) {
                                    PlayerData receiving = plugin.loadPlayerData(senderID);
                                    receiving.addTokens(amount);
                                    sending.removeTokens(amount);
                                    plugin.savePlayerData(senderID, sending);
                                    plugin.savePlayerData(receiverID, receiving);
                                    player.sendMessage("You have received " + amount + " token(s) from " + sender.getName() + ".");
                                    sender.sendMessage("You have sent " + amount + " token(s) to " + player.getName() + ".");
                                }
                                sender.sendMessage("You do not have " + amount + " token(s).");
                                return true;
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("Invalid number \"" + args[1] + "\".");
                }
            } else {
                sender.sendMessage("Requires two arguments, the receiver and the amount.");
            }
        } else {
            return false;
        }
        return true;
    }
}
