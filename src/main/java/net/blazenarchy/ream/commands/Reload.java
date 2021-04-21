package net.blazenarchy.ream.commands;

import net.blazenarchy.ream.Ream;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Reload implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        sender.sendMessage("Reloading Ream...");
        Ream.PLUGIN.loadAndSetConfig();
        sender.sendMessage("Ream reloaded.");
        return true;
    }
}
