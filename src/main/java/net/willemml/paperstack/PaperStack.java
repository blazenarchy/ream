package net.willemml.paperstack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.willemml.paperstack.commands.*;
import net.willemml.paperstack.permissions.Group;
import net.willemml.paperstack.permissions.PermissionsUtility;
import net.willemml.paperstack.player.PlayerData;
import net.willemml.paperstack.player.PlayerDataUtilities;
import net.willemml.paperstack.commands.*;
import org.apache.commons.lang.SerializationUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * A plugin for Blazenarchy to prevent illegal items, add a simple dupe, add some basic commands and stop players from going to fast.
 *
 * @author wnuke
 */
public final class PaperStack extends JavaPlugin implements Listener {
    public static final HashSet<String> BLOCKED_NICK = new HashSet<>();
    public static final HashSet<Material> DELETE = new HashSet<>();
    public static final HashSet<Material> NO_DUPE = new HashSet<>();
    public static final HashSet<Material> NO_STACK = new HashSet<>();
    public static final Gson gson = new GsonBuilder().serializeNulls().create();
    public static PaperStack PLUGIN;
    public static String chatFormat = "&r<%prefix%%display_name%&r> %message%";
    public static String nameFormat = "&r%prefix%%name%&r";
    public static String playerListHeader = "";
    public static String playerListFooter = "";
    public static boolean chat = true;
    public static boolean playerList = true;
    public static boolean antiSpeed = true;
    public static long checkInterval = 10;
    public static boolean newPlayerMessage = true;
    public static boolean ignore = true;
    public static boolean permissions = true;
    public static boolean currency = false;
    public static boolean toggleDeathMessages = true;
    public static boolean deleteDroppedItems = true;
    public static boolean deleteItems = true;
    public static boolean loginTeleport = true;
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
    public static HashMap<UUID, UUID> messageReply = new HashMap<>();
    public static HashMap<UUID, UUID> teleportRequests = new HashMap<>();
    public static HashMap<UUID, Location> playerPosTracking = new HashMap<>();
    private long ticksLeft = checkInterval;

