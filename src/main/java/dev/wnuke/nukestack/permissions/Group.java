package dev.wnuke.nukestack.permissions;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import dev.wnuke.nukestack.NukeStack;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import static dev.wnuke.nukestack.permissions.PermissionsUtility.groupFolder;

public class Group {
    @SerializedName("Name")
    private final String name;
    @SerializedName("Chat Prefix")
    private String prefix = "";
    @SerializedName("Permissions")
    private HashMap<String, Boolean> permissions;

    public Group(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    private void attachToPlayer(Player player, PermissionAttachment permissionAttachment) {
        if (NukeStack.PLUGIN == null) return;
        for (Map.Entry<String, Boolean> permission : permissions.entrySet()) {
            if (permission.getKey().startsWith("ns.group.") && !permission.getKey().equals("ns.group.")) {
                PermissionsUtility.getGroup(permission.getKey().replaceFirst("ns.group.", "")).attachToPlayer(player, permissionAttachment);
            } else {
                permissionAttachment.setPermission(permission.getKey(), permission.getValue());
            }
        }
    }

    public void attachToPlayer(Player player) {
        attachToPlayer(player, PermissionsUtility.getPermissionsAttachement(player));
    }

    public Group save() {
        PermissionsUtility.groups.remove(name);
        PermissionsUtility.groups.putIfAbsent(name, this);
        File playerDataFile = new File(groupFolder + name + ".json");
        playerDataFile.getParentFile().mkdirs();
        try {
            playerDataFile.createNewFile();
            FileWriter fw = new FileWriter(playerDataFile);
            fw.write(NukeStack.gson.toJson(this));
            fw.flush();
            fw.close();
        } catch (Exception e) {
            System.out.println("Failed to save group " + name + ", error:");
            e.printStackTrace();
        }
        return this;
    }

    public Group load() {
        File groupFile = new File(groupFolder + name + ".json");
        groupFile.getParentFile().mkdirs();
        try {
            return NukeStack.gson.fromJson(new FileReader(groupFile), new TypeToken<Group>() {
            }.getType());
        } catch (FileNotFoundException e) {
            return this.save();
        }
    }
}
