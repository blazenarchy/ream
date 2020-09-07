package dev.wnuke.nukestack.permissions;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import dev.wnuke.nukestack.NukeStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import static dev.wnuke.nukestack.permissions.PermissionsUtility.trackFolder;
import static dev.wnuke.nukestack.player.PlayerDataUtilities.playerDataFolder;

public class Track {
    @SerializedName("Name")
    private final String name;
    @SerializedName("Order")
    ArrayList<String> order = new ArrayList<>();

    public Track(String name) {
        this.name = name;
    }

    public String getNext(String current) {
        if (order.contains(current)) {
            int currentIndex = order.indexOf(current);
            if (currentIndex < order.size() - 1) {
                return order.get(currentIndex + 1);
            }
        }
        return current;
    }

    public String getPrevious(String current) {
        if (order.contains(current)) {
            int currentIndex = order.indexOf(current);
            if (currentIndex != 0) {
                return order.get(currentIndex - 1);
            }
        }
        return current;
    }

    public Track save() {
        PermissionsUtility.tracks.remove(name);
        PermissionsUtility.tracks.putIfAbsent(name, this);
        File trackFile = new File(playerDataFolder + name + ".json");
        trackFile.getParentFile().mkdirs();
        try {
            trackFile.createNewFile();
            FileWriter fw = new FileWriter(trackFile);
            fw.write(NukeStack.gson.toJson(this));
            fw.flush();
            fw.close();
        } catch (Exception e) {
            System.out.println("Failed to save track data for " + name + ", error:");
            e.printStackTrace();
        }
        return this;
    }

    public Track load() {
        File trackFile = new File(trackFolder + name + ".json");
        trackFile.getParentFile().mkdirs();
        try {
            return NukeStack.gson.fromJson(new FileReader(trackFile), new TypeToken<Track>() {
            }.getType());
        } catch (FileNotFoundException e) {
            System.out.println("Could not load track " + name + ", error:");
            e.printStackTrace();
        }
        return null;
    }
}
