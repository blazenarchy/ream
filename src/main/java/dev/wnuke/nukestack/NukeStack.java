package dev.wnuke.nukestack;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
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
public final class NukeStack extends JavaPlugin implements Listener {
    private static final HashSet<Material> NO_DUPE = new HashSet<>(Arrays.asList(Material.SKELETON_SKULL, Material.CREEPER_HEAD, Material.ZOMBIE_HEAD, Material.PLAYER_HEAD, Material.DRAGON_HEAD));
    private static final HashSet<Material> NO_STACK = new HashSet<>(Arrays.asList(Material.SHULKER_BOX, Material.TOTEM_OF_UNDYING));
    private static final HashSet<Material> DELETE = new HashSet<>(Arrays.asList(Material.END_PORTAL_FRAME, Material.BEDROCK, Material.BARRIER, Material.STRUCTURE_BLOCK));
    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    private final String playerDataFolder = getDataFolder() + "/player-data/";
    private int ticksLeft = 10;
    private HashMap<UUID, PlayerData> playerData;
    private HashMap<UUID, UUID> teleportRequests;

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
        Objects.requireNonNull(this.getCommand("dupe")).setExecutor(new DupeCommand(this));
        Objects.requireNonNull(this.getCommand("money")).setExecutor(new MoneyCommand(this));
        Objects.requireNonNull(this.getCommand("tpa")).setExecutor(new TeleportAskCommand(this));
        Objects.requireNonNull(this.getCommand("tpc")).setExecutor(new TeleportCancelCommand(this));
        Objects.requireNonNull(this.getCommand("tpn")).setExecutor(new TeleportNoCommand(this));
        Objects.requireNonNull(this.getCommand("tpy")).setExecutor(new TeleportYesCommand(this));
        this.getPluginLoader().createRegisteredListeners(this, this);
        playerData = new HashMap<>();
        teleportRequests = new HashMap<>();
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

