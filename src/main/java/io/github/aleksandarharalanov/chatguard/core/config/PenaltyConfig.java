package io.github.aleksandarharalanov.chatguard.core.config;

import io.github.aleksandarharalanov.chatguard.ChatGuard;
import io.github.aleksandarharalanov.chatguard.util.config.ConfigUtil;

import org.bukkit.entity.Player;

import java.util.List;

public final class PenaltyConfig {

    private PenaltyConfig() {}

    public static List<String> getStrikesKeys() {
        return ChatGuard.getStrikes().getKeys();
    }

    public static int getPlayerStrikes(Player player) {
        return getPlayerStrikes(player.getName());
    }

    public static int getPlayerStrikes(String playerName) {
        return ChatGuard.getStrikes().getInt(playerName + ".strikes", 0);
    }

    public static int getPlayerWarnings(Player player) {
        return getPlayerWarnings(player.getName());
    }

    public static int getPlayerWarnings(String playerName) {
        return ChatGuard.getStrikes().getInt(playerName + ".warnings", 0);
    }

    public static void setPlayerStrikes(Player player, int newStrike) {
        setPlayerStrike(player.getName(), newStrike);
    }

    public static void decrementPlayerStrike(Player player, int amount, long updateTime) {
        setPlayerStrikes(player.getName(), getPlayerStrikes(player) - amount, updateTime);
    }

    public static void incrementPlayerStrike(Player player, int amount) {
        setPlayerStrikes(player, getPlayerStrikes(player) + amount);
    }

    public static void setPlayerStrike(String playerName, int newStrike) {
        setPlayerStrikes(playerName, newStrike, System.currentTimeMillis());
    }

    public static void setPlayerStrikes(String playerName, int newStrike, long updateTime) {
        ConfigUtil strikes = ChatGuard.getStrikes();

        newStrike = Math.max(newStrike, 0);

        if(newStrike == 0 && getPlayerWarnings(playerName) <= 0)
            strikes.removeProperty(playerName);
        else {
            strikes.setProperty(playerName + ".strikes", newStrike);
            strikes.setProperty(playerName + ".muteUpdated", updateTime);
        }

        ChatGuard.getStrikes().save();
    }

    public static void decrementPlayerWarning(Player player, int amount, long updateTime) {
        setPlayerWarnings(player.getName(), getPlayerWarnings(player) - amount, updateTime);
    }

    public static void incrementPlayerWarnings(Player player) {
        setPlayerWarnings(player, getPlayerWarnings(player) + 1);
    }

    public static void setPlayerWarnings(Player player, int warnings) {
        setPlayerWarnings(player.getName(), warnings);
    }

    public static void setPlayerWarnings(String playerName, int warnings) {
        setPlayerWarnings(playerName, warnings, System.currentTimeMillis());
    }

    public static void setPlayerWarnings(String playerName, int newWarning, long updateTime) {
        ConfigUtil strikes = ChatGuard.getStrikes();

        newWarning = Math.max(newWarning, 0);

        if(newWarning == 0 && getPlayerStrikes(playerName) <= 0)
            strikes.removeProperty(playerName);
        else {
            strikes.setProperty(playerName + ".warnings", newWarning);
            strikes.setProperty(playerName + ".warnUpdated", updateTime);
        }

        ChatGuard.getStrikes().save();
    }

    public static String getAutoMuteDuration(Player player) {
        final List<String> penalties = FilterConfig.getAutoMuteDurations();
        final int maxPenalty = penalties.size() - 1;

        int playerPenalty = getPlayerStrikes(player);

        if (playerPenalty > maxPenalty)
            return penalties.get(maxPenalty);

        return penalties.get(playerPenalty);
    }

    public static long getLastMuteTime(Player player) {
        return getLastMuteTime(player.getName());
    }
    
    public static long getLastMuteTime(String playerName) {
        final String lastUpdatedString = ChatGuard.getStrikes().getString(playerName + ".muteUpdated");
        if(lastUpdatedString == null)
            return -1;

        try {
            return Long.parseLong(lastUpdatedString);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public static long getLastWarnTime(Player player) {
        return getLastWarnTime(player.getName());
    }
    
    public static long getLastWarnTime(String playerName) {
        final String lastUpdatedString = ChatGuard.getStrikes().getString(playerName + ".warnUpdated");
        if(lastUpdatedString == null)
            return -1;

        try {
            return Long.parseLong(lastUpdatedString);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }
}
