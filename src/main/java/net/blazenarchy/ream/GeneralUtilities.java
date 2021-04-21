package net.blazenarchy.ream;

import net.blazenarchy.ream.permissions.PermissionsUtility;
import net.blazenarchy.ream.player.LastLocation;
import net.blazenarchy.ream.player.PlayerData;
import net.blazenarchy.ream.player.PlayerDataUtilities;
import net.kyori.adventure.text.Component;
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

    public static String parsePlaceholders(Player player, String string) {
        String prefix = PlayerDataUtilities.loadPlayerData(player).getGroup().getPrefix().replaceAll("&(?=[0-9]|[a-f]|[k-o]|r)", "§").replace("%", "%%");
        String displayName = player.displayName().toString().replace("%", "%%");
        String name = player.getName().replace("%", "%%");
        return string.replaceAll("&(?=[0-9]|[a-f]|[k-o]|r)", "§")
                .replace("%prefix%", prefix)
                .replace("%name%", name)
                .replace("%display_name%", displayName);
    }

    public static void teleportPlayer(Player player, Location destination) {
        GeneralUtilities.hidePlayer(player);
        Ream.playerPosTracking.remove(player.getUniqueId());
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
        Ream.playerPosTracking.remove(player.getUniqueId());
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
                Ream.messageReply.remove(sender.getUniqueId());
                Ream.messageReply.put(sender.getUniqueId(), receiver.getUniqueId());
                Ream.messageReply.remove(receiver.getUniqueId());
                Ream.messageReply.put(receiver.getUniqueId(), sender.getUniqueId());
                sender.sendMessage(ChatColor.GRAY + "you -> " + ChatColor.RESET + receiver.displayName().toString() + ChatColor.GRAY + ": " + ChatColor.RESET + message);
                receiver.sendMessage(sender.displayName().toString() + ChatColor.GRAY + " -> " + "you: " + ChatColor.RESET + message);
            }
            return;
        }
        sender.sendMessage(ChatColor.RED + receiver.displayName().toString() + " is ignoring you.");
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
        checkForIllegals(player.getInventory(), !player.hasPermission("ream.illegal"), !player.hasPermission("ream.overstack"), false, player.getWorld(), player.getLocation());
    }

    public static void checkForIllegals(Inventory inventory, boolean removeIllegals, boolean unstackOverStacked, boolean dupe, @Nullable World world, @Nullable Location location) {
        if (dupe || (removeIllegals && Ream.deleteItems) || (unstackOverStacked && Ream.unstackItems)) {
            for (ItemStack itemStack : inventory) {
                if (itemStack != null) {
                    if (removeIllegals && Ream.deleteItems) {
                        if (Ream.DELETE.contains(itemStack.getType())) {
                            inventory.remove(itemStack);
                        }
                    }
                    if (unstackOverStacked && Ream.unstackItems) {
                        if (Ream.NO_STACK.contains(itemStack.getType())) {
                            int maxStack = itemStack.getMaxStackSize();
                            if (itemStack.getAmount() > maxStack) itemStack.setAmount(maxStack);
                        }
                    }
                    if (dupe) {
                        if (world != null && location != null) {
                            if (!Ream.NO_DUPE.contains(itemStack.getType())) {
                                world.dropItemNaturally(location, itemStack);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void performLogout(Player player) {
        Ream.messageReply.remove(player.getUniqueId());
        Ream.teleportRequests.remove(player.getUniqueId());
        Ream.playerPosTracking.remove(player.getUniqueId());
        checkForIllegals(player);
        PlayerDataUtilities.loadPlayerData(player).setLogoutLocation(LastLocation.fromLocation(player.getLocation())).save();
        if (Ream.permissions) {
            PermissionAttachment attachment = PermissionsUtility.permissionsMap.get(player.getUniqueId());
            if (attachment != null) attachment.remove();
            PermissionsUtility.permissionsMap.remove(player.getUniqueId());
        }
    }

    public static void setPlayerList(Player player) {
        player.sendPlayerListHeaderAndFooter(Component.text(parsePlaceholders(player, Ream.playerListHeader)), Component.text(parsePlaceholders(player, Ream.playerListFooter)));
        player.playerListName(Component.text(parsePlaceholders(player, Ream.nameFormat)));
    }

    public static void performLogin(Player player) {
        if (Ream.deleteOversizedItems) {
            cleanInventory(player.getInventory());
        }
        checkForIllegals(player);
        PlayerData playerData = PlayerDataUtilities.loadPlayerData(player);
        if (Ream.permissions) playerData.loadPermissions();
        if (Ream.loginTeleport) {
            Location logSpot = playerData.getLogoutLocation(Ream.PLUGIN.getServer());
            if (logSpot != null) {
                teleportPlayer(player, logSpot);
            }
        }
    }

    public static void hidePlayer(Player player) {
        for (Player other : Ream.PLUGIN.getServer().getOnlinePlayers()) {
            other.hidePlayer(Ream.PLUGIN, player);
        }
    }

    public static void unhidePlayer(Player player) {
        for (Player other : Ream.PLUGIN.getServer().getOnlinePlayers()) {
            other.showPlayer(Ream.PLUGIN, player);
        }
    }
}
