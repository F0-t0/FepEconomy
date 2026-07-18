package Fepbox.FepEconomy.MenuManager.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.MenuManager.DataManger;
import Fepbox.FepEconomy.MenuManager.MenuManager;
import Fepbox.FepEconomy.Utils.ColorUtils;
import Fepbox.FepEconomy.Utils.SQLHelper;
import Fepbox.FepEconomy.Utils.Scheduler;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;

public class balTop extends MenuManager {
    private int page = 1;

    public balTop(DataManger dataManger, int page) {
        super(dataManger);
        this.page = page;
        if (page < 1) {
            this.page = 1;
        }
    }

    @Override
    public String getTitle() {
        return ColorUtils.toLegacy(
                FepEconomy.getMessagesCfg().getString("balTop-Title",
                        "<gold>BalTOP"));
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void SetItems() {
        SQLHelper sql = new SQLHelper();
        Economy econ = FepEconomy.getPlugin().getVaultEconomy();

        ItemStack it = createItem(Material.SPECTRAL_ARROW,
                FepEconomy.getMessagesCfg().getString("nextPage-name", "<gold>Next Page"));
        ItemStack bk = createItem(Material.TIPPED_ARROW,
                FepEconomy.getMessagesCfg().getString("previousPage-name", "<gold>Previous Page"));
        ItemStack filler = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");

        int offset = (page - 1) * 9;
        Scheduler.runAsync(() -> {
            List<UUID> uuids = sql.getTopPlayers(offset, 10);
            boolean hasNext = uuids.size() > 9;
            List<UUID> pageUuids = uuids.subList(0, Math.min(9, uuids.size()));
            Scheduler.runSync(() -> {
                if (hasNext) {
                    inventory.setItem(50, it);
                }
                if (page != 1) {
                    inventory.setItem(48, bk);
                }
                int place = ((page - 1) * 9) + 1;
                int[] slots = {
                        13,
                        21, 22, 23,
                        29, 30, 31, 32, 33
                };
                int idx = 0;
                for (UUID uuid : pageUuids) {
                    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta meta = (SkullMeta) item.getItemMeta();

                    meta.setPlayerProfile(Bukkit.createProfile(uuid));

                    String name = FepEconomy.getMessagesCfg().getString("head-name",
                            "<white>%place%. <gold>%player%");
                    name = name.replace("%player%", sql.getNamebyUUID(uuid));
                    name = name.replace("%place%", String.valueOf(place));
                    meta.displayName(ColorUtils.deserialize(name));
                    List<String> lore = FepEconomy.getMessagesCfg().getStringList("head-lore");

                    for (int i = 0; i < lore.size(); i++) {
                        lore.set(i, lore.get(i).replace("%bal%",
                                econ.format(econ.getBalance(Bukkit.getOfflinePlayer(uuid)))));
                        lore.set(i, lore.get(i).replace("%place%", String.valueOf(place)));
                    }
                    List<Component> loreComponents = new ArrayList<>();
                    for (String line : lore) {
                        loreComponents.add(ColorUtils.deserialize(line));
                    }
                    meta.lore(loreComponents);
                    item.setItemMeta(meta);
                    inventory.setItem(slots[idx], item);
                    place++;
                    idx++;
                }
            });
        });
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, filler);
        }
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) {
            return;
        }
        if (e.getCurrentItem().getType() == Material.SPECTRAL_ARROW) {
            new balTop(FepEconomy.getDataManger(dataManger.getOwner()), page + 1).open();
        } else if (e.getCurrentItem().getType() == Material.TIPPED_ARROW) {
            new balTop(FepEconomy.getDataManger(dataManger.getOwner()), page - 1).open();
        }
    }
}
