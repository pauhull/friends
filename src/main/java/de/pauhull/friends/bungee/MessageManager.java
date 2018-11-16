package de.pauhull.friends.bungee;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;

public class MessageManager {

    @Getter
    private String prefix, requestReceived, onlyPlayers, playerDoesntExist, alreadyRequested, requestSent, requestWithdrawn,
            openRequests, youAccepted, youDenied, requestAccepted, requestDenied, noRequest, alreadyFriend, noFriend, friendRemoved,
            notSelf, noRequests, requestsAccepted, requestsDenied, noPermissions, notOnline, noMessages, messagesOn,
            messagesOff, messagesDisabled, messagesDisabledSelf, nowOnline, nowOffline, notificationsOn, notificationsOff,
            noJumping, sameServer, jumpingOn, jumpingOff, requestsOn, requestsOff, receivesNoRequests, unallowedCharacters,
            statusTooLong, statusChanged, yourStatus;

    public MessageManager load(Configuration config) {
        this.prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.Prefix"));
        this.alreadyFriend = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.AlreadyFriend"));
        this.alreadyRequested = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.AlreadyRequested"));
        this.friendRemoved = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.FriendRemoved"));
        this.jumpingOff = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.JumpingOff"));
        this.jumpingOn = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.JumpingOn"));
        this.messagesDisabled = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.MessagesDisabled"));
        this.messagesDisabledSelf = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.MessagesDisabledSelf"));
        this.messagesOff = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.MessagesOff"));
        this.messagesOn = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.MessagesOn"));
        this.noFriend = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.NoFriend"));
        this.noJumping = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.NoJumping"));
        this.noMessages = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.NoMessages"));
        this.noPermissions = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.NoPermissions"));
        this.noRequest = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.NoRequest"));
        this.noRequests = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.NoRequests"));
        this.notificationsOff = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.NotificationsOff"));
        this.notificationsOn = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.NotificationsOn"));
        this.notOnline = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.NotOnline"));
        this.notSelf = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.NotSelf"));
        this.nowOffline = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.NowOffline"));
        this.nowOnline = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.NowOnline"));
        this.onlyPlayers = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.OnlyPlayers"));
        this.openRequests = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.OpenRequests"));
        this.playerDoesntExist = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.PlayerDoesntExist"));
        this.requestAccepted = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.RequestAccepted"));
        this.requestDenied = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.RequestDenied"));
        this.requestReceived = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.RequestReceived"));
        this.requestsAccepted = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.RequestsAccepted"));
        this.requestsDenied = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.RequestsDenied"));
        this.requestSent = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.RequestSent"));
        this.requestWithdrawn = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.RequestWithdrawn"));
        this.sameServer = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.SameServer"));
        this.youAccepted = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.YouAccepted"));
        this.youDenied = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.YouDenied"));
        this.requestsOn = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.RequestsOn"));
        this.requestsOff = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.RequestsOff"));
        this.receivesNoRequests = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.ReceivesNoRequests"));
        this.statusTooLong = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.StatusTooLong"));
        this.unallowedCharacters = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.UnallowedCharacters"));
        this.statusChanged = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.StatusChanged"));
        this.yourStatus = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.YourStatus"));
        return this;
    }

}
