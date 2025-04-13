package io.github.aleksandarharalanov.chatguard.core.security.penalty;

import com.earth2me.essentials.Essentials;
import io.github.aleksandarharalanov.chatguard.core.config.FilterConfig;
import io.github.aleksandarharalanov.chatguard.core.config.PenaltyConfig;
import io.github.aleksandarharalanov.chatguard.core.log.LogAttribute;
import io.github.aleksandarharalanov.chatguard.core.log.LogType;
import io.github.aleksandarharalanov.chatguard.core.security.common.TimeFormatter;
import io.github.aleksandarharalanov.chatguard.core.security.penalty.plugin.EssentialsMuteHandler;
import io.github.aleksandarharalanov.chatguard.core.security.penalty.plugin.ZCoreMuteHandler;
import io.github.aleksandarharalanov.chatguard.util.misc.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public final class PenaltyEnforcer {

    private static final IMuteHandler muteHandler;

    static {
        PluginManager pM = Bukkit.getServer().getPluginManager();
        if (pM.getPlugin("Essentials") != null) {
            muteHandler = new EssentialsMuteHandler((Essentials) pM.getPlugin("Essentials"));
        } else if (pM.getPlugin("ZCore") != null) {
            muteHandler = new ZCoreMuteHandler();
        } else {
            muteHandler = null;
        }
    }

    private PenaltyEnforcer() {}

    public static void processMute(LogType logType, Player player) {
        if (muteHandler == null) {
            System.out.println("[ChatGuard] No compatible plugin found for auto mute feature. Please disable in config.");
            return;
        }

        if (!FilterConfig.getAutoMuteEnabled()) {
            return;
        }

        if (!logType.hasAttribute(LogAttribute.MUTE)) {
            return;
        }

        try {
            String duration = PenaltyConfig.getAutoMuteDuration(player);

            long timeStamp = TimeFormatter.parseDateDiff(duration, true);

            muteHandler.setPlayerMuteTimeout(player.getName(), timeStamp);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Bukkit.getServer().broadcastMessage(ColorUtil.translateColorCodes(String.format(
                "&c[ChatGuard] %s muted for %s. by system; content has bad words.",
                player.getName(), PenaltyConfig.getAutoMuteDuration(player)
        )));
    }

    public static void incrementStrikeTier(LogType logType, Player player, int severity) {
        if (!logType.hasAttribute(LogAttribute.STRIKE)) {
            return;
        }

        PenaltyConfig.incrementPlayerStrike(player, severity);
    }

    public static IMuteHandler getMuteHandler() {
        return muteHandler;
    }

    public static void updatePlayerStrikes(Player player) {
        if(!FilterConfig.getStrikeDecayEnabled())
            return;

            
        final int playerStrikeCount = PenaltyConfig.getPlayerStrike(player);
        if(playerStrikeCount <= 0)
            return; // player has no strikes

        final long lastMuteTime = PenaltyConfig.getLastMuteTime(player);

        final long timePassed = System.currentTimeMillis() - lastMuteTime;
        final long decayPeriod = FilterConfig.getStrikeDecayPeriod();

        final int strikesToRevoke = Math.min(
            (int) (timePassed / decayPeriod),
            playerStrikeCount
        );

        // this is only really done to prevent longer decay times than would normally happen under the config
        final long totalRevokePeriod = strikesToRevoke * decayPeriod;
        final long newPlayerUpdatedTime = lastMuteTime + totalRevokePeriod;

        PenaltyConfig.decrementPlayerStrike(player, strikesToRevoke, newPlayerUpdatedTime);

        String pointsString = "point";
        if(strikesToRevoke != 1)
            pointsString += "s";

        final String coloredMessage = ColorUtil.translateColorCodes(String.format("&aYour ChatGuard strike count has been reduced by %d %s! You have %d remaining", strikesToRevoke, pointsString, playerStrikeCount - strikesToRevoke));
        player.sendMessage(coloredMessage);
    }

    public static void handleWarning(Player player) {
        PenaltyConfig.incrementPlayerWarnings(player);

        final String coloredMessage = ColorUtil.translateColorCodes("&cWarning number: " + PenaltyConfig.getPlayerWarnings(player));
        player.sendMessage(coloredMessage);
    }
}
