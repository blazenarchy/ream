package dev.wnuke.nukestack;


import com.google.gson.annotations.SerializedName;
import org.bukkit.Location;
import org.bukkit.Server;

import java.util.Objects;

public class LastLocation {
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

    public static LastLocation fromLocation(Location location) {
        LastLocation lastLocation = new LastLocation();
        lastLocation.world = location.getWorld().getName();
        lastLocation.x = location.getX();
        lastLocation.y = location.getY();
        lastLocation.z = location.getZ();
        lastLocation.pitch = location.getPitch();
        lastLocation.yaw = location.getYaw();
        return lastLocation;
    }

    public Location asLocation(Server server) {
        return new Location(server.getWorld(world), x, y, z, pitch, yaw);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LastLocation)) return false;
        LastLocation that = (LastLocation) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0 &&
                Double.compare(that.z, z) == 0 &&
                Float.compare(that.pitch, pitch) == 0 &&
                Float.compare(that.yaw, yaw) == 0 &&
                world.equals(that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z, pitch, yaw);
    }

    @Override
    public String toString() {
        return "LastLocation{" +
                "world='" + world + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", pitch=" + pitch +
                ", yaw=" + yaw +
                '}';
    }
}
