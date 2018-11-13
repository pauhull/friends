package de.pauhull.friends.command.subcommand;

import de.pauhull.friends.Friends;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class RemoveSubCommand extends SubCommand {

    private Friends friends;

    public RemoveSubCommand() {
        super("remove");
        this.friends = Friends.getInstance();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + "§c/friend remove <Spieler>"));
            return;
        }

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;

        String removeName = args[1];
        friends.getUuidFetcher().fetchUUIDAsync(removeName, uuid -> {

            if (uuid == null) {
                player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getPlayerDoesntExist()));
                return;
            }

            friends.getUuidFetcher().fetchNameAsync(uuid, name -> {

                friends.getFriendTable().areFriends(player.getUniqueId(), uuid, areFriends -> {
                    if (areFriends) {
                        friends.getFriendTable().removeFriends(player.getUniqueId(), uuid);
                        player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getFriendRemoved(), name)));

                        ProxiedPlayer removed = ProxyServer.getInstance().getPlayer(uuid);
                        if (removed != null) {
                            removed.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getFriendRemoved(), player.getName())));
                        }
                    } else {
                        player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getNoFriend(), name)));
                    }
                });

            });
        });

    }

}
