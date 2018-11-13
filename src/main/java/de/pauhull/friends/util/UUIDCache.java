package de.pauhull.friends.util;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UUIDCache {

    private Map<UUID, Map.Entry<String, Long>> nameCache = new HashMap<>();
    private Map<String, Map.Entry<UUID, Long>> uuidCache = new HashMap<>();
    private int saveTimeMillis;

    public UUIDCache(int saveTimeMillis) {
        this.saveTimeMillis = saveTimeMillis;
    }

    public UUIDCache() {
        this(1000 * 60 * 60 * 12); // 12 hours
    }

    public void save(UUID uuid, String name) {
        long time = System.currentTimeMillis();
        nameCache.put(uuid, new AbstractMap.SimpleEntry<>(name, time));
        uuidCache.put(name, new AbstractMap.SimpleEntry<>(uuid, time));
    }

    public UUID getUUID(String name) {
        if (!uuidCache.containsKey(name))
            return null;

        Map.Entry<UUID, Long> cacheEntry = uuidCache.get(name);

        if (System.currentTimeMillis() - cacheEntry.getValue() > saveTimeMillis) {
            uuidCache.remove(name);
            return null;
        }

        return cacheEntry.getKey();
    }

    public String getName(UUID uuid) {
        if (!nameCache.containsKey(uuid))
            return null;

        Map.Entry<String, Long> cacheEntry = nameCache.get(uuid);

        if (System.currentTimeMillis() - cacheEntry.getValue() > saveTimeMillis) {
            nameCache.remove(uuid);
            return null;
        }

        return cacheEntry.getKey();
    }

}
