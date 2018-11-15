package de.pauhull.friends.command.subcommand;

import de.pauhull.friends.Friends;
import de.pauhull.friends.util.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class JumpSubCommand extends SubCommand {

    private Friends friends;

    public JumpSubCommand() {
        super("jump");
        this.friends = Friends.getInstance();
        this.setTabPermissions(Permissions.JUMP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer requester = (ProxiedPlayer) sender;

        if (!sender.hasPermission(Permissions.JUMP)) {
            sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getNoPermissions()));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + "§c/friend jump <Spieler>"));
            return;
        }

        String requestedPlayerName = args[1];

        friends.getUuidFetcher().fetchUUIDAsync(requestedPlayerName, requestedUUID -> {

            if (requestedUUID == null) {
                requester.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getPlayerDoesntExist()));
            } else {
                friends.getFriendTable().areFriends(requester.getUniqueId(), requestedUUID, areFriends -> {

                    if (areFriends) {
                        ProxiedPlayer requested = ProxyServer.getInstance().getPlayer(requestedUUID);

                        if (requested != null) {
                            friends.getSettingsTable().getJumping(requestedUUID, jumping -> {
                                if (jumping) {
                                    if (requester.getServer().getInfo().equals(requested.getServer().getInfo())) {
                                        requester.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getSameServer()));
                                    } else {
                                        requester.connect(requested.getServer().getInfo());
                                    }
                                } else {
                                    requester.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getNoJumping(), requested.getName())));
                                }
                            });
                        } else {
                            friends.getUuidFetcher().fetchNameAsync(requestedUUID, name -> {
                                requester.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getNotOnline(), name)));
                            });
                        }
                    } else {
                        friends.getUuidFetcher().fetchNameAsync(requestedUUID, name -> {
                            requester.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getNoFriend(), name)));
                        });
                    }

                });
            }

        });

    }

    //TODO tab complete

}
