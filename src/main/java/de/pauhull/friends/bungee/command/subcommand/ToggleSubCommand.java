package de.pauhull.friends.bungee.command.subcommand;

import de.pauhull.friends.bungee.Friends;
import de.pauhull.friends.common.util.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ToggleSubCommand extends SubCommand {

    private Friends friends;

    public ToggleSubCommand() {
        super("toggle", "togglemsg", "togglenotify", "togglejump");
        this.friends = Friends.getInstance();
        this.setTabPermissions(Permissions.TOGGLE, Permissions.TOGGLE, Permissions.TOGGLE, Permissions.TOGGLE);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!player.hasPermission(Permissions.TOGGLE)) {
            player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getNoPermissions()));
            return;
        }

        if (args[0].equalsIgnoreCase("togglemsg")) {
            friends.getSettingsTable().getMessages(player.getUniqueId(), messages -> {
                friends.getSettingsTable().setMessages(player.getUniqueId(), !messages);
                if (!messages) {
                    player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getMessagesOn()));
                } else {
                    player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getMessagesOff()));
                }
            });
        } else if (args[0].equalsIgnoreCase("togglenotify")) {
            friends.getSettingsTable().getNotifications(player.getUniqueId(), notifications -> {
                friends.getSettingsTable().setNotifications(player.getUniqueId(), !notifications);
                if (!notifications) {
                    player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getNotificationsOn()));
                } else {
                    player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getNotificationsOff()));
                }
            });
        } else if (args[0].equalsIgnoreCase("togglejump")) {
            friends.getSettingsTable().getJumping(player.getUniqueId(), jumping -> {
                friends.getSettingsTable().setJumping(player.getUniqueId(), !jumping);
                if (!jumping) {
                    player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getJumpingOn()));
                } else {
                    player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getJumpingOff()));
                }
            });
        } else if (args[0].equalsIgnoreCase("toggle")) {
            friends.getSettingsTable().getRequests(player.getUniqueId(), requests -> {
                friends.getSettingsTable().setRequests(player.getUniqueId(), !requests);
                if (!requests) {
                    player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getRequestsOn()));
                } else {
                    player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getRequestsOff()));
                }
            });
        }

    }

}
