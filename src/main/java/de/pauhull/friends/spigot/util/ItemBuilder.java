package de.pauhull.friends.spigot.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private int amount = 1;
    private short data = 0;
    private boolean glowing = false;
    private List<String> lore = new ArrayList<>();
    private String displayName = null;
    private Material material = Material.AIR;

    public ItemStack build() {
        ItemStack stack = new ItemStack(material, amount, data);
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(lore);

        if (displayName != null) {
            meta.setDisplayName(displayName);
        }

        if (glowing) {
            meta.addEnchant(Enchantment.DURABILITY, 0, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        stack.setItemMeta(meta);
        return stack;
    }

    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder setData(int data) {
        this.data = (short) data;
        return this;
    }

    public ItemBuilder setData(short data) {
        this.data = data;
        return this;
    }

    public ItemBuilder setGlowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        this.lore.add(line);
        return this;
    }

    public ItemBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

}
