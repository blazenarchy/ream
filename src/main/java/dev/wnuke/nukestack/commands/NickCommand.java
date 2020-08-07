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

    private boolean nickUsed(String nickToCheck) {
        for (PlayerData playerData : plugin.loadAllPlayerData()) {
            if (playerData.getNickName().equals(nickToCheck)) {
                return true;
            }
        }
        return false;
    }

    private String formatNick(String unformated) {
        return NukeStack.nickPrefix + unformated.replaceAll("&(?=[0-9]|[a-f])", "§") + "§r";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            Player player = (Player) sender;
            UUID playerID = player.getUniqueId();
            PlayerData playerData = plugin.loadPlayerData(playerID);
            if (playerData.getTokens() < NukeStack.tpaCost) {
                player.sendMessage("You do not have enough tokens, you need at least " + NukeStack.tpaCost + ".");
                return true;
            }
            if (args.length >= 1) {
                String nick = formatNick(args[0]);
                if (!nickUsed(nick)) {
                    player.setDisplayName(nick);
                    playerData.setNickName(nick);
                    playerData.removeTokens(NukeStack.tpaCost);
                    plugin.savePlayerData(playerID, playerData);
                }
            } else {
                player.setDisplayName(player.getName());
            }
        } else {
            sender.sendMessage("Consoles cannot nick themselves.");
        }
        return true;
    }
}