    public PlayerData loadPlayerDataNoCache(UUID player) {
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
        new Thread(() -> {
            playerData.remove(player);
            playerData.putIfAbsent(player, newPlayerData);
            File playerDataFile = new File(playerDataFolder + player.toString() + ".json");
            playerDataFile.getParentFile().mkdirs();
            try {
                if (!playerDataFile.createNewFile()) {
                    FileWriter fw = new FileWriter(playerDataFile);
                    gson.toJson(newPlayerData, fw);
                    fw.flush();
                    fw.close();
                }
            } catch (IOException e) {
                getLogger().warning("Failed to save player data for " + player.toString() + ", error:");
                e.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!playerData.containsKey(event.getPlayer().getUniqueId())) {
            playerData.put(event.getPlayer().getUniqueId(), new PlayerData());
        }
    }

    @EventHandler
    public void onTick(ServerTickEndEvent event) {
        if (ticksLeft == 0) {
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
                checkForIllegals(player.getInventory(), !player.hasPermission("nukestack.illegal"), !player.hasPermission("nukestack.overstack"), null, null);
            }
            ticksLeft = 10;
        } else {
            ticksLeft--;
        }
    }

    private static class TeleportYesCommand implements CommandExecutor {
        NukeStack plugin;

        public TeleportYesCommand(NukeStack plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof ConsoleCommandSender)) {
                if (sender.hasPermission("nukestack.tpy")) {
                    if (args.length > 0) {
                        for (Player player : plugin.getServer().getOnlinePlayers()) {
                            if (player.getPlayerListName().equals(args[0])) {
                                if (plugin.teleportRequests.get(player.getUniqueId()) == ((Player) sender).getUniqueId()) {
                                    if (plugin.loadPlayerData(player.getUniqueId()).getTokens() < 2) {
                                        player.sendMessage("Teleport cancelled, not enough tokens.");
                                        sender.sendMessage(player.getPlayerListName() + " does not have enough tokens, teleport cancelled.");
                                        return true;
                                    }
                                    player.sendMessage("Teleport request accepted, teleporting...");
                                    sender.sendMessage("Teleporting...");
                                    for (Player other : plugin.getServer().getOnlinePlayers()) {
                                        other.hidePlayer(plugin, player);
                                    }
                                    player.teleport((Player) sender);
                                    for (Player other : plugin.getServer().getOnlinePlayers()) {
                                        other.showPlayer(plugin, player);
                                    }
                                    return true;
                                }
                            }
                        }
                        sender.sendMessage("No player with name " + args[0] + " has requested to teleport to you.");
                    } else {
                        sender.sendMessage("You need to specify who you want to deny.");
                    }
                } else {
                    return false;
                }
            }
            return true;
        }
    }

    private static class TeleportNoCommand implements CommandExecutor {
        NukeStack plugin;

        public TeleportNoCommand(NukeStack plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof ConsoleCommandSender)) {
                if (sender.hasPermission("nukestack.tpn")) {
                    if (args.length > 0) {
                        for (Player player : plugin.getServer().getOnlinePlayers()) {
                            if (player.getPlayerListName().equals(args[0])) {
                                if (plugin.teleportRequests.get(player.getUniqueId()) == ((Player) sender).getUniqueId()) {
                                    player.sendMessage("Teleport denied.");
                                    sender.sendMessage("Teleport denied.");
                                    return true;
                                }
                            }
                        }
                        sender.sendMessage("No player with name " + args[0] + " has requested to teleport to you.");
                    } else {
                        sender.sendMessage("You need to specify who you want to deny.");
                    }
                } else {
                    return false;
                }
            }
            return true;
        }
    }

    private static class TeleportCancelCommand implements CommandExecutor {
        NukeStack plugin;

        public TeleportCancelCommand(NukeStack plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof ConsoleCommandSender)) {
                if (sender.hasPermission("nukestack.tpc")) {
                    plugin.teleportRequests.remove(((Player) sender).getUniqueId());
                    sender.sendMessage("Teleport request cancelled.");
                } else {
                    return false;
                }
            }
            return true;
        }
    }

    private static class TeleportAskCommand implements CommandExecutor {
        NukeStack plugin;

        public TeleportAskCommand(NukeStack plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof ConsoleCommandSender)) {
                if (sender.hasPermission("nukestack.tpa")) {
                    UUID playerID = ((Player) sender).getUniqueId();
                    if (plugin.loadPlayerData(playerID).getTokens() < 2) {
                        sender.sendMessage("You need at least 2 tokens to teleport to someone.");
                        return true;
                    }
                    if (plugin.teleportRequests.containsKey(playerID)) {
                        sender.sendMessage("Please wait for your current teleport request to expire or cancel it by running /tpc.");
                        return true;
                    }
                    if (args.length > 0) {
                        for (Player player : plugin.getServer().getOnlinePlayers()) {
                            if (player.getPlayerListName().equals(args[0])) {
                                plugin.teleportRequests.put(playerID, player.getUniqueId());
                                return true;
                            }
                        }
                        sender.sendMessage("No player found with name " + args[0] + ".");
                    } else {
                        sender.sendMessage("You need to specify who you want to teleport to.");
                    }
                } else {
                    return false;
                }
            }
            return true;
        }
    }

    private static class MoneyCommand implements CommandExecutor {
        NukeStack plugin;

        public MoneyCommand(NukeStack plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof ConsoleCommandSender)) {
                UUID playerID = ((Player) sender).getUniqueId();
                PlayerData playerData = plugin.loadPlayerData(playerID);
                playerData.setTokens(playerData.getTokens() + 1);
                plugin.savePlayerData(playerID, playerData);
            }
            return true;
        }
    }

    private static class DupeCommand implements CommandExecutor {
        NukeStack plugin;

        public DupeCommand(NukeStack plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof ConsoleCommandSender)) {
                if (!sender.hasPermission("nukestack.dupe")) {
                    return false;
                }
                Player player = (Player) sender;
                PlayerData playerData = plugin.loadPlayerData(player.getUniqueId());
                if (playerData.getTokens() < 2) {
                    player.sendMessage("You do not have enough tokens, you need at least 2.");
                    return true;
                }
                Inventory inventory;
                if (player.getVehicle() instanceof Donkey) {
                    inventory = ((Donkey) player.getVehicle()).getInventory();
                } else if (player.getVehicle() instanceof Llama) {
                    inventory = ((Llama) player.getVehicle()).getInventory();
                } else {
                    player.sendMessage("You must be riding a donkey or a llama to use this command.");
                    return true;
                }
                playerData.setTokens(playerData.getTokens() - 2);
                checkForIllegals(inventory, !player.hasPermission("simpledupe.illegal"), !player.hasPermission("simpledupe.overstack"), player.getWorld(), player.getLocation());
                playerData.increaseLifeTimeDupes();
                plugin.savePlayerData(player.getUniqueId(), playerData);
                player.sendMessage("Your items have been duplicated.");
            } else {
                sender.sendMessage("Cannot dupe as console.");
            }
            return true;
        }
    }

    private static class PlayerData {
        @SerializedName("ts")
        private long tokens = 0;
        @SerializedName("tt")
        private long lifeTimeTPs = 0;
        @SerializedName("td")
        private long lifeTimeDupes = 0;

        @Override
        public String toString() {
            return "PlayerData{" +
                    "tokens=" + tokens +
                    ", lifeTimeTPs=" + lifeTimeTPs +
                    ", lifeTimeDupes=" + lifeTimeDupes +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PlayerData)) return false;
            PlayerData that = (PlayerData) o;
            return getTokens() == that.getTokens() &&
                    getLifeTimeTPs() == that.getLifeTimeTPs() &&
                    getLifeTimeDupes() == that.getLifeTimeDupes();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getTokens(), getLifeTimeTPs(), getLifeTimeDupes());
        }

        public long getTokens() {
            return tokens;
        }

        public void setTokens(long tokens) {
            this.tokens = tokens;
        }

        public long getLifeTimeTPs() {
            return lifeTimeTPs;
        }

        public void increaseLifeTimeTPs() {
            this.lifeTimeTPs++;
        }

        public long getLifeTimeDupes() {
            return lifeTimeDupes;
        }

        public void increaseLifeTimeDupes() {
            this.lifeTimeDupes++;
        }
    }
}

