package net.blazenarchy.ream.commands;

import net.blazenarchy.ream.player.PlayerDataUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Balance implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            Player player = ((Player) sender).getPlayer();
            if (player == null) return false;
            sender.sendMessage("You have " + PlayerDataUtilities.loadPlayerData(player).getTokens() + " token(s).");
        } else {
            sender.sendMessage("You have infinite tokens.");
        }
        return true;
    }
}
