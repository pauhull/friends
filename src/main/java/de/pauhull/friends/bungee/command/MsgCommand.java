package de.pauhull.friends.bungee.command;

import com.google.common.collect.ImmutableSet;
import de.pauhull.friends.bungee.Friends;
import de.pauhull.friends.common.util.Permissions;
import de.pauhull.friends.common.util.TimedHashMap;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MsgCommand extends Command implements TabExecutor {

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

        friends.getSettingsTable().getMessages(player.getUniqueId(), sendMessages -> {

            if (sendMessages) {

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

                        friends.getSettingsTable().getMessages(uuid, messages -> {

                            if (messages) {
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
                            } else {
                                player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getMessagesDisabled(), receiver.getName())));
                            }

                        });

                    }

                });

            } else {
                player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getMessagesDisabledSelf()));
            }

        });

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length != 1 || !sender.hasPermission(Permissions.MSG)) {
            return ImmutableSet.of();
        }

        String search = args[0].toLowerCase();
        Set<String> matches = new HashSet<>();

        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.getName().toLowerCase().startsWith(search)) {
                matches.add(player.getName());
            }
        }

        return matches;
    }

}
