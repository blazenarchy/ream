package dev.wnuke.nukestack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class PlayerDataUtilities {
    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    public static final String playerDataFolder = "plugins/nukestack/player-data/";
    public static HashMap<UUID, PlayerData> playerData;

    public static PlayerData loadExistingPlayerData(UUID player) {
        PlayerData loadedData = playerData.getOrDefault(player, null);
        if (loadedData != null) {
            return loadedData;
        } else {
            return loadPlayerDataNoCreate(player);
        }
    }

    public static PlayerData loadPlayerData(OfflinePlayer player) {
        PlayerData loadedData = loadExistingPlayerData(player.getUniqueId());
        if (loadedData == null) {
            loadedData = loadPlayerDataNoCache(player);
        }
        loadedData.setUuidIfNull(player.getUniqueId());
        playerData.remove(player.getUniqueId());
        playerData.put(player.getUniqueId(), loadedData);
        return loadedData;
    }

    public static PlayerData loadPlayerDataNoCreate(UUID player) {
        File playerDataFile = new File(playerDataFolder + player.toString() + ".json");
        playerDataFile.getParentFile().mkdirs();
        try {
            return ((PlayerData) gson.fromJson(new FileReader(playerDataFile), new TypeToken<PlayerData>() {
            }.getType())).setUuidIfNull(player);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static PlayerData loadPlayerDataNoCache(OfflinePlayer player) {
        File playerDataFile = new File(playerDataFolder + player.getUniqueId().toString() + ".json");
        playerDataFile.getParentFile().mkdirs();
        try {
            return gson.fromJson(new FileReader(playerDataFile), new TypeToken<PlayerData>() {
            }.getType());
        } catch (FileNotFoundException e) {
            return new PlayerData(player).save();
        }
    }

    public static void savePlayerData(PlayerData newPlayerData) {
        playerData.remove(newPlayerData.getUuid());
        playerData.putIfAbsent(newPlayerData.getUuid(), newPlayerData);
        File playerDataFile = new File(playerDataFolder + newPlayerData.getUuid().toString() + ".json");
        playerDataFile.getParentFile().mkdirs();
        try {
            playerDataFile.createNewFile();
            FileWriter fw = new FileWriter(playerDataFile);
            fw.write(gson.toJson(newPlayerData));
            fw.flush();
            fw.close();
        } catch (Exception e) {
            System.out.println("Failed to save player data for " + newPlayerData.getUuid().toString() + ", error:");
            e.printStackTrace();
        }
    }

    public static HashSet<PlayerData> loadAllPlayerData() {
        HashSet<PlayerData> playerDataHashMap = new HashSet<>();
        File playerDataDir = new File(playerDataFolder);
        if (playerDataDir.isDirectory() && playerDataDir.listFiles() != null) {
            for (File file : Objects.requireNonNull(playerDataDir.listFiles())) {
                try {
                    UUID playerID = UUID.fromString(file.getName().replace(".json", ""));
                    playerDataHashMap.add(loadExistingPlayerData(playerID));
                } catch (Exception ignored) {
                }
            }
        }
        return playerDataHashMap;
    }
}
