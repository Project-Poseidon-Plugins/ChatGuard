package io.github.aleksandarharalanov.chatguard.core.log.logger;

import io.github.aleksandarharalanov.chatguard.core.config.CaptchaConfig;
import io.github.aleksandarharalanov.chatguard.core.config.FilterConfig;
import io.github.aleksandarharalanov.chatguard.core.log.LogAttribute;
import io.github.aleksandarharalanov.chatguard.core.log.LogType;
import io.github.aleksandarharalanov.chatguard.util.log.LogUtil;
import org.bukkit.entity.Player;

public final class ConsoleLogger {

    private ConsoleLogger() {}

    public static void log(LogType logType, Player player, String content) {
        if (!shouldConsoleLogEnabled(logType)) return;

        String logMessage = String.format("[ChatGuard] [%s]", logType.name());
        switch (logType) {
            case CHAT:
                logMessage = String.format("%s Stopped player '%s'; Bad content: '%s'", logMessage, player.getName(), content);
                break;
            case SIGN:
                logMessage = String.format("%s Stopped player '%s'; Bad sign: '%s'", logMessage, player.getName(), content);
                break;
            case NAME:
                logMessage = String.format("%s Stopped player '%s'; Bad name.", logMessage, content);
                break;
            case CAPTCHA:
                logMessage = String.format("%s Stopped player '%s'; Triggered captcha: '%s'", logMessage, player.getName(), content);
                break;
            default:
                return;
        }

        LogUtil.logConsoleInfo(logMessage);
    }

    private static boolean shouldConsoleLogEnabled(LogType logType) {
        if (logType.hasAttribute(LogAttribute.FILTER)) {
            return FilterConfig.getLogConsoleEnabled();
        }
        return CaptchaConfig.getLogConsoleEnabled();
    }
}
