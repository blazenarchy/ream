package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.PlayerData;
import dev.wnuke.nukestack.PlayerDataUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class HatCommand implements CommandExecutor {
    NukeStack plugin;

    public HatCommand(NukeStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            Player player = ((Player) sender).getPlayer();
            if (player == null) return false;
            UUID playerID = player.getUniqueId();
            PlayerData playerData = PlayerDataUtilities.loadPlayerData(playerID);
            if (playerData.getTokens() < NukeStack.hatCost) {
                player.sendMessage("You do not have enough tokens, you need at least " + NukeStack.hatCost + ".");
                return true;
            }
            ItemStack heldItem = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
            if (heldItem == null) {
                player.sendMessage("You must be holding an item.");
                return false;
            }
            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), null);
            player.getInventory().setHelmet(heldItem);
            playerData.removeTokens(NukeStack.hatCost);
            PlayerDataUtilities.savePlayerData(playerID, playerData);
        } else {
            sender.sendMessage("You are console, you don't have a head.");
        }
        return true;
    }
}
