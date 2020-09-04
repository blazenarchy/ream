package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.PlayerData;
import dev.wnuke.nukestack.PlayerDataUtilities;
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
                    player.sendMessage("Weather argument must be \"clear\", \"rain\" or \"snow\".");
                    return true;
                }
                UUID playerID = player.getUniqueId();
                PlayerData playerData = PlayerDataUtilities.loadPlayerData(playerID);
                if (playerData.getTokens() < NukeStack.playerWeatherCost) {
                    player.sendMessage("You do not have enough tokens, you need at least " + NukeStack.playerWeatherCost + ".");
                    return true;
                }
                player.setPlayerWeather(weatherType);
                playerData.removeTokens(NukeStack.playerWeatherCost);
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
