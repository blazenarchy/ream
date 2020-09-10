package dev.wnuke.nukestack;

import dev.wnuke.nukestack.permissions.PermissionsUtility;
import dev.wnuke.nukestack.player.LastLocation;
import dev.wnuke.nukestack.player.PlayerData;
import dev.wnuke.nukestack.player.PlayerDataUtilities;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;

import javax.annotation.Nullable;
import java.util.Random;

public class GeneralUtilities {
    private static final int maxPacketSize = 2048000;

    public static void teleportPlayer(Player player, Location destination) {
        GeneralUtilities.hidePlayer(player);
        NukeStack.playerPosTracking.remove(player.getUniqueId());
        PlayerDataUtilities.loadPlayerData(player).setLogoutLocation(LastLocation.fromLocation(destination)).save();
        double worldBorder = destination.getWorld().getWorldBorder().getSize();
        Random random = new Random();
        while (true) {
            double[] location = {0, 0, 0};
            for (int i = 0; i < location.length; i++) {
                location[i] = worldBorder * random.nextDouble() - worldBorder / 2;
            }
            Location farPlace = new Location(destination.getWorld(), location[0], location[1], location[2]);
            if (farPlace.getNearbyPlayers(8000).isEmpty()) {
                player.teleport(farPlace);
                break;
            }
        }
        player.teleport(destination);
        NukeStack.playerPosTracking.remove(player.getUniqueId());
        GeneralUtilities.unhidePlayer(player);
    }

    public static void sendPrivateMessage(Player sender, Player receiver, String... words) {
        if (!PlayerDataUtilities.loadPlayerData(receiver).hasIgnored(sender.getUniqueId())) {
            StringBuilder message = new StringBuilder();
            for (String word : words) {
                message.append(word).append(" ");
            }
            if (message.length() == 0) sender.sendMessage(ChatColor.RED + "You cannot send an empty message.");
            else {
                NukeStack.messageReply.remove(sender.getUniqueId());
                NukeStack.messageReply.put(sender.getUniqueId(), receiver.getUniqueId());
                NukeStack.messageReply.remove(receiver.getUniqueId());
                NukeStack.messageReply.put(receiver.getUniqueId(), sender.getUniqueId());
                sender.sendMessage(ChatColor.GRAY + "you -> " + ChatColor.RESET + receiver.getDisplayName() + ChatColor.GRAY + ": " + ChatColor.RESET + message);
                receiver.sendMessage(sender.getDisplayName() + ChatColor.GRAY + " -> " + "you: " + ChatColor.RESET + message);
            }
            return;
        }
        sender.sendMessage(ChatColor.RED + receiver.getDisplayName() + " is ignoring you.");
    }

    public static void cleanInventory(Inventory inventory) {
        ItemStack[] inventoryContents = inventory.getContents();
        int maxItemSize = maxPacketSize / inventoryContents.length;
        for (ItemStack item : inventoryContents) {
            if (item == null) continue;
            if (item.serializeAsBytes().length > maxItemSize) {
                inventory.remove(item);
            }
        }
    }

    public static void notEnoughTokens(Player player, long needed) {
        player.sendMessage(ChatColor.RED + "You do not have enough tokens, you need at least " + needed + ".");
    }

    public static void checkForIllegals(Player player) {
        checkForIllegals(player.getInventory(), !player.hasPermission("nukestack.illegal"), !player.hasPermission("nukestack.overstack"), false, player.getWorld(), player.getLocation());
    }

    public static void checkForIllegals(Inventory inventory, boolean removeIllegals, boolean unstackOverStacked, boolean dupe, @Nullable World world, @Nullable Location location) {
        if (dupe || (removeIllegals && NukeStack.deleteItems) || (unstackOverStacked && NukeStack.unstackItems)) {
            for (ItemStack itemStack : inventory) {
                if (itemStack != null) {
                    if (removeIllegals && NukeStack.deleteItems) {
                        if (NukeStack.DELETE.contains(itemStack.getType())) {
                            inventory.remove(itemStack);
                        }
                    }
                    if (unstackOverStacked && NukeStack.unstackItems) {
                        if (NukeStack.NO_STACK.contains(itemStack.getType())) {
                            int maxStack = itemStack.getMaxStackSize();
                            if (itemStack.getAmount() > maxStack) itemStack.setAmount(maxStack);
                        }
                    }
                    if (dupe) {
                        if (world != null && location != null) {
                            if (!NukeStack.NO_DUPE.contains(itemStack.getType())) {
                                world.dropItemNaturally(location, itemStack);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void performLogout(Player player) {
        NukeStack.messageReply.remove(player.getUniqueId());
        NukeStack.teleportRequests.remove(player.getUniqueId());
        NukeStack.playerPosTracking.remove(player.getUniqueId());
        checkForIllegals(player);
        PlayerDataUtilities.loadPlayerData(player).setLogoutLocation(LastLocation.fromLocation(player.getLocation())).save();
        if (NukeStack.permissions) {
            PermissionAttachment attachment = PermissionsUtility.permissionsMap.get(player.getUniqueId());
            if (attachment != null) attachment.remove();
            PermissionsUtility.permissionsMap.remove(player.getUniqueId());
        }
    }

    public static void performLogin(Player player) {
        if (NukeStack.deleteOversizedItems) {
            cleanInventory(player.getInventory());
        }
        checkForIllegals(player);
        PlayerData playerData = PlayerDataUtilities.loadPlayerData(player);
        if (NukeStack.permissions) playerData.loadPermissions();
        if (NukeStack.loginTeleport) {
            Location logSpot = playerData.getLogoutLocation(NukeStack.PLUGIN.getServer());
            if (logSpot != null) {
                teleportPlayer(player, logSpot);
            }
        }
    }

    public static void hidePlayer(Player player) {
        for (Player other : NukeStack.PLUGIN.getServer().getOnlinePlayers()) {
            other.hidePlayer(NukeStack.PLUGIN, player);
        }
    }

    public static void unhidePlayer(Player player) {
        for (Player other : NukeStack.PLUGIN.getServer().getOnlinePlayers()) {
            other.showPlayer(NukeStack.PLUGIN, player);
        }
    }
}
