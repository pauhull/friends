package de.pauhull.friends.spigot.inventory;

import com.ikeirnez.pluginmessageframework.PacketPlayer;
import de.pauhull.friends.common.packet.RunCommandPacket;
import de.pauhull.friends.spigot.SpigotFriends;
import de.pauhull.friends.spigot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AcceptMenu implements InventoryMenu {

    private static final String TITLE = "§cAnfrage: ";
    private static final ItemStack GLASS_PANE = new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setData(15).setDisplayName(" ").build();
    private static final ItemStack BACK = new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setDisplayName("§8» §cZurück").setData(14).build();
    private static final ItemStack ACCEPT = new ItemBuilder().setMaterial(Material.EMERALD_BLOCK).setDisplayName("§8» §a§lAnnehmen").build();
    private static final ItemStack DENY = new ItemBuilder().setMaterial(Material.REDSTONE_BLOCK).setDisplayName("§8» §c§lAblehnen").build();

    private SpigotFriends friends;

    public AcceptMenu(SpigotFriends friends) {
        this.friends = friends;

        Bukkit.getPluginManager().registerEvents(this, friends);
    }

    public void show(Player player, String viewed) {

        friends.getUuidFetcher().fetchUUIDAsync(viewed, uuid -> {
            friends.getSettingsTable().getStatus(uuid, statusMessage -> {
                Inventory inventory = Bukkit.createInventory(null, 45, TITLE + viewed);

                for (int i = 0; i < inventory.getSize(); i++) {
                    if ((i < 9 || i > inventory.getSize() - 9) || (i % 9 == 0 || (i + 1) % 9 == 0)) {
                        inventory.setItem(i, GLASS_PANE);
                    }
                }

                ItemStack head = friends.getHeadCache().getHead(viewed);
                ItemMeta meta = head.getItemMeta();
                meta.setDisplayName("§8» §a" + viewed);
                head.setItemMeta(meta);
                inventory.setItem(4, BACK);
                inventory.setItem(22, head);
                inventory.setItem(20, ACCEPT);
                inventory.setItem(24, DENY);

                SynchronousInventoryOpener.QUEUE.put(player, inventory);
            });
        });

    }

    @Override
    public void show(Player player) {
        this.show(player, player.getName());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        if (inventory != null && inventory.getTitle() != null && inventory.getTitle().startsWith(TITLE)) {
            event.setCancelled(true);
        } else {
            return;
        }

        String playerName = inventory.getTitle().substring(TITLE.length());

        ItemStack item = event.getCurrentItem();

        if (item != null) {
            if (item.equals(BACK)) {
                SpigotFriends.getFriendRequestMenu().show(player);
            } else if (item.equals(ACCEPT)) {
                friends.getPacketManager().sendPacket(new PacketPlayer(player), new RunCommandPacket("friend accept " + playerName));
                player.closeInventory();
            } else if (item.equals(DENY)) {
                friends.getPacketManager().sendPacket(new PacketPlayer(player), new RunCommandPacket("friend deny " + playerName));
                player.closeInventory();
            }
        }

    }


}
