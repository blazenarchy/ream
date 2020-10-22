package net.willemml.paperstack.commands;

import net.willemml.paperstack.GeneralUtilities;
import net.willemml.paperstack.PaperStack;
import net.willemml.paperstack.player.PlayerData;
import net.willemml.paperstack.player.PlayerDataUtilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hat implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            Player player = ((Player) sender).getPlayer();
            if (player == null) return false;
            PlayerData playerData = PlayerDataUtilities.loadPlayerData(player);
            if (playerData.getTokens() < PaperStack.hatCost) {
                GeneralUtilities.notEnoughTokens(player, PaperStack.hatCost);
                return true;
            }
            ItemStack heldItem = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
            if (heldItem == null) {
                player.sendMessage(ChatColor.RED + "You must be holding an item.");
                return false;
            }
            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), null);
            player.getInventory().setHelmet(heldItem);
            playerData.removeTokens(PaperStack.hatCost).save();
        } else {
            sender.sendMessage("You are console, you don't have a head.");
        }
        return true;
    }
}
