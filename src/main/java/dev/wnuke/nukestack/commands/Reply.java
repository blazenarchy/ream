package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.GeneralUtilities;
import dev.wnuke.nukestack.NukeStack;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Reply implements CommandExecutor {
    NukeStack plugin;

    public Reply(NukeStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = ((Player) sender).getPlayer();
        if (player == null) return false;
        UUID receiverID = NukeStack.messageReply.get(player.getUniqueId());
        if (receiverID != null) {
            Player receiver = plugin.getServer().getPlayer(receiverID);
            if (receiver != null) {
                GeneralUtilities.sendPrivateMessage(player, receiver, args);
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "You are not in a messaging session with an online player.");
        return true;
    }
}

