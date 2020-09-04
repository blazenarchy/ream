package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.player.PlayerData;
import dev.wnuke.nukestack.player.PlayerDataUtilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Pay implements CommandExecutor {
    NukeStack plugin;

    public Pay(NukeStack plugin) {
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
                            PlayerData receiving = PlayerDataUtilities.loadPlayerData(player);
                            receiving.addTokens(amount).save();
                            player.sendMessage("You have received " + amount + " token(s) from Console.");
                            sender.sendMessage(player.getName() + " received " + amount + " token(s).");
                        } else {
                            Player sendingPlayer = ((Player) sender).getPlayer();
                            if (sendingPlayer == null) return false;
                            PlayerData sending = PlayerDataUtilities.loadPlayerData(sendingPlayer);
                            long sendingBal = sending.getTokens();
                            if (sendingBal >= amount) {
                                sending.removeTokens(amount).save();
                                sender.sendMessage(ChatColor.YELLOW + "You have sent " + amount + " token(s) to " + player.getName() + ".");
                                if (sending.getTokens() == sendingBal - amount) {
                                    PlayerData receiving = PlayerDataUtilities.loadPlayerData(player);
                                    receiving.addTokens(amount).save();
                                    player.sendMessage(ChatColor.GREEN + "You have received " + amount + " token(s) from " + ((Player) sender).getDisplayName() + ".");
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
