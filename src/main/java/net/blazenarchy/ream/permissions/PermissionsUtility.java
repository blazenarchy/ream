package net.blazenarchy.ream.permissions;

import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.UUID;

public class PermissionsUtility {
    public static final String groupFolder = "plugins/Ream/groups/";
    public static final String trackFolder = "plugins/Ream/tracks/";
    public static final HashMap<String, Group> groups = new HashMap<>();
    public static final HashMap<String, Track> tracks = new HashMap<>();
    public static final HashMap<UUID, PermissionAttachment> permissionsMap = new HashMap<>();
    public static Group defaultGroup = new Group("default").load();

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

    public static Track getTrack(String trackName) {
        if (trackName != null) {
            if (!trackName.equals("")) {
                Track track = tracks.get(trackName);
                if (track == null) {
                    return new Track(trackName).load();
                }
                return track;
            }
        }
        return null;
    }
}
