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
        return this.getTokens() == that.getTokens() &&
                this.getLifeTimeTPs() == that.getLifeTimeTPs() &&
                this.getLifeTimeDupes() == that.getLifeTimeDupes() &&
                this.getNickName().equals(that.getNickName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTokens(), getLifeTimeTPs(), getLifeTimeDupes(), getNickName());
    }

    @Override
    public String toString() {
        return "PlayerData{" +
                "tokens=" + this.tokens +
                ", lifeTimeTPs=" + this.lifeTimeTPs +
                ", lifeTimeDupes=" + this.lifeTimeDupes +
                ", nickName='" + this.nickName + '\'' +
                '}';
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
