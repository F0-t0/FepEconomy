package Fepbox.FepEconomy.MenuManager.menus;

import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.MenuManager.DataManger;
import Fepbox.FepEconomy.MenuManager.MenuManager;
import Fepbox.FepEconomy.Utils.ColorUtils;
import Fepbox.FepEconomy.Utils.SQLHelper;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

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
        return ColorUtils.translateColorCodes(
                FepEconomy.getMessagesCfg().getString("balTop-Title",
                        "&6BalTOP")
        );
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void SetItems() {
        SQLHelper sql = new SQLHelper();
        Economy econ = FepEconomy.getPlugin().getVaultEconomy();

        ItemStack it = createItem(Material.SPECTRAL_ARROW, ColorUtils.translateColorCodes(
                FepEconomy.getMessagesCfg().getString("nextPage-name", "&6Next Page")
        ));
        ItemStack bk = createItem(Material.TIPPED_ARROW, ColorUtils.translateColorCodes(
                FepEconomy.getMessagesCfg().getString("previousPage-name", "&6Next Page")
        ));
        ItemStack filler = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");

        int maxFetch = FepEconomy.getPlugin().getConfig().getInt("max-fetch", 100);
        Bukkit.getScheduler().runTaskAsynchronously(FepEconomy.getPlugin(), () -> {
            List<UUID> uuids = sql.getTopPlayers((page - 1) * 9, maxFetch);
            uuids.removeIf(uuid -> FepEconomy.hasOfflinePermission(Bukkit.getOfflinePlayer(uuid), "FepEconomy.baltop.exempt"));
            int totalFiltered = uuids.size();
            List<UUID> pageUuids = uuids.subList(0, Math.min(9, totalFiltered));
            boolean hasNext = totalFiltered >= 9;
            Bukkit.getScheduler().runTask(FepEconomy.getPlugin(), () -> {
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
                    ItemMeta meta = item.getItemMeta();

                    SkullMeta sm = (SkullMeta) meta;
                    sm.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));

                    String name = FepEconomy.getMessagesCfg().getString("head-name",
                            "&6%player%");
                    name = name.replace("%player%", Bukkit.getOfflinePlayer(uuid).getName());
                    name = name.replace("%place%", String.valueOf(place));
                    meta.setDisplayName(ColorUtils.translateColorCodes(name));
                    List<String> lore = FepEconomy.getMessagesCfg().getStringList("head-lore");

                    for (int i = 0; i < lore.size(); i++) {
                        lore.set(i, ColorUtils.translateColorCodes(
                                lore.get(i).replace("%bal%", econ.format(econ.getBalance(Bukkit.getOfflinePlayer(uuid))))
                        ));
                        lore.set(i, ColorUtils.translateColorCodes(
                                lore.get(i).replace("%place%", String.valueOf(place))
                        ));
                    }
                    meta.setLore(lore);
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
