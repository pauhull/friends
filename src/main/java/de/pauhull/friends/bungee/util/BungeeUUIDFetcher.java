package de.pauhull.friends.bungee.util;

import de.pauhull.friends.common.util.CachedUUIDFetcher;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class BungeeUUIDFetcher extends CachedUUIDFetcher {

    public BungeeUUIDFetcher(ExecutorService executor) {
        super(executor);
    }

    @Override
    public void fetchUUIDAsync(String playerName, Consumer<UUID> consumer) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
        if (player != null) {
            UUID result = player.getUniqueId();
            cache.save(result, playerName);
            consumer.accept(player.getUniqueId());
            return;
        }

        super.fetchUUIDAsync(playerName, consumer);
    }

    @Override
    public void fetchNameAsync(UUID uuid, Consumer<String> consumer) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        if (player != null) {
            String result = player.getName();
            cache.save(uuid, result);
            consumer.accept(result);
            return;
        }

        super.fetchNameAsync(uuid, consumer);
    }

    @Override
    public void getNameCaseSensitive(String name, Consumer<String> consumer) {
        fetchUUIDAsync(name, uuid -> {
            if (uuid == null) {
                consumer.accept(null);
                return;
            }

            fetchNameAsync(uuid, consumer);
        });
    }

}
