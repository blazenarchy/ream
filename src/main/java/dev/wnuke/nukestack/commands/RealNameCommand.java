package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RealNameCommand implements CommandExecutor {
    NukeStack plugin;

    public RealNameCommand(NukeStack plugin) {
        this.plugin = plugin;
    }

    private String getRealName(String nick) {
        String noFormatNick = NickCommand.unFormatNick(nick);
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (NickCommand.unFormatNick(plugin.loadPlayerData(player.getUniqueId()).getNickName()).equals(noFormatNick)) {
                return player.getName();
            }
        }
        return "";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            String getResult = getRealName(args[0]);
            if (getResult.equals("")) {
                sender.sendMessage("No online players with name \"" + args[0] + "\".");
            } else {
                sender.sendMessage(args[0] + " is " + getResult + ".");
            }
        }
        return true;
    }
}

