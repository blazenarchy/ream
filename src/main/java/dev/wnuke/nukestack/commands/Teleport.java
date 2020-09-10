package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.GeneralUtilities;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Teleport implements CommandExecutor {
    public static Location parseLocation(String x, String y, String z, World world) {
        try {
            return new Location(world, Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("Console cannot teleport.");
            return true;
        }
        Player player = ((Player) sender).getPlayer();
        if (player == null) return true;
        if (args.length == 1) {
            Player destinationPlayer = player.getServer().getPlayer(args[0]);
            if (destinationPlayer != null) {
                player.sendMessage(ChatColor.GREEN + "Teleporting...");
                GeneralUtilities.teleportPlayer(player, destinationPlayer.getLocation());
            }
        } else if (args.length == 2) {
            Player playerOne = player.getServer().getPlayer(args[0]);
            Player playerTwo = player.getServer().getPlayer(args[1]);
            if (playerOne != null) {
                if (playerTwo != null) {
                    player.sendMessage(ChatColor.GREEN + "Teleporting " + playerOne.getName() + " to " + playerTwo.getName());
                    GeneralUtilities.teleportPlayer(playerOne, playerTwo.getLocation());
                } else sender.sendMessage(ChatColor.RED + "No player online with name " + args[1]);
            } else sender.sendMessage(ChatColor.RED + "No player online with name " + args[0]);
            return true;
        } else if (args.length == 3) {
            Location destination = parseLocation(args[0], args[1], args[2], player.getWorld());
            if (destination != null) {
                player.sendMessage(ChatColor.GREEN + "Teleporting...");
                GeneralUtilities.teleportPlayer(player, destination);
            }
        } else if (args.length == 4) {
            Player playerOne = player.getServer().getPlayer(args[0]);
            if (playerOne != null) {
                Location destination = parseLocation(args[0], args[1], args[2], player.getWorld());
                if (destination != null) {
                    player.sendMessage(ChatColor.GREEN + "Teleporting " + playerOne.getName() + " to " + args[0] + " " + args[1] + " " + args[2]);
                    GeneralUtilities.teleportPlayer(playerOne, destination);
                }
            } else sender.sendMessage(ChatColor.RED + "No player online with name " + args[0]);
        }

        return false;
    }
}
