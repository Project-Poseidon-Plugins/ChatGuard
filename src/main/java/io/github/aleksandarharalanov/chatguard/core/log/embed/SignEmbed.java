package io.github.aleksandarharalanov.chatguard.core.log.embed;

import io.github.aleksandarharalanov.chatguard.core.config.DiscordConfig;
import io.github.aleksandarharalanov.chatguard.core.config.PenaltyConfig;
import io.github.aleksandarharalanov.chatguard.core.log.LogType;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.Color;

public final class SignEmbed extends DiscordEmbed {

    private final String trigger;
    private final Location violationLocation;

    public SignEmbed(JavaPlugin plugin, Player player, String content, String trigger, Location violationLocation) {
        super(plugin, player, content);
        this.trigger = trigger;
        this.violationLocation = violationLocation;
        setupBaseEmbed();
    }

    @Override
    protected void setupEmbedDetails() {
        embed.setDescription(String.format(
            "Strike: %d",
            PenaltyConfig.getPlayerStrikes(player) + 1
        ));

        embed.setTitle("Sign Filter")
                .addField("Content:", content, false)
                .addField("Location:", getLocationString(), false)
                .addField("Trigger:", String.format("`%s`", trigger), true)
                .setColor(Color.decode(DiscordConfig.getEmbedColor(LogType.SIGN)));
    }

    private String getLocationString() {
        return String.format(
            "Location: %s, `/tppos %d %d %d`",
            violationLocation.getWorld().getName(),
            (int)violationLocation.getX(),
            (int)violationLocation.getY(),
            (int)violationLocation.getZ()
        );
    }
}
