package net.blazenarchy.ream.commands;

import net.blazenarchy.ream.GeneralUtilities;
import net.blazenarchy.ream.Ream;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Reply implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player player = ((Player) sender).getPlayer();
        if (player == null) return false;
        UUID receiverID = Ream.messageReply.get(player.getUniqueId());
        if (receiverID != null) {
            Player receiver = Ream.PLUGIN.getServer().getPlayer(receiverID);
            if (receiver != null) {
                GeneralUtilities.sendPrivateMessage(player, receiver, args);
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "You are not in a messaging session with an online player.");
        return true;
    }
}

