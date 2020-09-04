package dev.wnuke.nukestack;


import com.google.gson.annotations.SerializedName;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

public class LogoutLocation {
    @SerializedName("w")
    private String world = "world";
    @SerializedName("x")
    private double x = 0;
    @SerializedName("y")
    private double y = 0;
    @SerializedName("z")
    private double z = 0;
    @SerializedName("pi")
    private float pitch = 0;
    @SerializedName("ya")
    private float yaw = 0;

    public static LogoutLocation fromLocation(Location location) {
        LogoutLocation logoutLocation = new LogoutLocation();
        logoutLocation.world = location.getWorld().getName();
        logoutLocation.x = location.getX();
        logoutLocation.y = location.getY();
        logoutLocation.z = location.getZ();
        logoutLocation.pitch = location.getPitch();
        logoutLocation.yaw = location.getYaw();
        return logoutLocation;
    }

    public Location asLocation(Server server) {
        return new Location(server.getWorld(world), x, y, z, pitch, yaw);
    }
}
