package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.GeneralUtilities;
import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.PlayerData;
import dev.wnuke.nukestack.PlayerDataUtilities;
import org.bukkit.ChatColor;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerWeatherCommand implements CommandExecutor {
    NukeStack plugin;

    public PlayerWeatherCommand(NukeStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            if (args.length > 0) {
                Player player = ((Player) sender).getPlayer();
                if (player == null) return false;
                WeatherType weatherType;
                if (args[0].toLowerCase().equals("clear")) {
                    weatherType = WeatherType.CLEAR;
                } else if (args[0].toLowerCase().equals("rain") || args[0].toLowerCase().equals("snow")) {
                    weatherType = WeatherType.DOWNFALL;
                } else {
                    player.sendMessage(ChatColor.RED + "Valid weather arguments are \"clear\", \"rain\" or \"snow\".");
                    return true;
                }
                PlayerData playerData = PlayerDataUtilities.loadPlayerData(player);
                if (playerData.getTokens() < NukeStack.playerWeatherCost) {
                    GeneralUtilities.notEnoughTokens(player, NukeStack.playerWeatherCost);
                    return true;
                }
                player.setPlayerWeather(weatherType);
                playerData.removeTokens(NukeStack.playerWeatherCost).save();
            } else {
                sender.sendMessage(ChatColor.RED + "Needs a weather argument.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You are console, you must use standard time.");
        }
        return true;
    }
}
