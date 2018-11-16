package de.pauhull.friends.spigot.util;

import de.pauhull.friends.common.util.Reflection;
import de.pauhull.friends.common.util.TimedHashMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class HeadCache {

    private static Class<?> itemStack;
    private static Class<?> craftItemStackClass;
    private static Method asNMSCopy;
    private static Method asBukkitCopy;

    static {
        try {
            itemStack = Reflection.getNMSClass("ItemStack");
            craftItemStackClass = Reflection.getCraftBukkitClass("inventory.CraftItemStack");
            asNMSCopy = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
            asBukkitCopy = craftItemStackClass.getMethod("asBukkitCopy", itemStack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TimedHashMap<String, Object> heads = new TimedHashMap<>(TimeUnit.HOURS, 12);

    public ItemStack getHead(String owner) {
        try {
            if (heads.containsKey(owner)) {
                return (ItemStack) asBukkitCopy.invoke(null, heads.get(owner));
            } else {
                saveHead(owner);
                return (ItemStack) asBukkitCopy.invoke(null, heads.get(owner));
            }

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        }
    }

    public void saveHead(String owner) {
        try {
            ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            meta.setOwner(owner);
            stack.setItemMeta(meta);
            Object nmsStack = asNMSCopy.invoke(null, stack);
            heads.put(owner, nmsStack);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
