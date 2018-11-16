package de.pauhull.friends.bungee.command;

import de.pauhull.friends.bungee.Friends;
import de.pauhull.friends.common.util.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ReplyCommand extends Command {

    private Friends friends;

    public ReplyCommand(Friends friends) {
        super("r", null, "reply");
        this.friends = friends;
        friends.getProxy().getPluginManager().registerCommand(friends, this);
    }

    public static void register() {
        new ReplyCommand(Friends.getInstance());
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

        if (args.length < 1) {
            sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + "§c/r <Nachricht...>"));
            return;
        }

        friends.getSettingsTable().getMessages(player.getUniqueId(), sendMessages -> {

            if (sendMessages) {

                if (MsgCommand.lastMessageReceivedBy.containsKey(player.getName())) {
                    String sendTo = MsgCommand.lastMessageReceivedBy.get(player.getName());

                    ProxiedPlayer receiver = ProxyServer.getInstance().getPlayer(sendTo);
                    if (receiver == null) {
                        player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getNotOnline(), sendTo)));
                    } else {
                        friends.getFriendTable().areFriends(player.getUniqueId(), receiver.getUniqueId(), areFriends -> {
                            if (areFriends) {

                                friends.getSettingsTable().getMessages(receiver.getUniqueId(), receivesMessages -> {

                                    if (receivesMessages) {

                                        StringBuilder message = new StringBuilder();
                                        for (int i = 0; i < args.length; i++) {
                                            if (i > 0) {
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
                                        player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getMessagesDisabled(), receiver.getName())));
                                    }

                                });

                            } else {
                                player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getNoFriend(), receiver.getName())));
                            }
                        });
                    }
                } else {
                    player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getNoMessages()));
                }

            } else {
                player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getMessagesDisabledSelf()));
            }

        });

    }

}
