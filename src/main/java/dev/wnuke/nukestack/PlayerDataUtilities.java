package dev.wnuke.nukestack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

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
    public static HashMap<UUID, PlayerData> playerData;

    public static HashSet<PlayerData> loadAllPlayerData() {
        HashSet<PlayerData> playerDataHashMap = new HashSet<>();
        File playerDataDir = new File(NukeStack.playerDataFolder);
        if (playerDataDir.isDirectory() && playerDataDir.listFiles() != null) {
            for (File file : Objects.requireNonNull(playerDataDir.listFiles())) {
                UUID playerID = UUID.fromString(file.getName().replace(".json", ""));
                playerDataHashMap.add(loadPlayerData(playerID));
            }
        }
        return playerDataHashMap;
    }

    public static PlayerData loadPlayerData(UUID player) {
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

    public static PlayerData loadPlayerDataNoCache(UUID player) {
        File playerDataFile = new File(NukeStack.playerDataFolder + player.toString() + ".json");
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

    public static void savePlayerData(UUID player, PlayerData newPlayerData) {
        playerData.remove(player);
        playerData.putIfAbsent(player, newPlayerData);
        File playerDataFile = new File(NukeStack.playerDataFolder + player.toString() + ".json");
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
}
