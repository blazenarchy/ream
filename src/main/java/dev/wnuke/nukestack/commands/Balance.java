package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.player.PlayerDataUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Balance implements CommandExecutor {
    NukeStack plugin;

    public Balance(NukeStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
