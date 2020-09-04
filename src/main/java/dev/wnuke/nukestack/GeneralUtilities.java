package dev.wnuke.nukestack;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;

public class GeneralUtilities {
    private static final int maxPacketSize = 2048000;
    private final JavaPlugin plugin;

    public GeneralUtilities(JavaPlugin plugin) {
        this.plugin = plugin;
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

    public void performLogout(Player player) {
        PlayerDataUtilities.loadPlayerData(player).setLogoutLocation(LastLocation.fromLocation(player.getLocation())).save();
    }

    public void performLogin(Player player) {
        if (NukeStack.deleteOversizedItems) {
            cleanInventory(player.getInventory());
        }
        checkForIllegals(player.getInventory(), NukeStack.deleteItems, NukeStack.unstackItems, false, null, null);
        this.hidePlayer(player);
        Location logSpot = PlayerDataUtilities.loadPlayerData(player).getLogoutLocation(plugin.getServer());
        if (logSpot != null) {
            player.teleport(logSpot);
        }
        this.unhidePlayer(player);
    }

    public void hidePlayer(Player player) {
        for (Player other : plugin.getServer().getOnlinePlayers()) {
            other.hidePlayer(plugin, player);
        }
    }

    public void unhidePlayer(Player player) {
        for (Player other : plugin.getServer().getOnlinePlayers()) {
            other.showPlayer(plugin, player);
        }
    }
}