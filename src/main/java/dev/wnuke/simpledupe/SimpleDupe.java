package dev.wnuke.simpledupe;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;

/**
 * Blazenarchy's Dupe plugin, a configurable way of duplicating items without exploits.
 *
 * @author wnuke
 */
public final class SimpleDupe extends JavaPlugin implements Listener {
    private static final HashSet<Material> NO_DUPE = new HashSet<>(Arrays.asList(Material.SKELETON_SKULL, Material.CREEPER_HEAD, Material.ZOMBIE_HEAD, Material.PLAYER_HEAD, Material.DRAGON_HEAD));
    private static final HashSet<Material> NO_STACK = new HashSet<>(Arrays.asList(Material.SHULKER_BOX, Material.TOTEM_OF_UNDYING));
    private static final HashSet<Material> DELETE = new HashSet<>(Arrays.asList(Material.END_PORTAL_FRAME, Material.BEDROCK, Material.BARRIER, Material.STRUCTURE_BLOCK));
    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    private static int ticksLeft = 10;
    private static HashMap<UUID, Long> playerData;
    private final File playerDataFile = new File(getDataFolder(), "playerData.json");

    public static void checkForIllegals(Inventory inventory, boolean illegals, boolean overStacked, @Nullable World world, @Nullable Location location) {
        if (illegals) {
            for (Material delete : DELETE) {
                inventory.remove(delete);
            }
        }
        for (ItemStack itemStack : inventory) {
            if (!(itemStack == null)) {
                if (NO_STACK.contains(itemStack.getType()) && overStacked) {
                    int maxStack = itemStack.getMaxStackSize();
                    if (itemStack.getAmount() > maxStack) itemStack.setAmount(maxStack);
                }
                if (world != null && location != null) {
                    if (!NO_DUPE.contains(itemStack.getType())) {
                        world.dropItemNaturally(location, itemStack);
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        Objects.requireNonNull(this.getCommand("dupe")).setExecutor(new DupeCommand());
        Objects.requireNonNull(this.getCommand("money")).setExecutor(new MoneyCommand(this));
        this.getPluginLoader().createRegisteredListeners(this, this);
    }

    public void loadPlayerData() {
        new Thread(() -> {
            Thread.currentThread().setName(getName() + " Player Data Load Thread");
            playerDataFile.mkdirs();
            try {
                if (!playerDataFile.createNewFile()) {
                    try {
                        playerData = gson.fromJson(new FileReader(playerDataFile), new TypeToken<HashMap<UUID, Long>>() {
                        }.getType());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (playerData == null) {
                playerData = new HashMap<>();
            }
        }).start();
    }

    public void savePlayerData() {
        new Thread(() -> {
            Thread.currentThread().setName(getName() + " Player Data Save Thread");
            playerDataFile.mkdirs();
            try {
                if (!playerDataFile.createNewFile()) {
                    FileWriter fw = new FileWriter(playerDataFile);
                    gson.toJson(playerData, fw);
                    fw.flush();
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!playerData.containsKey(event.getPlayer().getUniqueId())) {
            playerData.put(event.getPlayer().getUniqueId(), 0L);
        }
    }

    @EventHandler
    public void onTick(ServerTickEndEvent event) {
        if (ticksLeft == 0) {
            for (World world : getServer().getWorlds()) {
                new Thread(() -> {
                    Thread.currentThread().setName(world.getName() + " Item Scanner");
                    for (Item item : world.getEntitiesByClass(Item.class)) {
                        ItemStack itemStack = item.getItemStack();
                        if (DELETE.contains(itemStack.getType())) {
                            item.remove();
                        } else if (NO_STACK.contains(itemStack.getType())) {
                            int maxStack = itemStack.getMaxStackSize();
                            if (itemStack.getAmount() > maxStack) itemStack.setAmount(maxStack);
                        }
                    }
                }).start();
            }
            for (Player player : getServer().getOnlinePlayers()) {
                new Thread(() -> {
                    Thread.currentThread().setName(player.getUniqueId() + " Item Scanner");
                    checkForIllegals(player.getInventory(), !player.hasPermission("simpledupe.illegal"), !player.hasPermission("simpledupe.overstack"), null, null);
                }).start();
            }
            ticksLeft = 10;
        } else {
            ticksLeft--;
        }
    }

    private static class MoneyCommand implements CommandExecutor {
        SimpleDupe plugin;

        public MoneyCommand(SimpleDupe plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof ConsoleCommandSender)) {
                UUID playerID = ((Player) sender).getUniqueId();
                playerData.replace(playerID, playerData.get(playerID) + 1);
                plugin.savePlayerData();
            }
            return true;
        }
    }

    private static class DupeCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof ConsoleCommandSender)) {
                if (!sender.hasPermission("simpledupe.dupe")) {
                    return false;
                }
                Player player = (Player) sender;
                InventoryHolder inventory;
                if (player.getVehicle() instanceof Donkey) {
                    inventory = (InventoryHolder) ((Donkey) player.getVehicle()).getInventory();
                } else if (player.getVehicle() instanceof Llama) {
                    inventory = (InventoryHolder) ((Llama) player.getVehicle()).getInventory();
                } else {
                    player.sendMessage("You must be riding a donkey or a llama to use this command.");
                    return true;
                }
                new Thread(() -> {
                    Thread.currentThread().setName(player.getUniqueId() + " Dupe");
                    checkForIllegals(inventory.getInventory(), !player.hasPermission("simpledupe.illegal"), !player.hasPermission("simpledupe.overstack"), player.getWorld(), player.getLocation());
                    player.sendMessage("Your items have been duplicated.");
                }).start();
            } else {
                sender.sendMessage("Cannot dupe as console.");
            }
            return true;
        }
    }
}

