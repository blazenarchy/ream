package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.GeneralUtilities;
import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.permissions.PermissionsUtility;
import dev.wnuke.nukestack.player.PlayerData;
import dev.wnuke.nukestack.player.PlayerDataUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Groups implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 2) {
            Player player = NukeStack.PLUGIN.getServer().getPlayer(args[0]);
            if (player == null) return false;
            PlayerData playerData = PlayerDataUtilities.loadPlayerData(player);
            boolean success = false;
            switch (args[1]) {
                case "set":
                    playerData.setGroup(args[2]).save();
                    success = true;
                    break;
                case "promote":
                    playerData.promote(args[2]).save();
                    success = true;
                    break;
                case "demote":
                    playerData.demote(args[2]).save();
                    success = true;
                    break;
            }
            if (success) {
                GeneralUtilities.performLogout(player);
                GeneralUtilities.performLogin(player);
                return true;
            }
        }
        return false;
    }
}
