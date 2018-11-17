package de.pauhull.friends.spigot.listener;

import de.pauhull.friends.spigot.SpigotFriends;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLoginListener implements Listener {

    private SpigotFriends friends;

    public PlayerLoginListener(SpigotFriends friends) {
        this.friends = friends;

        Bukkit.getPluginManager().registerEvents(this, friends);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        friends.getHeadCache().saveHead(player.getName());
        friends.getUuidFetcher().fetchUUIDAsync(player.getName(), ignored -> {
        });
    }

}
