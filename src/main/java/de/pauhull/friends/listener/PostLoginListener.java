package de.pauhull.friends.listener;

import de.pauhull.friends.Friends;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PostLoginListener implements Listener {

    private Friends friends;

    public PostLoginListener(Friends friends) {
        this.friends = friends;
        friends.getProxy().getPluginManager().registerListener(friends, this);
    }

    public static void register() {
        new PostLoginListener(Friends.getInstance());
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        friends.getFriendRequestTable().getOpenFriendRequests(player.getUniqueId(), requests -> {

            if (requests != 0) {
                player.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + String.format(friends.getMessages().getOpenRequests(), requests)));
            }

        });
    }

}
