package net.blazenarchy.ream.commands;

import net.blazenarchy.ream.Ream;
import net.blazenarchy.ream.player.PlayerDataUtilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Ignore implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            Player player = ((Player) sender).getPlayer();
            if (player == null) return false;
            UUID uuidToIgnore = Ream.PLUGIN.getServer().getPlayerUniqueId(args[0]);
            if (uuidToIgnore == null) {
                sender.sendMessage(ChatColor.RED + "No player with name " + args[0] + ".");
            } else {
                boolean ignored = PlayerDataUtilities.loadPlayerData(player).toggleIgnore(uuidToIgnore).save().hasIgnored(uuidToIgnore);
                if (ignored) {
                    sender.sendMessage(ChatColor.GREEN + "You are now ignoring " + args[0] + ".");
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + "You no longer ignoring " + args[0] + ".");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must specify who you want to ignore.");
        }
        return true;
    }
}

