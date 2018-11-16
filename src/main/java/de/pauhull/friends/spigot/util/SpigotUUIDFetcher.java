package de.pauhull.friends.spigot.util;

import de.pauhull.friends.common.util.CachedUUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class SpigotUUIDFetcher extends CachedUUIDFetcher {

    public SpigotUUIDFetcher(ExecutorService executor) {
        super(executor);
    }

    @Override
    public void fetchUUIDAsync(String playerName, Consumer<UUID> consumer) {
        Player player = Bukkit.getPlayer(playerName);
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
        Player player = Bukkit.getPlayer(uuid);
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
