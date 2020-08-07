package dev.wnuke.nukestack;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class PlayerData {
    @SerializedName("ts")
    private long tokens = 0;
    @SerializedName("tt")
    private long lifeTimeTPs = 0;
    @SerializedName("td")
    private long lifeTimeDupes = 0;

    @Override
    public String toString() {
        return "PlayerData{" +
                "tokens=" + tokens +
                ", lifeTimeTPs=" + lifeTimeTPs +
                ", lifeTimeDupes=" + lifeTimeDupes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerData)) return false;
        PlayerData that = (PlayerData) o;
        return getTokens() == that.getTokens() &&
                getLifeTimeTPs() == that.getLifeTimeTPs() &&
                getLifeTimeDupes() == that.getLifeTimeDupes();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTokens(), getLifeTimeTPs(), getLifeTimeDupes());
    }

    public long getTokens() {
        return tokens;
    }

    public void addTokens(long amount) {
        tokens += amount;
    }

    public void removeTokens(long amount) {
        tokens -= amount;
    }

    public void setTokens(long tokens) {
        this.tokens = tokens;
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
