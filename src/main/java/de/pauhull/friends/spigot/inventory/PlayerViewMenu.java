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

public class PlayerViewMenu implements InventoryMenu {

    private static final ItemStack GLASS_PANE = new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setData(15).setDisplayName(" ").build();
    private static final ItemStack REMOVE_FRIEND = new ItemBuilder().setMaterial(Material.REDSTONE).setDisplayName("§8» §cFreund entfernen").build();
    private static final ItemStack INVITE_PARTY = new ItemBuilder().setMaterial(Material.CAKE).setDisplayName("§8» §dIn Party einladen").build();
    private static final ItemStack JUMP = new ItemBuilder().setMaterial(Material.FIREWORK).setDisplayName("§8» §eZu Freund springen").build();
    private static final ItemStack BACK = new ItemBuilder().setMaterial(Material.ARROW).setDisplayName("§8» §cZurück").build();

    private SpigotFriends friends;

    public PlayerViewMenu(SpigotFriends friends) {
        this.friends = friends;

        Bukkit.getPluginManager().registerEvents(this, friends);
    }

    public void show(Player player, String viewed) {

        friends.getUuidFetcher().fetchUUIDAsync(viewed, uuid -> {
            friends.getSettingsTable().getStatus(uuid, statusMessage -> {
                Inventory inventory = Bukkit.createInventory(null, 45, "§cFreund: " + viewed);

                for (int i = 0; i < inventory.getSize(); i++) {
                    if ((i < 9 || i > inventory.getSize() - 9) || (i % 9 == 0 || (i + 1) % 9 == 0)) {
                        inventory.setItem(i, GLASS_PANE);
                    }
                }

                ItemStack head = friends.getHeadCache().getHead(viewed);
                ItemMeta meta = head.getItemMeta();
                meta.setDisplayName("§8» §a" + viewed);
                head.setItemMeta(meta);
                inventory.setItem(13, head);
                ItemStack status = new ItemStack(Material.PAPER);
                ItemMeta statusMeta = status.getItemMeta();
                statusMeta.setDisplayName("§8» §6Status: §r" + statusMessage);
                status.setItemMeta(statusMeta);
                inventory.setItem(22, status);
                inventory.setItem(20, JUMP);
                inventory.setItem(24, INVITE_PARTY);
                inventory.setItem(30, REMOVE_FRIEND);
                inventory.setItem(32, BACK);

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

        if (inventory != null && inventory.getTitle() != null && inventory.getTitle().startsWith("§cFreund: ")) {
            event.setCancelled(true);
        } else {
            return;
        }

        String playerName = inventory.getTitle().substring("§cFreund: ".length());

        ItemStack item = event.getCurrentItem();

        if (item != null) {
            if (item.equals(BACK)) {
                SpigotFriends.getMainMenu().show(player, 0);
            } else if (item.equals(REMOVE_FRIEND)) {
                friends.getPacketManager().sendPacket(new PacketPlayer(player), new RunCommandPacket("friend remove " + playerName));
                player.closeInventory();
            } else if (item.equals(JUMP)) {
                friends.getPacketManager().sendPacket(new PacketPlayer(player), new RunCommandPacket("friend jump " + playerName));
            } else if (item.equals(INVITE_PARTY)) {
                friends.getPacketManager().sendPacket(new PacketPlayer(player), new RunCommandPacket("party invite " + playerName));
            }
        }

    }

}
