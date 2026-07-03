package Fepbox.FepEconomy.MenuManager;

import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.Utils.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MenuManager implements InventoryHolder {
    protected DataManger dataManger;
    protected Inventory inventory;

    public MenuManager(DataManger dataManger) {
        this.dataManger = dataManger;
    }

    public abstract String getTitle();

    public abstract int getSlots();

    public abstract void SetItems();

    public abstract void handleMenu(InventoryClickEvent e);

    public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), getTitle());

        this.SetItems();

        dataManger.getOwner().openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public <T> ItemStack createItem(Material material, Component displayname, HashMap<String, T> persistentData, Component... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(displayname);

        meta.lore(Arrays.asList(lore));


        for (Map.Entry<String, T> entry : persistentData.entrySet()) {
            NamespacedKey key = new NamespacedKey(FepEconomy.getPlugin(), entry.getKey());
            Object persistent = entry.getValue();

            if (persistent instanceof Integer) {
                meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, (Integer) persistent);
            } else if (persistent instanceof String) {
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, (String) persistent);
            } else if (persistent instanceof Boolean) {
                meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, (Boolean) persistent);
            } else if (persistent instanceof Double) {
                meta.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, (Double) persistent);
            } else if (persistent instanceof Float) {
                meta.getPersistentDataContainer().set(key, PersistentDataType.FLOAT, (Float) persistent);
            } else if (persistent instanceof Long) {
                meta.getPersistentDataContainer().set(key, PersistentDataType.LONG, (Long) persistent);
            } else if (persistent instanceof Byte) {
                meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (Byte) persistent);
            }
        }
        item.setItemMeta(meta);

        return item;
    }

    protected ItemStack createItem(Material material, Component displayname, Component... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(displayname);

        meta.lore(Arrays.asList(lore));
        item.setItemMeta(meta);

        return item;
    }

    protected ItemStack createItem(Material material, String displayname, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ColorUtils.deserialize(displayname));

        List<Component> loreComponents = new ArrayList<>();
        for (String line : lore) {
            loreComponents.add(ColorUtils.deserialize(line));
        }
        meta.lore(loreComponents);
        item.setItemMeta(meta);

        return item;
    }
}
