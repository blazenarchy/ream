package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.GeneralUtilities;
import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.player.PlayerData;
import dev.wnuke.nukestack.player.PlayerDataUtilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Dupe implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            Player player = ((Player) sender).getPlayer();
            if (player == null) return false;
            PlayerData playerData = PlayerDataUtilities.loadPlayerData(player);
            if (playerData.getTokens() < NukeStack.dupeCost) {
                GeneralUtilities.notEnoughTokens(player, NukeStack.dupeCost);
                return true;
            }
            Inventory inventory;
            if (player.getVehicle() instanceof Donkey) {
                inventory = ((Donkey) player.getVehicle()).getInventory();
            } else if (player.getVehicle() instanceof Llama) {
                inventory = ((Llama) player.getVehicle()).getInventory();
            } else {
                player.sendMessage(ChatColor.RED + "You must be riding a donkey or a llama to use this command.");
                return true;
            }
            playerData.removeTokens(NukeStack.dupeCost).increaseLifeTimeDupes().save();
            GeneralUtilities.checkForIllegals(inventory, !player.hasPermission("simpledupe.illegal"), !player.hasPermission("simpledupe.overstack"), true, player.getWorld(), player.getLocation());
            player.sendMessage(ChatColor.GREEN + "Your items have been duplicated.");
        } else {
            sender.sendMessage(ChatColor.RED + "Cannot dupe as console.");
        }
        return true;
    }
}
