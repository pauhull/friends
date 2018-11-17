package de.pauhull.friends.spigot.inventory;

import com.ikeirnez.pluginmessageframework.PacketPlayer;
import de.pauhull.friends.common.packet.RunCommandPacket;
import de.pauhull.friends.common.util.Permissions;
import de.pauhull.friends.common.util.TimedHashMap;
import de.pauhull.friends.spigot.SpigotFriends;
import de.pauhull.friends.spigot.util.ItemBuilder;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class SettingsMenu implements InventoryMenu {

    private static final String TITLE = "§cEinstellungen";
    private static final ItemStack GLASS_PANE = new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setData(15).setDisplayName(" ").build();
    private static final ItemStack FRIEND_REQUESTS = new ItemBuilder().setMaterial(Material.NETHER_STAR).setDisplayName("§8» §eFreundschaftsanfragen").build();
    private static final ItemStack JUMP = new ItemBuilder().setMaterial(Material.FIREWORK).setDisplayName("§8» §eNachjoinen").build();
    private static final ItemStack PARTY = new ItemBuilder().setMaterial(Material.CAKE).setDisplayName("§8» §ePartyeinladungen").build();
    private static final ItemStack CLANS = new ItemBuilder().setMaterial(Material.BANNER).setDisplayName("§8» §eClaneinladungen").setData(15).build();
    private static final ItemStack MESSAGES = new ItemBuilder().setMaterial(Material.BOOK).setDisplayName("§8» §ePrivatnachrichten").build();
    private static final ItemStack NOTIFICATIONS = new ItemBuilder().setMaterial(Material.NAME_TAG).setDisplayName("§8» §eOnline/Offline-Nachrichten").build();
    private static final ItemBuilder STATUS = new ItemBuilder().setMaterial(Material.PAPER);
    private static final ItemStack BACK = new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setDisplayName("§8» §cZurück").setData(14).build();
    private static final ItemStack CHANGE_STATUS = new ItemBuilder().setMaterial(Material.ANVIL).setDisplayName("§8× §e§lStatus ändern").build();
    private static final ItemStack ENABLED = new ItemBuilder().setMaterial(Material.INK_SACK).setDisplayName("§8× §a§lAktiviert").setData(10).build();
    private static final ItemStack DISABLED = new ItemBuilder().setMaterial(Material.INK_SACK).setDisplayName("§8× §c§lDeaktiviert").setData(1).build();
    private static final ItemStack UNAVAILABLE = new ItemBuilder().setMaterial(Material.INK_SACK).setDisplayName("§8× §7Nicht verfügbar").setData(8).build();

    private TimedHashMap<Player, Integer> cooldown = new TimedHashMap<>(TimeUnit.MILLISECONDS, 750);

    private SpigotFriends friends;

    public SettingsMenu(SpigotFriends friends) {
        this.friends = friends;

        Bukkit.getPluginManager().registerEvents(this, friends);
    }

    @Override
    public void show(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, TITLE);
        for (int i = 0; i < inventory.getSize(); i++) {
            if ((i < 9 || i > inventory.getSize() - 9) || (i % 9 == 0 || (i + 1) % 9 == 0)) {
                inventory.setItem(i, GLASS_PANE);
            }
        }

        inventory.setItem(10, FRIEND_REQUESTS);
        inventory.setItem(12, JUMP);
        inventory.setItem(14, PARTY);
        inventory.setItem(16, NOTIFICATIONS);
        inventory.setItem(29, CLANS);
        //31: status;
        inventory.setItem(33, MESSAGES);
        inventory.setItem(4, BACK);

        inventory.setItem(23, UNAVAILABLE);
        inventory.setItem(38, UNAVAILABLE);

        friends.getUuidFetcher().fetchUUIDAsync(player.getName(), uuid -> {
            friends.getSettingsTable().getRequests(uuid, requests -> {
                friends.getSettingsTable().getJumping(uuid, jumping -> {
                    friends.getSettingsTable().getMessages(uuid, messages -> {
                        friends.getSettingsTable().getStatus(uuid, status -> {
                            friends.getSettingsTable().getNotifications(uuid, notifications -> {
                                if (requests) {
                                    inventory.setItem(19, ENABLED);
                                } else {
                                    inventory.setItem(19, DISABLED);
                                }

                                if (jumping) {
                                    inventory.setItem(21, ENABLED);
                                } else {
                                    inventory.setItem(21, DISABLED);
                                }

                                if (notifications) {
                                    inventory.setItem(25, ENABLED);
                                } else {
                                    inventory.setItem(25, DISABLED);
                                }

                                if (messages) {
                                    inventory.setItem(42, ENABLED);
                                } else {
                                    inventory.setItem(42, DISABLED);
                                }

                                ItemStack statusStack = STATUS.setDisplayName("§8» §eStatus: §f" + status).build();
                                inventory.setItem(31, statusStack);
                                if (player.hasPermission(Permissions.STATUS)) {
                                    inventory.setItem(40, CHANGE_STATUS);
                                } else {
                                    inventory.setItem(40, DISABLED);
                                }

                                SynchronousInventoryOpener.QUEUE.put(player, inventory);

                            });
                        });
                    });
                });
            });
        });

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        ItemStack item = event.getCurrentItem();

        if (inventory != null && inventory.getTitle() != null && inventory.getTitle().equals(TITLE)) {
            event.setCancelled(true);
        } else {
            return;
        }

        if (item != null) {
            if (item.equals(BACK)) {
                SpigotFriends.getMainMenu().show(player);
            } else if (item.equals(CHANGE_STATUS)) {
                friends.getUuidFetcher().fetchUUIDAsync(player.getName(), uuid -> {
                    friends.getSettingsTable().getStatus(uuid, status -> {
                        new AnvilGUI(friends, player, status.replace('§', '&'), (ignored, reply) -> {
                            friends.getPacketManager().sendPacket(new PacketPlayer(player), new RunCommandPacket("friend status " + reply));
                            return null;
                        });
                    });
                });
            }

            int slot = event.getSlot();
            switch (slot) {
                case 19:
                    handleSlotClick(player, inventory, item, slot, "friend toggle");
                    break;
                case 21:
                    handleSlotClick(player, inventory, item, slot, "friend togglejump");
                    break;
                case 25:
                    handleSlotClick(player, inventory, item, slot, "friend togglenotify");
                    break;
                case 42:
                    handleSlotClick(player, inventory, item, slot, "friend togglemsg");
                    break;
            }


        }

    }

    private void handleSlotClick(Player player, Inventory inventory, ItemStack item, int slot, String command) {
        if (!item.equals(ENABLED) && !item.equals(DISABLED)) {
            return;
        }

        if (cooldown.containsKey(player) && cooldown.get(player) == slot) {
            player.sendMessage("§3Freunde §8» §cBitte warte einen Moment, bevor du diese Einstellung wieder ändern kannst.");
            return;
        }

        cooldown.put(player, slot);
        friends.getPacketManager().sendPacket(new PacketPlayer(player), new RunCommandPacket(command));
        if (item.equals(ENABLED)) {
            inventory.setItem(slot, DISABLED);
        } else {
            inventory.setItem(slot, ENABLED);
        }
    }

}
