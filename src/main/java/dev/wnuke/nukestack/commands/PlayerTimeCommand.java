package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.PlayerData;
import dev.wnuke.nukestack.PlayerDataUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerTimeCommand implements CommandExecutor {
    NukeStack plugin;

    public PlayerTimeCommand(NukeStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            if (args.length > 0) {
                long time = 0;
                try {
                    time = Long.parseLong(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("Invalid time argument, must be a number.");
                    return true;
                }
                Player player = ((Player) sender).getPlayer();
                if (player == null) return false;
                UUID playerID = player.getUniqueId();
                PlayerData playerData = PlayerDataUtilities.loadPlayerData(playerID);
                if (playerData.getTokens() < NukeStack.playerTimeCost) {
                    player.sendMessage("You do not have enough tokens, you need at least " + NukeStack.playerTimeCost + ".");
                    return true;
                }
                player.setPlayerTime(time, false);
                playerData.removeTokens(NukeStack.playerTimeCost);
                PlayerDataUtilities.savePlayerData(playerID, playerData);
            } else {
                sender.sendMessage("Needs a time argument.");
            }
        } else {
            sender.sendMessage("You are console, you must use standard time.");
        }
        return true;
    }
}
