package dev.wnuke.nukestack.permissions;

import dev.wnuke.nukestack.NukeStack;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.UUID;

public class PermissionsUtility {
    public static final String groupFolder = "plugins/nukestack/groups/";
    public static final HashMap<String, Group> groups = new HashMap<>();
    private static final HashMap<UUID, PermissionAttachment> permissionsMap = new HashMap<>();
    private static final Group defaultGroup = new Group("default").load();

    public static Group getGroup(String groupName) {
        if (groupName.equals("default")) {
            return defaultGroup;
        }
        Group group = groups.get(groupName);
        if (group == null) {
            group = new Group(groupName).load();
        }
        return group;
    }

    public static PermissionAttachment getPermissionsAttachement(Player player) {
        PermissionAttachment permissionAttachment = permissionsMap.get(player.getUniqueId());
        if (permissionAttachment == null) {
            permissionAttachment = player.addAttachment(NukeStack.PLUGIN);
            permissionsMap.put(player.getUniqueId(), permissionAttachment);
        }
        return permissionAttachment;
    }
}
