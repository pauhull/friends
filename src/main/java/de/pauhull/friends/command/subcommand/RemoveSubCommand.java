package de.pauhull.friends.command.subcommand;

import com.google.common.collect.ImmutableSet;
import de.pauhull.friends.Friends;
import de.pauhull.friends.util.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;
import java.util.Set;

public class RemoveSubCommand extends SubCommand {

    private Friends friends;

    public RemoveSubCommand() {
        super("remove");
        this.friends = Friends.getInstance();
        this.setTabPermissions(Permissions.REMOVE);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!player.hasPermission(Permissions.REMOVE)) {
            player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getNoPermissions()));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + "§c/friend remove <Spieler>"));
            return;
        }

        String removeName = args[1];

        if (player.getName().equalsIgnoreCase(removeName)) {
            player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getNotSelf()));
            return;
        }

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
