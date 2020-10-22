package net.willemml.paperstack.commands;

import net.willemml.paperstack.GeneralUtilities;
import net.willemml.paperstack.PaperStack;
import net.willemml.paperstack.player.PlayerData;
import net.willemml.paperstack.player.PlayerDataUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class Suicide implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            Player player = ((Player) sender).getPlayer();
            if (player == null) return false;
            PlayerData playerData = PlayerDataUtilities.loadPlayerData(player);
            if (playerData.getTokens() < PaperStack.suicideCost) {
                GeneralUtilities.notEnoughTokens(player, PaperStack.suicideCost);
                return true;
            }
            EntityDamageEvent damageEvent = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.SUICIDE, Float.MAX_VALUE);
            PaperStack.PLUGIN.getServer().getPluginManager().callEvent(damageEvent);
            damageEvent.getEntity().setLastDamageCause(damageEvent);
            player.setHealth(0);
            playerData.removeTokens(PaperStack.suicideCost).save();
        } else {
            sender.sendMessage("You are console, you can't kill yourself.");
        }
        return true;
    }
}
