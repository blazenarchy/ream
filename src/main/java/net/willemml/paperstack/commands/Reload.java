package net.willemml.paperstack.commands;

import net.willemml.paperstack.PaperStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reload implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("Reloading NukeStack...");
        PaperStack.PLUGIN.loadAndSetConfig();
        sender.sendMessage("NukeStack relaoded.");
        return true;
    }
}
