package de.pauhull.friends.command.subcommand;

import de.pauhull.friends.Friends;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class AcceptDenySubCommand extends SubCommand {

    private Friends friends;

    public AcceptDenySubCommand() {
        super("accept", "deny");
        this.friends = Friends.getInstance();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length < 2) {
            if (args[0].equalsIgnoreCase("accept")) {
                sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + "§c/friend accept <Spieler>"));
            } else {
                sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + "§c/friend deny <Spieler>"));
            }
            return;
        }

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        UUID to = player.getUniqueId();
        String fromName = args[1];

        friends.getUuidFetcher().fetchUUIDAsync(fromName, from -> {

            ProxiedPlayer fromPlayer = ProxyServer.getInstance().getPlayer(from);

            if (from == null) {
                player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getPlayerDoesntExist()));
                return;
            }

            friends.getFriendRequestTable().getTime(from, to, time -> {

                if (time == null) {
                    friends.getUuidFetcher().fetchNameAsync(from, name -> {
                        player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getNoRequest(), name)));
                    });
                } else {
                    if (args[0].equalsIgnoreCase("accept")) {
                        friends.getFriendRequestTable().acceptFriendRequest(from, to);

                        friends.getUuidFetcher().fetchNameAsync(from, name -> {
                            player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getYouAccepted(), name)));
                        });

                        if (fromPlayer != null) {
                            fromPlayer.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getRequestAccepted(), player.getName())));
                        }
                    } else {
                        friends.getFriendRequestTable().denyFriendRequest(from, to);

                        friends.getUuidFetcher().fetchNameAsync(from, name -> {
                            player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getYouDenied(), name)));
                        });

                        if (fromPlayer != null) {
                            fromPlayer.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getRequestDenied(), player.getName())));
                        }
                    }
                }

            });

        });

    }

}
