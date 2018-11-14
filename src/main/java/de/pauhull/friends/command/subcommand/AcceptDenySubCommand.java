package de.pauhull.friends.command.subcommand;

import com.google.common.collect.ImmutableSet;
import de.pauhull.friends.Friends;
import de.pauhull.friends.util.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class AcceptDenySubCommand extends SubCommand {

    private Friends friends;

    public AcceptDenySubCommand() {
        super("accept", "deny");
        this.friends = Friends.getInstance();
        this.setTabPermissions(Permissions.ACCEPT, Permissions.DENY);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args[0].equalsIgnoreCase("accept")) {
            if (!player.hasPermission(Permissions.ACCEPT)) {
                player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getNoPermissions()));
                return;
            }
        } else {
            if (!player.hasPermission(Permissions.DENY)) {
                player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getNoPermissions()));
                return;
            }
        }

        if (args.length < 2) {
            if (args[0].equalsIgnoreCase("accept")) {
                sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + "§c/friend accept <Spieler>"));
            } else {
                sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + "§c/friend deny <Spieler>"));
            }
            return;
        }

        UUID to = player.getUniqueId();
        String fromName = args[1];

        if (fromName.equalsIgnoreCase("all")) {
            if (args[0].equalsIgnoreCase("accept")) {
                if (!player.hasPermission(Permissions.ACCEPT_ALL)) {
                    sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + "§c/friend deny <Spieler>"));
                    return;
                }
            } else {
                if (!player.hasPermission(Permissions.DENY_ALL)) {
                    sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + "§c/friend deny <Spieler>"));
                    return;
                }
            }

            friends.getFriendRequestTable().getOpenFriendRequests(player.getUniqueId(), requests -> {

                if (requests > 0) {
                    Consumer<Collection<ProxiedPlayer>> consumer = players -> {
                        for (ProxiedPlayer proxiedPlayer : players) {
                            if (proxiedPlayer == null)
                                continue;

                            if (args[0].equalsIgnoreCase("accept")) {
                                proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getRequestAccepted(), player.getName())));
                            } else {
                                proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getRequestDenied(), player.getName())));
                            }
                        }

                        if (args[0].equalsIgnoreCase("accept")) {
                            player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getRequestsAccepted(), players.size())));
                        } else {
                            player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getRequestsDenied(), players.size())));
                        }
                    };

                    if (args[0].equalsIgnoreCase("accept")) {
                        friends.getFriendRequestTable().acceptAll(player.getUniqueId(), consumer);
                    } else {
                        friends.getFriendRequestTable().denyAll(player.getUniqueId(), consumer);
                    }
                } else {
                    player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getNoRequests()));
                }

            });

            return;
        }

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

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length != 2) {
            return ImmutableSet.of();
        }

        String search = args[1].toLowerCase();
        Set<String> matches = new HashSet<>();

        if (args[0].equalsIgnoreCase("accept")) {
            if (sender.hasPermission(Permissions.ACCEPT_ALL)) {
                matches.add("all");
            }
        } else {
            if (sender.hasPermission(Permissions.DENY_ALL)) {
                matches.add("all");
            }
        }

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
