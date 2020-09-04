package dev.wnuke.nukestack;

import com.google.gson.annotations.SerializedName;
import org.bukkit.Location;
import org.bukkit.Server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class PlayerData {
    @SerializedName("ts")
    private long tokens = NukeStack.startingMoney;
    @SerializedName("tt")
    private long lifeTimeTPs = 0;
    @SerializedName("td")
    private long lifeTimeDupes = 0;
    @SerializedName("nn")
    private String nickName = "";
    @SerializedName("kills")
    private long kills = 0;
    @SerializedName("deaths")
    private long deaths = 0;
    @SerializedName("ks")
    private long killStreak = 0;
    @SerializedName("ig")
    private ArrayList<UUID> ignored = new ArrayList<>();
    @SerializedName("ll")
    private LogoutLocation logoutLocation = new LogoutLocation();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerData)) return false;
        PlayerData that = (PlayerData) o;
        return this.getTokens() == that.getTokens() &&
                this.getLifeTimeTPs() == that.getLifeTimeTPs() &&
                this.getLifeTimeDupes() == that.getLifeTimeDupes() &&
                this.getNickName().equals(that.getNickName());
    }

    public String getNickName() {
        return this.nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public long getTokens() {
        if (NukeStack.currency) {
            return this.tokens;
        } else {
            return Long.MAX_VALUE;
        }
    }

    public void addTokens(long amount) {
        if (NukeStack.currency) {
            if (amount < 0) {
                amount *= -1;
            }
            this.tokens += amount;
        }
    }

    public void removeTokens(long amount) {
        if (NukeStack.currency) {
            if (amount < 0) {
                amount *= -1;
            }
            this.tokens -= amount;
        }
    }

    public void setLogoutLocation(LogoutLocation logoutLocation) {
        this.logoutLocation = logoutLocation;
    }

    public Location getLogoutLocation(Server server) {
        return logoutLocation.asLocation(server);
    }

    public long getLifeTimeTPs() {
        return this.lifeTimeTPs;
    }

    public void increaseLifeTimeTPs() {
        this.lifeTimeTPs++;
    }

    public long getLifeTimeDupes() {
        return this.lifeTimeDupes;
    }

    public void increaseLifeTimeDupes() {
        this.lifeTimeDupes++;
    }
}
