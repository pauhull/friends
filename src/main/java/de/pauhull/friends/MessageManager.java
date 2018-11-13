package de.pauhull.friends;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;

public class MessageManager {

    @Getter
    private String prefix, requestReceived, onlyPlayers, playerDoesntExist, alreadyRequested, requestSent, requestWithdrawn,
            openRequests;

    public MessageManager load(Configuration config) {
        this.prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.Prefix"));
        this.requestReceived = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.RequestReceived"));
        this.onlyPlayers = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.OnlyPlayers"));
        this.playerDoesntExist = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.PlayerDoesntExist"));
        this.alreadyRequested = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.AlreadyRequested"));
        this.requestSent = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.RequestSent"));
        this.requestWithdrawn = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.RequestWithdrawn"));
        this.openRequests = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.OpenRequests"));
        return this;
    }

}
