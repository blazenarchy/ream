package dev.wnuke.simpledupe;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Blazenarchy's Dupe plugin, a configurable way of duplicating items without exploits.
 *
 * @author wnuke
 */
public final class SimpleDupe extends JavaPlugin {
    public static FileConfiguration CONFIG;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getCommand("dupe").setExecutor(new DupeCommand(this));
        CONFIG = this.getConfig();
    }

    private static class DupeCommand implements CommandExecutor, Listener {
        JavaPlugin plugin;
        public DupeCommand(JavaPlugin plugin) {
            this.plugin = plugin;
        }
        HashMap<String, Long> players = new HashMap<>();
        @EventHandler
        public void onTick(ServerTickEndEvent event) {
            for (Map.Entry<String, Long> player : players.entrySet()) {
                if (player.getValue() + CONFIG.getLong("delay") < System.currentTimeMillis()) {
                    players.remove(player.getKey());
                }
            }
        }

        private void help(CommandSender sender) {
            sender.sendMessage("Invalid argument(s), valid ones are:");
            sender.sendMessage(" - wait <ticks>: changes the amount of time players have to wait before their items are dupicated.");
            sender.sendMessage(" - delay <milliseconds>: changes the amount of time players have to wait before running the command again.");
            sender.sendMessage(" - mode <mode>: changes the current dupe mode.");
            sender.sendMessage(" - show: displays the current values of all settings.");
            sender.sendMessage(" - reload: reloads the configuration file.");
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender.hasPermission("simpledupe.config") && args.length > 0) {
                switch (args[0]) {
                    case "wait":
                        if (args.length == 2) {
                            try {
                                CONFIG.set("wait", Long.parseLong(args[1]));
                                sender.sendMessage("Configuration has been updated.");
                            } catch (NumberFormatException e) {
                                sender.sendMessage("Second argument must be a non decimal number.");
                            }
                        } else help(sender);
                        break;
                    case "delay":
                        if (args.length == 2) {
                            try {
                                CONFIG.set("delay", Long.parseLong(args[1]));
                                sender.sendMessage("Configuration has been updated.");
                            } catch (NumberFormatException e) {
                                sender.sendMessage("Second argument must be a non decimal number.");
                            }
                        } else help(sender);
                        break;
                    case "mode":
                        if (args.length == 2) {
                            String[] validModes = {"donkey"};
                            if (Arrays.asList(validModes).contains(args[1])) {
                                CONFIG.set("mode", args[1]);
                                sender.sendMessage("Configuration has been updated.");
                            } else {
                                sender.sendMessage("\"" + args[1] + "\" is not a valid mode, valid modes are:");
                                for (String mode : validModes) {
                                    sender.sendMessage(" - " + mode);
                                }
                            }
                        } else help(sender);
                        break;
                    case "show":
                        sender.sendMessage("Current configuration values are:");
                        sender.sendMessage("mode: " + CONFIG.getString("mode"));
                        sender.sendMessage("wait: " + CONFIG.getString("wait"));
                        sender.sendMessage("delay: " + CONFIG.getString("delay"));
                        break;
                    case "reload":
                        try {
                            CONFIG.load(plugin.getDataFolder().getAbsolutePath() + "/config.yml");
                            sender.sendMessage("Configuration has been reloaded.");
                        } catch (IOException | InvalidConfigurationException e) {
                            sender.sendMessage("Configuration failed to reload.");
                            e.printStackTrace();
                        }
                        break;
                    case "default":
                        help(sender);
                        break;
                }
                try {
                    CONFIG.save(plugin.getDataFolder().getAbsolutePath() + "/config.yml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (!(sender instanceof ConsoleCommandSender)) {
                if (players.containsKey(sender.getName()) && !sender.hasPermission("simpledupe.nodelay")) {
                    sender.sendMessage("You must wait " + (players.get(sender.getName()) * 1000) + " seconds before running this command again.");
                } else {
                    if (Objects.equals(CONFIG.get("mode"), "donkey")) {
                        Player player = (Player) sender;
                        ItemStack[] itemStacks;
                        if (player.getVehicle() instanceof Donkey) {
                            itemStacks = ((Donkey) player.getVehicle()).getInventory().getStorageContents();
                        } else if (player.getVehicle() instanceof Llama) {
                            itemStacks = ((Llama) player.getVehicle()).getInventory().getStorageContents();
                        } else {
                            player.sendMessage("You must be riding a donkey or a llama to use this command.");
                            return true;
                        }
                        long delay = 0L;
                        if (!player.hasPermission("simpledupe.instant")) {
                            delay = CONFIG.getLong("wait");
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                            if (!player.isInsideVehicle()) {
                                player.sendMessage("You must stay on your Donkey/Llama until the dupe completes.");
                                return;
                            }
                            for (ItemStack itemStack : itemStacks) {
                                if (!(itemStack == null)) {
                                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                                }
                            }
                            player.sendMessage("Your items have been duplicated.");
                        }, delay);
                    }
                }
            } else {
              sender.sendMessage("Requires arguments to run as console.");
            }
            return true;
        }
    }
}
