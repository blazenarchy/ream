package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.player.PlayerData;
import dev.wnuke.nukestack.player.PlayerDataUtilities;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class Info implements CommandExecutor {
    NukeStack plugin;

    public Info(NukeStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            UUID playerID = plugin.getServer().getPlayerUniqueId(args[0]);
            if (playerID != null) {
                OfflinePlayer player = plugin.getServer().getOfflinePlayer(playerID);
                int kills = player.getStatistic(Statistic.PLAYER_KILLS);
                int deaths = player.getStatistic(Statistic.DEATHS);
                float killDeathRatio = 0;
                if (deaths != 0) killDeathRatio = kills / deaths;
                Date joinDate = new Date(player.getFirstPlayed());
                Date seenDate = new Date(player.getLastSeen());
                PlayerData playerData = PlayerDataUtilities.loadPlayerData(player);
                DateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                sender.sendMessage(player.getName() + ChatColor.GREEN + " info:\n"
                        + ChatColor.DARK_GREEN + "Joined" + ChatColor.WHITE + ": " + formatter.format(joinDate) + "\n"
                        + ChatColor.DARK_GREEN + "Last Seen" + ChatColor.WHITE + ": " + formatter.format(seenDate) + "\n\n"
                        + ChatColor.DARK_GREEN + "Kills" + ChatColor.WHITE + ": " + kills + "\n"
                        + ChatColor.DARK_GREEN + "Deaths" + ChatColor.WHITE + ": " + deaths + "\n"
                        + ChatColor.DARK_GREEN + "K/D Ratio" + ChatColor.WHITE + ": " + killDeathRatio + "\n"
                        + ChatColor.DARK_GREEN + "Kill Streak" + ChatColor.WHITE + ": " + playerData.getStreak() + "\n\n"
                        + ChatColor.DARK_GREEN + "Tokens" + ChatColor.WHITE + ": " + playerData.getTokens() + "\n"
                        + ChatColor.DARK_GREEN + "Nick Name" + ChatColor.WHITE + ": " + playerData.getNickName() + "\n");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must specify who's join date you want to see.");
        }
        return true;
    }
}

