package io.github.aleksandarharalanov.chatguard.core.security.filter;

import io.github.aleksandarharalanov.chatguard.core.config.FilterConfig;
import io.github.aleksandarharalanov.chatguard.core.log.LogType;
import io.github.aleksandarharalanov.chatguard.core.security.common.ContentHandler;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

public final class FilterHandler {

    private FilterHandler() {}

    public static boolean isChatContentBlocked(Player player, String content) {
        return isBlocked(LogType.CHAT, player, content, null);
    }

    public static boolean isSignContentBlocked(Player player, SignChangeEvent event) {
        String[] content = event.getLines();
        Location eventLocation = event.getBlock().getLocation();

        return isBlocked(LogType.SIGN, player, ContentHandler.mergeContent(content), eventLocation);
    }

    public static boolean isPlayerNameBlocked(Player player) {
        return isBlocked(LogType.NAME, player, player.getName(), null);
    }

    private static boolean isBlocked(LogType logType, Player player, String content, Location violationLocation) {
        FilterTrigger trigger = GetBlockedTrigger(content);

        if (trigger == null)
            return false;

        trigger.setViolationLocation(violationLocation);

        FilterFinalizer.finalizeActions(logType, player, content, trigger);
        return true;
    }

    public static FilterTrigger GetBlockedTrigger(String content) {
        String sanitizedContent = ContentHandler.sanitizeContent(content, FilterConfig.getWhitelist());
        FilterTrigger trigger = FilterDetector.checkFilters(sanitizedContent);

        if (trigger == null)
            return null;

        return trigger;
    }
}
