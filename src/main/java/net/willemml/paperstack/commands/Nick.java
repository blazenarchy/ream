package net.willemml.paperstack.commands;

import net.willemml.paperstack.GeneralUtilities;
import net.willemml.paperstack.PaperStack;
import net.willemml.paperstack.player.PlayerData;
import net.willemml.paperstack.player.PlayerDataUtilities;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Nick implements CommandExecutor {
    public static String unFormatNick(String nick) {
        if (nick != null) {
            if (!nick.equals("")) {
                return nick.replaceAll("§([0-9]|[a-f]|r)", "").toLowerCase();
            }
        }
        return null;
    }

    private boolean nickUsed(String nickToCheck) {
        String noFormatNick = unFormatNick(nickToCheck);
        for (String bannedNick : PaperStack.BLOCKED_NICK) {
            if (noFormatNick.contains(bannedNick)) {
                return true;
            }
        }
        for (PlayerData playerData : PlayerDataUtilities.loadAllPlayerData()) {
            String noFormatName = unFormatNick(playerData.getNickName());
            if (noFormatName != null) {
                if (noFormatName.equals(noFormatNick)) {
                    return true;
                }
            }
        }
        for (Player player : PaperStack.PLUGIN.getServer().getOnlinePlayers()) {
            List<String> names = Arrays.asList(player.getCustomName(), player.getDisplayName(), player.getName(), player.getPlayerListName());
            for (String name : names) {
                String noFormatName = unFormatNick(name);
                if (noFormatName != null) {
                    if (noFormatName.contains(noFormatNick) || noFormatNick.contains(noFormatName)) {
                        return true;
                    }
                }
            }
        }
        for (OfflinePlayer player : PaperStack.PLUGIN.getServer().getOfflinePlayers()) {
            String playerName = player.getName();
            if (playerName != null) {
                String noFormatName = unFormatNick(playerName);
                if (noFormatName != null) {
                    if (noFormatName.contains(noFormatNick) || noFormatNick.contains(noFormatName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String formatNick(String unformated) {
        return PaperStack.nickPrefix + unformated.replaceAll("&(?=[0-9]|[a-f]|r)", "§") + "§r";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            Player player = ((Player) sender).getPlayer();
            if (player == null) return false;
            PlayerData playerData = PlayerDataUtilities.loadPlayerData(player);
            if (args.length >= 1) {
                if (playerData.getTokens() < PaperStack.nickCost) {
                    GeneralUtilities.notEnoughTokens(player, PaperStack.nickCost);
                    return true;
                }
                String nick = formatNick(args[0]);
                if (!nickUsed(nick)) {
                    player.setDisplayName(nick);
                    playerData.setNickName(nick).removeTokens(PaperStack.tpaCost).save();
                    player.sendMessage(ChatColor.GREEN + "Your new nickname is \"" + nick + "\".");
                } else {
                    player.sendMessage(ChatColor.RED + "That name is already in use or is not allowed.");
                }
            } else {
                player.setDisplayName(player.getName());
                playerData.setNickName("").save();
                sender.sendMessage(ChatColor.GREEN + "Your nick name has been removed.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Consoles cannot nick themselves.");
        }
        return true;
    }
}

