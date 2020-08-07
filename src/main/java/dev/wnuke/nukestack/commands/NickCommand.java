package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NickCommand implements CommandExecutor {
    NukeStack plugin;

    public NickCommand(NukeStack plugin) {
        this.plugin = plugin;
    }

    private boolean checkNickused(String nickToCheck) {
        for (PlayerData playerData : plugin.loadAllPlayerData()) {
            if (playerData.getNickName().equals(nickToCheck)) {
                return true;
            }
        }
        return false;
    }

    private String formatNick(String unformated) {
        return "";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("nukestack.nick")) {
            if (!(sender instanceof ConsoleCommandSender)) {
                Player player = (Player) sender;
                if (args.length >= 1) {

                } else {
                    player.setDisplayName(player.getName());
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
