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
import org.jetbrains.annotations.NotNull;

public class PlayerTime implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            if (args.length > 0) {
                long time;
                try {
                    time = Long.parseLong(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid time argument, must be a number.");
                    return true;
                }
                Player player = ((Player) sender).getPlayer();
                if (player == null) return false;
                PlayerData playerData = PlayerDataUtilities.loadPlayerData(player);
                if (playerData.getTokens() < Ream.playerTimeCost) {
                    GeneralUtilities.notEnoughTokens(player, Ream.playerTimeCost);
                    return true;
                }
                player.setPlayerTime(time, false);
                playerData.removeTokens(Ream.playerTimeCost).save();
            } else {
                sender.sendMessage("Needs a time argument.");
            }
        } else {
            sender.sendMessage("You are console, you must use standard time.");
        }
        return true;
    }
}
