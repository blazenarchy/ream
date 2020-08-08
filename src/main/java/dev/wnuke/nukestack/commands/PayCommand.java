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
        if (args.length >= 2) {
            try {
                long amount = Long.parseLong(args[1]);
                if (amount < 0) {
                    sender.sendMessage("You cant send negative money.");
                    return true;
                }
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (player.getName().toLowerCase().equals(args[0].toLowerCase())) {
                        if (sender instanceof ConsoleCommandSender) {
                            UUID playerID = player.getUniqueId();
                            PlayerData receiving = plugin.loadPlayerData(player.getUniqueId());
                            receiving.addTokens(amount);
                            plugin.savePlayerData(playerID, receiving);
                            player.sendMessage("You have received " + amount + " token(s) from Console.");
                            sender.sendMessage(player.getName() + " received " + amount + " token(s).");
                        } else {
                            UUID senderID = ((Player) sender).getUniqueId();
                            PlayerData sending = plugin.loadPlayerData(senderID);
                            long sendingBal = sending.getTokens();
                            if (sendingBal >= amount) {
                                sending.removeTokens(amount);
                                plugin.savePlayerData(senderID, sending);
                                sender.sendMessage("You have sent " + amount + " token(s) to " + player.getName() + ".");
                                if (sending.getTokens() == sendingBal - amount) {
                                    UUID receiverID = player.getUniqueId();
                                    PlayerData receiving = plugin.loadPlayerData(receiverID);
                                    receiving.addTokens(amount);
                                    plugin.savePlayerData(receiverID, receiving);
                                    player.sendMessage("You have received " + amount + " token(s) from " + sender.getName() + ".");
                                }
                                return true;
                            }
                            sender.sendMessage("You do not have " + amount + " token(s).");
                        }
                        return true;
                    }
                }
                sender.sendMessage("No player with name \"" + args[0] + "\".");
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number \"" + args[1] + "\".");
            }
        } else {
            sender.sendMessage("Requires two arguments, the receiver and the amount.");
        }
        return true;
    }
}
