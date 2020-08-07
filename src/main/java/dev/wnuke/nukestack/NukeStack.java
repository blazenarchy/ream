package dev.wnuke.nukestack;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.wnuke.nukestack.commands.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

/**
 * Blazenarchy's Dupe plugin, a configurable way of duplicating items without exploits.
 *
 * @author wnuke
 */
public final class NukeStack extends JavaPlugin implements Listener {
    private static final HashSet<Material> NO_DUPE = new HashSet<>(Arrays.asList(Material.SKELETON_SKULL, Material.CREEPER_HEAD, Material.ZOMBIE_HEAD, Material.PLAYER_HEAD, Material.DRAGON_HEAD, Material.DRAGON_EGG));
    private static final HashSet<Material> NO_STACK = new HashSet<>(Arrays.asList(Material.SHULKER_BOX, Material.TOTEM_OF_UNDYING));
    private static final HashSet<Material> DELETE = new HashSet<>(Arrays.asList(Material.END_PORTAL_FRAME, Material.BEDROCK, Material.BARRIER, Material.STRUCTURE_BLOCK));
    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    private final String playerDataFolder = getDataFolder() + "/player-data/";
    public HashMap<UUID, PlayerData> playerData;
    public HashMap<UUID, UUID> teleportRequests;
    private HashMap<UUID, Location> playerPosTracking;
    private int ticksLeft = 10;

    public static void checkForIllegals(Inventory inventory, boolean removeIllegals, boolean removeOverStacked, boolean dupe, @Nullable World world, @Nullable Location location) {
        for (ItemStack itemStack : inventory) {
            if (itemStack != null) {
                if (removeIllegals) {
                    if (DELETE.contains(itemStack.getType())) {
                        inventory.remove(itemStack);
                    }
                }
                if (removeOverStacked) {
                    if (NO_STACK.contains(itemStack.getType())) {
                        int maxStack = itemStack.getMaxStackSize();
                        if (itemStack.getAmount() > maxStack) itemStack.setAmount(maxStack);
                    }
                }
                if (dupe) {
                    if (world != null && location != null) {
                        if (!NO_DUPE.contains(itemStack.getType())) {
                            world.dropItemNaturally(location, itemStack);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        Objects.requireNonNull(this.getCommand("dupe")).setExecutor(new DupeCommand(this));
        Objects.requireNonNull(this.getCommand("balance")).setExecutor(new BalanceCommand(this));
        Objects.requireNonNull(this.getCommand("pay")).setExecutor(new PayCommand(this));
        Objects.requireNonNull(this.getCommand("tpask")).setExecutor(new TeleportAskCommand(this));
        Objects.requireNonNull(this.getCommand("tpcancel")).setExecutor(new TeleportCancelCommand(this));
        Objects.requireNonNull(this.getCommand("tpdeny")).setExecutor(new TeleportNoCommand(this));
        Objects.requireNonNull(this.getCommand("tpaccept")).setExecutor(new TeleportYesCommand(this));
        getServer().getPluginManager().registerEvents(this, this);
        playerData = new HashMap<>();
        teleportRequests = new HashMap<>();
        playerPosTracking = new HashMap<>();
        getLogger().info("Loaded NukeStack by wnuke.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled NukeStack by wnuke.");
    }

    public PlayerData loadPlayerData(UUID player) {
        PlayerData loadedData;
        if (playerData.containsKey(player)) {
            loadedData = playerData.get(player);
            if (loadedData != null) {
                return playerData.get(player);
            }
        }
        loadedData = loadPlayerDataNoCache(player);
        playerData.remove(player);
        playerData.put(player, loadedData);
        return loadedData;
    }

    protected PlayerData loadPlayerDataNoCache(UUID player) {
        File playerDataFile = new File(playerDataFolder + player.toString() + ".json");
        playerDataFile.getParentFile().mkdirs();
        try {
            return gson.fromJson(new FileReader(playerDataFile), new TypeToken<PlayerData>() {
            }.getType());
        } catch (FileNotFoundException e) {
            PlayerData newPlayerData = new PlayerData();
            savePlayerData(player, newPlayerData);
            return newPlayerData;
        }
    }

    public void savePlayerData(UUID player, PlayerData newPlayerData) {
        playerData.remove(player);
        playerData.putIfAbsent(player, newPlayerData);
        File playerDataFile = new File(playerDataFolder + player.toString() + ".json");
        playerDataFile.getParentFile().mkdirs();
        try {
            playerDataFile.createNewFile();
            FileWriter fw = new FileWriter(playerDataFile);
            fw.write(gson.toJson(newPlayerData));
            fw.flush();
            fw.close();
        } catch (Exception e) {
            System.out.println("Failed to save player data for " + player.toString() + ", error:");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID playerID = event.getPlayer().getUniqueId();
        if (!playerData.containsKey(playerID)) {
            PlayerData newPlayerData = new PlayerData();
            playerData.put(event.getPlayer().getUniqueId(), newPlayerData);
            savePlayerData(playerID, newPlayerData);
            return;
        }
        loadPlayerData(playerID);
    }

    @EventHandler
    public void onTick(ServerTickEndEvent event) {
        if (ticksLeft == 0) {
            ticksLeft = 10;
            for (World world : getServer().getWorlds()) {
                for (Item item : world.getEntitiesByClass(Item.class)) {
                    ItemStack itemStack = item.getItemStack();
                    if (DELETE.contains(itemStack.getType())) {
                        item.remove();
                    } else if (NO_STACK.contains(itemStack.getType())) {
                        int maxStack = itemStack.getMaxStackSize();
                        if (itemStack.getAmount() > maxStack) itemStack.setAmount(maxStack);
                    }
                }
            }
            for (Player player : getServer().getOnlinePlayers()) {
                if (!player.hasPermission("nukestack.cheat")) {
                    UUID playerID = player.getUniqueId();
                    Location playerPos = player.getLocation();
                    if (playerPosTracking.containsKey(playerID)) {
                        Location lastPlayerPos = playerPosTracking.get(playerID);
                        if (playerPos.getWorld().getUID().equals(lastPlayerPos.getWorld().getUID())) {
                            double distance = playerPos.distanceSquared(lastPlayerPos);
                            if (distance > 160) {
                                player.teleport(lastPlayerPos);
                            }
                        }
                        playerPosTracking.replace(playerID, playerPos);
                    } else {
                        playerPosTracking.put(playerID, playerPos);
                    }
                }
                checkForIllegals(player.getInventory(), !player.hasPermission("nukestack.illegal"), !player.hasPermission("nukestack.overstack"), false, null, null);
            }
        } else {
            ticksLeft--;
        }
    }
}

