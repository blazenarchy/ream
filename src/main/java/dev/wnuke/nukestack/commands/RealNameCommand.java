package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.PlayerDataUtilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RealNameCommand implements CommandExecutor {
    NukeStack plugin;

    public RealNameCommand(NukeStack plugin) {
        this.plugin = plugin;
    }

    private HashMap<String, String> getRealNames() {
        HashMap<String, String> nickNames = new HashMap<>();
        String playerNick;
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            playerNick = PlayerDataUtilities.loadPlayerData(player).getNickName();
            if (!playerNick.isEmpty()) {
                nickNames.put(playerNick, player.getName());
            }
        }
        return nickNames;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        HashMap<String, String> nickNames = getRealNames();
        if (args.length > 0) {
            String realName = null;
            for (Map.Entry<String, String> nick : nickNames.entrySet()) {
                if (NickCommand.unFormatNick(nick.getKey()).equals(NickCommand.unFormatNick(args[0]))) {
                    realName = nick.getValue();
                    break;
                }
            }
            if (realName == null) {
                sender.sendMessage(ChatColor.RED + "No online players with name \"" + args[0] + "\".");
            } else {
                sender.sendMessage(args[0] + " is " + realName + ".");
            }
        } else {
            sender.sendMessage(ChatColor.YELLOW + "List of nicknames and who they are:");
            for (Map.Entry<String, String> nick : nickNames.entrySet()) {
                sender.sendMessage("  " + nick.getKey() + ChatColor.YELLOW + " is " + ChatColor.RESET + nick.getValue());
            }
        }
        return true;
    }
}