    public void loadAndSetConfig() {
        reloadConfig();
        saveDefaultConfig();
        chat = getConfig().getBoolean("chat");
        chatFormat = getConfig().getString("chatformat");
        nameFormat = getConfig().getString("nameformat");
        playerList = getConfig().getBoolean("playerlist");
        playerListHeader = getConfig().getString("header");
        playerListFooter = getConfig().getString("footer");
        ignore = getConfig().getBoolean("ignore");
        permissions = getConfig().getBoolean("permissions");
        loginTeleport = getConfig().getBoolean("loginTeleport");
        toggleDeathMessages = getConfig().getBoolean("toggledeathmessages");
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
            if (material != null) DELETE.add(material);
        }
        for (String item : getConfig().getStringList("noDupe")) {
            Material material = Material.getMaterial(item);
            if (material != null) NO_DUPE.add(material);
        }
        for (String item : getConfig().getStringList("noStack")) {
            Material material = Material.getMaterial(item);
            if (material != null) NO_STACK.add(material);
        }
        for (Map.Entry<UUID, PermissionAttachment> attachment : PermissionsUtility.permissionsMap.entrySet()) {
            attachment.getValue().remove();
        }
        messageReply.clear();
        teleportRequests.clear();
        playerPosTracking.clear();
        PermissionsUtility.groups.clear();
        PlayerDataUtilities.playerData.clear();
        PermissionsUtility.permissionsMap.clear();
        PermissionsUtility.defaultGroup = new Group("default").load();
        for (Player player : getServer().getOnlinePlayers()) {
            GeneralUtilities.performLogout(player);
            GeneralUtilities.performLogin(player);
        }
    }

    @Override
    public void onEnable() {
        PLUGIN = this;
        loadAndSetConfig();
        Objects.requireNonNull(this.getCommand("nsreload")).setExecutor(new Reload());
        if (ignore) {
            Objects.requireNonNull(this.getCommand("ignore")).setExecutor(new Ignore());
        }
        if (permissions) {
            Objects.requireNonNull(this.getCommand("group")).setExecutor(new Groups());
        }
        if (toggleDeathMessages) {
            Objects.requireNonNull(this.getCommand("toggledeathmessages")).setExecutor(new ToggleDeathMessages());
        }
        if (getConfig().getBoolean("teleport")) {
            Objects.requireNonNull(this.getCommand("teleport")).setExecutor(new Teleport());
        }
        if (getConfig().getBoolean("messaging")) {
            Objects.requireNonNull(this.getCommand("message")).setExecutor(new Message());
            Objects.requireNonNull(this.getCommand("reply")).setExecutor(new Reply());
        }
        if (getConfig().getBoolean("info")) {
            Objects.requireNonNull(this.getCommand("info")).setExecutor(new Info());
        }
        if (getConfig().getBoolean("suicide")) {
            Objects.requireNonNull(this.getCommand("suicide")).setExecutor(new Suicide());
        }
        if (getConfig().getBoolean("playertime")) {
            Objects.requireNonNull(this.getCommand("playertime")).setExecutor(new PlayerTime());
        }
        if (getConfig().getBoolean("playerweather")) {
            Objects.requireNonNull(this.getCommand("playerweather")).setExecutor(new PlayerWeather());
        }
        if (getConfig().getBoolean("hat")) {
            Objects.requireNonNull(this.getCommand("hat")).setExecutor(new Hat());
        }
        if (getConfig().getBoolean("dupe")) {
            Objects.requireNonNull(this.getCommand("dupe")).setExecutor(new Dupe());
        }
        if (getConfig().getBoolean("currency")) {
            currency = true;
            Objects.requireNonNull(this.getCommand("balance")).setExecutor(new Balance());
            Objects.requireNonNull(this.getCommand("pay")).setExecutor(new Pay());
        }
        if (getConfig().getBoolean("tpa")) {
            Objects.requireNonNull(this.getCommand("tpask")).setExecutor(new TeleportAsk());
            Objects.requireNonNull(this.getCommand("tpcancel")).setExecutor(new TeleportCancel());
            Objects.requireNonNull(this.getCommand("tpdeny")).setExecutor(new TeleportNo());
            Objects.requireNonNull(this.getCommand("tpaccept")).setExecutor(new TeleportYes());
        }
        if (getConfig().getBoolean("nick")) {
            Objects.requireNonNull(this.getCommand("nick")).setExecutor(new Nick());
            Objects.requireNonNull(this.getCommand("realname")).setExecutor(new RealName());
        }
        if (getConfig().getBoolean("packetfilter")) {
            try {
                ProtocolManager manager = ProtocolLibrary.getProtocolManager();
                ArrayList<PacketType> packetTypes = new ArrayList<>();
                for (PacketType packetType : PacketType.Play.Server.getInstance()) {
                    packetTypes.add(packetType);
                }
                manager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, packetTypes) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        int packetSize = SerializationUtils.serialize(event.getPacket()).length;
                        if (packetSize >= 2097152) {
                            event.setCancelled(true);
                        }
                    }
                });
            } catch (Exception e) {
                getLogger().warning("Could not load packet filter.");
            }
        }
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Loaded NukeStack by wnuke.");
    }

    @Override
    public void onDisable() {
        for (PermissionAttachment permissionAttachment : PermissionsUtility.permissionsMap.values()) {
            permissionAttachment.remove();
        }
        PermissionsUtility.groups.clear();
        PlayerDataUtilities.playerData.clear();
        PermissionsUtility.permissionsMap.clear();
        teleportRequests.clear();
        playerPosTracking.clear();
        messageReply.clear();
        getLogger().info("Disabled NukeStack by wnuke.");
    }

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (ignore)
            event.getRecipients().removeIf(p -> PlayerDataUtilities.loadPlayerData(p).hasIgnored(player.getUniqueId()));
        if (chat) {
            String message = event.getMessage().replace("%", "%%");
            if (player.hasPermission("nukestack.colourchat")) message.replaceAll("&(?=[0-9]|[a-f]|r)", "§");
            if (player.hasPermission("nukestack.formatchat")) message.replaceAll("&(?=[k-o]|r)", "§");
            if (message.startsWith(">")) message = ChatColor.GREEN + message;
            String format = GeneralUtilities.parsePlaceholders(player, chatFormat).replace("%message%", message);
            event.setFormat(format);
        }
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
        if (toggleDeathMessages && event.getDeathMessage() != null) {
            for (Player player : getServer().getOnlinePlayers()) {
                if (PlayerDataUtilities.loadPlayerData(player).deathMessages()) {
                    player.sendMessage(event.getDeathMessage());
                }
            }
            event.setDeathMessage(null);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        GeneralUtilities.performLogin(event.getPlayer());
        if (playerList) GeneralUtilities.setPlayerList(event.getPlayer());
        if (!event.getPlayer().hasPlayedBefore() && newPlayerMessage) {
            getServer().broadcastMessage(ChatColor.AQUA + event.getPlayer().getDisplayName() + " joined for the first time!");
            event.setJoinMessage(null);
        }
        PlayerData joinedPlayerData = PlayerDataUtilities.loadPlayerData(event.getPlayer());
        String nick = joinedPlayerData.getNickName();
        if (!nick.isEmpty()) {
            event.getPlayer().setDisplayName(nick);
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        playerPosTracking.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        GeneralUtilities.performLogout(event.getPlayer());
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
                    GeneralUtilities.checkForIllegals(player);
                }
            }
        } else {
            ticksLeft--;
        }
    }
}

