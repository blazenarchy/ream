package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class SeenCommand implements CommandExecutor {
    NukeStack plugin;

    public SeenCommand(NukeStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            UUID playerID = plugin.getServer().getPlayerUniqueId(args[0]);
            if (playerID != null) {
                OfflinePlayer player = plugin.getServer().getOfflinePlayer(playerID);
                Date joinDate = new Date(player.getFirstPlayed());
                Date seenDate = new Date(player.getLastSeen());
                DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
                dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                timeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                sender.sendMessage(ChatColor.GOLD + player.getName() + " joined on "
                        + dateFormatter.format(joinDate) + " at "
                        + timeFormatter.format(joinDate) + " and was last seen on "
                        + dateFormatter.format(seenDate) + " at "
                        + timeFormatter.format(seenDate) + ".");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must specify who's join date you want to see.");
        }
        return true;
    }
}

