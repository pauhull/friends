package de.pauhull.friends.spigot.inventory;

import com.ikeirnez.pluginmessageframework.PacketPlayer;
import de.pauhull.friends.common.packet.RunCommandPacket;
import de.pauhull.friends.spigot.SpigotFriends;
import de.pauhull.friends.spigot.util.ItemBuilder;
import de.pauhull.friends.spigot.util.Util;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainMenu implements InventoryMenu {

    private static final String TITLE = "§cFreunde";
    private static final ItemStack SETTINGS = new ItemBuilder().setMaterial(Material.PAPER).setDisplayName("§8» §eEinstellungen").build();
    private static final ItemStack REQUESTS = new ItemBuilder().setMaterial(Material.BOOK).setDisplayName("§8» §eFreundschaftsanfragen").build();
    private static final ItemStack NO_FRIENDS = new ItemBuilder().setMaterial(Material.BARRIER).setDisplayName("§8» §cDu hast keine Freunde.").build();
    private static final ItemStack GLASS_PANE = new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setData(15).setDisplayName(" ").build();
    private static final ItemStack NEXT_PAGE = new ItemBuilder().setMaterial(Material.ARROW).setGlowing(true).setDisplayName("§8» §eNächste Seite").build();
    private static final ItemStack PREVIOUS_PAGE = new ItemBuilder().setMaterial(Material.ARROW).setGlowing(true).setDisplayName("§8» §eVorherige Seite").build();
    private static final ItemStack NEXT_PAGE_DISABLED = new ItemBuilder().setMaterial(Material.ARROW).setDisplayName("§8» §7Keine nächste Seite").build();
    private static final ItemStack PREVIOUS_PAGE_DISABLED = new ItemBuilder().setMaterial(Material.ARROW).setDisplayName("§8» §7Keine vorherige Seite").build();
    private static final ItemStack SEND_REQUEST = new ItemBuilder().setMaterial(Material.ANVIL).setDisplayName("§8» §eAnfrage stellen").build();

    private SpigotFriends friends;

    public MainMenu(SpigotFriends friends) {
        this.friends = friends;
        Bukkit.getPluginManager().registerEvents(this, friends);
    }

    public void show(Player player, int page) {

        int results = 45;
        int start = page * results;

        friends.getUuidFetcher().fetchUUIDAsync(player.getName(), playerUUID -> {
            friends.getFriendTable().getFriends(playerUUID, start, results, friendList -> {

                int inventorySize = (int) Math.floor((double) friendList.size() / 9.0 + 1.0) * 9 + 9;
                if (inventorySize > 54) {
                    inventorySize = 54;
                }

                Inventory inventory = Bukkit.createInventory(null, inventorySize, TITLE + " §8(Seite " + (page + 1) + ")");

                for (int i = 0; i < 9; i++) {
                    inventory.setItem(inventorySize - 9 + i, GLASS_PANE);
                }

                inventory.setItem(inventorySize - 9, page > 0 ? PREVIOUS_PAGE : PREVIOUS_PAGE_DISABLED);
                inventory.setItem(inventorySize - 7, SEND_REQUEST);
                inventory.setItem(inventorySize - 5, SETTINGS);
                inventory.setItem(inventorySize - 3, REQUESTS);
                inventory.setItem(inventorySize - 1, friendList.size() >= 45 ? NEXT_PAGE : NEXT_PAGE_DISABLED);

                if (friendList.isEmpty()) {
                    if (page == 0) {
                        inventory.setItem(0, NO_FRIENDS);
                    }
                } else {
                    for (int i = 0; i < friendList.size(); i++) {
                        UUID friendUUID = friendList.get(i);
                        final int index = i;
                        friends.getUuidFetcher().fetchNameAsync(friendUUID, name -> {
                            friends.getLastOnlineTable().getLastOnline(friendUUID, lastOnline -> {
                                friends.getFriendTable().getTime(playerUUID, friendUUID, friendTime -> {
                                    friends.getSettingsTable().getStatus(friendUUID, status -> {

                                        ItemStack head = friends.getHeadCache().getHead(name);
                                        ItemMeta meta = head.getItemMeta();
                                        meta.setDisplayName("§8» §a" + name);
                                        List<String> lore = new ArrayList<>();

                                        lore.add("§f" + status);

                                        lore.add("§8§m                   ");

                                        if (Bukkit.getPlayer(name) != null) {
                                            lore.add("§a§lONLINE");
                                        } else {
                                            if (lastOnline == null) {
                                                lore.add("§8× §fLetztes Onlinedatum unbekannt");
                                            } else {
                                                lore.add("§8× §fZuletzt online vor " + Util.formatTime(lastOnline, System.currentTimeMillis()));
                                            }
                                        }

                                        if (friendTime != null) {
                                            lore.add("§8× §fBefreundet seit " + Util.formatTime(friendTime, System.currentTimeMillis()));
                                        } else {
                                            lore.add("§8× §fBefreundet seit: Unbekannt");
                                        }

                                        meta.setLore(lore);
                                        head.setItemMeta(meta);

                                        inventory.setItem(index, head);
                                    });
                                });
                            });
                        });
                    }
                }

                SynchronousInventoryOpener.QUEUE.put(player, inventory);

            });
        });

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        if (inventory == null || inventory.getTitle() == null || !inventory.getTitle().startsWith(TITLE)) {
            return;
        }

        event.setCancelled(true);

        String currentPageString = inventory.getTitle().substring((TITLE + " §8(Seite ").length(), inventory.getTitle().length() - 1);
        int pageIndex = Integer.parseInt(currentPageString) - 1;

        ItemStack item = event.getCurrentItem();

        if (item != null) {
            if (item.equals(NEXT_PAGE)) {
                SpigotFriends.getMainMenu().show(player, pageIndex + 1);
            } else if (item.equals(PREVIOUS_PAGE)) {
                SpigotFriends.getMainMenu().show(player, Math.max(0, pageIndex - 1));
            } else if (item.equals(SETTINGS)) {
                SpigotFriends.getSettingsMenu().show(player);
            } else if (item.equals(REQUESTS)) {
                SpigotFriends.getFriendRequestMenu().show(player);
            } else if (item.equals(SEND_REQUEST)) {
                new AnvilGUI(friends, player, "", (ignored, reply) -> {
                    friends.getPacketManager().sendPacket(new PacketPlayer(player), new RunCommandPacket("friend add " + reply));
                    return null;
                });
            }
        }

        if (item != null && item.getType() == Material.SKULL_ITEM && item.getDurability() == 3) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            SpigotFriends.getPlayerViewMenu().show(player, meta.getOwner());
        }

    }

    @Override
    public void show(Player player) {
        this.show(player, 0);
    }

}
