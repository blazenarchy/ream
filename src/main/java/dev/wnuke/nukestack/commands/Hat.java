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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hat implements CommandExecutor {
    NukeStack plugin;

    public Hat(NukeStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            Player player = ((Player) sender).getPlayer();
            if (player == null) return false;
            PlayerData playerData = PlayerDataUtilities.loadPlayerData(player);
            if (playerData.getTokens() < NukeStack.hatCost) {
                GeneralUtilities.notEnoughTokens(player, NukeStack.hatCost);
                return true;
            }
            ItemStack heldItem = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
            if (heldItem == null) {
                player.sendMessage(ChatColor.RED + "You must be holding an item.");
                return false;
            }
            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), null);
            player.getInventory().setHelmet(heldItem);
            playerData.removeTokens(NukeStack.hatCost).save();
        } else {
            sender.sendMessage("You are console, you don't have a head.");
        }
        return true;
    }
}
