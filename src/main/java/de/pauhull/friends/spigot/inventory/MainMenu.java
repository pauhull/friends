package de.pauhull.friends.spigot.inventory;

import de.pauhull.friends.spigot.SpigotFriends;
import de.pauhull.friends.spigot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainMenu implements Listener {

    private static final String TITLE = "§cFreunde";
    private static final ItemStack SETTINGS = new ItemBuilder().setMaterial(Material.PAPER).setDisplayName("§8» §eEinstellungen").build();
    private static final ItemStack NO_FRIENDS = new ItemBuilder().setMaterial(Material.BARRIER).setDisplayName("§8» §cDu hast keine Freunde.").build();

    private SpigotFriends friends;

    public MainMenu(SpigotFriends friends) {
        this.friends = friends;
        Bukkit.getPluginManager().registerEvents(this, friends);
    }

    public void show(Player player, int page) {

        int results = 45;
        int start = 1 + page * results;

        System.out.println(player.getUniqueId().toString());

        friends.getFriendTable().getFriends(player.getUniqueId(), start, results, friendList -> {

            int inventorySize = (int) Math.floor((double) friendList.size() / 9.0 + 1.0) * 9 + 9;
            if (inventorySize > 54) {
                inventorySize = 54;
            }

            Inventory inventory = Bukkit.createInventory(null, inventorySize, TITLE + " §8(Seite " + (page + 1) + ")");

            inventory.setItem(inventorySize - 9, SETTINGS);

            if (friendList.isEmpty()) {
                inventory.setItem(0, NO_FRIENDS);
            } else {
                for (int i = 0; i < friendList.size(); i++) {
                    UUID friendUUID = friendList.get(i);
                    final int index = i;
                    friends.getUuidFetcher().fetchNameAsync(friendUUID, name -> {
                        friends.getLastOnlineTable().getLastOnline(friendUUID, lastOnline -> {
                            friends.getFriendTable().getTime(player.getUniqueId(), friendUUID, friendTime -> {
                                friends.getSettingsTable().getStatus(friendUUID, status -> {

                                    ItemStack head = friends.getHeadCache().getHead(name);
                                    ItemMeta meta = head.getItemMeta();
                                    meta.setDisplayName("§8» §a" + name);
                                    List<String> lore = new ArrayList<>();

                                    lore.add(status);

                                    lore.add("§8§m               ");

                                    if (Bukkit.getPlayer(friendUUID) != null) {
                                        lore.add("§a§lONLINE");
                                    } else {
                                        if (lastOnline == null) {
                                            lore.add("§8× §fLetztes Onlinedatum unbekannt");
                                        } else {
                                            lore.add("§8× §fZuletzt online vor " + format(lastOnline, System.currentTimeMillis()));
                                        }
                                    }

                                    if (friendTime != null) {
                                        lore.add("§8× §fBefreundet seit " + format(friendTime, System.currentTimeMillis()));
                                    } else {
                                        lore.add("§8× §fBefreundet seit: Unbekannt");
                                    }

                                    meta.setLore(lore);

                                    inventory.setItem(index, head);
                                });
                            });
                        });
                    });
                }
            }

            player.openInventory(inventory);
            player.updateInventory();

        });

    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        if (inventory == null || inventory.getTitle() == null || !inventory.getTitle().startsWith(TITLE)) {
            return;
        }

        event.setCancelled(true);

    }

    private String format(long then, long now) {
        long duration = then - now;

        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long years = (long) Math.floor((double) days / 360.0);

        if (seconds == 1) {
            return "1 Sekunde";
        } else if (seconds < 60) {
            return seconds + " Sekunden";
        } else if (minutes == 1) {
            return "1 Minute";
        } else if (minutes < 60) {
            return minutes + " Minuten";
        } else if (hours == 1) {
            return "1 Stunde";
        } else if (hours < 60) {
            return hours + " Stunden";
        } else if (days == 1) {
            return "1 Tag";
        } else if (days < 365) {
            return days + " Tage";
        } else if (years == 1 && days == 365) {
            return "1 Jahr";
        } else if (days % 365 == 0) {
            return years + " Jahre";
        } else {
            return years + " Jahre, " + days + " Tage";
        }

    }

}
