package dev.wnuke.nukestack.permissions;

import com.google.gson.reflect.TypeToken;
import dev.wnuke.nukestack.NukeStack;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.UUID;

public class PermissionsUtility {
    public static final String groupFolder = "plugins/nukestack/groups/";
    private static final HashMap<UUID, PermissionAttachment> permissionsMap = new HashMap<>();
    private static Group defaultGroup = new Group("default");
    public static final HashMap<String, Group> groups = new HashMap<>();

    public static Group getGroup(String groupName) {
        Group group = groups.get(groupName);
        if (group == null) {
            group = loadGroup(groupName);
        }
        return group;
    }

    public static PermissionAttachment getPermissionsAttachement(Player player) {
        if (NukeStack.PLUGIN == null) return null;
        PermissionAttachment permissionAttachment = permissionsMap.get(player.getUniqueId());
        if (permissionAttachment == null) {
            permissionAttachment = player.addAttachment(NukeStack.PLUGIN);
            permissionsMap.put(player.getUniqueId(), permissionAttachment);
        }
        return permissionAttachment;
    }

    public static Group loadGroup(String groupName) {
        File groupFile = new File(groupFolder + groupName + ".json");
        groupFile.getParentFile().mkdirs();
        try {
            return NukeStack.gson.fromJson(new FileReader(groupFile), new TypeToken<Group>() {
            }.getType());
        } catch (FileNotFoundException e) {
            return new Group(groupName).save();
        }
    }
}
