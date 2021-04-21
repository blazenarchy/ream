package net.blazenarchy.ream.commands;

import net.blazenarchy.ream.Ream;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeleportNo implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            if (args.length > 0) {
                Player player = Ream.PLUGIN.getServer().getPlayer(args[0]);
                if (player != null) {
                    if (Ream.teleportRequests.get(player.getUniqueId()) == ((Player) sender).getUniqueId()) {
                        player.sendMessage(ChatColor.RED + "Teleport denied.");
                        sender.sendMessage(ChatColor.GREEN + "Teleport denied.");
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.RED + "No player with name " + args[0] + " has requested to teleport to you.");
            } else {
                sender.sendMessage(ChatColor.RED + "You need to specify who you want to deny.");
            }
        }
        return true;
    }
}
