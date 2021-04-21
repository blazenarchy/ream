package net.blazenarchy.ream.permissions;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import net.blazenarchy.ream.Ream;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Group {
    @SerializedName("Name")
    private final String name;
    @SerializedName("Permissions")
    private final HashMap<String, Boolean> permissions = new HashMap<>();
    @SerializedName("Chat Prefix")
    private String prefix = "";

    public Group(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public Group attachToPlayer(PermissionAttachment permissionAttachment, HashSet<String> permissionsGiven) {
        if (permissionsGiven == null) permissionsGiven = new HashSet<>();
        HashSet<String> groups = new HashSet<>();
        for (Map.Entry<String, Boolean> permission : permissions.entrySet()) {
            if (permission.getKey().startsWith("ns.group.") && !permission.getKey().equals("ns.group.")) {
                groups.add((permission.getKey()));
            } else {
                if (!permissionsGiven.contains(permission.getKey())) {
                    permissionAttachment.setPermission(permission.getKey(), permission.getValue());
                    permissionsGiven.add(permission.getKey());
                }
            }
        }
        for (String group : groups) {
            PermissionsUtility.getGroup(group.replaceFirst("ns.group.", "")).attachToPlayer(permissionAttachment, permissionsGiven);
        }
        return this;
    }

    public Group attachToPlayer(Player player) {
        PermissionAttachment permissionAttachment = PermissionsUtility.permissionsMap.get(player.getUniqueId());
        if (permissionAttachment == null) {
            permissionAttachment = player.addAttachment(Ream.PLUGIN);
            PermissionsUtility.permissionsMap.put(player.getUniqueId(), permissionAttachment);
        }
        attachToPlayer(permissionAttachment, null);
        return this;
    }

    public Group save() {
        PermissionsUtility.groups.remove(name);
        PermissionsUtility.groups.putIfAbsent(name, this);
        File playerDataFile = new File(PermissionsUtility.groupFolder + name + ".json");
        playerDataFile.getParentFile().mkdirs();
        try {
            playerDataFile.createNewFile();
            FileWriter fw = new FileWriter(playerDataFile);
            fw.write(Ream.gson.toJson(this));
            fw.flush();
            fw.close();
        } catch (Exception e) {
            System.out.println("Failed to save group " + name + ", error:");
            e.printStackTrace();
        }
        return this;
    }

    public Group load() {
        File groupFile = new File(PermissionsUtility.groupFolder + name + ".json");
        groupFile.getParentFile().mkdirs();
        try {
            return Ream.gson.fromJson(new FileReader(groupFile), new TypeToken<Group>() {
            }.getType());
        } catch (FileNotFoundException e) {
            return this.save();
        }
    }
}
