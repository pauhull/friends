package de.pauhull.friends.spigot.inventory;

import de.pauhull.friends.spigot.SpigotFriends;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SynchronousInventoryOpener {

    public static final Map<Player, Inventory> QUEUE = new HashMap<>();

    public static void run() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SpigotFriends.getInstance(), () -> {
            if (!QUEUE.isEmpty()) {
                Iterator<Player> iterator = QUEUE.keySet().iterator();

                while (iterator.hasNext()) {
                    Player player = iterator.next();
                    player.openInventory(QUEUE.get(player));
                    iterator.remove();
                }
            }
        }, 0, 1);
    }

}
