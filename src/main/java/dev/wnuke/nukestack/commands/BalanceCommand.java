package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.PlayerDataUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {
    NukeStack plugin;

    public BalanceCommand(NukeStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("You have " + PlayerDataUtilities.loadPlayerData(((Player) sender).getUniqueId()).getTokens() + " token(s).");
        } else {
            sender.sendMessage("You have infinite tokens.");
        }
        return true;
    }
}
