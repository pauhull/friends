package de.pauhull.friends;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;

public class MessageManager {

    @Getter
    private String prefix, requestReceived, onlyPlayers, playerDoesntExist, alreadyRequested, requestSent, requestWithdrawn,
            openRequests, youAccepted, youDenied, requestAccepted, requestDenied, noRequest, alreadyFriend, noFriend, friendRemoved;

    public MessageManager load(Configuration config) {
        this.prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.Prefix"));
        this.requestReceived = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.RequestReceived"));
        this.onlyPlayers = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.OnlyPlayers"));
        this.playerDoesntExist = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.PlayerDoesntExist"));
        this.alreadyRequested = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.AlreadyRequested"));
        this.requestSent = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.RequestSent"));
        this.requestWithdrawn = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.RequestWithdrawn"));
        this.openRequests = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.OpenRequests"));
        this.youAccepted = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.YouAccepted"));
        this.youDenied = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.YouDenied"));
        this.requestAccepted = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.RequestAccepted"));
        this.requestDenied = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.RequestDenied"));
        this.noRequest = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.NoRequest"));
        this.alreadyFriend = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.AlreadyFriend"));
        this.noFriend = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.NoFriend"));
        this.friendRemoved = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.FriendRemoved"));
        return this;
    }

}
