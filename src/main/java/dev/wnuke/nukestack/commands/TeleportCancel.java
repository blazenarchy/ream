package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class TeleportCancel implements CommandExecutor {
    NukeStack plugin;

    public TeleportCancel(NukeStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            NukeStack.teleportRequests.remove(((Player) sender).getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Teleport request cancelled.");
        }
        return true;
    }
}