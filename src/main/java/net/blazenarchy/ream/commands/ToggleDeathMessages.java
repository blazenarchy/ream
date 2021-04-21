package net.blazenarchy.ream.commands;

import net.blazenarchy.ream.player.PlayerData;
import net.blazenarchy.ream.player.PlayerDataUtilities;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ToggleDeathMessages implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        PlayerData playerData = PlayerDataUtilities.loadPlayerData((OfflinePlayer) sender).toggleDeathMessages().save();
        if (playerData.deathMessages()) {
            sender.sendMessage(ChatColor.GREEN + "You are now receiving death messages.");
        } else {
            sender.sendMessage(ChatColor.GREEN + "You are no longer receiving death messages.");
        }
        return true;
    }
}
