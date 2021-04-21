package net.blazenarchy.ream.commands;

import net.blazenarchy.ream.GeneralUtilities;
import net.blazenarchy.ream.Ream;
import net.blazenarchy.ream.player.PlayerData;
import net.blazenarchy.ream.player.PlayerDataUtilities;
import org.bukkit.ChatColor;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerWeather implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            if (args.length > 0) {
                Player player = ((Player) sender).getPlayer();
                if (player == null) return true;
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
                if (playerData.getTokens() < Ream.playerWeatherCost) {
                    GeneralUtilities.notEnoughTokens(player, Ream.playerWeatherCost);
                    return true;
                }
                player.setPlayerWeather(weatherType);
                playerData.removeTokens(Ream.playerWeatherCost).save();
            } else {
                sender.sendMessage(ChatColor.RED + "Needs a weather argument.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You are console, you must use standard weather.");
        }
        return true;
    }
}
