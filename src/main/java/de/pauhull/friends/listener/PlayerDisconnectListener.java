package de.pauhull.friends.listener;

import de.pauhull.friends.Friends;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnectListener implements Listener {

    private Friends friends;

    public PlayerDisconnectListener(Friends friends) {
        this.friends = friends;
        friends.getProxy().getPluginManager().registerListener(friends, this);
    }

    public static void register() {
        new PlayerDisconnectListener(Friends.getInstance());
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        friends.getSettingsTable().getNotifications(player.getUniqueId(), sendsNotifications -> {
            if (sendsNotifications) {
                friends.getFriendTable().getFriends(player.getUniqueId(), players -> {

                    for (ProxiedPlayer friend : players) {
                        friends.getSettingsTable().getNotifications(friend.getUniqueId(), receivesNotifications -> {
                            if (receivesNotifications) {
                                friend.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getNowOffline(), player.getName())));
                            }
                        });
                    }

                });
            }
        });

    }

}
