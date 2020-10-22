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

public class PlayerTime implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
                if (playerData.getTokens() < PaperStack.playerTimeCost) {
                    GeneralUtilities.notEnoughTokens(player, PaperStack.playerTimeCost);
                    return true;
                }
                player.setPlayerTime(time, false);
                playerData.removeTokens(PaperStack.playerTimeCost).save();
            } else {
                sender.sendMessage("Needs a time argument.");
            }
        } else {
            sender.sendMessage("You are console, you must use standard time.");
        }
        return true;
    }
}
