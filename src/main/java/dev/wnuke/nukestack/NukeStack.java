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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

/**
 * A plugin for Blazenarchy to prevent illegal items, add a simple dupe, add some basic commands and stop players from going to fast.
 *
 * @author wnuke
 */
public final class NukeStack extends JavaPlugin implements Listener {
    public static final HashSet<Material> NO_DUPE = new HashSet<>();
    public static final HashSet<Material> NO_STACK = new HashSet<>();
    public static final HashSet<Material> DELETE = new HashSet<>();
    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    public static boolean deleteItems = true;
    public static boolean unstackItems = true;
    public static boolean deleteDroppedItems = true;
    public static boolean antiSpeed = true;
    public static boolean currency = false;
    public static String nickPrefix = ".";
    public static long nickCost = 0;
    public static long dupeCost = 2;
    public static long suicideCost = 2;
    public static long tpaCost = 2;
    public static long checkInterval = 10;
    public static long maxSpeed = 160;
    public static long startingMoney = 0;
    private final String playerDataFolder = getDataFolder() + "/player-data/";
    public HashMap<UUID, PlayerData> playerData;
    public HashMap<UUID, UUID> teleportRequests;
    private HashMap<UUID, Location> playerPosTracking;
    private long ticksLeft = checkInterval;

    public void checkForIllegals(Inventory inventory, boolean removeIllegals, boolean unstackOverStacked, boolean dupe, @Nullable World world, @Nullable Location location) {
        if (dupe || (removeIllegals && deleteItems) || (unstackOverStacked && unstackItems))
            for (ItemStack itemStack : inventory) {
                if (itemStack != null) {
                    if (removeIllegals && deleteItems) {
                        if (DELETE.contains(itemStack.getType())) {
                            inventory.remove(itemStack);
                        }
                    }
                    if (unstackOverStacked && unstackItems) {
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

    public void loadAndSetConfig() {
        reloadConfig();
        saveDefaultConfig();
        deleteItems = getConfig().getBoolean("deleteIllegals");
        unstackItems = getConfig().getBoolean("unstackOverstacked");
        deleteDroppedItems = getConfig().getBoolean("deleteDroppedIllegals");
        antiSpeed = getConfig().getBoolean("antiSpeed");
        nickPrefix = getConfig().getString("nickPrefix");
        nickCost = getConfig().getLong("nickCost");
        dupeCost = getConfig().getLong("dupeCost");
        suicideCost = getConfig().getLong("suicideCost");
        tpaCost = getConfig().getLong("tpaCost");
        checkInterval = getConfig().getLong("checkInterval");
        startingMoney = getConfig().getLong("startingMoney");
        maxSpeed = getConfig().getLong("maxSpeed");
        for (String item : getConfig().getStringList("noDupe")) {
            Material material = Material.getMaterial(item);
            if (material != null) {
                NO_DUPE.add(material);
            }
        }
        for (String item : getConfig().getStringList("noStack")) {
            Material material = Material.getMaterial(item);
            if (material != null) {
                NO_STACK.add(material);
            }
        }
        for (String item : getConfig().getStringList("illegals")) {
            Material material = Material.getMaterial(item);
            if (material != null) {
                DELETE.add(material);
            }
        }
    }

    @Override
    public void onEnable() {
        loadAndSetConfig();
        Objects.requireNonNull(this.getCommand("nsreload")).setExecutor(new ReloadCommand(this));
        if (getConfig().getBoolean("suicide")) {
            Objects.requireNonNull(this.getCommand("suicide")).setExecutor(new SuicideCommand(this));
        }
        if (getConfig().getBoolean("dupe")) {
            Objects.requireNonNull(this.getCommand("dupe")).setExecutor(new DupeCommand(this));
        }
        if (getConfig().getBoolean("currency")) {
            currency = true;
            Objects.requireNonNull(this.getCommand("balance")).setExecutor(new BalanceCommand(this));
            Objects.requireNonNull(this.getCommand("pay")).setExecutor(new PayCommand(this));
        }
        if (getConfig().getBoolean("tpa")) {
            Objects.requireNonNull(this.getCommand("tpask")).setExecutor(new TeleportAskCommand(this));
            Objects.requireNonNull(this.getCommand("tpcancel")).setExecutor(new TeleportCancelCommand(this));
            Objects.requireNonNull(this.getCommand("tpdeny")).setExecutor(new TeleportNoCommand(this));
            Objects.requireNonNull(this.getCommand("tpaccept")).setExecutor(new TeleportYesCommand(this));
        }
        if (getConfig().getBoolean("nick")) {
            Objects.requireNonNull(this.getCommand("nick")).setExecutor(new NickCommand(this));
        }
        getServer().getPluginManager().registerEvents(this, this);
        playerData = new HashMap<>();
        teleportRequests = new HashMap<>();
        playerPosTracking = new HashMap<>();
        loadAllPlayerData();
        getLogger().info("Loaded NukeStack by wnuke.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled NukeStack by wnuke.");
    }

    public HashSet<PlayerData> loadAllPlayerData() {
        HashSet<PlayerData> playerDataHashMap = new HashSet<>();
        File playerDataDir = new File(playerDataFolder);
        if (playerDataDir.isDirectory() && playerDataDir.listFiles() != null) {
            for (File file : Objects.requireNonNull(playerDataDir.listFiles())) {
                UUID playerID = UUID.fromString(file.getName().replace(".json", ""));
                playerDataHashMap.add(loadPlayerData(playerID));
            }
        }
        return playerDataHashMap;
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
        } else {
            PlayerData joinedPlayerData = loadPlayerData(playerID);
            String nick = joinedPlayerData.getNickName();
            if (!nick.isEmpty()) {
                event.getPlayer().setDisplayName(nick);
            }
        }
    }

    @EventHandler
    public void onTick(ServerTickEndEvent event) {
        if (ticksLeft == 0) {
            ticksLeft = checkInterval;
            if (deleteDroppedItems || unstackItems) {
                for (World world : getServer().getWorlds()) {
                    for (Item item : world.getEntitiesByClass(Item.class)) {
                        ItemStack itemStack = item.getItemStack();
                        if (deleteDroppedItems) {
                            if (DELETE.contains(itemStack.getType())) {
                                item.remove();
                            }
                        }
                        if (unstackItems) {
                            if (NO_STACK.contains(itemStack.getType())) {
                                int maxStack = itemStack.getMaxStackSize();
                                if (itemStack.getAmount() > maxStack) itemStack.setAmount(maxStack);
                            }
                        }
                    }
                }
            }
            if (deleteItems || unstackItems || antiSpeed) {
                for (Player player : getServer().getOnlinePlayers()) {
                    if (antiSpeed) {
                        if (!player.hasPermission("nukestack.cheat")) {
                            UUID playerID = player.getUniqueId();
                            Location playerPos = player.getLocation();
                            if (playerPosTracking.containsKey(playerID)) {
                                Location lastPlayerPos = playerPosTracking.get(playerID);
                                if (playerPos.getWorld().getUID().equals(lastPlayerPos.getWorld().getUID())) {
                                    double distance = playerPos.distanceSquared(lastPlayerPos);
                                    if (distance > maxSpeed) {
                                        player.teleport(lastPlayerPos);
                                    }
                                }
                                playerPosTracking.replace(playerID, player.getLocation());
                            } else {
                                playerPosTracking.put(playerID, playerPos);
                            }
                        }
                    }
                    checkForIllegals(player.getInventory(), !player.hasPermission("nukestack.illegal"), !player.hasPermission("nukestack.overstack"), false, null, null);
                }
            }
        } else {
            ticksLeft--;
        }
    }
}

