package io.github.aleksandarharalanov.chatguard.core.log.logger;

import io.github.aleksandarharalanov.chatguard.ChatGuard;
import io.github.aleksandarharalanov.chatguard.core.config.DiscordConfig;
import io.github.aleksandarharalanov.chatguard.core.config.FilterTerm;
import io.github.aleksandarharalanov.chatguard.core.log.LogType;
import io.github.aleksandarharalanov.chatguard.core.log.embed.*;
import io.github.aleksandarharalanov.chatguard.util.log.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;

public final class DiscordLogger {

    private DiscordLogger() {}

    public static void log(LogType logType, Player player, String content) {
        log(logType, player, content, null, false, null);
    }

    public static void log(LogType logType, Player player, String content, FilterTerm triggerFilter, boolean warned, Location violationLocation) 
    {
        if (!DiscordConfig.getDiscordLogEnabled(logType)) {
            return;
        }

        String webhookUrl = DiscordConfig.getWebhookUrl();
        String webhookName = DiscordConfig.getWebhookName(logType);
        String webhookIcon = DiscordConfig.getWebhookIcon(logType);

        DiscordUtil webhook = new DiscordUtil(webhookUrl);
        webhook.setUsername(webhookName);
        webhook.setAvatarUrl(webhookIcon);

        
        String trigger = "";
        int severity = 0;
        
        if(triggerFilter != null) {
            trigger = triggerFilter.getName();
            severity = triggerFilter.getSeverity();
        }

        DiscordEmbed embed;
        switch (logType) {
            case CHAT:
                embed = new ChatEmbed(ChatGuard.getInstance(), player, content, trigger, severity, warned);
                break;
            case SIGN:
                embed = new SignEmbed(ChatGuard.getInstance(), player, content, trigger, violationLocation);
                break;
            case NAME:
                embed = new NameEmbed(ChatGuard.getInstance(), player, content, trigger);
                break;
            case CAPTCHA:
                embed = new CaptchaEmbed(ChatGuard.getInstance(), player, content);
                break;
            default:
                System.out.println("[ChatGuard] Something went wrong when constructing webhook embed to log.");
                return;
        }
        webhook.addEmbed(embed.getEmbed());

        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(ChatGuard.getInstance(), () -> {
            try {
                webhook.execute();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }, 1L);
    }
}
