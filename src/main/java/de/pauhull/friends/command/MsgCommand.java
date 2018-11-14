package de.pauhull.friends.command;

import de.pauhull.friends.Friends;
import de.pauhull.friends.util.Permissions;
import de.pauhull.friends.util.TimedHashMap;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MsgCommand extends Command {

    public static Map<String, String> lastMessageReceivedBy = new TimedHashMap<>(TimeUnit.MINUTES, 15);

    private Friends friends;

    public MsgCommand(Friends friends) {
        super("msg");
        this.friends = friends;
        friends.getProxy().getPluginManager().registerCommand(friends, this);
    }

    public static void register() {
        new MsgCommand(Friends.getInstance());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!player.hasPermission(Permissions.MSG)) {
            player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getNoPermissions()));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + "§c/msg <Spieler> <Nachricht...>"));
            return;
        }

        String sendTo = args[0];

        friends.getUuidFetcher().fetchUUIDAsync(sendTo, uuid -> {

            if (uuid == null) {
                player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getPlayerDoesntExist()));
            } else {
                ProxiedPlayer receiver = ProxyServer.getInstance().getPlayer(uuid);
                if (receiver == null) {
                    friends.getUuidFetcher().fetchNameAsync(uuid, name -> {
                        player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getNotOnline(), name)));
                    });
                    return;
                }

                friends.getFriendTable().areFriends(player.getUniqueId(), receiver.getUniqueId(), areFriends -> {
                    if (areFriends) {

                        StringBuilder message = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            if (i > 1) {
                                message.append(" ");
                            }

                            message.append(args[i]);
                        }

                        BaseComponent[] msg = TextComponent.fromLegacyText(String.format("§7%s » %s: %s", player.getName(), receiver.getName(), message));
                        player.sendMessage(msg);
                        receiver.sendMessage(msg);

                        MsgCommand.lastMessageReceivedBy.put(receiver.getName(), player.getName());
                        MsgCommand.lastMessageReceivedBy.put(player.getName(), receiver.getName());

                    } else {
                        player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getNoFriend(), receiver.getName())));
                    }
                });
            }

        });

        //TODO: tab complete

    }

}
