package de.pauhull.friends.spigot.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface InventoryMenu extends Listener {

    void show(Player player);

    @EventHandler
    void onInventoryClick(InventoryClickEvent event);

}
