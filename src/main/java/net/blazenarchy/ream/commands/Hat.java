package net.blazenarchy.ream.commands;

import net.blazenarchy.ream.GeneralUtilities;
import net.blazenarchy.ream.Ream;
import net.blazenarchy.ream.player.PlayerData;
import net.blazenarchy.ream.player.PlayerDataUtilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Hat implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            Player player = ((Player) sender).getPlayer();
            if (player == null) return false;
            PlayerData playerData = PlayerDataUtilities.loadPlayerData(player);
            if (playerData.getTokens() < Ream.hatCost) {
                GeneralUtilities.notEnoughTokens(player, Ream.hatCost);
                return true;
            }
            ItemStack heldItem = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
            if (heldItem == null) {
                player.sendMessage(ChatColor.RED + "You must be holding an item.");
                return false;
            }
            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), null);
            player.getInventory().setHelmet(heldItem);
            playerData.removeTokens(Ream.hatCost).save();
        } else {
            sender.sendMessage("You are console, you don't have a head.");
        }
        return true;
    }
}
