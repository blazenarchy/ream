package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.PlayerData;
import dev.wnuke.nukestack.PlayerDataUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DupeCommand implements CommandExecutor {
    NukeStack plugin;

    public DupeCommand(NukeStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            Player player = (Player) sender;
            PlayerData playerData = PlayerDataUtilities.loadPlayerData(player.getUniqueId());
            if (playerData.getTokens() < NukeStack.dupeCost) {
                player.sendMessage("You do not have enough tokens, you need at least " + NukeStack.dupeCost + ".");
                return true;
            }
            Inventory inventory;
            if (player.getVehicle() instanceof Donkey) {
                inventory = ((Donkey) player.getVehicle()).getInventory();
            } else if (player.getVehicle() instanceof Llama) {
                inventory = ((Llama) player.getVehicle()).getInventory();
            } else {
                player.sendMessage("You must be riding a donkey or a llama to use this command.");
                return true;
            }
            playerData.removeTokens(NukeStack.dupeCost);
            NukeStack.UTILITIES.checkForIllegals(inventory, !player.hasPermission("simpledupe.illegal"), !player.hasPermission("simpledupe.overstack"), true, player.getWorld(), player.getLocation());
            playerData.increaseLifeTimeDupes();
            PlayerDataUtilities.savePlayerData(player.getUniqueId(), playerData);
            player.sendMessage("Your items have been duplicated.");
        } else {
            sender.sendMessage("Cannot dupe as console.");
        }
        return true;
    }
}
