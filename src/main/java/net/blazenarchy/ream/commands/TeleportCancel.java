package net.blazenarchy.ream.commands;

import net.blazenarchy.ream.Ream;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeleportCancel implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            Ream.teleportRequests.remove(((Player) sender).getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Teleport request cancelled.");
        }
        return true;
    }
}
