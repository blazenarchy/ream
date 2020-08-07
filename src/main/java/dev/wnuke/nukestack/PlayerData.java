package dev.wnuke.nukestack;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class PlayerData {
    @SerializedName("ts")
    private long tokens = NukeStack.startingMoney;
    @SerializedName("tt")
    private long lifeTimeTPs = 0;
    @SerializedName("td")
    private long lifeTimeDupes = 0;
    @SerializedName("nn")
    private String nickName = "";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerData)) return false;
        PlayerData that = (PlayerData) o;
        return getTokens() == that.getTokens() &&
                getLifeTimeTPs() == that.getLifeTimeTPs() &&
                getLifeTimeDupes() == that.getLifeTimeDupes() &&
                getNickName().equals(that.getNickName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTokens(), getLifeTimeTPs(), getLifeTimeDupes(), getNickName());
    }

    @Override
    public String toString() {
        return "PlayerData{" +
                "tokens=" + tokens +
                ", lifeTimeTPs=" + lifeTimeTPs +
                ", lifeTimeDupes=" + lifeTimeDupes +
                ", nickName='" + nickName + '\'' +
                '}';
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public long getTokens() {
        if (NukeStack.currency) {
            return tokens;
        } else {
            return 9223372036854775807L;
        }
    }

    public void addTokens(long amount) {
        if (NukeStack.currency) {
            tokens += amount;
        }
    }

    public void removeTokens(long amount) {
        if (NukeStack.currency) {
            tokens -= amount;
        }
    }

    public long getLifeTimeTPs() {
        return lifeTimeTPs;
    }

    public void increaseLifeTimeTPs() {
        this.lifeTimeTPs++;
    }

    public long getLifeTimeDupes() {
        return lifeTimeDupes;
    }

    public void increaseLifeTimeDupes() {
        this.lifeTimeDupes++;
    }
}
