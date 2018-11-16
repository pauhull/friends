package de.pauhull.friends.bungee.listener;

import de.pauhull.friends.bungee.BungeeFriends;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnectListener implements Listener {

    private BungeeFriends friends;

    public PlayerDisconnectListener(BungeeFriends friends) {
        this.friends = friends;
        friends.getProxy().getPluginManager().registerListener(friends, this);
    }

    public static void register() {
        new PlayerDisconnectListener(BungeeFriends.getInstance());
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        friends.getLastOnlineTable().setLastOnline(player.getUniqueId());

        friends.getSettingsTable().getNotifications(player.getUniqueId(), sendsNotifications -> {
            if (sendsNotifications) {
                friends.getFriendTable().getFriends(player.getUniqueId(), players -> {

                    for (ProxiedPlayer friend : players) {
                        friends.getSettingsTable().getNotifications(friend.getUniqueId(), receivesNotifications -> {
                            if (receivesNotifications) {
                                friend.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPrefix() + String.format(friends.getMessages().getNowOffline(), player.getName())));
                            }
                        });
                    }

                });
            }
        });

    }

}
