package net.willemml.paperstack.commands;

import net.willemml.paperstack.player.PlayerData;
import net.willemml.paperstack.player.PlayerDataUtilities;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ToggleDeathMessages implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerData playerData = PlayerDataUtilities.loadPlayerData((OfflinePlayer) sender).toggleDeathMessages().save();
        if (playerData.deathMessages()) {
            sender.sendMessage(ChatColor.GREEN + "You are now receiving death messages.");
        } else {
            sender.sendMessage(ChatColor.GREEN + "You are no longer receiving death messages.");
        }
        return true;
    }
}
