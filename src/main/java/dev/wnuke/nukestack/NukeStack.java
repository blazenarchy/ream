package dev.wnuke.nukestack;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import dev.wnuke.nukestack.commands.*;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

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
    public static final HashSet<String> BLOCKED_NICK = new HashSet<>();
    public static final HashSet<Material> DELETE = new HashSet<>();
    public static final HashSet<Material> NO_DUPE = new HashSet<>();
    public static final HashSet<Material> NO_STACK = new HashSet<>();
    public static GeneralUtilities UTILITIES;
    public static boolean antiSpeed = true;
    public static long checkInterval = 10;
    public static boolean newPlayerMessage = true;
    public static boolean ignore = true;
    public static boolean currency = false;
    public static boolean deleteDroppedItems = true;
    public static boolean deleteItems = true;
    public static long dupeCost = 2;
    public static long hatCost = 0;
    public static long maxSpeed = 140;
    public static long nickCost = 0;
    public static String nickPrefix = ".";
    public static long playerTimeCost = 0;
    public static long playerWeatherCost = 0;
    public static long startingMoney = 0;
    public static long suicideCost = 0;
    public static long tpaCost = 2;
    public static boolean unstackItems = true;
    public static boolean deleteOversizedItems = true;
    public static HashMap<UUID, UUID> teleportRequests;
    public static HashMap<UUID, Location> playerPosTracking;
    private long ticksLeft = checkInterval;

    public void loadAndSetConfig() {
        PlayerDataUtilities.playerData = new HashMap<>();
        teleportRequests = new HashMap<>();
        playerPosTracking = new HashMap<>();
        reloadConfig();
        saveDefaultConfig();
        ignore = getConfig().getBoolean("ignore");
        antiSpeed = getConfig().getBoolean("antiSpeed");
        checkInterval = getConfig().getLong("checkInterval");
        deleteDroppedItems = getConfig().getBoolean("deleteDroppedIllegals");
        deleteItems = getConfig().getBoolean("deleteIllegals");
        dupeCost = getConfig().getLong("dupeCost");
        hatCost = getConfig().getLong("hatCost");
        maxSpeed = getConfig().getLong("maxSpeed");
        newPlayerMessage = getConfig().getBoolean("newPlayerMessage");
        nickCost = getConfig().getLong("nickCost");
        nickPrefix = getConfig().getString("nickPrefix");
        playerTimeCost = getConfig().getLong("playerTimeCost");
        playerWeatherCost = getConfig().getLong("playerWeatherCost");
        startingMoney = getConfig().getLong("startingMoney");
        suicideCost = getConfig().getLong("suicideCost");
        tpaCost = getConfig().getLong("tpaCost");
        deleteOversizedItems = getConfig().getBoolean("deleteOversized");
        unstackItems = getConfig().getBoolean("unstackOverstacked");
        BLOCKED_NICK.addAll(getConfig().getStringList("bannedNicks"));
        for (String item : getConfig().getStringList("illegals")) {
            Material material = Material.getMaterial(item);
            if (material != null) {
                DELETE.add(material);
            }
        }
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
    }

    @Override
    public void onEnable() {
        UTILITIES = new GeneralUtilities(this);
        loadAndSetConfig();
        Objects.requireNonNull(this.getCommand("nsreload")).setExecutor(new ReloadCommand(this));
        if (ignore) {
            Objects.requireNonNull(this.getCommand("ignore")).setExecutor(new IgnoreCommand(this));
        }
        if (getConfig().getBoolean("suicide")) {
            Objects.requireNonNull(this.getCommand("suicide")).setExecutor(new SuicideCommand(this));
        }
        if (getConfig().getBoolean("playertime")) {
            Objects.requireNonNull(this.getCommand("playertime")).setExecutor(new PlayerTimeCommand(this));
        }
        if (getConfig().getBoolean("playerweather")) {
            Objects.requireNonNull(this.getCommand("playerweather")).setExecutor(new PlayerWeatherCommand(this));
        }
        if (getConfig().getBoolean("hat")) {
            Objects.requireNonNull(this.getCommand("hat")).setExecutor(new HatCommand(this));
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
            Objects.requireNonNull(this.getCommand("realname")).setExecutor(new RealNameCommand(this));
        }
        getServer().getPluginManager().registerEvents(this, this);
        PlayerDataUtilities.loadAllPlayerData();
        getLogger().info("Loaded NukeStack by wnuke.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled NukeStack by wnuke.");
    }

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        event.getRecipients().removeIf(player -> PlayerDataUtilities.loadPlayerData(player).hasIgnored(event.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        playerPosTracking.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        playerPosTracking.remove(killed.getUniqueId());
        PlayerDataUtilities.loadPlayerData(killed).endStreak().save();
        Player killer = killed.getKiller();
        if (killer != null) {
            if (killer.getAddress() == null || killed.getAddress() == null) return;
            if (killer.getAddress().getAddress().equals(killed.getAddress().getAddress())) return;
            PlayerDataUtilities.loadPlayerData(killer).incrementStreak().save();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore() && newPlayerMessage) {
            getServer().broadcastMessage(ChatColor.AQUA + event.getPlayer().getDisplayName() + " joined for the first time!");
        }
        PlayerData joinedPlayerData = PlayerDataUtilities.loadPlayerData(event.getPlayer());
        String nick = joinedPlayerData.getNickName();
        if (!nick.isEmpty()) {
            event.getPlayer().setDisplayName(nick);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UTILITIES.performLogin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        UTILITIES.performLogout(event.getPlayer());
    }

    @EventHandler
    public void onInventoryUpdate(InventoryEvent event) {
        GeneralUtilities.cleanInventory(event.getInventory());
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
                        if (!player.isDead()) {
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
                    }
                    GeneralUtilities.checkForIllegals(player.getInventory(), !player.hasPermission("nukestack.illegal"), !player.hasPermission("nukestack.overstack"), false, null, null);
                }
            }
        } else {
            ticksLeft--;
        }
    }
}

