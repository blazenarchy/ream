package net.blazenarchy.ream.commands;

import net.blazenarchy.ream.GeneralUtilities;
import net.blazenarchy.ream.Ream;
import net.blazenarchy.ream.player.PlayerData;
import net.blazenarchy.ream.player.PlayerDataUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Groups implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 2) {
            Player player = Ream.PLUGIN.getServer().getPlayer(args[0]);
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
