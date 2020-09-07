package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.player.PlayerData;
import dev.wnuke.nukestack.player.PlayerDataUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Groups implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 1) {
            Player player = NukeStack.PLUGIN.getServer().getPlayer(args[1]);
            if (player == null) return false;
            PlayerData playerData = PlayerDataUtilities.loadPlayerData(player);
            if (args[0].equals("set") && args.length > 2) {
                playerData.setGroup(args[2]);
            }
            playerData.save();
        }
        return false;
    }
}
