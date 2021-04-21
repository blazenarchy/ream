package net.blazenarchy.ream.commands;

import net.blazenarchy.ream.Ream;
import net.blazenarchy.ream.player.PlayerDataUtilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RealName implements CommandExecutor {
    private HashMap<String, String> getRealNames() {
        HashMap<String, String> nickNames = new HashMap<>();
        String playerNick;
        for (Player player : Ream.PLUGIN.getServer().getOnlinePlayers()) {
            playerNick = PlayerDataUtilities.loadPlayerData(player).getNickName();
            if (!playerNick.isEmpty()) {
                nickNames.put(playerNick, player.getName());
            }
        }
        return nickNames;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        HashMap<String, String> nickNames = getRealNames();
        if (args.length > 0) {
            String realName = null;
            for (Map.Entry<String, String> nick : nickNames.entrySet()) {
                if (Nick.unFormatNick(nick.getKey()).equals(Nick.unFormatNick(args[0]))) {
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

