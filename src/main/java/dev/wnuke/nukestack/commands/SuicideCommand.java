package dev.wnuke.nukestack.commands;

import dev.wnuke.nukestack.NukeStack;
import dev.wnuke.nukestack.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

public class SuicideCommand implements CommandExecutor {
    NukeStack plugin;

    public SuicideCommand(NukeStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            Player player = ((Player) sender).getPlayer();
            if (player == null) return false;
            UUID playerID = player.getUniqueId();
            PlayerData playerData = plugin.loadPlayerData(playerID);
            if (playerData.getTokens() < NukeStack.suicideCost) {
                player.sendMessage("You do not have enough tokens, you need at least " + NukeStack.suicideCost + ".");
                return true;
            }
            EntityDamageEvent damageEvent = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.SUICIDE, Float.MAX_VALUE);
            plugin.getServer().getPluginManager().callEvent(damageEvent);
            damageEvent.getEntity().setLastDamageCause(damageEvent);
            player.setHealth(0);
            playerData.removeTokens(NukeStack.suicideCost);
            plugin.savePlayerData(playerID, playerData);
        } else {
            sender.sendMessage("You are console, you can't kill yourself.");
        }
        return true;
    }
}
