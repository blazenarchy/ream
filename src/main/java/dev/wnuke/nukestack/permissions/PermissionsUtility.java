package dev.wnuke.nukestack.permissions;

import dev.wnuke.nukestack.NukeStack;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.UUID;

public class PermissionsUtility {
    public static final String groupFolder = "plugins/nukestack/groups/";
    public static final HashMap<String, Group> groups = new HashMap<>();
    public static final HashMap<UUID, PermissionAttachment> permissionsMap = new HashMap<>();
    public static final Group defaultGroup = new Group("default").load();

    public static Group getGroup(String groupName) {
        if (groupName != null) {
            if (!groupName.equals("default")) {
                Group group = groups.get(groupName);
                if (group == null) {
                    group = new Group(groupName).load();
                }
                return group;
            }
        }
        return defaultGroup;
    }
}
