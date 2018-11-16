package de.pauhull.friends.bungee.command.subcommand;

import com.google.common.collect.ImmutableSet;
import de.pauhull.friends.bungee.BungeeFriends;
import de.pauhull.friends.common.util.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;
import java.util.Set;

public class AddSubCommand extends SubCommand {

    private BungeeFriends friends;

    public AddSubCommand() {
        super("add");
        this.friends = BungeeFriends.getInstance();
        this.setTabPermissions(Permissions.ADD);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer requester = (ProxiedPlayer) sender;

        if (!requester.hasPermission(Permissions.ADD)) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPrefix() + friends.getMessages().getNoPermissions()));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPrefix() + "§c/friend add <Spieler>"));
            return;
        }

        String requestedPlayerName = args[1];

        if (requester.getName().equalsIgnoreCase(requestedPlayerName)) {
            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPrefix() + friends.getMessages().getNotSelf()));
            return;
        }

        friends.getUuidFetcher().fetchUUIDAsync(requestedPlayerName, requestedUUID -> {

            if (requestedUUID == null) {
                requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPrefix() + friends.getMessages().getPlayerDoesntExist()));
            } else {
                friends.getUuidFetcher().fetchNameAsync(requestedUUID, name -> {

                    friends.getFriendRequestTable().getTime(requestedUUID, requester.getUniqueId(), time -> {
                        if (time != null) {
                            friends.getFriendRequestTable().acceptFriendRequest(friends.getFriendTable(), requestedUUID, requester.getUniqueId());

                            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPrefix() + String.format(friends.getMessages().getYouAccepted(), name)));

                            ProxiedPlayer requested = ProxyServer.getInstance().getPlayer(requestedUUID);
                            if (requested != null) {
                                requested.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPrefix() + String.format(friends.getMessages().getRequestAccepted(), requester.getName())));
                            }
                        } else {
                            friends.getFriendTable().areFriends(requester.getUniqueId(), requestedUUID, alreadyFriends -> {

                                if (alreadyFriends) {
                                    requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPrefix() + String.format(friends.getMessages().getAlreadyFriend(), name)));
                                } else {
                                    friends.getFriendRequestTable().getTime(requester.getUniqueId(), requestedUUID, timeStamp -> {
                                        if (timeStamp != null) {
                                            requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPrefix() + String.format(friends.getMessages().getAlreadyRequested(), name)));
                                        } else {
                                            friends.getSettingsTable().getRequests(requestedUUID, receivesRequests -> {

                                                if (receivesRequests) {
                                                    friends.getFriendRequestTable().sendFriendRequest(requester.getUniqueId(), requestedUUID);
                                                    requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPrefix() + String.format(friends.getMessages().getRequestSent(), name)));

                                                    ProxiedPlayer requested = ProxyServer.getInstance().getPlayer(name);
                                                    if (requested != null) {
                                                        requested.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPrefix() +
                                                                String.format(friends.getMessages().getRequestReceived(), requester.getName())));

                                                        HoverEvent acceptHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§aAnfrage annehmen"));
                                                        HoverEvent denyHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§cAnfrage ablehnen"));
                                                        ClickEvent acceptClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + requester.getName());
                                                        ClickEvent denyClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + requester.getName());

                                                        BaseComponent[] message = new ComponentBuilder(BungeeFriends.getPrefix()).append("§8[").append("§a§lAnnehmen").event(acceptHover).event(acceptClick)
                                                                .append("§8/").append("§c§lAblehnen").event(denyHover).event(denyClick).append("§8]").create();

                                                        requested.sendMessage(message);
                                                    }
                                                } else {
                                                    requester.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPrefix() + String.format(friends.getMessages().getReceivesNoRequests(), name)));
                                                }

                                            });
                                        }

                                    });
                                }

                            });
                        }
                    });

                });
            }

        });

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length != 2) {
            return ImmutableSet.of();
        }

        String search = args[1].toLowerCase();
        Set<String> matches = new HashSet<>();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {

            if (player.equals(sender))
                continue;

            if (!player.getName().toLowerCase().startsWith(search))
                continue;

            matches.add(player.getName());
        }

        return matches;
    }

}
