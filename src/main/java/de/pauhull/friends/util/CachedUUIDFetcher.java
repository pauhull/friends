package de.pauhull.friends.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class CachedUUIDFetcher {

    private static UUIDCache cache = new UUIDCache();

    private ExecutorService executor;

    public CachedUUIDFetcher(ExecutorService executor) {
        this.executor = executor;
    }

    public static UUID parseUUIDFromString(String uuidAsString) {
        String[] parts = {
                "0x" + uuidAsString.substring(0, 8),
                "0x" + uuidAsString.substring(8, 12),
                "0x" + uuidAsString.substring(12, 16),
                "0x" + uuidAsString.substring(16, 20),
                "0x" + uuidAsString.substring(20, 32)
        };

        long mostSigBits = Long.decode(parts[0]).longValue();
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(parts[1]).longValue();
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(parts[2]).longValue();

        long leastSigBits = Long.decode(parts[3]).longValue();
        leastSigBits <<= 48;
        leastSigBits |= Long.decode(parts[4]).longValue();

        return new UUID(mostSigBits, leastSigBits);
    }

    public void fetchUUIDAsync(String playerName, Consumer<UUID> consumer) {
        executor.execute(() -> {

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
            if (player != null) {
                UUID result = player.getUniqueId();
                cache.save(result, playerName);
                consumer.accept(player.getUniqueId());
                return;
            }

            UUID uuid = cache.getUUID(playerName);
            if (uuid != null) {
                consumer.accept(uuid);
                return;
            }

            try {
                // Get response from Mojang API
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() == 400) {
                    ProxyServer.getInstance().getLogger().severe("There is no player with the name \"" + playerName + "\"!");
                    consumer.accept(null);
                    return;
                }

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                // Parse JSON response and get UUID
                JsonElement element = new JsonParser().parse(bufferedReader);
                JsonObject object = element.getAsJsonObject();
                String uuidAsString = object.get("id").getAsString();

                inputStream.close();
                bufferedReader.close();

                // Return UUID
                UUID result = parseUUIDFromString(uuidAsString);
                cache.save(result, playerName);
                consumer.accept(result);
            } catch (IOException e) {
                ProxyServer.getInstance().getLogger().severe("Couldn't connect to URL.");
                e.printStackTrace();
                consumer.accept(null);
            }
        });
    }

    public void fetchNameAsync(UUID uuid, Consumer<String> consumer) {
        executor.execute(() -> {

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
            if (player != null) {
                String result = player.getName();
                cache.save(uuid, result);
                consumer.accept(result);
                return;
            }

            String playerName = cache.getName(uuid);
            if (playerName != null) {
                consumer.accept(playerName);
                return;
            }

            try {
                // Get response from Mojang API
                URL url = new URL(String.format("https://api.mojang.com/user/profiles/%s/names", uuid.toString().replace("-", "")));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() == 400) {
                    ProxyServer.getInstance().getLogger().severe("There is no player with the UUID \"" + uuid.toString() + "\"!");
                    consumer.accept(null);
                    return;
                }

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                // Parse JSON response and return name
                JsonElement element = new JsonParser().parse(bufferedReader);
                JsonArray array = element.getAsJsonArray();
                JsonObject object = array.get(0).getAsJsonObject();

                bufferedReader.close();
                inputStream.close();

                String result = object.get("name").getAsString();
                cache.save(uuid, result);
                consumer.accept(result);
            } catch (IOException e) {
                ProxyServer.getInstance().getLogger().severe("Couldn't connect to URL \"https://api.mojang.com/\". Is there an internet connection?");
                e.printStackTrace();
                consumer.accept(null);
            }
        });
    }

}