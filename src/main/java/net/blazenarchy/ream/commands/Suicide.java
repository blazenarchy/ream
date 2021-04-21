package net.blazenarchy.ream.commands;

import net.blazenarchy.ream.GeneralUtilities;
import net.blazenarchy.ream.Ream;
import net.blazenarchy.ream.player.PlayerData;
import net.blazenarchy.ream.player.PlayerDataUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class Suicide implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            Player player = ((Player) sender).getPlayer();
            if (player == null) return false;
            PlayerData playerData = PlayerDataUtilities.loadPlayerData(player);
            if (playerData.getTokens() < Ream.suicideCost) {
                GeneralUtilities.notEnoughTokens(player, Ream.suicideCost);
                return true;
            }
            EntityDamageEvent damageEvent = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.SUICIDE, Float.MAX_VALUE);
            Ream.PLUGIN.getServer().getPluginManager().callEvent(damageEvent);
            damageEvent.getEntity().setLastDamageCause(damageEvent);
            player.setHealth(0);
            playerData.removeTokens(Ream.suicideCost).save();
        } else {
            sender.sendMessage("You are console, you can't kill yourself.");
        }
        return true;
    }
}
