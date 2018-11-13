package de.pauhull.friends.command.subcommand;

import de.pauhull.friends.Friends;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AddSubCommand extends SubCommand {

    private Friends friends;

    public AddSubCommand() {
        super("add");
        this.friends = Friends.getInstance();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + "§c/friend add <Spieler>"));
            return;
        }

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer requester = (ProxiedPlayer) sender;

        String requestedPlayerName = args[1];
        friends.getUuidFetcher().fetchUUIDAsync(requestedPlayerName, requestedUUID -> {

            if (requestedUUID == null) {
                requester.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getPlayerDoesntExist()));
            } else {
                friends.getUuidFetcher().fetchNameAsync(requestedUUID, name -> {
                    friends.getFriendTable().areFriends(requester.getUniqueId(), requestedUUID, alreadyFriends -> {

                        if (alreadyFriends) {
                            requester.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getAlreadyFriend()));
                        } else {
                            friends.getFriendRequestTable().getTime(requester.getUniqueId(), requestedUUID, timeStamp -> {
                                if (timeStamp != null) {
                                    requester.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getAlreadyRequested(), name)));
                                } else {
                                    friends.getFriendRequestTable().sendFriendRequest(requester.getUniqueId(), requestedUUID);
                                    requester.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getRequestSent(), name)));

                                    ProxiedPlayer requested = ProxyServer.getInstance().getPlayer(name);
                                    if (requested != null) {
                                        requested.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() +
                                                String.format(friends.getMessages().getRequestReceived(), requester.getName())));

                                        HoverEvent acceptHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§aAnfrage annehmen"));
                                        HoverEvent denyHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§cAnfrage ablehnen"));
                                        ClickEvent acceptClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + requester.getName());
                                        ClickEvent denyClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + requester.getName());

                                        BaseComponent[] message = new ComponentBuilder(Friends.getPrefix()).append("§8[").append("§a§lAnnehmen").event(acceptHover).event(acceptClick)
                                                .append("§8/").append("§c§lAblehnen").event(denyHover).event(denyClick).append("§8]").create();

                                        requester.sendMessage(message);
                                    }
                                }

                            });
                        }

                    });
                });
            }

        });

    }

}
